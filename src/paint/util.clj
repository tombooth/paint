(ns paint.util
  (:require [clojure.math.numeric-tower :refer [abs round]]))




(defmacro for-patch-index [width height i j patch-width patch-height [arr-index-name patch-index-name] & body]
  `(let [top-left# (dec (+ ~i (* ~width (dec ~j))))]
     (doseq [patch-row# (range ~patch-height)
             patch-col# (range ~patch-width)
             :let [row-start# (* ~width (+ patch-row# (dec ~j)))
                   row-end# (+ row-start# ~width)
                   ~arr-index-name (+ top-left# (* ~width patch-row#) patch-col#)
                   ~patch-index-name (+ (* ~patch-width patch-row#) patch-col#)]
             :when (and (>= ~arr-index-name 0)
                        (< ~arr-index-name (* ~width ~height))
                        (>= ~arr-index-name row-start#)
                        (< ~arr-index-name row-end#))]
       ~@body)))



(defn extract [arr width height i j patch-width patch-height]
  (let [patch (transient (vec (repeat (* patch-width patch-height) nil)))]
    (for-patch-index width height i j patch-width patch-height
                     [arr-index patch-index]
                     (assoc! patch patch-index (nth arr arr-index)))
    (persistent! patch)))



(defn patch [arr width height i j patch-arr patch-width patch-height]
  (let [trans-arr (transient arr)]
    (for-patch-index width height i j patch-width patch-height
                     [arr-index patch-index]
                     (assoc! trans-arr arr-index (nth patch-arr patch-index)))
    (persistent! trans-arr)))




;; Copied from https://github.com/jolby/colors/blob/master/src/com/evocomputing/colors.clj

(defn- hue-to-rgb
  "Convert hue color to rgb components
Based on algorithm described in:
http://en.wikipedia.org/wiki/Hue#Computing_hue_from_RGB
and:
http://www.w3.org/TR/css3-color/#hsl-color"
  [m1, m2, hue]
  (let* [h (cond
           (< hue 0) (inc hue)
           (> hue 1) (dec hue)
           :else hue)]
        (cond
         (< (* h 6) 1) (+ m1 (* (- m2 m1) h 6))
         (< (* h 2) 1) m2
         (< (* h 3) 2) (+ m1 (* (- m2 m1) (- (/ 2.0 3) h) 6))
         :else m1)))

(defn hsl-to-rgb
  "Given color with HSL values return vector of r, g, b.

Based on algorithms described in:
http://en.wikipedia.org/wiki/Luminance-Hue-Saturation#Conversion_from_HSL_to_RGB
and:
http://en.wikipedia.org/wiki/Hue#Computing_hue_from_RGB
and:
http://www.w3.org/TR/css3-color/#hsl-color"
  [hue saturation lightness]
  (let* [h (/ hue 360.0)
         s (/ saturation 100.0)
         l (/ lightness 100.0)
         m2 (if (<= l 0.5) (* l (+ s 1))
                (- (+ l s) (* l s)))
         m1 (- (* l 2) m2)]
        (into []
              (map #(round (* 0xff %))
                   [(hue-to-rgb m1 m2 (+ h (/ 1.0 3)))
                    (hue-to-rgb m1 m2 h)
                    (hue-to-rgb m1 m2 (- h (/ 1.0 3)))]))))
(defn rgb-to-hsl
  "Given the three RGB values, convert to HSL and return vector of
  Hue, Saturation, Lightness.

Based on algorithm described in:
http://en.wikipedia.org/wiki/Luminance-Hue-Saturation#Conversion_from_RGB_to_HSL_overview"
  [red green blue]
  (let* [r (/ red 255.0)
         g (/ green 255.0)
         b (/ blue 255.0)
         min (min r g b)
         max (max r g b)
         delta (- max min)
         l (/ (+ max min) 2.0)
         h (condp = max
                  min 0.0
                  r (* 60 (/ (- g b) delta))
                  g (+ 120 (* 60 (/ (- b r) delta)))
                  b (+ 240 (* 60 (/ (- r g) delta))))
         s (cond
            (= max min) 0.0
            (< l 0.5) (/ delta (* 2 l))
            :else (/ delta (- 2 (* 2 l))))]
        [(mod h 360.0) (* 100.0 s) (* 100.0 l)]))

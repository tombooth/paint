(ns paint.core
  (:require [clojure.math.numeric-tower :refer [abs]]
            [paint.util :as util]))


(defn create-substrate
  ([width height]
     (create-substrate width height {}))
  ([width height attributes]
     {:width width
      :height height
      :count (* width height)
      :cells (vec (repeat (* width height)
                          (merge {:color [255 255 255]
                                  :liquid-content 0
                                  :drying-rate 0
                                  :gravity-direction [0 1 0]
                                  :floor 0
                                  :absorbency 50
                                  :paint-content 0
                                  :paint-color nil
                                  :mix-range 10}
                                 attributes)))}))


(defn paint-ratio [{paint-content-a :paint-content}
                   {paint-content-b :paint-content}]
  (let [total (+ paint-content-a paint-content-b)]
    (/ paint-content-b total)))


(defn interpolate-vectors [v1 v2 ratio]
  (let [diff-vector (map - v2 v1)
        ratio-vector (map #(* % ratio) diff-vector)]
    (map + v1 ratio-vector)))

(defn interpolate-colors [{color-a :paint-color}
                          {color-b :paint-color}
                          ratio]
  (let [hsl-a (apply util/rgb-to-hsl color-a)
        hsl-b (apply util/rgb-to-hsl color-b)
        new-hsl (interpolate-vectors hsl-a hsl-b ratio)]
    (apply util/hsl-to-rgb new-hsl)))

(defn interpolate-key [key map-a map-b ratio]
  (let [val-a (map-a key)
        val-b (map-b key)
        diff (- val-b val-a)]
    (+ val-a (* diff ratio))))


(defn cells-mix? [into other]
  (let [diff (abs (- (:liquid-content other)
                     (:liquid-content into)))]
    (<= diff (:mix-range into))))

(defn mix [into other]
  (if (cells-mix? into other)
    (let [ratio (paint-ratio into other)]
      (merge into {:liquid-content (interpolate-key :liquid-content into other ratio)
                   :drying-rate (interpolate-key :drying-rate into other ratio)
                   :paint-content (+ (:paint-content into) (:paint-content other))
                   :paint-color (interpolate-colors into other ratio)
                   :mix-range (interpolate-key :mix-range into other ratio)}))
    (merge into other)))


(defn cell-at [substrate i j]
  (nth (:cells substrate)
       (+ i (* j (:width substrate)))
       nil))

(defn apply-brush [substrate i j brush-width brush-height brush-fn]
  (let [cells (:cells substrate)
        substrate-width (:width substrate)
        substrate-height (:height substrate)
        extracted (util/extract cells substrate-width
                                substrate-height i j
                                brush-width brush-height)
        brushed (brush-fn brush-width brush-height)
        mixed (map mix extracted brushed)]
    (assoc substrate :cells (util/patch cells substrate-width
                                        substrate-height i j
                                        mixed brush-width brush-height))))


(defn age-paint [host]
  (let [{liquid-content :liquid-content
         drying-rate :drying-rate} host
         should-dry (< (rand) drying-rate)
         new-liquid-content (if should-dry
                              (dec liquid-content)
                              liquid-content)]
    (if (<= new-liquid-content 0)
      [true (assoc host :liquid-content 0)]
      [false (assoc host :liquid-content new-liquid-content)])))


(defn engine-cycle [substrate i j]
  (let [{width :width height :height cells :cells} substrate
        cluster (util/extract cells width height i j)
        host (nth cluster 4)
        patched (assoc cluster 4 (assoc host :color [255 0 0]))]
    (assoc substrate :cells (util/patch cells width height i j patched))))


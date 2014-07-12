(ns paint.mixer.hsl
  (:require [paint.mixer :as base]
            [paint.util :as util]))



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



(defrecord HSLMixer []

  base/Mixer

  (mix [this into other]
    (if (base/cells-mix? into other)
      (let [ratio (paint-ratio into other)]
        (merge into {:liquid-content (interpolate-key :liquid-content into other ratio)
                     :drying-rate (interpolate-key :drying-rate into other ratio)
                     :paint-content (+ (:paint-content into) (:paint-content other))
                     :paint-color (interpolate-colors into other ratio)
                     :mix-range (interpolate-key :mix-range into other ratio)}))
      (merge into other))))

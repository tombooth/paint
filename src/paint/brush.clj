(ns paint.brush)

(defn block [color]
  (fn [cells width height]
    (map #(assoc % :paint-color color) cells)))


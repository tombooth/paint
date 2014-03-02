(ns paint.brush)

(defn block
  ([color] (block color 15 1 10 10))
  ([color liquid-content drying-rate paint-content mix-range]
     (fn [width height]
       (repeat (* width height)
               {:liquid-content liquid-content
                :drying-rate drying-rate
                :paint-content paint-content
                :paint-color color
                :mix-range mix-range}))))


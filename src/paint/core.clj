(ns paint.core
  (:require [paint.util :as util]))


(defn create-substrate [width height attributes]
  {:width width
   :height height
   :count (* width height)
   :cells (vec (repeat (* width height)
                       attributes))})

(defn cell-at [substrate i j]
  (nth (:cells substrate)
       (+ i (* j (:width substrate)))
       nil))

(defn apply-brush [substrate i j brush-width brush-height brush-fn]
  (let [cells (:cells substrate)
        substrate-width (:width substrate)
        substrate-height (:height substrate)
        extracted (util/extract cells substrate-width
                                substrate-height i j)
        brushed (brush-fn extracted brush-width brush-height)]
    (assoc substrate :cells (util/patch cells substrate-width
                                        substrate-height i j
                                        brushed))))



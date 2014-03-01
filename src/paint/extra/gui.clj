(ns paint.extra.gui
  (gen-class)
  (:require [quil.core :as quil]
            [paint.core :as paint]
            [paint.brush :as brush]))


(def substrate-state (atom {}))
(def brush-state (atom {:width 3 :height 3
                        :fn (brush/block [0 0 0])}))

(defn setup []
  nil)

(defn draw-pixels [substrate]
  (quil/load-pixels)
  (let [pixels (quil/pixels)]
    (doall (map-indexed #(aset-int pixels %1
                                   (apply quil/color (:color %2)))
                        (:cells substrate))))
  (quil/update-pixels))

(defn draw []
  (if-let [substrate @substrate-state]
    (draw-pixels substrate)))

(defn mouse-dragged []
  (let [x (quil/mouse-x)
        y (quil/mouse-y)
        brush @brush-state]
    (swap! substrate-state
           paint/apply-brush
           x y (:width brush)
           (:height brush)
           (:fn brush))))

(defn show-window [width height attributes]
  (reset! substrate-state
          (paint/create-substrate width height
                                  attributes))
  (quil/sketch :title "Painting with Clojure"
               :setup setup
               :draw draw
               :mouse-dragged mouse-dragged
               :size [width height]
               :renderer :p2d))

(defn -main [& args]
  (show-window 640 480 {:color [255 255 255]}))



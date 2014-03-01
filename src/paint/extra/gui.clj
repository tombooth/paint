(ns paint.extra.gui
  (gen-class)
  (:require [quil.core :as quil]
            [paint.core :as paint]
            [paint.brush :as brush]))


(def substrate-state (atom {}))
(def brush-state (atom {:width 3 :height 3
                        :fn (brush/block [0 0 0])}))


(defn engine [substrate-state]
  ;; width and height invariant throughout the life time of the painting
  (let [{max-i :width max-j :height} @substrate-state]
    (println "Starting engine...")
    (doseq [[i j] (repeatedly #(vector (rand-int max-i)
                                       (rand-int max-j)))]
      (swap! substrate-state paint/engine-cycle i j))))


(defn setup []
  (.start (Thread. (partial engine substrate-state))))


(defn memoized-color []
  (let [mem (atom {})]
    (fn [{[r g b] :color}]
      (let [int-val (bit-or (bit-shift-left r 0)
                            (bit-shift-left g 8)
                            (bit-shift-left b 16))]
        (if-let [e (find @mem int-val)]
          (val e)
          (let [ret (quil/color r g b)]
            (swap! mem assoc int-val ret)
            ret))))))


(def convert-to-color (memoized-color))

(defn draw-pixels [substrate]
  (quil/load-pixels)
  (let [pixels (quil/pixels)
        num-pixels (:count substrate)
        colors (map convert-to-color (:cells substrate))
        to-write (int-array num-pixels colors)]
    (System/arraycopy to-write 0 pixels 0 num-pixels))
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



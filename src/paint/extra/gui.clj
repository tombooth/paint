(ns paint.extra.gui
  (gen-class)
  (:require [quil.core :as quil]
            [paint.core :as paint]
            [paint.brush :as brush]))


(def substrate-state (atom {}))
(def brushes (atom [{:width 10 :height 10
                     :fn (brush/block [0 0 0])}
                    {:width 10 :height 10
                     :fn (brush/block [255 0 0])}
                    {:width 10 :height 10
                     :fn (brush/block [0 255 0])}
                    {:width 10 :height 10
                     :fn (brush/block [0 0 255])}]))
(def selected-brush (atom 1))


(defn engine [substrate-state]
  ;; width and height invariant throughout the life time of the painting
  (let [{max-i :width max-j :height} @substrate-state]
    (println "Starting engine...")
    (doseq [[i j] (repeatedly #(vector (rand-int max-i)
                                       (rand-int max-j)))]
      (swap! substrate-state paint/engine-cycle i j))))


(defn setup []
  (comment (.start (Thread. (partial engine substrate-state)))))


(defn memoized-color []
  (let [mem (atom {})]
    (fn [{color :color paint-color :paint-color}]
      (let [[r g b] (if (nil? paint-color) color paint-color)
            int-val (bit-or (bit-shift-left r 0)
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
        brush-num @selected-brush
        brushes @brushes]
    (if (and (> brush-num 0)
             (<= brush-num (count brushes)))
      (let [brush (nth brushes (dec brush-num))]
        (swap! substrate-state
               paint/apply-brush
               x y (:width brush)
               (:height brush)
               (:fn brush)))
      (println "Invalid brush selected"))))

(defn mouse-clicked []
  (let [x (quil/mouse-x)
        y (quil/mouse-y)
        brush-num @selected-brush]
    (if (= brush-num 0)
      (println (paint/cell-at @substrate-state
                              x y)))))
(defn key-typed []
  (let [key (str (quil/raw-key))]
    (if (re-matches #"[0-9]" key)
      (let [num (Integer/parseInt key)
            brushes @brushes]
        (if (<= num (count brushes))
          (do (reset! selected-brush num)
              (if (= num 0)
                (println "Selected inspector, cells clicked will output their properties")
                (println "Selected brush" num "with settings:"
                         (nth brushes (dec num)))))
          (println "Invalid brush selected"))))))

(defn show-window [width height]
  (reset! substrate-state
          (paint/create-substrate width height))
  (quil/sketch :title "Painting with Clojure"
               :setup setup
               :draw draw
               :mouse-dragged mouse-dragged
               :mouse-clicked mouse-clicked
               :key-typed key-typed
               :size [width height]
               :renderer :p2d))

(defn -main [& args]
  (show-window 640 480))



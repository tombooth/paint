(ns paint.core
  (:require [paint.mixer :as mixer]
            [paint.mixer.hsl :as hsl-mixer]
            [paint.util :as util]))


(defn create-substrate
  ([width height]
     (create-substrate width height (hsl-mixer/->HSLMixer) {}))
  ([width height mixer attributes]
     {:width width
      :height height
      :count (* width height)
      :mixer-instance mixer
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



(defn cell-at [substrate i j]
  (nth (:cells substrate)
       (+ i (* j (:width substrate)))
       nil))

(defn apply-brush [substrate i j brush-width brush-height brush-fn]
  (let [cells (:cells substrate)
        mixer-instance (:mixer-instance substrate)
        substrate-width (:width substrate)
        substrate-height (:height substrate)
        
        extracted (util/extract cells substrate-width
                                substrate-height i j
                                brush-width brush-height)
        
        brushed (brush-fn brush-width brush-height)
        
        mixed (map (partial mixer/mix mixer-instance) extracted brushed)]
    
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
  (let [{width :width
         height :height
         cells :cells
         mixer-instance :mixer-instance} substrate
        cluster (util/extract cells width height i j)
        host (nth cluster 4)
        patched (assoc cluster 4 (assoc host :color [255 0 0]))]
    (assoc substrate :cells (util/patch cells width height i j patched))))


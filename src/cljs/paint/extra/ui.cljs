(ns paint.extra.ui
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [paint.core :as paint]))

(enable-console-print!)

(defn- hex-color [& args]
  (apply str "#" (map #(.toString % 16) args)))

(defn- build-cell [cell]
  (let [color (if (nil? (:paint-color cell)) (:color cell) (:paint-color cell))
        hex-color (apply hex-color color)
        {absorbency :absorbency
         floor :floor
         liquid-content :liquid-content
         paint-content :paint-content} cell

        ;; The 3 in bucket-height is for the stroke width at the
        ;; bottom of the bucket
        bucket-height (+ absorbency 3)

        ;; When floor is 0 the path generated should be at the same
        ;; height as the bottom line of the bucket. This means that
        ;; given a line width of 3 it will need to be pushed up two pixels
        floor-height (+ floor 2)

        ;; The top of floor is going to be one pixel above its height
        ;; due to the stroke width of 3
        floor-top (+ floor-height 1)
        
        liquid-height (+ floor-top liquid-content)
        paint-height (+ floor-top paint-content)

        ;; Add 5 to the max to give some breathing room
        total-height (+ (max bucket-height floor-height liquid-height paint-height) 5)]
    (dom/td nil
            (dom/svg #js {:width 80 :height total-height}
                     (dom/g #js {:transform (str "translate(0, " total-height ") scale(1,-1)")}
                            (dom/path #js {:d (str "M2," bucket-height " L2,2 L78,2 L78," bucket-height)
                                           :style #js {:strokeWidth "3px"
                                                       :stroke hex-color
                                                       :fill "transparent"}} nil)
                            (dom/path #js {:d (str "M0," floor-height " L80," floor-height)
                                           :style #js {:strokeWidth "3px"
                                                       :stroke hex-color}})
                            (dom/rect #js {:x 6 :y floor-top :width 33 :height liquid-content
                                           :style #js {:strokeWidth "0px" :fill hex-color}})
                            (dom/rect #js {:x 41 :y floor-top :width 33 :height paint-content
                                           :style #js {:strokeWidth "0px" :fill hex-color}}))))))

(defn- build-row [row]
  (apply dom/tr nil (map build-cell row)))

(defn- visualise-cells [cells owner options]
  (reify
    om/IRender
    (render [this]
      (apply dom/table nil
             (map build-row (partition (:width options) cells))))))

(defn- visualisation [substrate owner]
  (reify
    om/IRender
    (render [this]
      (om/build visualise-cells (:cells substrate)
                {:opts {:width (:width substrate)}}))))

(defn ^:export visualise-substrate
  [width height target]
  (let [substrate (paint/create-substrate width height)
        substrate-state (atom substrate)]
    (om/root visualisation substrate-state {:target target})))

(ns paint.mixer
  (:require [clojure.math.numeric-tower :refer [abs]]))


(defn cells-mix? [into other]
  (let [diff (abs (- (:liquid-content other)
                     (:liquid-content into)))]
    (<= diff (:mix-range into))))


(defprotocol Mixer
  (mix [this into other]))



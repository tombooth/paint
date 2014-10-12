(ns paint.mixer)


(defn cells-mix? [into other]
  (if (not (or (nil? into) (nil? other)))
    (let [diff (- (:liquid-content other)
                  (:liquid-content into))]
      (<= (Math/abs (double diff)) (:mix-range into)))))


(defprotocol Mixer
  (mix [this into other]))



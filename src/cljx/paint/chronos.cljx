(ns paint.chronos)


(defprotocol Chronos
  (age [this cell]))


(defrecord BasicChronos []

  Chronos

  (age [this cell]
    (let [{liquid-content :liquid-content
           drying-rate :drying-rate} cell
           should-dry (< (rand) drying-rate)
           new-liquid-content (if should-dry
                                (dec liquid-content)
                                liquid-content)]
      (if (<= new-liquid-content 0)
        [true (assoc cell :liquid-content 0)]
        [false (assoc cell :liquid-content new-liquid-content)]))))

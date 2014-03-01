(ns paint.util)




(defmacro for-patch-index [width height i j [arr-index-name patch-index-name] & body]
  `(let [top-left# (dec (+ ~i (* ~width (dec ~j))))]
     (doseq [patch-row# (range 3)
             patch-col# (range 3)
             :let [row-start# (* ~width (+ patch-row# (dec ~j)))
                   row-end# (+ row-start# ~width)
                   ~arr-index-name (+ top-left# (* ~width patch-row#) patch-col#)
                   ~patch-index-name (+ (* 3 patch-row#) patch-col#)]
             :when (and (>= ~arr-index-name 0)
                        (< ~arr-index-name (* ~width ~height))
                        (>= ~arr-index-name row-start#)
                        (< ~arr-index-name row-end#))]
       ~@body)))



(defn extract [arr width height i j]
  (let [patch (transient (vec (repeat 9 nil)))]
    (for-patch-index width height i j
                     [arr-index patch-index]
                     (assoc! patch patch-index (nth arr arr-index)))
    (persistent! patch)))



(defn patch [arr width height i j patch-arr]
  (let [trans-arr (transient arr)]
    (for-patch-index width height i j
                     [arr-index patch-index]
                     (assoc! trans-arr arr-index (nth patch-arr patch-index)))
    (persistent! trans-arr)))

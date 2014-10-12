(ns paint.macros)


(defmacro for-patch-index [width height i j patch-width patch-height [arr-index-name patch-index-name] & body]
  `(let [top-left# (dec (+ ~i (* ~width (dec ~j))))]
     (doseq [patch-row# (range ~patch-height)
             patch-col# (range ~patch-width)
             :let [row-start# (* ~width (+ patch-row# (dec ~j)))
                   row-end# (+ row-start# ~width)
                   ~arr-index-name (+ top-left# (* ~width patch-row#) patch-col#)
                   ~patch-index-name (+ (* ~patch-width patch-row#) patch-col#)]
             :when (and (>= ~arr-index-name 0)
                        (< ~arr-index-name (* ~width ~height))
                        (>= ~arr-index-name row-start#)
                        (< ~arr-index-name row-end#))]
       ~@body)))


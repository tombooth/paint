(ns paint.util)

(def not-nil? (complement nil?))
(def not-empty? (complement empty?))

(defn- around-indices [width]
  (let [diff (quot width 2)]
    (range (- 0 diff) (+ 1 diff))))

(defn around-cols [i around-width row-width col-height]
  (let [row-i (mod i row-width)
        max-i (* row-width col-height)
        diff (around-indices around-width)
        indices (map #(+ i %) diff)]
    (map #(if (and (or (and (> % i) (> (mod % row-width) row-i))
                       (and (< % i) (< (mod % row-width) row-i))
                       (= % i))
                   (>= % 0)
                   (< % max-i))
            %
            nil)
         indices)))

(defn around-rows [i around-height around-width row-width col-height]
  (let [diff (around-indices around-height)
        rows (map #(+ i (* % row-width)) diff)]
    (map #(around-cols % around-width row-width col-height)
         rows)))

(defn back-to-patch [arr width]
  (map-indexed (fn [j row]
                 (map-indexed #(if (not-nil? %2)
                                 (+ %1 (* j width)))
                              row))
               arr))

(defn- seq-thread [a b]
  (let [sa (seq a) sb (seq b)]
    (lazy-seq
     (when (or sa sb)
       (cons [(first sa) (first sb)]
             (seq-thread (rest sa) (rest sb)))))))

(defn thread [a b]
  (filter not-nil? (flatten (seq-thread a b))))

(defn sections [arr indices]
  (let [pairs (partition 2 indices)]
    (map (fn [[start end]] (take (- end start)
                                (drop start arr)))
         pairs)))

(defn to-section-indices [rows]
  (flatten (map #(vector (first %) (inc (last %)))
                rows)))

(defn filter-rows [rows]
  (filter not-empty?
          (map #(filter not-nil? %) rows)))





(defn extract [arr width height i j]
  (let [center (+ i (* width j))
        square (flatten (around-rows center 3 3 width height))]
    (map #(if (not-nil? %) (nth arr % nil)) square)))



(defn patch [arr width height i j patch-arr]
  (let [center (+ i (* width j))
        patch-indices (around-rows center 3 3 width height)
        original-sections (sections arr
                                    (concat [0]
                                            (to-section-indices
                                             (filter-rows patch-indices))
                                            [(count arr)]))
        patch-sections (sections patch-arr
                                 (to-section-indices
                                  (filter-rows
                                   (back-to-patch patch-indices 3))))]
    (thread original-sections patch-sections)))

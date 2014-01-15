(ns paint.core-test
  (:require [clojure.test :refer :all]
            [paint.core :as core]))


(deftest test-create-substrate
  (let [substrate (core/create-substrate 100 200 {:color [0 0 0]})]
    (is (= (:width substrate) 100))
    (is (= (:height substrate) 200))
    (is (= (count (:cells substrate)) 20000))
    (is (= (first (:cells substrate)) {:color [0 0 0]}))))


(deftest test-cell-at
  (let [substrate (core/create-substrate 1 1 {:color [0 0 0]})]
    (is (= (core/cell-at substrate 0 0) {:color [0 0 0]}))
    (is (nil? (core/cell-at substrate 1 1)))))


(defn hard-white-brush [cells width height]
  (map #(assoc % :color [255 255 255]) cells))

(deftest test-apply-brush
  (let [substrate (core/create-substrate 10 10 {:color [0 0 0]})
        painted-substrate (core/apply-brush substrate 2 2 3 3 hard-white-brush)]
    (is (not (= substrate painted-substrate)))
    (is (= (:color (core/cell-at painted-substrate 0 0)) [0 0 0]))
    (is (= (:color (core/cell-at painted-substrate 2 2)) [255 255 255]))))



(ns paint.core-test
  (:require [clojure.test :refer :all]
            [paint.core :as core]))


(deftest test-create-substrate
  (let [substrate (core/create-substrate 100 200 {:color [0 0 0]})]
    (is (= (:width substrate) 100))
    (is (= (:height substrate) 200))
    (is (= (count (:cells substrate)) 20000))
    (is (= (-> substrate :cells first :color) [0 0 0]))))


(deftest test-cell-at
  (let [substrate (core/create-substrate 1 1 {:color [0 0 0]})]
    (is (= (:color (core/cell-at substrate 0 0)) [0 0 0]))
    (is (nil? (core/cell-at substrate 1 1)))))


(defn hard-white-brush [cells width height]
  (map #(assoc % :paint-color [255 255 255]) cells))

(deftest test-apply-brush
  (let [substrate (core/create-substrate 10 10)
        painted-substrate (core/apply-brush substrate 2 2 3 3 hard-white-brush)]
    (is (not (= substrate painted-substrate)))
    (is (= (:paint-color (core/cell-at painted-substrate 0 0)) nil))
    (is (= (:paint-color (core/cell-at painted-substrate 2 2)) [255 255 255]))))

(deftest test-age-paint

  (testing "if it is dry then do nothing"
    (let [host {:liquid-content 0
                :drying-rate 1}]
      (is (= (core/age-paint host)
             [true host]))))

  (testing "if it is went and will dry then it should reduce liquid content"
    (let [host {:liquid-content 10
                :drying-rate 1}]
      (is (= (core/age-paint host)
             [false {:liquid-content 9
                     :drying-rate 1}]))))

  (testing "if it cannot dry then the liquid content should be the same"
    (let [host {:liquid-content 10
                :drying-rate 0}]
      (is (= (core/age-paint host)
             [false {:liquid-content 10
                     :drying-rate 0}]))))

  (testing "if it has dried the paint completely stop"
    (let [host {:liquid-content 1
                :drying-rate 1}]
      (is (= (core/age-paint host)
             [true {:liquid-content 0
                     :drying-rate 1}])))))

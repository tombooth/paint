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

(deftest test-paint-ratio
  (is (= (core/paint-ratio {:paint-content 3}
                           {:paint-content 3})
         1/2))

  (is (= (core/paint-ratio {:paint-content 0}
                           {:paint-content 6})
         1))

  (is (= (core/paint-ratio {:paint-content 6}
                           {:paint-content 0})
         0))

  (is (= (core/paint-ratio {:paint-content 2}
                           {:paint-content 4})
         2/3))

  (is (= (core/paint-ratio {:paint-content 4}
                           {:paint-content 2})
         1/3)))


(deftest test-interpolate-vectors
  (is (= (core/interpolate-vectors [0 0 0] [4 4 4] 1/4)
         [1 1 1])))

(deftest test-interpolate-colors
  (is (= (core/interpolate-colors {:paint-color [0 0 0]}
                                  {:paint-color [255 255 255]}
                                  0.5)
         [128 128 128])))

(deftest test-interpolate-key
  (is (= (core/interpolate-key :foo
                               {:foo 0}
                               {:foo 8}
                               0.5)
         4.0)))

(deftest test-cells-mix?
  (is (core/cells-mix? {:liquid-content 10
                        :mix-range 10}
                       {:liquid-content 5}))
  (is (not (core/cells-mix? {:liquid-content 10
                             :mix-range 10}
                            {:liquid-content 30}))))

(defn cell [l p c r]
  {:liquid-content l
   :drying-rate 1
   :paint-content p
   :paint-color c
   :mix-range r})

(deftest test-mix
  (is (= (core/mix (cell 0 0 nil 0)
                   (cell 10 10 [0 0 0] 10))
         {:liquid-content 10
          :drying-rate 1
          :paint-content 10
          :paint-color [0 0 0]
          :mix-range 10}))

  (is (= (core/mix (cell 10 3 [0 0 0] 10)
                   (cell 10 3 [255 255 255] 20))
         {:liquid-content 10
          :drying-rate 1
          :paint-content 6
          :paint-color [128 128 128]
          :mix-range 15})))



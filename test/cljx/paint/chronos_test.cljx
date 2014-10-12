(ns paint.chronos-test
  #+clj (:require [clojure.test :refer :all]
                  [paint.chronos :as chronos])
  #+cljs (:require-macros [cemerick.cljs.test
                           :refer (is deftest testing block-or-done)])
  #+cljs (:require [cemerick.cljs.test :as t]
                   [paint.chronos :as chronos]))


(deftest test-age-paint

  (let [chronos-instance (chronos/->BasicChronos)]
    
    (testing "if it is dry then do nothing"
      (let [cell {:liquid-content 0
                  :drying-rate 1}]
        (is (= (chronos/age chronos-instance cell)
               [true cell]))))

    (testing "if it is went and will dry then it should reduce liquid content"
      (let [cell {:liquid-content 10
                  :drying-rate 1}]
        (is (= (chronos/age chronos-instance cell)
               [false {:liquid-content 9
                       :drying-rate 1}]))))

    (testing "if it cannot dry then the liquid content should be the same"
      (let [cell {:liquid-content 10
                  :drying-rate 0}]
        (is (= (chronos/age chronos-instance cell)
               [false {:liquid-content 10
                       :drying-rate 0}]))))

    (testing "if it has dried the paint completely stop"
      (let [cell {:liquid-content 1
                  :drying-rate 1}]
        (is (= (chronos/age chronos-instance cell)
               [true {:liquid-content 0
                      :drying-rate 1}]))))))




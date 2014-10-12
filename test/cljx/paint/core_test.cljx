(ns paint.core-test
  #+clj (:require [clojure.test :refer :all]
                  [paint.core :as core])
  #+cljs (:require-macros [cemerick.cljs.test
                           :refer (is deftest testing block-or-done)])
  #+cljs (:require [cemerick.cljs.test :as t]
                   [paint.core :as core]))


(deftest test-create-substrate
  (let [substrate (core/create-substrate 100 200 nil nil {:color [0 0 0]})]
    (is (= (:width substrate) 100))
    (is (= (:height substrate) 200))
    (is (= (count (:cells substrate)) 20000))
    (is (= (-> substrate :cells first :color) [0 0 0]))))


(deftest test-cell-at
  (let [substrate (core/create-substrate 1 1 nil nil {:color [0 0 0]})]
    (is (= (:color (core/cell-at substrate 0 0)) [0 0 0]))
    (is (nil? (core/cell-at substrate 1 1)))))


(defn hard-white-brush [width height]
  (repeat (* width height)
          {:liquid-content 15
           :drying-rate 1
           :paint-content 1
           :paint-color [255 255 255]
           :mix-range 1}))

(deftest test-apply-brush
  (let [substrate (core/create-substrate 10 10)
        painted-substrate (core/apply-brush substrate 2 2 3 3 hard-white-brush)]
    (is (not (= substrate painted-substrate)))
    (is (= (:paint-color (core/cell-at painted-substrate 0 0)) nil))
    (is (= (:paint-color (core/cell-at painted-substrate 2 2)) [255 255 255]))))


(ns paint.mixer.hsl-test
  #+clj (:require [clojure.test :refer :all]
                  [paint.mixer :as mixer]
                  [paint.mixer.hsl :as hsl-mixer])
  #+cljs (:require-macros [cemerick.cljs.test
                           :refer (is deftest testing block-or-done)])
  #+cljs (:require [cemerick.cljs.test :as t]
                   [paint.mixer :as mixer]
                   [paint.mixer.hsl :as hsl-mixer]))

(defn- scale [x y]
  (if (or (zero? x) (zero? y))
    1
    (Math/abs x)))

(defn- float=
  ([x y] (float= x y 0.00001))
  ([x y epsilon]
     (let [floatX (float x)
           floatY (float y)
           difference (- floatX floatY)]
       (<= (Math/abs difference) (* (scale floatX floatY) epsilon)))))

(deftest test-paint-ratio
  (is (float= (hsl-mixer/paint-ratio {:paint-content 3}
                                     {:paint-content 3})
              0.5))

  (is (float= (hsl-mixer/paint-ratio {:paint-content 0}
                                     {:paint-content 6})
              1))

  (is (float= (hsl-mixer/paint-ratio {:paint-content 6}
                                     {:paint-content 0})
              0))

  (is (float= (hsl-mixer/paint-ratio {:paint-content 2}
                                     {:paint-content 4})
              (/ 2 3)))
  
  (is (float= (hsl-mixer/paint-ratio {:paint-content 4}
                                     {:paint-content 2})
              (/ 1 3))))


(deftest test-interpolate-vectors
  (is (= (hsl-mixer/interpolate-vectors [0 0 0] [4 4 4] 0.25)
         [1.0 1.0 1.0])))

(deftest test-interpolate-colors
  (is (= (hsl-mixer/interpolate-colors {:paint-color [0 0 0]}
                                       {:paint-color [255 255 255]}
                                       0.5)
         [128 128 128])))

(deftest test-interpolate-key
  (is (= (hsl-mixer/interpolate-key :foo
                                    {:foo 0}
                                    {:foo 8}
                                    0.5)
         4.0)))

(defn cell [l p c r]
  {:liquid-content l
   :drying-rate 1
   :paint-content p
   :paint-color c
   :mix-range r})

(deftest test-mix
  (let [mixer-instance (hsl-mixer/->HSLMixer)]
    (is (= (mixer/mix mixer-instance
                      (cell 0 0 nil 0)
                      (cell 10 10 [0 0 0] 10))
           {:liquid-content 10
            :drying-rate 1
            :paint-content 10
            :paint-color [0 0 0]
            :mix-range 10}))

    (is (= (mixer/mix mixer-instance
                      (cell 10 3 [0 0 0] 10)
                      (cell 10 3 [255 255 255] 20))
           {:liquid-content 10
            :drying-rate 1
            :paint-content 6
            :paint-color [128 128 128]
            :mix-range 15}))))


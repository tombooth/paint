(ns paint.util-test
  (:require [clojure.test :refer :all]
            [paint.util :as util]))

(def square [1 2 3 4
             4 5 6 7
             8 9 1 2
             3 4 5 6])

(deftest test-extraction
  (testing "edges"
    (is (= (util/extract square 4 4 0 0) [nil nil nil
                                          nil 1   2
                                          nil 4   5]))
    (is (= (util/extract square 4 4 1 0) [nil nil nil
                                          1   2   3
                                          4   5   6]))
    (is (= (util/extract square 4 4 3 3) [1   2   nil
                                          5   6   nil
                                          nil nil nil]))
    (is (= (util/extract square 4 4 1 1) [1 2 3
                                          4 5 6
                                          8 9 1]))))

(def patch [9 8 7
            6 5 4
            4 3 2])

(deftest test-patch
  (is (= (util/patch square 4 4 0 0 patch) [5 4 3 4
                                            3 2 6 7
                                            8 9 1 2
                                            3 4 5 6]))
  (is (= (util/patch square 4 4 1 1 patch) [9 8 7 4
                                            6 5 4 7
                                            4 3 2 2
                                            3 4 5 6]))
  (is (= (util/patch square 4 4 3 3 patch) [1 2 3 4
                                            4 5 6 7
                                            8 9 9 8
                                            3 4 6 5]))
  (is (= (util/patch square 4 4 1 3 patch) [1 2 3 4
                                            4 5 6 7
                                            9 8 7 2
                                            6 5 4 6]))
  (is (= (util/patch square 4 4 0 3 patch) [1 2 3 4
                                            4 5 6 7
                                            8 7 1 2
                                            5 4 5 6])))



(deftest test-rgb-to-hsl
  (is (= (util/rgb-to-hsl 255 0 0)
         [0.0 100.0 50.0])))

(deftest test-hsl-to-rgb
  (is (= (util/hsl-to-rgb 0.0 100.0 50.0)
         [255 0 0])))

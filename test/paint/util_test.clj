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

(deftest test-thread
  (is (= (util/thread [0 1 2 3] [9 8 7])
         [0 9 1 8 2 7 3])))

(deftest test-sections
  (is (= (util/sections square [0 2 5 6])
         [[1 2 ] [5]]))
  (is (= (util/sections square [0 0 2 5 7 9])
         [[] [3 4 4] [7 8]])))

(deftest test-around-cols
  (is (= (util/around-cols 1 3 3 3) [0 1 2]))
  (is (= (util/around-cols 0 3 3 3) [nil 0 1])))

(deftest test-around-rows
  (is (= (util/around-rows 4 3 3 3 3)
         [[0 1 2] [3 4 5] [6 7 8]])))

(deftest test-to-section-indices
  (is (= (util/to-section-indices [[0 1 2] [3 4 5]])
         [0 3 3 6])))

(deftest test-filter-rows
  (is (= (util/filter-rows [[nil nil] [nil 1]])
         [[1]])))

(deftest test-back-to-patch
  (is (= (util/back-to-patch [[nil nil] [nil 15]] 2)
         [[nil nil] [nil 3]])))


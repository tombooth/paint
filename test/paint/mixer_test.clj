(ns paint.mixer-test
  (:require [clojure.test :refer :all]
            [paint.mixer :as mixer]))


(deftest test-cells-mix?
  (is (mixer/cells-mix? {:liquid-content 10
                        :mix-range 10}
                       {:liquid-content 5}))
  (is (not (mixer/cells-mix? {:liquid-content 10
                             :mix-range 10}
                            {:liquid-content 30}))))

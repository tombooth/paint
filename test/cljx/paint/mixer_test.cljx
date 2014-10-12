(ns paint.mixer-test
  #+clj (:require [clojure.test :refer :all]
                  [paint.mixer :as mixer])
  #+cljs (:require-macros [cemerick.cljs.test
                           :refer (is deftest testing block-or-done)])
  #+cljs (:require [cemerick.cljs.test :as t]
                   [paint.mixer :as mixer]))


(deftest test-cells-mix?
  (is (mixer/cells-mix? {:liquid-content 10
                        :mix-range 10}
                       {:liquid-content 5}))
  (is (not (mixer/cells-mix? {:liquid-content 10
                             :mix-range 10}
                             {:liquid-content 30})))
  (is (not (mixer/cells-mix? {:liquid-content 10
                              :mix-range 10}
                             nil)))
  (is (not (mixer/cells-mix? nil
                             {:liquid-content 30}))))

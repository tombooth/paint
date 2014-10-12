(defproject paint "0.1.0-SNAPSHOT"
  :description "Library from paint emulation"
  :url "https://github.com/tombooth/paint"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2371"]
                 [quil "2.2.2"]]

  :plugins [[com.keminglabs/cljx "0.4.0"]
            [com.cemerick/clojurescript.test "0.3.1"]
            [lein-cljsbuild "1.0.3"]]

  :hooks [cljx.hooks
          leiningen.cljsbuild]
 
  :cljx {:builds [{:source-paths ["src/cljx"]
                   :output-path "target/classes"
                   :rules :clj}
                  {:source-paths ["src/cljx"]
                   :output-path "target/cljs-generated"
                   :rules :cljs}
                  {:source-paths ["test/cljx"]
                   :output-path "target/test-classes"
                   :rules :clj}
                  {:source-paths ["test/cljx"]
                   :output-path "target/cljs-tests-generated"
                   :rules :cljs}]}

  :cljsbuild {:builds {:main {:source-paths ["target/cljs-generated"]
                              :compiler {:output-to "dist/paint.js"
                                         :output-dir "dist/main.tmp"
                                         :source-map "dist/paint.map.js"
                                         :pretty-print true}}
                       :test {:source-paths ["target/cljs-generated"
                                             "target/cljs-tests-generated"]
                              :compiler {:output-to "target/cljs/testable.js"
                                         :optimizations :whitespace
                                         :pretty-print true}}
                       :dist {:source-paths ["target/cljs-generated"]
                              :compiler {:output-to "dist/paint.min.js"
                                         :output-dir "dist/dist.tmp"
                                         :optimizations :advanced
                                         :source-map "dist/paint.min.map.js"
                                         :pretty-print false}}}
              :test-commands {"unit" ["phantomjs" :runner
                                      "this.literal_js_was_evaluated=true"
                                      "target/cljs/testable.js"]}}

  :source-paths ["src/clj" "target/classes"]
  :test-paths ["test/clj" "target/test-classes"]

  :main paint.extra.gui)

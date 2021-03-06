(defproject savings-tracker "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj"]
  :repl-options {:timeout 200000} ;; Defaults to 30000 (30 seconds)

  :test-paths ["spec/clj"]

  :dependencies [[com.andrewmcveigh/cljs-time "0.3.4"]
                 [com.cognitect/transit-cljs "0.8.225"]
                 [com.lucasbradstreet/cljs-uuid-utils "1.0.1"]
                 [com.mchange/c3p0 "0.9.5.2"]
                 [com.stuartsierra/component "0.3.0"]
                 [migratus "0.8.11"]
                 [org.clojure/clojure "1.7.0-beta2"]
                 [org.clojure/clojurescript "1.7.48" :scope "provided"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [org.postgresql/postgresql "9.4.1208"]
                 [ring "1.3.2"]
                 [ring/ring-defaults "0.1.3"]
                 [ring/ring-json "0.4.0"]
                 [org.slf4j/slf4j-log4j12 "1.7.9"]
                 [bidi "1.25.1"]
                 [compojure "1.3.4"]
                 [enlive "1.1.5"]
                 [om "0.8.0-rc1"]
                 [environ "1.0.0"]
                 [crypto-password "0.1.3"]]

  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-environ "1.0.0"]]

  :min-lein-version "2.5.0"

  :uberjar-name "savings-tracker.jar"

  :cljsbuild {:builds {:app {:source-paths ["src/cljs"]
                             :compiler {:output-to     "resources/public/js/app.js"
                                        :output-dir    "resources/public/js/out"
                                        :source-map    "resources/public/js/out.js.map"
                                        :preamble      ["react/react.min.js"]
                                        :optimizations :none
                                        :pretty-print  true}}}}

  :aliases {"migrate" ["run" "-m" "leiningen.migrate/migrate"]}

  :profiles {:dev {:source-paths ["env/dev/clj"]
                   :test-paths ["test/clj"]

                   :dependencies [[figwheel "0.3.3"]
                                  [figwheel-sidecar "0.3.3"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [org.clojure/tools.nrepl "0.2.10"]
                                  [weasel "0.7.0-SNAPSHOT"]]

                   :repl-options {:init-ns savings-tracker.server
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                   :plugins [[lein-figwheel "0.3.1"]]

                   :figwheel {:http-server-root "public"
                              :server-port 3449
                              :css-dirs ["resources/public/css"]}

                   :env {:is-dev true}

                   :cljsbuild {:test-commands { "test" ["phantomjs" "env/test/js/unit-test.js" "env/test/unit-test.html"] }
                               :builds {:app {:source-paths ["env/dev/cljs"]
                                              :figwheel true}
                                        :test {:source-paths ["src/cljs" "test/cljs"]
                                               :compiler {:output-to     "resources/public/js/app_test.js"
                                                          :output-dir    "resources/public/js/test"
                                                          :source-map    "resources/public/js/test.js.map"
                                                          :preamble      ["react/react.min.js"]
                                                          :optimizations :whitespace
                                                          :pretty-print  false}}}}}

             :uberjar {:source-paths ["env/prod/clj"]
                       :hooks [leiningen.cljsbuild]
                       :env {:production true}
                       :omit-source true
                       :aot :all
                       :cljsbuild {:builds {:app
                                            {:source-paths ["env/prod/cljs"]
                                             :compiler
                                             {:optimizations :advanced
                                              :pretty-print false}}}}}})

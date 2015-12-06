(defproject essence "0.1.0-SNAPSHOT"
  :description "Essence"
  :url "http://github.com/brain-fn/essence"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [compojure "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [http-kit "2.1.18"]
                 [hiccup "1.0.5"]
                 [com.cemerick/friend "0.2.1" :exclusions [org.clojure/core.cache]]
                 [com.taoensso/sente "1.6.0"]
                 [bidi "1.21.1"]
                 [kibu/pushy "0.3.6"]
                 [com.novemberain/monger "3.0.0-rc2"]
                 [org.omcljs/om "1.0.0-alpha25"]]
  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-2"]]
  :source-paths ["src" "src-cljs"]
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]
  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src-cljs"]
                :figwheel true
                :compiler {:main essence.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/essence.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}
               {:id "min"
                :source-paths ["src-cljs"]
                :compiler {:output-to "resources/public/js/compiled/essence.js"
                           :main essence.core
                           :jar true
                           :optimizations :advanced
                           :pretty-print false}}]}
  :figwheel {:css-dirs ["resources/public/css"]}
  :hooks [leiningen.cljsbuild]
  :aot [essence.core]
  :main essence.core
  :jar-name "essence.jar"
  :uberjar-name "essence-standalone.jar")

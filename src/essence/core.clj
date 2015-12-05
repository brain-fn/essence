(ns essence.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [compojure.handler :refer [site]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [org.httpkit.server :as httpkit]))

(defn index [req]
  (slurp (io/resource "public/index.html")))

(defroutes app-routes
  (GET "/" [] index)
  (route/resources "/resources/")
  (route/not-found "There's no Essence here :("))

(def app
  (wrap-defaults app-routes site-defaults))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn -main []
  (let [env (System/getenv)
        port (read-string (get env "PORT" "8080"))]
    (reset! server (httpkit/run-server #'app {:port port}))
    (println (str "Server started on port " port))))

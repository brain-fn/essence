(ns essence.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [compojure.handler :refer [site]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [cemerick.friend :as friend]
            [cemerick.friend.workflows :as workflows]
            [taoensso.sente :as sente]
            [taoensso.sente.server-adapters.http-kit :refer [sente-web-server-adapter]]
            [org.httpkit.server :as httpkit]
            [essence.handlers :as handlers]))

(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente/make-channel-socket! sente-web-server-adapter {:packer :edn})]
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids) ; Watchable, read-only atom
)

(defroutes app-routes
  (GET "/" [] handlers/index)
  (GET "/app/" [] handlers/app-handler)
  (POST "/query" [] handlers/query)
  (GET "/logout" [] handlers/logout)
  (GET  "/ws" req (ring-ajax-get-or-ws-handshake req))
  (POST "/ws" req (ring-ajax-post                req))
  (route/resources "/app/")
  (route/not-found "There's no Essence here :("))

(defn credentials-fn [creds]
  (let [res  {:identity (:username creds)
              :roles #{::user}}]
    ; (println creds)
    ; (println res)
    res))

(def app
  (-> app-routes
      (friend/authenticate
       {:allow-anon? true
        :login-uri "/login"
        :default-landing-uri "/app/"
        :unauthorized-handler handlers/unauthorized-handler
        :credential-fn credentials-fn
        :workflows [(workflows/interactive-form)]})
      (wrap-defaults site-defaults)))

(defonce webserver (atom nil))

(defn stop-web-server! []
  (when-let [w @webserver]
    ((:stop-fn w))
    (reset! webserver nil)))

(defn start-web-server! []
  (stop-web-server!)
  (let [env (System/getenv)
        port (read-string (get env "PORT" "8080"))
        stop-fn (httpkit/run-server #'app {:port port})]
    (reset! webserver {:server nil
                       :port port
                       :stop-fn (fn [] (stop-fn :timeout 100))})
    (println (str "Server started on port " port))))

(defonce router (atom nil))

(defn stop-router! []
  (when-let [stop-f @router]
    (stop-f)))

(defn start-router! []
  (stop-router!)
  (reset! router
          (sente/start-chsk-router! ch-chsk handlers/query)))

(defn start! []
  (start-router!)
  (start-web-server!))

(defn -main []
  (start!))

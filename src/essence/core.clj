(ns essence.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [compojure.handler :refer [site]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as resp]
            [ring.util.codec :as codec]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [cemerick.friend :as friend]
            [cemerick.friend.workflows :as workflows]
            [hiccup.core :as h]
            [hiccup.form :as form]
            [hiccup.page :as page]
            [org.httpkit.server :as httpkit]))

(defn head []
  [:head
   [:title "Essence"]
   (page/include-css "css/style.css")])

(defn wrap-page [& markup]
  (h/html (head)
          [:body markup]))

(defn index [req]
  (wrap-page [:h1 "Essence"]
             [:p [:a {:href "/app/"} "Look Inside a Book!"]]
             (form/form-to [:post "/login"]
                           (anti-forgery-field)
                           (form/text-field "username")
                           (form/submit-button "Sign In"))))

(defn app-handler [req]
  (let [current-user (friend/current-authentication req)]
    (wrap-page [:div {:id "app"}]
               (form/hidden-field {:id "current-user"} "current-user" (:identity current-user))
               (page/include-js "js/compiled/essence.js"))))

(defn login [req]
  (friend/authorize #{::user} "You're a user"))

(defn logout [req]
  (friend/logout* (resp/redirect (str (:context req) "/"))))

(defroutes app-routes
  (GET "/" [] index)
  (GET "/app/" [] app-handler)
  (POST "/login" [] login)
  (POST "/logout" [] logout)
  (route/resources "/app/")
  (route/not-found "There's no Essence here :("))

(defn credentials-fn [creds]
  (let [res  {:identity (:username creds)
              :roles #{::user}}]
    ; (println creds)
    ; (println res)
    res))

(defn unauthorized-handler [req]
  (-> "You do not have sufficient privileges to access "
      (str (:uri req))
      resp/response
      (resp/status 401)))

(def app
  (-> app-routes
      (friend/authenticate
       {:allow-anon? true
        :login-uri "/login"
        :default-landing-uri "/app/"
        :unauthorized-handler unauthorized-handler
        :credential-fn credentials-fn
        :workflows [(workflows/interactive-form)]})
      (wrap-defaults site-defaults)))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server []
  (let [env (System/getenv)
        port (read-string (get env "PORT" "8080"))]
    (reset! server (httpkit/run-server #'app {:port port}))
    (println (str "Server started on port " port))))

(defn restart []
  (stop-server)
  (start-server))

(defn -main []
  (start-server))

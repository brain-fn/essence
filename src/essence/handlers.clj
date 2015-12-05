(ns essence.handlers
  (:require [essence.parser :refer [parser]]
            [ring.util.response :as resp]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [cemerick.friend :as friend]
            [hiccup.core :as h]
            [hiccup.form :as form]
            [hiccup.page :as page]))

(defn query 
  [{:keys [event id ?data ring-req ?reply-fn send-fn] :as msg}]
  ; (println "MSG: ")
  ; (clojure.pprint/pprint event)
  (when (= id :essence/query)
    (let [whoami (friend/current-authentication ring-req)
          current-user (:identity whoami)
          state (atom {:app/user current-user})
          res (parser {:state state} ?data)]
      (when ?reply-fn
        (?reply-fn res)))))

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
  (wrap-page [:div {:id "app"}]
             (page/include-js "js/compiled/essence.js")))

(defn unauthorized-handler [req]
  (-> "You do not have sufficient privileges to access "
      (str (:uri req))
      resp/response
      (resp/status 401)))


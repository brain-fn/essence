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
  (clojure.pprint/pprint event)
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
   (page/include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
   (page/include-css "css/style.css")])

(defn wrap-page [& markup]
  (h/html (head)
          [:body [:div {:class "container-fluid" :id "wrap"}
                  [:nav {:class "navbar navbar-default"
                         :role "navigation"}
                    [:div {:class "collapse navbar-collapse navbar-ex1-collapse"}
                     [:a {:href "/"}
                      [:img {:src "img/Essence_logo_small.png"
                            :style "padding-top:5px"}]]
                     [:ui {:class "navbar-nav nav navbar-right"}
                      [:li [:a {:href "/app/"} "look inside"]]
                      [:li
                       [:div {:style "height:30px; padding-top:10px; margin-right:10px"}
                        (form/form-to [:post "/login"]
                                      (anti-forgery-field)
                                      (form/text-field "username")
                                      (form/submit-button "Sign In"))
                        ]]
                      ]
                      ]]
                  markup]]))

(defn index [req]
  (wrap-page
             [:div {:class "row"}
              [:div {:class "col-md-4 col-md-offset-3"}
                [:img {:src "img/animation.gif"}]
                ]
              ]
             [:div {:class "row"}
              [:div
               [:p {:class " text-center"}
                [:a {:href "/app/" :class"btn btn-primary" :role "button"} "Look Inside a Book!"]]]]))

(defn app-handler [req]
  (wrap-page [:div {:id "app"}]
             (page/include-js "js/compiled/essence.js")))

(defn unauthorized-handler [req]
  (-> "You do not have sufficient privileges to access "
      (str (:uri req))
      resp/response
      (resp/status 401)))


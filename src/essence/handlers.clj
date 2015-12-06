(ns essence.handlers
  (:require [essence.parser :refer [parser]]
            [ring.util.response :as resp]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [cemerick.friend :as friend]
            [hiccup.core :as h]
            [hiccup.form :as form]
            [hiccup.page :as page]))

(defn get-current-user [req]
  (-> req friend/current-authentication :identity))

(defn query 
  [{:keys [event id ?data ring-req ?reply-fn send-fn] :as msg}]
  (clojure.pprint/pprint event)
  (when (= id :essence/query)
    (let [current-user (get-current-user ring-req)
          state (atom {:app/user current-user})
          res (parser {:state state} ?data)]
      (when ?reply-fn
        (?reply-fn res)))))

(defn head []
  [:head
   [:title "Essence"]
   (page/include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css")
   (page/include-css "css/style.css")])

(defn wrap-page [req & markup]
  (h/html (head)
          [:body [:div {:class "container-fluid" :id "wrap"}
                  [:nav {:class "navbar navbar-default"
                         :role "navigation"}
                   [:div {:class "collapse navbar-collapse navbar-ex1-collapse"}
                    [:a {:href "/"}
                     [:img {:src "img/Essence_logo_small.png"
                            :style "padding-top:5px"}]]
                    [:ui {:class "navbar-nav nav navbar-right"}
                     (if-not (get-current-user req)
                      [:li {:style "padding-top:10px;padding-right:10px"}
                        [:span "Pick a username -> "]])
                     [:li
                      [:div {:style "height:30px; padding-top:10px; margin-right:10px"}
                       (if-let [user (get-current-user req)]
                         [:span
                          (str "Welcome, " user "! ")
                          [:a {:href "/logout"} "Log Out"]]

                         (form/form-to [:post "/login"]
                                       (anti-forgery-field)
                                       (form/text-field "username")
                                       (form/submit-button "Sign In")))]]]]]
                  markup]]))

(defn index [req]
  (wrap-page req
             [:div {:class "row"}
              [:div {:class "col-md-4 col-md-offset-3"}
               [:img {:src "img/animation.gif"}]]]
             [:div {:class "row"}
              [:div {:class "col-md-6 col-md-offset-3"}
               [:p {:class " text-center"}
                [:a {:href "/app/" :class"btn btn-primary" :role "button"} "Look Inside a Book!"]]
               [:p
                [:div {:class "well"}
                 [:p
                  "We've called it \"Essence\" because it represents the idea of the main points, ideas and topics that books consist of. We wanted to make a \"ideas\" browser - once you've seen an curious idea in the book, you want to see where more that idea can be found."
                   [:br]
                  "Or you want to compare books by some property or topic coverage. " [:br]]
                 [:p
                  "\nWhat book about Clojure is best for beginners? " [:br]
                  "What for advanced coders? " [:br]
                  "Is that book practical or conceptual? " [:br]
                  "Fun or dull?"]]]]]))

(defn app-handler [req]
  (wrap-page req
             [:div {:id "app"}]
             (page/include-js "js/compiled/essence.js")))

(defn unauthorized-handler [req]
  (-> "You do not have sufficient privileges to access "
      (str (:uri req))
      resp/response
      (resp/status 401)))


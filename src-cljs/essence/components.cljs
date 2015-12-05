(ns essence.components
  (:require [clojure.string :as string]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defui Header
  static om/IQuery
  (query [this]
    [:name])
  Object
  (render [this]
    (let [{:keys [:name]} (om/props this)]
      (dom/div #js {:id "header"}
        (str "Welcome, " (or name "stranger") "! ")
        (if-not name
          (dom/a #js {:href "/"} "Log In")
          (dom/a #js {:href "/logout"} "Log Out"))))))

(def header (om/factory Header))


(defui Book
  static om/IQuery
  (query [this]
    [#_:_id :name :year :cover :authors :goodreads-link])
  Object
  (render [this]
    (let [{:keys [name authors]} (om/props this)]
      (dom/li nil (str name " by " authors)))))

(def book (om/factory Book))

(defui BookList
  Object
  (render [this]
    (let [books (om/props this)]
      (dom/ul nil (map book books)))))

(def book-list (om/factory BookList))

(defui App
  static om/IQuery
  (query [this]
    [{:app/user (om/get-query Header)}
     {:books (om/get-query Book)}])
  Object
  (render [this]
    (dom/div nil
      (header (:app/user (om/props this)))
      (book-list (:books (om/props this))))))

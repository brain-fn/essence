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
      (dom/div #js {:className "row" :id "header"}
        (dom/div #js {:className "col-xs-12"}
          (str "Welcome, " (or name "stranger") "! ")
          (if-not name
            (dom/a #js {:href "/"} "Log In")
            (dom/a #js {:href "/logout"} "Log Out")))))))

(def header (om/factory Header))


(defui Book
  static om/Ident
  (ident [this props]
    [:books/by-name (:name props)])
  static om/IQuery
  (query [this]
    [:name :year :cover :authors :goodreads-link :impressions/count])
  Object
  (render [this]
          (let [{:keys [name authors year cover goodreads-link] :as props} (om/props this)]
            (dom/div #js {:className "row book"}
              (dom/div #js {:className "col-xs-1"}
                (dom/img #js {:src cover :width "100px"}))
              (dom/div #js {:className "col-xs-11"}
                (dom/div nil (dom/strong nil name))
                (dom/div nil authors)
                (dom/div #js {:className "impressions"}
                  (str "Impressions: " (:impressions/count props)))
                (dom/div #js {:className "text-muted"} (str "published " year))
                (dom/div nil
                  (dom/a #js {:href goodreads-link} "Book on Goodreads")))))))

(def book (om/factory Book))

(defui BookList
  Object
  (render [this]
          (let [books (om/props this)]
            (dom/div nil (map book books)))))

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

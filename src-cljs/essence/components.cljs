(ns essence.components
  (:require [clojure.string :as string]
            [cljs.pprint :refer [pprint]]
            [bidi.bidi :refer [match-route path-for]]
            [pushy.core :as pushy]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(enable-console-print!)

(def routes
  ["/app/" {"" :books
            "books/" {"" :books
                      [:id "/"] :books/by-id}}])

(def route->url
  (partial path-for routes))

(defui Book
  static om/Ident
  (ident [this props]
    [:books/by-id (:_id props)])
  static om/IQuery
  (query [this]
    [:_id :name :year :cover :authors :goodreads-link :impressions/count])
  Object
  (render [this]
          (let [{:keys [name authors year cover goodreads-link _id] :as props} (om/props this)]
            (dom/div #js {:className "row book"}
              (dom/div #js {:className "col-xs-1"}
                (dom/img #js {:src cover :width "100px"}))
              (dom/div #js {:className "col-xs-11"}
                (dom/a #js {:href (route->url :books/by-id :id _id)}
                  (dom/div nil (dom/strong nil name)))
                (dom/div nil authors)
                (dom/div #js {:className "impressions"}
                  (str "Impressions: " (:impressions/count props)))
                (dom/div #js {:className "text-muted"} (str "published " year))
                (dom/div nil
                  (dom/a #js {:href goodreads-link} "Book on Goodreads")))))))

(def book (om/factory Book))

(defui BookList
  static om/IQuery
  (query [this]
         [{:books (om/get-query Book)}])
  Object
  (render [this]
          (let [books (-> this om/props :books)]
            (dom/div nil (map book books)))))

(defui Impression
  static om/Ident
  (ident [this props]
    [:impressions/by-id (:_id props)])
  static om/IQuery
  (query [this]
    [:_id :opinion :rating])
  Object
  (render [this]
    (let [{:keys [:_id :opinion :rating]} (om/props this)]
      (dom/p nil (str rating "   " opinion)))))

(def impression (om/factory Impression))

(defui FullBook
  static om/Ident
  (ident [this props]
    [:books/by-id (:_id (-> props vals ffirst))])
  static om/IQuery
  (query [this]
    `[{[:books/by-id ?id]
       ~[:_id :name :year :cover :authors :goodreads-link
         {:impressions (om/get-query Impression)}]}])
  Object
  (render [this]
    (let [{:keys [name impressions]} (-> this om/props vals first)]
      (dom/div nil
        (dom/strong nil name)
        (map impression impressions)))))

(def book-list-q (om/get-query BookList))

(def route->query
  {:index book-list-q
   :books book-list-q
   :books/by-id (om/get-query FullBook)})

(def book-list (om/factory BookList))

(def route->factory
  {:index book-list
   :books book-list
   :books/by-id (om/factory FullBook)})

(defui App
  static om/IQueryParams
  (params [this] {})
  static om/IQuery
  (query [this]
    [:route
     {:app/user [:name]}
     {:subquery (om/get-query BookList)}])
  Object
  (render [this]
    (let [route (-> this om/props :route :handler)
          factory (route->factory route)]
      (dom/div nil
        #_(dom/div nil (str (-> this om/props :route)))
        (factory (-> this om/props :subquery))))))

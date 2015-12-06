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
                      [:id "/"] :books/by-id}
            ["book-insights/" :id "/"] :book-insights/by-id}])

(def route->url
  (partial path-for routes))

(defui Book
  static om/Ident
  (ident [this props]
    [:books/by-id (:_id props)])
  static om/IQuery
  (query [this]
    [:_id :name :year :cover :authors :goodreads-link :impressions])
  Object
  (render [this]
          (let [{:keys [name authors year cover goodreads-link _id impressions] :as props} ( om/props this)]
            (dom/div #js {:className "row book" }
                     (dom/div #js {:className "col-xs-1 col-xs-offset-4" }
                              (dom/img #js {:src cover :width "80px" }))
                     (dom/div #js {:className "col-xs-4" }
                              (dom/a #js {:href ( route->url :book-insights/by-id :id _id)}
                                     (dom/div nil ( dom/strong nil name)
                                              (dom/span #js {:className "text-muted" } ( str " (" year ")"))))
                              (dom/div #js {:className "small" } ( str "by " authors))
                              (dom/div #js {:className "pull-right" }
                                       (dom/a #js {:href goodreads-link} "Book on Goodreads" ))
                              (dom/div #js {:className "impressions" }
                                       (str "Impressions: " impressions)))))))

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

(defui Comparable
  static om/Ident
  (ident [this props]
    [:comparable/by-id (:_id props)])
  static om/IQuery
  (query [this]
    [:_id :idea-note])
  Object
  (render [this]
    (let [{:keys [idea-note]} (-> this om/props)]
      (dom/div nil idea-note))))

(def comparable (om/factory Comparable))

(defui Idea
  static om/Ident
  (ident [this props]
    [:ideas/by-id (:_id props)])
  static om/IQuery
  (query [this]
    [:_id :idea-note])
  Object
  (render [this]
    (let [{:keys [idea-note]} (-> this om/props)]
      (dom/div nil idea-note))))

(def idea (om/factory Idea))

(defui FullBookInsights
  static om/Ident
  (ident [this props]
    [:book-insights/by-id (:_id (-> props vals ffirst))])
  static om/IQuery
  (query [this]
    `[{[:book-insights/by-id ?id]
       ~[:_id :name :year :cover :authors :goodreads-link
         {:ideas (om/get-query Idea)}
         {:good-for (om/get-query Comparable)}
         {:bad-for (om/get-query Comparable)}]}])
  Object
  (render [this]
    (let [props (om/props this)
          book (-> props vals first)
          {:keys [name ideas good-for bad-for]} book]
      (dom/div nil
        (dom/strong nil name)
        (dom/div nil "Ideas")
        (map idea ideas)
        (dom/hr nil)
        (dom/div nil "Good For")
        (map comparable good-for)
        (dom/hr nil)
        (dom/div nil "Bad For")
        (map comparable bad-for)))))

(def book-list-q (om/get-query BookList))

(def route->query
  {:index book-list-q
   :books book-list-q
   :books/by-id (om/get-query FullBook)
   :book-insights/by-id (om/get-query FullBookInsights)})

(def book-list (om/factory BookList))

(def route->factory
  {:index book-list
   :books book-list
   :books/by-id (om/factory FullBook)
   :book-insights/by-id (om/factory FullBookInsights)})

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

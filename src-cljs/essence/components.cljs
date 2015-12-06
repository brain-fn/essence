(ns essence.components
  (:require [clojure.string :as string]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defui Book
  static om/Ident
  (ident [this props]
    [:books/by-id (:_id props)])
  static om/IQuery
  (query [this]
    [:_id :name :year :cover :authors :goodreads-link :impressions/count])
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
  static om/IQuery
  (query [this]
         [{:books (om/get-query Book)}])
  Object
  (render [this]
          (let [books (-> this om/props :books)]
            (dom/div nil (map book books)))))

(def route->query
  {:index (om/get-query BookList)})

(def route->factory
  {:index (om/factory BookList)})

(defui App
  static om/IQueryParams
  (params [this]
    {:subquery (:index route->query)})
  static om/IQuery
  (query [this]
    `[:route
      {:app/user [:name]}
      {:subquery ?subquery}])
  Object
  (render [this]
    (let [route (-> this om/props :route)
          factory (route->factory route)]
      (dom/div nil
        (factory (-> this om/props :subquery))))))

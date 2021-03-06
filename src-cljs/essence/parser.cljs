(ns essence.parser
  (:require [om.next :as om]
            [cljs.pprint :refer [pprint]]))

(defn by-ident [st i]
  (get-in st i))

(defmulti read om/dispatch)
(defmulti mutate om/dispatch)

(def parser
  (om/parser {:read read
              :mutate mutate}))

(defmethod read :route
  [{:keys [state]} k _]
  {:value (or (get @state k) {:handler :index})})

(defmethod read :subquery
  [{:keys [query ast] :as env} k params]
  {:value (parser env query params)
   :server true})

(defmethod read :app/user
  [{:keys [state ast] :as env} k _]
  (let [user (get @state k)]
    {:value user
     :server true}))

(defmethod read :books
  [{:keys [state ast target] :as env} k _]
  (let [st @state
        books (mapv #(get-in st %) (get (:subquery st) k))]
    {:value books
     :server true}))

(defn resolve-impressions [st impressions query]
  (let [impr-q (first (filter (fn [fragment] (and (map? fragment)
                                                  (= [:impressions] (keys fragment))))
                              query))
        impr-q (:impressions impr-q)]
    (into [] (map (fn [i] (select-keys
                           (by-ident st i)
                           impr-q))
                  impressions))))

(defn get-book-by-id [{:keys [state ast query] :as env} subqueries]
  (let [st @state
        book (-> (by-ident st (:key ast))
                 vals
                 ffirst
                 (select-keys (into [] (concat query subqueries))))]
    book))

(defmethod read :books/by-id
  [{:keys [state ast query] :as env} k params]
  (let [book (get-book-by-id env [:impressions])]
    {:value (assoc book :impressions (resolve-impressions @state (:impressions book) query))}))

(defmethod read :book-insights/by-id
  [{:keys [state ast query] :as env} k params]
  (let [book (get-book-by-id env [:ideas :good-for :bad-for])]
    {:value book}))

(defmethod mutate 'route/set
  [{:keys [state target]} _ params]
  (when (nil? target)
    {:value {:keys [:route]}
     :action (fn [] (swap! state assoc :route params))}))

(defmethod mutate 'impression/rate
  [{:keys [state target]} _ params]
  {:server true})

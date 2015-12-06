(ns essence.parser
  (:require [om.next.server :as om]
            [essence.db :as db]))

(defn dispatch [_ k _] k)

(defmulti readf dispatch)
(defmulti mutate dispatch)

(def parser
  (om/parser {:read readf
              :mutate mutate}))

(defmethod readf :default [_ k _]
  (println "Don't know how to read " k)
  {:value :unknown})

(defmethod readf :app/user [{:keys [state]} k _]
  {:value {:name (get @state k)}})

(defmethod readf :subquery
  [{:keys [query ast] :as env} k params]
  {:value (parser env query params)})

(defn has-subquery? [q k]
  (contains? (set q) k))

(defn subjoins [{:keys [parser query] :as env} params]
  (let [joins (filter map? query)]
    (mapv #(parser (merge env params) [%]) joins)))

(defn add-impression-count [book query]
  (if (has-subquery? query :impressions/count)
    (assoc book :impressions/count (rand-int 25))
    book))

(defn format-book [book {:keys [:query] :as env}]
  (let [sub (subjoins env {:book-id (:_id book)})
        book (-> book
                 (select-keys query)   ;; TODO: use mongo filter here 
                 (add-impression-count query))]
    (if (empty? sub)
      book
      (into [] (map #(merge book %) sub)))))

(defmethod readf :books [{:keys [state query] :as env} k _]
  {:value (mapv #(format-book % env) (db/list-books))})

(defmethod readf :books/by-id [{:keys [state ast query] :as env} k _]
  (let [id (nth (:key ast) 1)]
    {:value (format-book (db/get-book-data id) env)}))

(defmethod readf :impressions [{:keys [:query :book-id]} _ _]
  {:value (into [] (db/get-book-impressions book-id))})

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

(defn join-key [expr]
  (-> expr vec ffirst))

(defn join? [expr]
  (and (map? expr)
       (= 1 (count expr))
       (keyword? (join-key expr))))

(defn joins-to-keywords [query]
  (letfn [(change [expr]
            (if (join? expr)
              (join-key expr)
              expr))]
    (map change query)))

(defn format-book [book {:keys [:query] :as env}]
  (let [sub (subjoins env {:book book})
        book (-> book
                 (select-keys (joins-to-keywords query))   ;; TODO: use mongo filter here
                 )]
    (if (empty? sub)
      book
      (into [] (map #(merge book %) sub)))))

(defmethod readf :books [{:keys [state query] :as env} k _]
  {:value (mapv #(format-book % env) (db/list-books))})

(defmethod readf :books/by-id [{:keys [state ast query] :as env} k _]
  (let [id (nth (:key ast) 1)]
    {:value (format-book (db/get-book-data id) env)}))

(defmethod readf :impressions [{:keys [:query :book]} _ _]
  {:value (into [] (db/get-book-impressions (:_id book)))})

(defmethod readf :book-insights/by-id [{:keys [state ast query] :as env} k _]
  (let [id (nth (:key ast) 1)]
    {:value (format-book (db/get-book-insight id ) env)}))

(defmethod readf :ideas [{:keys [:book]} _ _]
  {:value (into [] (:ideas book))})

(defmethod readf :good-for [{:keys [:book]} _ _]
  {:value (into [] (:good-for book))})

(defmethod readf :bad-for [{:keys [:book]} _ _]
  {:value (into [] (:bad-for book))})

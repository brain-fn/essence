(ns essence.parser
  (:require [om.next :as om]))

(defmulti read om/dispatch)
(defmulti mutate om/dispatch)

(def parser
  (om/parser {:read read
              :mutate mutate}))

(defmethod read :route
  [{:keys [state]} k _]
  {:value (or (get @state k) :index)})

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

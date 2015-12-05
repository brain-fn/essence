(ns essence.parser
  (:require [om.next :as om]))

(defmulti read om/dispatch)
(defmulti mutate om/dispatch)

(def parser
  (om/parser {:read read
              :mutate mutate}))

(defmethod read :app/user
  [{:keys [state ast] :as env} k _]
  (let [user (get @state k)]
    {:value user
     :remote true
     :server ast}))

(defmethod read :books
  [{:keys [state ast] :as env} k _]
  (let [books (get @state k)]
    {:value books
     :remote true
     :server ast}))

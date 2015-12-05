(ns essence.parser
  (:require [om.next :as om]))

(defmulti read om/dispatch)
(defmulti mutate om/dispatch)

(def parser
  (om/parser {:read read
              :mutate mutate}))

(defmethod read :app/user
  [{:keys [state] :as env} k _]
  (let [st @state]
    {:value (get st k)}))

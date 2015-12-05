(ns essence.parser
  (:require [om.next.server :as om]
            [essence.db :as db]))

; TODO: move to db
(def app-state (atom {}))

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

(defmethod readf :books [{:keys [state query]} k _]
  {:value (map #(select-keys % query)  ;; TODO: use mongo filter here
               (db/list-books))})

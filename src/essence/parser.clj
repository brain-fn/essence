(ns essence.parser
  (:require [om.next.server :as om]))

; TODO: move to db
(def app-state (atom {}))

(defn dispatch [_ k _] k)

(defmulti readf dispatch)
(defmulti mutate dispatch)

(def parser
  (om/parser {:read readf
              :mutate mutate}))

(defmethod readf :app/user [{:keys [state]} k _]
  {:value (get @state k)})

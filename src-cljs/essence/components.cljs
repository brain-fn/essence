(ns essence.components
  (:require [clojure.string :as string]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defui App
  static om/IQuery
  (query [this]
    [:app/user])
  Object
  (render [this]
    (let [{:keys [:app/user]} (om/props this)]
      (dom/div nil (str "Hello, " user)))))

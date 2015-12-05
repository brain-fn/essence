(ns essence.components
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

(defui App
  static om/IQuery
  (query [this]
    [:app/hello])
  Object
  (render [this]
          (let [{:keys [:app/hello]} (om/props this)]
            (dom/div nil (or hello "Hi")))))

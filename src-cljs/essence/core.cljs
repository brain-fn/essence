(ns essence.core
  (:require [om.next :as om]
            [essence.parser :refer [parser]]
            [essence.components :refer [App]]))

(enable-console-print!)

(def reconciler
  (om/reconciler {:state {:app/hello "Hello, Om!"}
                  :parser parser
                  :remotes []}))

(om/add-root! reconciler
              App
              (.. js/document (getElementById "app")))

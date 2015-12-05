(ns essence.core
  (:require [clojure.string :as string]
            [om.next :as om]
            [essence.parser :refer [parser]]
            [essence.components :refer [App]]))

(enable-console-print!)

(def current-user
  (.. js/document (getElementById "current-user") -value))

(def reconciler
  (om/reconciler {:state {:app/user current-user}
                  :parser parser
                  :remotes []}))

(om/add-root! reconciler
              App
              (.. js/document (getElementById "app")))

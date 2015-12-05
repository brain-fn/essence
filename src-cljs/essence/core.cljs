(ns essence.core
  (:require [clojure.string :as string]
            [cljs.pprint :refer [pprint]]
            [om.next :as om]
            [taoensso.sente :as sente :refer (cb-success?)]
            [essence.parser :refer [parser]]
            [essence.components :refer [App]]))

(enable-console-print!)

(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/ws" {:type :auto})]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state)   ; Watchable, read-only atom
)

(defmulti ws-handler :id)

(def router (atom nil))

(defn stop-router! []
  (when-let [stop-f @router]
    (stop-f)))

(defn start-router! []
  (stop-router!)
  (reset! router
          (sente/start-chsk-router! ch-chsk ws-handler)))

(start-router!)

(defn send-to-server [{:keys [server] :as remotes} cb]
  (chsk-send! [:essence/query server]
              5000
              (fn [reply]
                (pprint reply)
                (cb reply))))

(def reconciler
  (om/reconciler {:state {:app/user "who are you?"}
                  :parser parser
                  :send send-to-server
                  :remotes [:server]}))

(defmethod ws-handler :default
  [{:keys [event] :as msg}]
  (pprint event))

(defmethod ws-handler :chsk/state [msg]
  ;; mount Om after ws connection is established
  (om/add-root! reconciler
                App
                (.. js/document (getElementById "app"))))


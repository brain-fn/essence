(ns essence.core
  (:require [clojure.string :as string]
            [cljs.pprint :refer [pprint]]
            [om.next :as om]
            [bidi.bidi :refer [match-route path-for]]
            [pushy.core :as pushy]
            [taoensso.sente :as sente :refer (cb-success?)]
            [essence.parser :refer [parser]]
            [essence.components :refer [App routes route->query]]))

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
  (om/reconciler {:state {}
                  :parser parser
                  :send send-to-server
                  :remotes [:server]}))

(defn route-dispatch! [match]
  (pprint (str "ROTE_DISPATCH: " match))
  (let [{:keys [handler route-params] :as route} match
        app (om/class->any reconciler App)]
    (when app
      (let [new-query (route->query handler)
            query (mapv (fn [q]
                          (if (and (map? q) (contains? (set (keys q)) :subquery))
                            (assoc q :subquery new-query)
                            q))
                        (om/get-query app))]
        (om/set-query! app {:query query :params route-params})))
    (om/transact! reconciler `[(route/set ~route)
                               :route])))

(def history
  (pushy/pushy route-dispatch! (partial match-route routes)))

(pushy/start! history)

(defmethod ws-handler :default
  [{:keys [event] :as msg}]
  (pprint event))

(defmethod ws-handler :chsk/state [msg]
  ;; mount Om after ws connection is established
  (om/add-root! reconciler
                App
                (.. js/document (getElementById "app")))
  ;; dispatch current path (for figwheel autoreload)
  (let [path (.-pathname (.-location js/document))]
    (route-dispatch! (match-route routes path))))

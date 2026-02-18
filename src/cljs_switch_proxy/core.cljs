(ns cljs-switch-proxy.core
  (:require [clojure.core.async :as a]
            [replicant.dom :as rd]
            [cljs-switch-proxy.ui :as ui]
            [cljs-switch-proxy.event :as event]))

(defn main []
  (let [ch (a/chan 1024)
        dispatch-fn (partial a/put! ch)
        db-atom (atom {})
        context {:dispatch-fn dispatch-fn :db-atom db-atom}
        dom (js/document.getElementById "app")]
    (add-watch db-atom ::render
               (fn [_ _ _ db]
                 (let [{:keys [current-proxy urls]} db]
                   (rd/render dom (ui/render-page context current-proxy urls)))))
    (a/go-loop []
      (when-let [event (a/<! ch)]
        (prn event)
        (event/handle-event context event)
        (recur)))
    (dispatch-fn {:event :refresh-current-proxy})
    (dispatch-fn {:event :refresh-proxy-list})))

(ns cljs-switch-proxy.event
  (:require [clojure.edn :as edn]))

(defmulti handle-event
  "Handle event."
  (fn [_context event] (:event event)))

(def system-proxy #js {"mode" "system"})

(defn url->server-proxy
  [url]
  (let [url (js/URL. url)
        scheme (let [scheme (aget url "protocol")]
                 (subs scheme 0 (dec (count scheme))))
        host (aget url "hostname")
        port (parse-long (aget url "port"))
        server #js {"scheme" scheme "host" host "port" port}]
    #js {"mode" "fixed_servers" "rules" #js {"singleProxy" server}}))

(defmethod handle-event :refresh-current-proxy [{:keys [db-atom]} _event]
  (let [settings (aget js/chrome "proxy" "settings")]
    (-> (.get settings #js {"incognito" false})
        (.then #(swap! db-atom assoc :current-proxy (aget % "value"))))))

(defmethod handle-event :refresh-proxy-list [{:keys [db-atom]} _event]
  (let [storage (aget js/chrome "storage" "sync")]
    (-> (.get storage "switch-proxy-urls")
        (.then #(when-let [urls (some-> % (aget "switch-proxy-urls") edn/read-string)]
                  (swap! db-atom assoc :urls urls))))))

(defmethod handle-event :switch-proxy [{:keys [dispatch-fn]} {:keys [proxy]}]
  (let [settings (aget js/chrome "proxy" "settings")
        value (case (:type proxy)
                :system system-proxy
                :server (url->server-proxy (:url proxy)))]
    (-> (.set settings #js {"value" value "scope" "regular"})
        (.then #(dispatch-fn {:event :refresh-current-proxy})))))

(defn set-storage-urls
  "Set storage urls."
  [urls]
  (let [storage (aget js/chrome "storage" "sync")
        data (doto #js {}
               (aset "switch-proxy-urls" (str urls)))]
    (.set storage data)))

(defmethod handle-event :delete-proxy [{:keys [db-atom]} {:keys [url]}]
  (let [{:keys [urls]} (swap! db-atom update :urls
                              (fn [urls]
                                (vec (remove (partial = url) urls))))]
    (set-storage-urls urls)))

(defmethod handle-event :add-proxy [{:keys [db-atom]} {:keys [url]}]
  (let [{:keys [urls]} (swap! db-atom update :urls (fnil conj []) url)]
    (set-storage-urls urls)))

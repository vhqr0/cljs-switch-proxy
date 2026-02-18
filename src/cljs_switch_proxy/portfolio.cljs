(ns cljs-switch-proxy.portfolio
  (:require [portfolio.replicant :refer-macros [defscene]]
            [portfolio.ui :as portfolio]
            [cljs-switch-proxy.ui :as ui]))

(defscene switch-button
  (ui/render-switch-button
   {:dispatch-fn (comp js/alert pr-str)} {:type :system}))

(defscene delete-button
  (ui/render-delete-button
   {:dispatch-fn (comp js/alert pr-str)} {:type :system}))

(defscene system-proxy
  (ui/render-proxy {:dispatch-fn (comp js/alert pr-str)} {:type :system}))

(defscene server-proxy
  (ui/render-proxy {:dispatch-fn (comp js/alert pr-str)} {:type :server :url "socks5://localhost:1080"}))

(defscene input-proxy
  (ui/render-proxy {:dispatch-fn (comp js/alert pr-str)} {:type :input}))

(defscene proxy-list
  (ui/render-proxy-list {:dispatch-fn (comp js/alert pr-str)} ["socks5://localhost:1080"]))

(defscene current-proxy
  (ui/render-current-proxy #js {"type" "socks5" "host" "localhost" "port" "1080"}))

(defscene page
  (ui/render-page
   {:dispatch-fn (comp js/alert pr-str)}
   #js {"type" "socks5" "host" "localhost" "port" "1080"}
   ["socks5://localhost:1080"]))

(defn main
  []
  (portfolio/start! {:config {:css-paths ["/css/main.css"]}}))

(ns cljs-switch-proxy.ui
  (:require [clojure.string :as str]))

(defmulti current-proxy->text
  "Convert current proxy settings to text."
  (fn [current-proxy] (aget current-proxy "mode")))

(defmethod current-proxy->text :default [current-proxy]
  (js/JSON.stringify current-proxy))

(defmethod current-proxy->text "system" [_current-proxy]
  "system")

(defmethod current-proxy->text "fixed_servers" [current-proxy]
  (if-let [server (aget current-proxy "rules" "singleProxy")]
    (let [scheme (aget server "scheme")
          host (aget server "host")
          port (aget server "port")]
      (str scheme "://" host ":" port))
    (js/JSON.stringify current-proxy)))

(defn render-current-proxy
  "Render current proxy."
  [current-proxy]
  [:div
   {:class ["border-4" "border-yellow-500" "bg-yellow-100" "text-yellow-500" "font-semibold" "test-sm" "p-4" "rounded-lg"]}
   (current-proxy->text current-proxy)])

(defn render-switch-button
  "Render switch button."
  [context proxy]
  (let [{:keys [dispatch-fn]} context]
    [:button
     {:class ["bg-blue-500" "hover:bg-blue-700" "text-white" "px-4" "py-2" "rounded" "transition"]
      :on {:click #(dispatch-fn {:event :switch-proxy :proxy proxy})}}
     "Switch"]))

(defn render-delete-button
  "Render delete b utton."
  [context proxy]
  (let [{:keys [dispatch-fn]} context
        {:keys [url]} proxy]
    [:button
     {:class ["bg-red-500" "hover:bg-red-700" "text-white" "px-4" "py-2" "rounded" "transition"]
      :on {:click #(dispatch-fn {:event :delete-proxy :url url})}}
     "Delete"]))

(defmulti render-proxy
  "Render proxy item."
  (fn [_context proxy] (:type proxy)))

(defmethod render-proxy :system [context proxy]
  [:div
   {:class ["border-l-4" "border-blue-500" "bg-blue-100" "p-4" "flex" "justify-between" "items-center" "rounded-lg"]}
   [:span
    {:class ["text-blue-500" "font-semibold"]}
    "system"]
   (render-switch-button context proxy)])

(defmethod render-proxy :server [context proxy]
  (let [{:keys [url]} proxy]
    [:div
     {:class ["border-l-4" "border-blue-500" "bg-blue-100" "p-4" "flex" "justify-between" "items-center" "rounded-lg"]}
     [:span
      {:class ["text-blue-500" "font-semibold"]}
      url]
     [:div
      {:class ["flex" "flex-wrap" "items-center" "gap-2"]}
      (render-delete-button context proxy)
      (render-switch-button context proxy)]]))

(defn render-input-proxy
  "Render input proxy."
  [context]
  (let [{:keys [dispatch-fn]} context]
    [:form
     {:class ["border-l-4" "border-green-500" "bg-green-100" "p-4" "flex" "justify-between" "items-center" "rounded-lg"]
      :on {:submit (fn [e]
                     (.preventDefault e)
                     (let [url (aget e "target" "url" "value")]
                       (dispatch-fn {:event :add-proxy :url url})))}}
     [:input
      {:name "url" :type "text" :placeholder "URL"
       :class ["bg-green-200" "text-green-700" "border" "border-green-300" "focus:border-green-400" "focus:outline-none" "rounded-lg" "px-4" "py-2"]}]
     [:button
      {:class ["bg-green-500" "hover:bg-green-700" "text-white" "px-4" "py-2" "rounded" "transition"]}
      "Add"]]))

(defn render-proxy-list
  "Render proxy list."
  [context current-proxy urls]
  [:ul
   {:class ["space-y-2"]}
   (when (some? current-proxy)
     [:li (render-current-proxy current-proxy)])
   [:li (render-proxy context {:type :system})]
   (->> urls
        (map
         (fn [url]
           [:li
            (render-proxy context {:type :server :url url})])))
   [:li (render-input-proxy context)]])

(defn render-app
  "Render app."
  [context db]
  (let [{:keys [current-proxy urls]} db]
    [:div
     {:class ["w-[400px]" "p-4"]}
     (render-proxy-list context current-proxy urls)]))

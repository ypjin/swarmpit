(ns swarmpit.component.node.list
  (:require [material.component :as comp]
            [material.icon :as icon]
            [swarmpit.component.mixin :as mixin]
            [swarmpit.component.state :as state]
            [swarmpit.component.handler :as handler]
            [swarmpit.routes :as routes]
            [clojure.string :as string]
            [sablono.core :refer-macros [html]]
            [rum.core :as rum]))

(enable-console-print!)

(def cursor [:page :node :list])

(defn- filter-items
  "Filter list items based on given predicate"
  [items predicate]
  (filter #(string/includes? (:nodeName %) predicate) items))

(defn- node-item-state [value]
  (case value
    "ready" (comp/label-green value)
    "down" (comp/label-red value)))

(defn- node-item-states [item]
  [:div.node-item-states
   [:span.node-item-state (node-item-state (:state item))]
   (if (:leader item)
     [:span.node-item-state (comp/label-blue "leader")])
   [:span.node-item-state (comp/label-blue (:role item))]])

(defn- node-item-header [item]
  [:div
   [:span
    [:svg.node-item-ico {:width  "24"
                         :height "24"
                         :fill   "rgb(117, 117, 117)"}
     [:path {:d icon/docker}]]]
   [:span [:b (:nodeName item)]]])

(defn- node-item
  [item]
  (html
    [:div.mdl-cell.node-item {:key (:id item)}
     (node-item-header item)
     (node-item-states item)
     [:div
      [:span.node-item-secondary "ip: " (:address item)]]
     [:div
      [:span.node-item-secondary "version: " (:engine item)]]
     [:div
      [:span.node-item-secondary "availability: " (:availability item)]]]))

(defn- data-handler
  []
  (handler/get
    (routes/path-for-backend :nodes)
    {:on-success (fn [response]
                   (state/update-value [:data] response cursor))}))

(defn- init-state
  [nodes]
  (state/set-value {:filter {:nodeName ""}
                    :data   nodes} cursor))

(def refresh-state-mixin
  (mixin/refresh data-handler))

(def init-state-mixin
  (mixin/init
    (fn [data]
      (init-state data))))

(rum/defc form < rum/reactive
                 init-state-mixin
                 refresh-state-mixin [_]
  (let [{:keys [filter data]} (state/react cursor)
        filtered-items (filter-items data (:nodeName filter))]
    [:div
     [:div.form-panel
      [:div.form-panel-left
       (comp/panel-text-field
         {:hintText "Filter by name"
          :onChange (fn [_ v]
                      (state/update-value [:filter :nodeName] v cursor))})]]
     [:div.content-grid.mdl-grid
      (->> (sort-by :nodeName filtered-items)
           (map #(node-item %)))]]))

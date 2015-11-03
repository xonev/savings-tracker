(ns savings-tracker.app
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [com.stuartsierra.component :as component]
            [savings-tracker.pages.goals :refer [goals-view]]
            [savings-tracker.persistence :refer [persist retrieve]]
            [savings-tracker.util.presentation :as p]))

(declare run)

(defrecord App [persister]
  component/Lifecycle

  (start [this]
    (-> this
      (assoc :persister persister)
      (run)))

  (stop [this]
    (assoc this :persister nil)))

(defn new-app []
  (map->App {}))

(defn run
  [{:keys [persister]}]
  (om/root
    (fn [app owner]
      (reify
        om/IRender
        (render [_]
          (dom/div nil
            (dom/div #js {:className "row"}
              (dom/div #js {:className "small-5 columns"}
                (dom/h1 nil "Savings Tracker"))
              (dom/div #js {:className "small-7 columns"}
                (dom/h2 #js {:className "right"} (str "Balance: " (p/currency (:balance app))))))
            (om/build goals-view (:goals app))))))
    (atom (or (retrieve persister)
              {:balance 0
               :goals []}))
    {:target (. js/document (getElementById "app"))
     :tx-listen (fn [tx-data root-cursor]
                  (persist persister (om/value root-cursor)))}))


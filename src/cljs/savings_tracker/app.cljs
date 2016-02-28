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
      (merge this
             {:persister persister
              :state (atom (or (retrieve persister)
                               {:balance 0
                                :route :index
                                :goals []}))}))

  (stop [this]
    (merge this {:persister nil
                 :state nil})))

(defn new-app []
  (map->App {}))

(defn transact-state! [app & args]
  "This function should be called in the same manner as om/update! but with the
  app component as the first argument instead of a cursor"
  (apply om/transact! (om/ref-cursor (om/root-cursor (:state app))) args))

(defn update-state! [app & args]
  "This function should be called in the same manner as om/update! but with the
  app component as the first argument instead of a cursor"
  (apply om/update! (om/ref-cursor (om/root-cursor (:state app))) args))

(defn run
  [{:keys [persister state]} router]
  (om/root
    (fn [app owner]
      (reify
        om/IRender
        (render [_]
          (case (:route app)
            :index (dom/div nil
                     (dom/div #js {:className "row"}
                       (dom/div #js {:className "small-5 columns"}
                         (dom/h1 nil "Savings Tracker"))
                       (dom/div #js {:className "small-7 columns"}
                         (dom/h2 #js {:className "right"} (str "Balance: " (p/currency (:balance app))))))
                     (om/build goals-view (:goals app)))
            :login (dom/div nil "Login!")
            :register (dom/div nil "Register!")
            (dom/div nil "404!")))))
    state
    {:target (. js/document (getElementById "app"))
     :shared {:router router}
     :tx-listen (fn [tx-data root-cursor]
                  (persist persister (om/value root-cursor)))}))


(ns savings-tracker.router
  (:require [com.stuartsierra.component :as component]
            [savings-tracker.routes :refer [routes]]))

(defrecord Router [db]
  component/Lifecycle

  (start [this]
    (assoc this :db db
                :routes (routes db)))

  (stop [this]
    (dissoc this :db :routes)))

(defn new-router []
  (map->Router {}))

(ns savings-tracker.core
  (:require [com.stuartsierra.component :as component]
            [savings-tracker.local-storage :refer [new-store]]
            [savings-tracker.persistence :refer [new-persister]]
            [savings-tracker.app :refer [new-app run]]))

(defn app []
  (component/system-map
    :store (new-store)
    :persister (component/using
                 (new-persister "savings-tracker")
                 [:store])
    :app (component/using
           (new-app)
           [:persister])))

(defn main []
  (component/start (app)))

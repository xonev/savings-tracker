(ns savings-tracker.core
  (:require [com.stuartsierra.component :as component]
            [savings-tracker.persistence :refer [new-persister]]
            [savings-tracker.app :refer [new-app run]]))

(defn app []
  (component/system-map
    :persister (new-persister "savings-tracker")
    :app (component/using
           (new-app)
           [:persister])))

(defn main []
  (let [system (component/start (app))]
    (run system)))

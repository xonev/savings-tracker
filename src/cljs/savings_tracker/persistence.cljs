  (ns savings-tracker.persistence
    (:require [cognitect.transit :as t]
              [com.stuartsierra.component :as component]
              [savings-tracker.local-storage :refer [set-item! get-item remove-item!]]))

(defrecord LocalPersister [storage-key store]
  component/Lifecycle

  (start [this]
    (-> this
      (assoc :json-writer (t/writer :json-verbose))
      (assoc :store store)
      (assoc :key storage-key)))

  (stop [this]
    (-> this
      (assoc :json-writer nil)
      (assoc :store nil)
      (assoc :key nil))))

(defn new-persister
  [storage-key]
  (map->LocalPersister {:storage-key storage-key}))

(defn persist
  [{:keys [json-writer store storage-key]} data]
  (let [serialized (t/write json-writer data)]
    (set-item! store storage-key serialized)))

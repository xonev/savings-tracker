  (ns savings-tracker.persistence
    (:require [cognitect.transit :as t]
              [com.stuartsierra.component :as component]))

(defrecord LocalPersister [storage-key store]
  component/Lifecycle

  (start [this]
    (assoc this :json-writer (t/writer :json-verbose)))

  (stop [this]
    (assoc this :json-writer nil)))

(defn new-persister
  [storage-key]
  (map->LocalPersister {:storage-key storage-key
                        :store nil}))

(defn persist
  [persister data]
  (.log js/console (t/write (:json-writer persister) data)))

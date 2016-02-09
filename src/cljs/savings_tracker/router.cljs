(ns savings-tracker.router
  (:require [com.stuartsierra.component :as component]
            [secretary.core :as secretary :refer-macros [defroute]]
            [clojure.string :as string])
  (:import [goog.history Html5History EventType]
           [goog.events]))

(declare start-history)
(declare stop-history)

(deftype TokenTransformer []
  Object
  (createUrl [this token pathPrefix location]
    (str (aget location "origin") pathPrefix token (aget location "search") (aget location "hash")))
  (retrieveToken [this pathPrefix location]
    (string/replace-first (aget location "pathname") pathPrefix "")))

(defrecord Router [app]
  component/Lifecycle

  (start [this]
    (assoc this :history (start-history)))

  (stop [this]
    (stop-history this)
    (assoc this :history nil)))

(defn new-router []
  (map->Router {}))

(defn start-history []
  (doto (Html5History. js/window (TokenTransformer.))
    (.setUseFragment false)
    (goog.events/listen EventType.NAVIGATE #(js/console.log %))
    (.setEnabled true)))

(defn stop-history
  [{:keys [history]}]
  (doto history
    (.setEnabled false)
    (.removeAllListeners)))

(defn navigate!
  [{:keys [history]} token]
  (.setToken history token))

(ns savings-tracker.router
  (:require [com.stuartsierra.component :as component]
            [clojure.string :as string]
            [savings-tracker.app :as application]
            [savings-tracker.routes :as routes])
  (:import [goog.history Html5History EventType]
           [goog.events]))

(declare new-history)
(declare start-history)
(declare stop-history)
(declare run-app)
(declare dispatch-route!)

(deftype TokenTransformer []
  Object
  (createUrl [this token pathPrefix location]
    (str (aget location "origin") pathPrefix token (aget location "search") (aget location "hash")))
  (retrieveToken [this pathPrefix location]
    (string/replace-first (aget location "pathname") pathPrefix "")))

(defrecord Router [app]
  component/Lifecycle

  (start [this]
    (let [this (as-> this t
                   (merge t
                          {:history (new-history)
                           :app app})
                   (assoc t :history (start-history t)))]
      (run-app this app)
      this))

  (stop [this]
    (stop-history this)
    (merge this {:history nil
                 :app nil})))

(defn new-router []
  (map->Router {}))

(defn new-history []
  (doto (Html5History. js/window (TokenTransformer.))
      (.setUseFragment false)))

(defn start-history [router]
  (doto (:history router)
    (goog.events/listen
      EventType.NAVIGATE
      #(dispatch-route! router (aget % "token")))
    (.setEnabled true)))

(defn stop-history
  [{:keys [history]}]
  (doto history
    (.setEnabled false)
    (.removeAllListeners)))

(defn navigate!
  [{:keys [history]} token]
  (.setToken history token))

(defn dispatch-route!
  [router url]
  (if-let [matched-route (routes/match-route url)]
    (application/update-state! (:app router) [:route] (:handler matched-route))
    (application/update-state! (:app router) [:route] :404)))

(defn run-app
  [router app]
  (application/run app router))

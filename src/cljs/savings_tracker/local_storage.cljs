(ns savings-tracker.local-storage
  (:require [com.stuartsierra.component :as component]))

(defrecord LocalStorage []
  component/Lifecycle

  (start [this]
    (let [localStorage (aget js/window "localStorage")]
      (merge this {:set-item! #(.setItem localStorage %1 %2)
                   :get-item #(.getItem localStorage %1)
                   :remove-item! #(.removeItem localStorage %1)})))
  (stop [this]
    (merge this {:set-item! nil
                 :get-item nil
                 :remove-item! nil})))

(defn new-store []
  (map->LocalStorage {}))

(defn set-item!
  [{:keys [set-item!]} key val]
  (set-item! key val))

(defn get-item
  [{:keys [get-item]} key]
  (get-item key))

(defn remove-item!
  [{:keys [remove!]} key]
  (remove-item! key))


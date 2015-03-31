(ns savings-tracker.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(def app-state (atom {}))

(defn main []
  (om/root
    (fn [app owner]
      (reify
        om/IRender
        (render [_]
          (dom/div nil
            (dom/h1 nil "Savings Tracker")
            (dom/h2 nil "Balances")))))
    app-state
    {:target (. js/document (getElementById "app"))}))

(ns savings-tracker.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(def app-state (atom {:balance 1000
                      :goals [{:name "Vacation"
                               :start "2015-01-03T00:00:00Z"
                               :end "2015-04-03T00:00:00Z"
                               :amount 1450.50
                               :saved 1200}
                              {:name "Tires"
                               :start "2015-01-03T00:00:00Z"
                               :end "2015-04-03T00:00:00Z"
                               :amount 500
                               :saved 259.45}]}))

(defn goal-view
  [goal owner]
  (reify
    om/IRender
    (render [_]
      (dom/li nil (str (:name goal) " " (:saved goal) "/" (:amount goal) " " (:end goal))))))

(defn goal-views
  [goals owner]
  (reify
    om/IRender
    (render [_]
      (apply dom/ul nil (om/build-all goal-view goals)))))

(defn main []
  (om/root
    (fn [app owner]
      (reify
        om/IRender
        (render [_]
          (dom/div nil
            (dom/h1 nil "Savings Tracker")
            (dom/h2 nil (str "Balance: " (:balance app)))
            (om/build goal-views (:goals app))))))
    app-state
    {:target (. js/document (getElementById "app"))}))

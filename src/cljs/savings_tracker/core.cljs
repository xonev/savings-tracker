(ns savings-tracker.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! <! chan]]))

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

(defn add-goal
  [goals]
  (om/transact! goals #(conj % {:name "Cheesey Poofs" :start "2015-04-01T00:00:00Z" :end "2015-04-30T00:00:00Z" :amount 1000 :saved 0})))

(defn goal-view
  [goal owner]
  (reify
    om/IRender
    (render [_]
      (dom/li nil (str (:name goal) " " (:saved goal) "/" (:amount goal) " " (:end goal))))))

(defn input-view
  [goal owner]
  (reify
    om/IRenderState
    (render-state [owner {:keys [label]}]
      (dom/label nil
        label
        (dom/input #js {:type "text"})))))

(defn goal-edit-view
  [goal owner]
  (reify
    om/IRender
    (render [_]
      (apply dom/div #js {:className "row"}
        (for [label ["Name" "Start" "End" "Amount"]]
          (om/build input-view goal {:init-state {:label label}}))))))

(defn goals-view
  [goals owner]
  (reify
    om/IRender
    (render [_]
      (apply dom/ul nil (dom/li nil
                          (om/build goal-edit-view {})
                          (dom/a #js {:onClick #(add-goal goals) :className "button tiny"} "Add Goal"))
                        (om/build-all goal-view goals)))))

(defn main []
  (om/root
    (fn [app owner]
      (reify
        om/IRender
        (render [_]
          (dom/div nil
            (dom/h1 nil "Savings Tracker")
            (dom/h2 nil (str "Balance: " (:balance app)))
            (om/build goals-view (:goals app))))))
    app-state
    {:target (. js/document (getElementById "app"))}))

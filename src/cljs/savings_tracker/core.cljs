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
      (dom/div #js {:className "row"}
        (dom/div #js {:className "small-3 columns"} (:name goal))
        (dom/div #js {:className "small-3 columns"} (:saved goal))
        (dom/div #js {:className "small-3 columns"} (:amount goal))
        (dom/div #js {:className "small-3 columns"} (:end goal))))))

(defn input-view
  [goal owner]
  (reify
    om/IRenderState
    (render-state [owner {:keys [label]}]
      (dom/div #js {:className "large-6 columns"}
        (dom/label #js {:className "row collapse"}
          (dom/div #js {:className "small-3 columns"}
            (dom/span #js {:className "prefix"} label))
          (dom/div #js {:className "small-9 columns"}
            (dom/span nil (dom/input #js {:type "text"}))))))))

(defn goal-edit-view
  [goal owner]
  (reify
    om/IRender
    (render [_]
      (apply dom/form #js {:className "row"}
        (concat
          (for [label ["Name" "Amount" "Start" "End"]]
            (om/build input-view goal {:init-state {:label label}}))
          [(dom/div #js {:className "columns"}
             (dom/a #js {:className "button tiny"} "Create Goal"))])))))

(defn goals-view
  [goals owner]
  (reify
    om/IRender
    (render [_]
      (apply dom/div nil
        (om/build-all goal-view goals)))))

(defn main []
  (om/root
    (fn [app owner]
      (reify
        om/IRender
        (render [_]
          (dom/div nil
            (dom/div #js {:className "row"}
              (dom/div #js {:className "small-5 columns"}
                (dom/h1 nil "Savings Tracker"))
              (dom/div #js {:className "small-7 columns"}
                (dom/h2 #js {:className "right"} (str "Balance: " (:balance app)))))
            (om/build goal-edit-view {})
            (om/build goals-view (:goals app))))))
    app-state
    {:target (. js/document (getElementById "app"))}))

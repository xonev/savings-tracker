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
  [goals goal]
  (om/transact! goals #(conj % goal)))

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
    (render-state [owner {:keys [label field chan]}]
      (dom/div #js {:className "large-6 columns"}
        (dom/label #js {:className "row collapse"}
          (dom/div #js {:className "small-3 columns"}
            (dom/span #js {:className "prefix"} label))
          (dom/div #js {:className "small-9 columns"}
            (dom/span nil (dom/input #js {:onChange #(put! chan {:field field
                                                                 :value (.. % -target -value)})
                                          :type "text"
                                          :value (goal field)}))))))))

(defn goal-edit-view
  [goal owner]
  (reify
    om/IInitState
    (init-state [_]
      {:changing-goal goal
       :goal-changes (chan)})
    om/IWillMount
    (will-mount [_]
      (let [goal-changes (om/get-state owner :goal-changes)]
        (go-loop []
          (let [{:keys [field value]} (<! goal-changes)
                changing-goal (om/get-state owner :changing-goal)]
            (om/set-state! owner :changing-goal (assoc changing-goal field value))
            (recur)))))
    om/IRenderState
    (render-state [_ {:keys [chan goal-changes changing-goal]}]
      (apply dom/form #js {:className "row"}
        (concat
          (for [label ["Name" "Amount" "Start" "End"]]
            (om/build input-view changing-goal {:init-state {:label label
                                                    :field (keyword (.toLowerCase label))
                                                    :chan goal-changes}}))
          [(dom/div #js {:className "columns"}
             (dom/a #js {:onClick #(put! chan {:event :add-goal
                                               :goal changing-goal})
                         :className "button tiny"} "Create Goal"))])))))

(defn goals-view
  [goals owner]
  (reify
    om/IInitState
    (init-state [_]
      {:goals-updates (chan)})
    om/IWillMount
    (will-mount [_]
      (let [goals-updates (om/get-state owner :goals-updates)]
        (go-loop []
          (let [{:keys [event goal]} (<! goals-updates)]
            (add-goal goals goal)
            (recur)))))
    om/IRenderState
    (render-state [_ {:keys [goals-updates]}]
      (apply dom/div nil
        (om/build goal-edit-view {} {:init-state {:chan goals-updates}})
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
            (om/build goals-view (:goals app))))))
    app-state
    {:target (. js/document (getElementById "app"))}))

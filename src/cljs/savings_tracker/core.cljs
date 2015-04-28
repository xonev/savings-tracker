(ns savings-tracker.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! <! chan]]
            [cljs-time.core :as t]
            [cljs-time.format :as f]))

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

(def rfc-formatter (f/formatters :date-time-no-ms))
(def display-formatter (f/formatter "MMM d, yyyy"))

(defn add-goal
  [goals goal]
  (om/transact! goals #(conj % goal)))

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
    (render-state [_ {:keys [chan goal-changes changing-goal is-editing?]}]
      (apply dom/form #js {:className "row"}
        (concat
          (for [label ["Name" "Amount" "Start" "End"]]
            (om/build input-view changing-goal {:init-state
                                                {:label label
                                                 :field (keyword (.toLowerCase label))
                                                 :chan goal-changes}}))
          [(dom/div #js {:className "columns"}
             (dom/a #js {:onClick #(put! chan {:event :add-goal
                                               :goal changing-goal})
                         :className "button tiny"}
                    (if is-editing? "Update Goal" "Create Goal")))])))))

(defn currency
  [number]
  (.format (js/numeral number) "$0,0[.]00"))

(defn goal-view
  [goal owner]
  (reify
    om/IInitState
    (init-state [_]
      {:is-editing? false
       :goal-updates (chan)})
    om/IWillMount
    (will-mount [_]
      (let [goal-updates (om/get-state owner :goal-updates)]
        (go-loop []
          (let [update-event (<! goal-updates)]
            (om/update! goal (:goal update-event))
            (om/set-state! owner :is-editing? false)
            (recur)))))
    om/IRenderState
    (render-state [_ {:keys [is-editing? goal-updates]}]
      (if is-editing?
        (om/build goal-edit-view goal {:init-state {:chan goal-updates
                                                    :is-editing? true}})
        (dom/div #js {:className "row"}
          (dom/div #js {:className "small-3 columns"} (:name goal))
          (dom/div #js {:className "small-2 columns"} (currency (:saved goal)))
          (dom/div #js {:className "small-2 columns"} (currency (:amount goal)))
          (dom/div #js {:className "small-3 columns"} (f/unparse display-formatter (f/parse rfc-formatter (:end goal))))
          (dom/div #js {:className "small-2 columns"}
            (dom/a #js {:onClick #(om/set-state! owner :is-editing? true)}
              (dom/i #js {:className "fi-pencil"}))))))))

(defn goals-view
  [goals owner]
  (reify
    om/IInitState
    (init-state [_]
      {:goals-updates (chan)
       :adding-goal? false})
    om/IWillMount
    (will-mount [_]
      (let [goals-updates (om/get-state owner :goals-updates)]
        (go-loop []
          (let [{:keys [event goal]} (<! goals-updates)]
            (add-goal goals goal)
            (om/set-state! owner :adding-goal? false)
            (recur)))))
    om/IRenderState
    (render-state [_ {:keys [goals-updates adding-goal?]}]
      (apply dom/div nil
        (if adding-goal?
          (om/build goal-edit-view {} {:init-state {:chan goals-updates}})
          (dom/div #js {:className "row"}
            (dom/div #js {:className "columns"}
              (dom/a #js {:onClick #(om/set-state! owner :adding-goal? true)
                          :className "button tiny"}
                     "Add Goal"))))
        (dom/div #js {:className "row labels"}
          (dom/div #js {:className "small-3 columns"} "Goal")
          (dom/div #js {:className "small-2 columns"} "Amount Saved")
          (dom/div #js {:className "small-2 columns"} "Goal Amount")
          (dom/div #js {:className "small-3 columns"} "Save by When?")
          (dom/div #js {:className "small-2 columns"} "Actions"))
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
                (dom/h2 #js {:className "right"} (str "Balance: " (currency (:balance app))))))
            (om/build goals-view (:goals app))))))
    app-state
    {:target (. js/document (getElementById "app"))}))

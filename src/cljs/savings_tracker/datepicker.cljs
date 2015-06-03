(ns savings-tracker.datepicker
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! <! chan]]))

(defn datepicker-view
  [date owner]
  (reify
    om/IDidMount
    (did-mount [_]
      (let [input (om/get-node owner "date-field")]
        (.pickadate (js/$ input)
                    (clj->js {:selectYears true
                              :selectMonths true}))))
    om/IRenderState
    (render-state [_ {:keys [label field chan]}]
      (dom/input #js {:type "text"
                      :ref "date-field"
                      :value date}))))

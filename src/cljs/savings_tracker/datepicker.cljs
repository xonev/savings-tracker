(ns savings-tracker.datepicker
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! <! chan]]))

(defn datepicker-view
  [date owner]
  (reify
    om/IDidMount
    (did-mount [_]
      )
    om/IRenderState
    (render-state [owner {:keys [label field chan]}]
      (dom/input #js {:type "text"
                      :value date}))))


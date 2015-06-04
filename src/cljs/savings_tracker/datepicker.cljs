(ns savings-tracker.datepicker
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put!]]))

(defn- on-set
  [picker channel field context]
  (let [context (js->clj context :keywordize-keys true)]
    (when (:select context)
      (put! channel {:field field
                     :value (.get picker "select" "yyyy-mm-dd")})
      (.close picker true))))

(defn datepicker-view
  [{date :value :keys [container-id]} owner]
  (reify
    om/IDidMount
    (did-mount [_]
      (let [input (om/get-node owner "date-field")
            $input (.pickadate (js/$ input)
                               (clj->js {:container (str "#" container-id)
                                         :selectYears true
                                         :selectMonths true
                                         :format "mmm d, yyyy"
                                         :formatSubmit "yyyy-mm-dd"}))
            picker (.pickadate $input "picker")]
        (.on picker "set" (partial on-set picker (om/get-state owner :chan) (om/get-state owner :field)))))
    om/IRender
    (render [_]
      (dom/input #js {:type "text"
                      :ref "date-field"
                      :data-value date}))))

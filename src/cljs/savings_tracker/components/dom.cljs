(ns savings-tracker.components.dom
  (:require [om.dom :as dom :include-macros true]
            [savings-tracker.router :refer [navigate!]]))

(defn link [router route text]
  (dom/a #js {:href route
              :onClick (fn [e]
                         (.preventDefault e)
                         (navigate! router route))}
         text))

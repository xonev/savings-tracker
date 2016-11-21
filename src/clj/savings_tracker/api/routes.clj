(ns savings-tracker.api.routes
  (:require [savings-tracker.api.users :as users]))

(defn api-routes
  [db]
  {"/v1/" {"users" users/index}})

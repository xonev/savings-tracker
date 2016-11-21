(ns savings-tracker.api.users)

(defn index
  [request]
  (println "index route called")
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body {:test "abc123"}})

(defn create-user [user-data]
  )

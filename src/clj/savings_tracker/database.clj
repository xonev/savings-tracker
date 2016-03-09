(ns savings-tracker.database
  (:require [com.stuartsierra.component :as component])
  (:require [clojure.java.jdbc :as jdbc])
  (:import com.mchange.v2.c3p0.ComboPooledDataSource))

(declare pool)

(defrecord Database [driver-manager-spec]
  component/Lifecycle

  (start [this]
    (merge this {:conn (pool driver-manager-spec)
                 :spec driver-manager-spec}))

  (stop [this]
    (dissoc this :conn)))

(defn new-database
  [driver-manager-spec]
  "Creates a new database component using a DriverManager spec map. See here
  for more details:
  http://clojure.github.io/java.jdbc/#clojure.java.jdbc/get-connection"
  (map->Database {:driver-manager-spec driver-manager-spec}))

(defn execute!
  [{:keys [conn]} & args]
  (apply jdbc/execute! conn args))

(defn query
  [{:keys [conn]} & args]
  (apply jdbc/query conn args))

(defn insert!
  [{:keys [conn]} & args]
  (apply jdbc/insert! conn args))

(defn pool
  [spec]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname spec))
               (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
               (.setUser (:user spec))
               (.setPassword (:password spec))
               ;; expire excess connections after 30 minutes of inactivity:
               (.setMaxIdleTimeExcessConnections (* 30 60))
               ;; expire connections after 3 hours of inactivity:
               (.setMaxIdleTime (* 3 60 60)))]
    {:datasource cpds}))

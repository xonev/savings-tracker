(ns leiningen.migrate
  (:require [migratus.core :as migratus]))

(declare dispatch-task)

(def config
  {:store :database
   :migration-dir "migrations/"
   :migration-table-name "migrations"
   :db {:subprotocol "postgresql"
        :subname "//127.0.0.1:5432/clojure_test"
        :user "clojure_test"
        :password "clojure_test"}})

(defn migrate
  "Manipulate database migrations"
  [& args]
  (if (= (count args) 0)
    (migratus/migrate config)
    (apply dispatch-task args)))

(defn dispatch-task
  [task & args]
  (case task
    "rollback" (migratus/rollback config)
    "up" (apply migratus/up config args)
    "down" (apply migratus/down config args)
    "reset" (migratus/reset config)
    "create" (apply migratus/create config args)
    "destroy" (apply migratus/destroy config args)))

(ns savings-tracker.server
  (:require [com.stuartsierra.component :as component]
            [savings-tracker.dev :refer [is-dev? browser-repl start-figwheel]]
            [net.cgrand.reload :refer [auto-reload]]
            [ring.middleware.reload :as reload]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [savings-tracker.routes :refer [routes]]))

(declare run-web-server)
(declare run-auto-reload)

(defrecord Server [port]
  component/Lifecycle

  (start [this]
    (when is-dev?
      (run-auto-reload))
    (assoc this :jetty-server (run-web-server port)))

  (stop [this]
    (dissoc this :jetty-server)))

(defn new-server
  [port]
  (map->Server {:port port}))

(defn app-system [config]
  (let [{:keys [port]} config]
    (component/system-map
      :server (new-server port))))

(def http-handler
  (if is-dev?
    (reload/wrap-reload (wrap-defaults #'routes api-defaults))
    (wrap-defaults routes api-defaults)))

(defn run-web-server [& [port]]
    (print "Starting web server on port" port ".\n")
    (run-jetty http-handler {:port port :join? false}))

(defn run-auto-reload []
  (auto-reload *ns*)
  (start-figwheel))

(defn run [& [port]]
  (let [port (Integer. (or port (env :port) 10555))]
    (component/start (app-system {:port port}))))

(defn -main [& [port]]
  (run port))

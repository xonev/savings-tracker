(ns savings-tracker.server
  (:require [com.stuartsierra.component :as component]
            [savings-tracker.dev :refer [is-dev? browser-repl start-figwheel stop-figwheel]]
            [net.cgrand.reload :refer [auto-reload]]
            [ring.middleware.reload :as reload]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [savings-tracker.database :refer [new-database]]
            [savings-tracker.router :refer [new-router]]))

(declare run-web-server)
(declare run-auto-reload)

(defrecord Server [port router]
  component/Lifecycle

  (start [this]
    (let [this (assoc this
                      :jetty-server (run-web-server (:routes router) port)
                      :router router)]
      (if is-dev?
        (assoc this :figwheel (run-auto-reload))
        this)))

  (stop [this]
    (.stop (:jetty-server this))
    (let [this (dissoc this :jetty-server :router)]
      (if is-dev?
        (do
          (stop-figwheel (:figwheel this))
          (dissoc this :figwheel))
        this))))

(defn new-server
  [port]
  (map->Server {:port port}))

(defn app-system [config]
  (let [{:keys [port]} config]
    (component/system-map
      :db (new-database {:subprotocol "postgresql"
                         :subname "//127.0.0.1:5432/savings_tracker"
                         :user "savings_tracker"
                         :password "savings_tracker"})
      :router (component/using (new-router) [:db])
      :server (component/using (new-server port) [:router]))))

(defn http-handler
  [routes]
  (let [handler (-> routes
                    (wrap-defaults api-defaults)
                    wrap-json-response
                    wrap-json-body)]
    (if is-dev?
      (reload/wrap-reload handler)
      handler)))

(defn run-web-server [routes & [port]]
    (print "Starting web server on port" port ".\n")
    (run-jetty (http-handler routes) {:port port :join? false}))

(defn run-auto-reload []
  (auto-reload *ns*)
  (start-figwheel))

(defn run [& [port]]
  (let [port (Integer. (or port (env :port) 10555))]
    (component/start (app-system {:port port}))))

(defn stop [system]
  (component/stop system))

(defn reset [system]
  (stop system)
  (run))

(defn -main [& [port]]
  (run port))

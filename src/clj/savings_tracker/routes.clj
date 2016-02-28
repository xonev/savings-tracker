(ns savings-tracker.routes
  (:require [clojure.java.io :as io]
            [savings-tracker.dev :refer [is-dev? inject-devmode-html]]
            [net.cgrand.enlive-html :refer [deftemplate]]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources]]))

(deftemplate page (io/resource "index.html") []
  [:body] (if is-dev? inject-devmode-html identity))

(defroutes routes
  (resources "/")
  (resources "/react" {:root "react"})
  (GET "/*" req (page)))

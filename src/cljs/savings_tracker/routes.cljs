(ns savings-tracker.routes
  (:require [bidi.bidi :as bidi]))

(def routes ["" {"" :index
                 "login" :login}])

(defn match-route
  [url]
  (bidi/match-route routes url))

(defn path-for
  [route]
  (bidi/path-for routes route))


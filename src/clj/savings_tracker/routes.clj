(ns savings-tracker.routes
  (:require [clojure.java.io :as io]
            [savings-tracker.dev :refer [is-dev? inject-devmode-html]]
            [net.cgrand.enlive-html :refer [deftemplate]]
            [bidi.ring :refer [make-handler ->Resources]]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources]]))

(deftemplate page (io/resource "index.html") []
  [:body] (if is-dev? inject-devmode-html identity))

(def routes
  (make-handler
    ["/" [["react" (->Resources {:prefix "react"})]
          ["js" (->Resources {:prefix "public/js"})]
          ["css" (->Resources {:prefix "public/css"})]
          ["vendor" (->Resources {:prefix "public/vendor"})]
          [true (fn [request] {:status 200
                               :headers {"Content-Type" "text/html"}
                               :body (apply str (page))})]]]))

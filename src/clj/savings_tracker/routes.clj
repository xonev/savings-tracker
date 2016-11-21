(ns savings-tracker.routes
  (:require [clojure.java.io :as io]
            [savings-tracker.dev :refer [is-dev? inject-devmode-html]]
            [net.cgrand.enlive-html :refer [deftemplate]]
            [bidi.ring :refer [make-handler ->Resources]]
            [savings-tracker.api.routes :refer [api-routes]]))

(deftemplate page (io/resource "index.html") []
  [:body] (if is-dev? inject-devmode-html identity))

(defn routes
  [db]
  (make-handler
    ["/" [["react" (->Resources {:prefix "react"})]
          ["js" (->Resources {:prefix "public/js"})]
          ["css" (->Resources {:prefix "public/css"})]
          ["vendor" (->Resources {:prefix "public/vendor"})]
          ["api" (api-routes db)]
          [true (fn [request] {:status 200
                               :headers {"Content-Type" "text/html"}
                               :body (apply str (page))})]]]))


(ns user
  (:require [lacinia-example.core :refer [start stop]]))

(def ^:dynamic server)

(defn go []
  (alter-var-root #'server (fn [_] (start {:port 8080 :join? false}))))

(defn reset []
  (stop server)
  (go))

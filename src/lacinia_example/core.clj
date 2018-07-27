(ns lacinia-example.core
  (:require [com.walmartlabs.lacinia :refer [execute]]
            [com.walmartlabs.lacinia.schema :as s]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [cheshire.core :as j]
            [ring.adapter.jetty :refer [run-jetty]]))

(def example '{:objects {:Product
                         {:fields {:id {:type Int}
                                   :name {:type String}}}}

               :queries {:product {:type :Product
                                   :args {:id {:type Int
                                               :default-value 1}}
                                   :resolve :resolve-product}}})

(defn find-product [context args _value]
  {:id (:id args) :name "test"})

(def schema
  (s/compile (attach-resolvers example {:resolve-product find-product})))

(defn q [query]
  (execute schema query {} {}))

(q "{ product(id:1) { name id }}")

(q "{ product(id:1000) { id }}")

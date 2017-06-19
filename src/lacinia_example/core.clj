(ns lacinia-example.core
  (:require [com.walmartlabs.lacinia :refer [execute]]
            [com.walmartlabs.lacinia.schema :as s]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [clojure.data.json :as json]
            [cheshire.core :as j]))

(def schema '{:objects {:Product
                        {:fields {:id {:type Int}
                                  :name {:type String}}}}

              :queries {:product {:type :Product
                                  :args {:id {:type Int
                                              :default-value 1}}
                                  :resolve :resolve-product}}})

(defn find-product [context args _value]
  {:id (:id args) :name "test"})

(defn -main [& args]
  (let [query (str "{product(id: " (first args) "){name}}")
        result (execute (s/compile (attach-resolvers schema {:resolve-product find-product})) query {} {})]
    (println (j/generate-string result))))

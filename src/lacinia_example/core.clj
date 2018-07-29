(ns lacinia-example.core
  (:require [com.walmartlabs.lacinia :refer [execute]]
            [com.walmartlabs.lacinia.schema :as s]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers attach-scalar-transformers]]
            [cheshire.core :as j]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.adapter.jetty :refer [run-jetty]]))

(def example '{:scalars {:DateTime
                         {:parse :date-transformers
                          :serialize :date-transformers}}
               :objects {:Product
                         {:fields {:id {:type Int}
                                   :name {:type String}
                                   :createdAt {:type :DateTime}}}}

               :queries {:product {:type :Product
                                   :args {:id {:type Int
                                               :default-value 1}}
                                   :resolve :resolve-product}}})

(defn find-product [context args _value]
  {:id (:id args) :name "test" :createdAt "20180728010100"})

(def schema
  (-> example
      (attach-resolvers {:resolve-product find-product})
      (attach-scalar-transformers {:date-transformers (s/as-conformer #(re-matches #"[0-9]{14}" %))})
      s/compile))

(defn q [query]
  (execute schema query {} {}))

(defn handler [{:keys [body]}]
  {:status 200 :headers {} :body (q (:query body))})

(defn -main [& args]
  (run-jetty (-> handler
                 (wrap-json-body {:keywords? true :bigdecimals? true})
                 wrap-json-response)
             {:port 8080}))

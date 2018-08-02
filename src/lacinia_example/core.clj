(ns lacinia-example.core
  (:require [com.walmartlabs.lacinia :refer [execute]]
            [com.walmartlabs.lacinia.schema :as s]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers attach-scalar-transformers]]
            [cheshire.core :as j]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring-graphql-ui.core :refer [wrap-graphiql]]
            [com.walmartlabs.lacinia.parser.schema :refer [parse-schema]]))

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

(def example-sdl "
  {
    scalar DateTime

    type Product {
      id: Int
      name: String
      createdAt: DateTime
    }

    type Query {
      product(id: Int): Product
    }

    schema {
      query: Query
    }
  }
  ")

(defn find-product [context args _value]
  {:id (:id args) :name "test" :createdAt "20180728010100"})

(def schema
  (-> example
      (attach-resolvers {:resolve-product find-product})
      (attach-scalar-transformers {:date-transformers (s/as-conformer #(re-matches #"[0-9]{14}" %))})
      s/compile))

(def schema
  (s/compile
   (parse-schema example-sdl
                 {:resolvers {:Query {:product find-product}}
                  :scalars {:DateTime {:parse (s/as-conformer #(re-matches #"[0-9]{14}" %))
                                       :serialize (s/as-conformer #(re-matches #"[0-9]{14}" %))}}})))

(defn handler [{{:keys [query variables operationName]} :body}]
  (let [result (execute schema query variables nil {:operation-name operationName})]
    {:status 200 :headers {} :body result}))

(defn start [options]
  (run-jetty (-> #'handler
                 (wrap-json-body {:keywords? true :bigdecimals? true})
                 (wrap-graphiql {:path "/graphiql"
                                 :endpoint "/graphql"})
                 wrap-json-response)
             options))

(defn stop [server]
  (.stop server))

(defn -main [& args]
  (start {:port 8080}))

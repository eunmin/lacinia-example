(defproject lacinia-example "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.walmartlabs/lacinia "0.28.0"]
                 [ring "1.7.0-RC1"]
                 [ring/ring-json "0.4.0"]
                 [cheshire "5.7.1"]
                 [threatgrid/ring-graphql-ui "0.1.1"]]
  :main lacinia-example.core)

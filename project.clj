(defproject us.2da/hugsql-adapter-clickhouse-native-jdbc "0.1.0"
  :description "ClickHouse native JDBC adapter for HugSQL."
  :url "https://github.com/2DA-Analytics/hugsql-adapter-clickhouse-native-jdbc"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [com.github.housepower/clickhouse-native-jdbc "1.6-stable"]
                 [com.layerware/hugsql-adapter "0.4.9"]]
  :profiles {:dev {:dependencies [[com.layerware/hugsql-core "0.4.9"]]}})

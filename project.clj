(defproject us.2da/hugsql-adapter-clickhouse-native-jdbc "0.1.2"
  :description "ClickHouse native JDBC adapter for HugSQL."
  :url "https://github.com/2DA-Analytics/hugsql-adapter-clickhouse-native-jdbc"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [com.github.housepower/clickhouse-native-jdbc "1.6-stable"]
                 [com.layerware/hugsql-adapter "0.4.9"]]
  :plugins [[jonase/eastwood "0.3.3"]
            [lein-ancient "0.6.15"]
            [lein-cloverage "1.0.13"]
            [lein-codox "0.10.6"]]
  :profiles {:dev {:dependencies [[com.layerware/hugsql-core "0.4.9"]]}})

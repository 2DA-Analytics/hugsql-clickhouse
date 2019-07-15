(task-options!
 pom {:project 'us.2da/hugsql-adapter-clickhouse-native-jdbc
      :description "Clickhouse hative JDBC adapter for HugSQL."
      :developers {"Anthony Shull" "anthony.shull@2da.us"}
      :license {"Apache License, Version 2.0" "http://www.apache.org/licenses/LICENSE-2.0.html"}
      :scm {:url "http://github.com/2DA-Analytics/hugsql-adapter-clickhouse-native-jdbc"}
      :url "http://github.com/2DA-Analytics/hugsql-adapter-clickhouse-native-jdbc"
      :version "0.1.6"}
 push {:repo "clojars"})

(set-env!
 :source-paths #{"src" "test"}
 :dependencies '[[org.clojure/clojure "1.10.1"]
                 [adzerk/boot-test "1.2.0"]
                 [com.github.housepower/clickhouse-native-jdbc "1.7-stable"]
                 [com.layerware/hugsql "0.4.9"]
                 [metosin/bat-test "0.4.3" :scope "test"]])

(require '[adzerk.boot-test :refer :all]
         '[metosin.bat-test :refer [bat-test]])

(deftask deploy
  "Build and deploy the project to Clojars."
  []
  (comp (pom)
        (jar)
        (install)
        (push :repo-map {:url "https://clojars.org/repo/"
                         :username (System/getenv "CLOJARS_USERNAME")
                         :password (System/getenv "CLOJARS_PASSWORD")
                         :update :always})))

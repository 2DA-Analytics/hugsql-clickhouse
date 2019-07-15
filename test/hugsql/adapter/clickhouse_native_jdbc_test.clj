(ns hugsql.adapter.clickhouse-native-jdbc-test
  (:require [clojure.test :refer :all]
            [hikari-cp.core :refer [close-datasource make-datasource]]
            [hugsql.core :as hugsql]
            [hugsql.adapter.clickhouse-native-jdbc :as clickhouse])
  (:import (java.sql Connection DriverManager)))

(hugsql/def-db-fns "./hugsql/adapter/fns.sql")
(hugsql/set-adapter! (clickhouse/hugsql-adapter-clickhouse-native-jdbc))

(def conn (.getConnection (make-datasource {:jdbc-url "jdbc:clickhouse://127.0.0.1:9000"})))

(def ocher {:id 1
            :name "ocher"
            :intensity "high"
            :synonyms ["yellow" "brown"]
            :cmyk [[1.0 0.0 0.0 0.0]]})

(def crimson {:id 2
              :name "crimson"
              :intensity "high"
              :synonyms ["maroon"]
              :cmyk [[0.1 0.2 0.3 0.4]]})

(defn database
  [tests]
  (create-test-database conn)
  (tests)
  (drop-test-database conn))

(use-fixtures :once database)

(deftest create-table-test
  (testing "Can create a table."
    (is (not (nil? (create-colors-table conn))))))

(deftest insert-row-test
  (testing "Can insert a row."
    (is (false? (insert-color conn {:ks (map name (keys ocher))
                                    :vs (vals ocher)}))))
  (testing "Can insert another row."
    (is (false? (insert-color conn {:ks (map name (keys crimson))
                                    :vs (vals crimson)}))))
  (testing "Can select a row."
    (is (= (:id (select-color-by-id conn {:id 1}))
           1)))
  (testing "Can select multiple rows."
    (is (= (count (select-all-colors conn))
           2))))

(deftest add-column-test
  (testing "Can add a column to an existing table."
    (is (not (nil? (add-column conn {:tbl :test.colors :col :hex :typ :String})))))
  (testing "Can insert a partial row."
    (is (false? (insert-color conn {:ks ["id" "name"]
                                    :vs [3 "ivory"]})))))

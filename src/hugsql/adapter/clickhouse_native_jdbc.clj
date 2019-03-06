(ns hugsql.adapter.clickhouse-native-jdbc
  "ClickHouse native JDBC adapter for HugSQL."
  (:require [hugsql.adapter :as adapter])
  (:import (java.sql Connection DriverManager ResultSet Statement))
  (:gen-class))

(defn get-column-info
  "Given `results` return a vector of keys."
  [func results]
  (let [metadata (.getMetaData results)
        column-count (.getColumnCount metadata)]
    (loop [columns (range 1 (inc column-count)) keys (transient [])]
      (if (not (empty? columns))
        (recur (rest columns) (conj! keys (func metadata (first columns))))
        (persistent! keys)))))

(def get-column-names
  (partial get-column-info (fn [metadata column]
                             (.getColumnName metadata column))))

(def get-column-type-names
  (partial get-column-info (fn [metadata column]
                             (.getColumnTypeName metadata column))))

(defn result->map
  [keys result])

(defn process-result
  ""
  [result]
  nil)

(defn process-results
  ""
  [results]
  nil)

(defn sqlvec->query
  "Convert a sqlvec to a SQL string."
  [sqlvec]
  nil)

(deftype HugsqlAdapterClickhouseNativeJdbc []
  
  adapter/HugsqlAdapter
  
  (execute [this db sqlvec options]
    (let [query (sqlvec->query sqlvec)]
      (-> db
          .createStatement
          (.executeQuery query))))

  (query [this db sqlvec options]
    (let [query (sqlvec->query sqlvec)]
      (-> db
          .createStatement
          (.executeQuery query))))

  (result-one [this results options]
    results)

  (result-many [this results options]
    results)

  (result-affected [this results options]
    results)

  (result-raw [this results options]
    results)

  (on-exception [this exception]
    (throw exception)))

(defn hugsql-adapter-clickhouse-native-jdbc []
  (->HugsqlAdapterClickhouseNativeJdbc))

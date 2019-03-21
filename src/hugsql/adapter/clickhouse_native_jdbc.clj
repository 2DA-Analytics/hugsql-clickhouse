(ns hugsql.adapter.clickhouse-native-jdbc
  "ClickHouse native JDBC adapter for HugSQL."
  (:require [clojure.string :as string]
            [hugsql.adapter :as adapter])
  (:import (java.sql Connection DriverManager ResultSet Statement))
  (:gen-class))

(defn get-column-info
  "Given `results` return a vector of keys."
  [func results]
  (let [metadata (.getMetaData results)
        column-count (.getColumnCount metadata)]
    (loop [columns (range 1 (inc column-count)) keys (transient [])]
      (if (empty? columns)
        (persistent! keys)
        (recur (rest columns) (conj! keys (func metadata (first columns))))))))

(def get-column-names
  "Get column names for ClickHouse `results`."
  (partial get-column-info (fn [metadata column]
                             (.getColumnName metadata column))))

(defn result->map
  "Turn a ClickHouse `result` into a map."
  [keys result]
  (reduce (fn [acc key] (assoc acc (keyword key) (.getObject result key)))
          {}
          keys))

(defn process-result
  "Process an individual ClickHouse `result`."
  [result]
  (-> result
      get-column-names
      (result->map result)))

(defn process-results
  "Process a list of ClickHouse `results`."
  [results]
  (let [processed (transient [])]
    (while (.next results)
      (conj! processed (process-result results)))
    (persistent! processed)))

(defprotocol Stringer
  "Interface for converting instances of specific classes into strings."
  (stringify
    [obj]
    (str obj)))

(extend-protocol Stringer
  java.lang.String
  (stringify
    [obj]
    (str "'" obj "'"))
  clojure.lang.Keyword
  (stringify
   [obj]
   (name obj))
  clojure.lang.PersistentVector
  (stringify
    [obj]
    (string/replace (str obj) #" " ", ")))

(defn sqlvec->query
  "Convert a `sqlvec` to a SQL string."
  [sqlvec]
  (loop [query (string/replace (first sqlvec) #"\n" " ") vals (rest sqlvec)]
    (if (not (empty? vals))
      (recur (string/replace-first query #"\?" (stringify (first vals))) (rest vals))
      query)))

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
    (-> results
        process-results
        first))

  (result-many [this results options]
    (-> results
        process-results))

  (result-affected [this results options]
    results)

  (result-raw [this results options]
    results)

  (on-exception [this exception]
    (throw exception)))

(defn hugsql-adapter-clickhouse-native-jdbc []
  (->HugsqlAdapterClickhouseNativeJdbc))

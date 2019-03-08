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
      (if (empty? columns)
        (persistent! keys)
        (recur (rest columns) (conj! keys (func metadata (first columns))))))))

(def get-column-names
  (partial get-column-info (fn [metadata column]
                             (.getColumnName metadata column))))

(def get-column-type-names
  (partial get-column-info (fn [metadata column]
                             (.getColumnTypeName metadata column))))

(defn result->map
  [keys result]
  (reduce (fn [acc key]
            (let [field (first key)
                  field-type (keyword (second key))]
              (assoc acc (keyword field) (.getObject result field))))
          {}
          keys))

(defn process-result
  ""
  [result]
  (let [names (get-column-names result)
        type-names (get-column-type-names result)
        keys (map vector names type-names)]
    (result->map keys result)))

(defn process-results
  ""
  [results]
  (let [processed (transient [])]
    (while (.next results)
      (conj! processed (process-result results)))
    (persistent! processed)))

(defprotocol Stringer
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
    (clojure.string/replace (str obj) #" " ", ")))

(defn sqlvec->query
  "Convert a sqlvec to a SQL string."
  [sqlvec]
  (loop [query (clojure.string/replace (first sqlvec) #"\n" " ") vals (rest sqlvec)]
    (if (not (empty? vals))
      (recur (clojure.string/replace-first query #"\?" (stringify (first vals))) (rest vals))
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

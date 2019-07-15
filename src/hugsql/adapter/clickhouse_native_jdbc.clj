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
  (reduce (fn [acc key]
            (let [array? (instance? com.github.housepower.jdbc.ClickHouseArray (.getObject result key))]
              (cond array?
                    (assoc acc (keyword key) (vec (.getArray (.getObject result key))))
                    :else
                    (assoc acc (keyword key) (.getObject result key)))))
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

(defprotocol Type
  (object->string [this]))

(extend-protocol Type
  clojure.lang.Keyword
  (object->string [obj] (name obj))
  java.lang.Object
  (object->string [obj] (str obj))
  clojure.lang.PersistentVector
  (object->string [obj] (-> (vec (map object->string obj))
                            str
                            (string/replace #"\"" "")
                            (string/replace #"([^,]) " "$1, ")))
  java.lang.String
  (object->string [obj] (str "'" obj "'")))

(defn sqlvec->query
  "Convert a `sqlvec` to a SQL string."
  [sqlvec]
  (loop [query (string/replace (first sqlvec) #"\n" " ") vals (rest sqlvec)]
    (if (seq vals)
      (recur (string/replace-first query #"\?" (object->string (first vals))) (rest vals))
      query)))

(deftype HugsqlAdapterClickhouseNativeJdbc []

  adapter/HugsqlAdapter

  (execute [this db sqlvec options]
    (let [query (sqlvec->query sqlvec)]
      (-> db
          (.prepareStatement query)
          (.execute))))

  (query [this db sqlvec options]
    (let [query (sqlvec->query sqlvec)]
      (-> db
          (.prepareStatement query)
          (.executeQuery))))

  (result-one [this results options]
    (-> results
        process-results
        first))

  (result-many [this results options]
    (process-results results))

  (result-affected [this results options]
    results)

  (result-raw [this results options]
    results)

  (on-exception [this exception]
    (throw exception)))

(defn hugsql-adapter-clickhouse-native-jdbc []
  (->HugsqlAdapterClickhouseNativeJdbc))

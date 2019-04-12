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

(defn ascertain-object-type
  "Return the appropriate type for the object for stringifying it."
  [obj]
  (cond (number? obj)
        :number
        (= "class clojure.lang.Keyword" (str (class obj)))
        :keyword
        (= "class java.lang.String" (str (class obj)))
        :string
        (= "class clojure.lang.PersistentVector" (str (class obj)))
        :persistent-vector))

(defmulti object->string
  "Convert an object into a string representation for ClickHouse."
  ascertain-object-type)

(defmethod object->string :number [obj]
  (str obj))

(defmethod object->string :keyword [obj]
  (name obj))

(defmethod object->string :string [obj]
  (str "'" obj "'"))

(defmethod object->string :persistent-vector [obj]
  (string/replace (str obj) #" " ", "))

(defn sqlvec->query
  "Convert a `sqlvec` to a SQL string."
  [sqlvec]
  (loop [query (string/replace (first sqlvec) #"\n" " ") vals (rest sqlvec)]
    (if (not (empty? vals))
      (recur (string/replace-first query #"\?" (object->string (first vals))) (rest vals))
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

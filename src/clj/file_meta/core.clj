(ns file-meta.core 
  (:require [c3kit.apron.utilc :as utilc]
            [clojure.java.io :as io]))

(defn get-files[input-path]
  (->> (.listFiles (io/file input-path))
       (remove #(.isDirectory %))))

(defn get-name [file]
  (.getName file))

(defn touch [file]
  (spit file nil :append true))

(defn meta-file [config]
  (let [file (str (:src config) "/.metadata.edn")]
    (touch file)
    file))

(defn meta-data [config]
  (utilc/<-edn (slurp (meta-file config))))

(defn find-files 
  ([config]
   (map get-name (get-files (:src config)))) 
  ([config filter-fn]
   (filter #(filter-fn (get (meta-data config) %)) (find-files config))))

(defn change-meta [change-fn config file value]
  (let [meta-file (meta-file config)]
    (spit meta-file (change-fn (utilc/<-edn (slurp meta-file)) file value))))

(defn assoc-meta [config file value]
  (change-meta assoc config file value))

(defn update-meta [config file f]
  (change-meta update config file f))

(defn get-meta [config file]
  (get (utilc/<-edn (slurp (meta-file config))) file))
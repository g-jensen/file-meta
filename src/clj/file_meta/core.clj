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

(defn meta-file [path]
  (let [file (str path "/.metadata.edn")]
    (touch file)
    file))

(defn meta-data [path]
  (utilc/<-edn (slurp (meta-file path))))

(defn find-files 
  ([path]
   (map get-name (get-files path))) 
  ([path filter-fn]
   (filter #(filter-fn (get (meta-data path) %)) (find-files path))))

(defn change-meta [change-fn path file value]
  (let [meta-file (meta-file path)]
    (spit meta-file (change-fn (utilc/<-edn (slurp meta-file)) file value))))

(defn assoc-meta [path file value]
  (change-meta assoc path file value))

(defn update-meta [path file f]
  (change-meta update path file f))

(defn get-meta [path file]
  (get (utilc/<-edn (slurp (meta-file path))) file))
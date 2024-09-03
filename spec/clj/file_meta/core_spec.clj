(ns file-meta.core-spec
  (:require [file-meta.core :as sut]
            [clojure.java.io :as io]
            [speclj.core :refer :all]))

(def path "spec/tmp")
(def in-path (str path "/in"))
(def out-path (str path "/out"))
(def other-path (str path "/other-dir"))

(def paths [path in-path out-path other-path])

(defn- files [dir] (remove #(.isDirectory %) (.listFiles (io/file dir))))
(defn- clear-files [] (run! io/delete-file (apply concat (map files paths))))
(defn- touch [path file-name] (spit (str path "/" file-name) nil))

(defn- init-paths []
  (mapv #(.mkdir (io/file %)) paths))

(describe "Core"
  (before (clear-files)
          (init-paths))

  (it "finds nothing with no files"
    (should= [] (sut/find-files path)))
          
  (it "finds files in src directory"
    (touch path "f1.test")
    (touch other-path "f2.test")
    (should= ["f1.test"] (sut/find-files path))
    (should= ["f2.test"] (sut/find-files other-path)))
          
  (it "finds files in src directory with filter"
    (touch path "f1.test")
    (touch path "f2.test")
    (sut/assoc-meta path "f1.test" {:abc :def})
    (sut/assoc-meta path "f2.test" {:ghi :jkl})
    (should= ["f2.test"] (sut/find-files path :ghi)))
          
  (context "metadata"
    (it "sets metadata"
      (touch path "f1.test")
      (sut/assoc-meta path "f1.test" 1)
      (should= 1 (sut/get-meta path "f1.test")))
    
    (it "resets metadata"
      (touch path "f2.test")
      (sut/assoc-meta path "f2.test" 1)
      (sut/assoc-meta path "f2.test" 2)
      (should= 2 (sut/get-meta path "f2.test")))
    
    (it "update metadata"
      (touch path "f3.test")
      (sut/assoc-meta path "f3.test" 2)
      (sut/update-meta path "f3.test" inc)
      (should= 3 (sut/get-meta path "f3.test")))))
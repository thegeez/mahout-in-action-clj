(ns mahout-in-action.intro-recommender
  (:require [clojure.java.io :as io])
  (:import [org.apache.mahout.cf.taste.model DataModel]
           [org.apache.mahout.cf.taste.impl.model.file FileDataModel]
           [org.apache.mahout.cf.taste.similarity UserSimilarity]
           [org.apache.mahout.cf.taste.impl.similarity PearsonCorrelationSimilarity]
           [org.apache.mahout.cf.taste.neighborhood UserNeighborhood]
           [org.apache.mahout.cf.taste.impl.neighborhood NearestNUserNeighborhood]
           [org.apache.mahout.cf.taste.recommender Recommender]
           [org.apache.mahout.cf.taste.impl.recommender GenericUserBasedRecommender]))

(defn -main [& args]
  (let [^DataModel model (FileDataModel. (io/as-file (io/resource "intro.csv")))
        ^UserSimilarity similarity (PearsonCorrelationSimilarity. model)
        ^UserNeighborhood neighborhood (NearestNUserNeighborhood. 2 similarity model)
        ^Recommender recommender (GenericUserBasedRecommender. model neighborhood similarity)
        user1-recommendations (.recommend recommender
                                          1 ;;user-id
                                          1 ;;#items to recommend
                                          )]
    (doseq [r user1-recommendations]
      (prn r))))

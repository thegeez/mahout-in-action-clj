(ns mahout-in-action.ch2-3
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log])
  (:import [org.apache.mahout.cf.taste.model DataModel]
           [org.apache.mahout.cf.taste.impl.model.file FileDataModel]
           [org.apache.mahout.cf.taste.similarity UserSimilarity]
           [org.apache.mahout.cf.taste.impl.similarity PearsonCorrelationSimilarity]
           [org.apache.mahout.cf.taste.neighborhood UserNeighborhood]
           [org.apache.mahout.cf.taste.impl.neighborhood NearestNUserNeighborhood]
           [org.apache.mahout.cf.taste.recommender Recommender]
           [org.apache.mahout.cf.taste.impl.recommender GenericUserBasedRecommender]
           [org.apache.mahout.common RandomUtils]
           [org.apache.mahout.cf.taste.eval RecommenderEvaluator RecommenderBuilder
            RecommenderIRStatsEvaluator]
           [org.apache.mahout.cf.taste.impl.eval AverageAbsoluteDifferenceRecommenderEvaluator
            RMSRecommenderEvaluator GenericRecommenderIRStatsEvaluator]))



(defn -main [& args]
  (let [_ (RandomUtils/useTestSeed)

        ^RecommenderEvaluator evaluator
        #_(RMSRecommenderEvaluator. )
        (AverageAbsoluteDifferenceRecommenderEvaluator.)
        builder (reify RecommenderBuilder
                       (^Recommender buildRecommender [this ^DataModel model]
                                     (let [^UserSimilarity similarity (PearsonCorrelationSimilarity. model)
                                           ^UserNeighborhood neighborhood (NearestNUserNeighborhood. 2 similarity model)
                                           ^Recommender recommender (GenericUserBasedRecommender. model neighborhood similarity)]
                                       recommender)))
        
        ^DataModel model (FileDataModel. (io/as-file (io/resource "large/ml-100k/ua.base" #_"intro.csv")))
        score (.evaluate evaluator
                         builder
                         nil ;; datamodel builder
                         model
                         0.7  ;; train with 70%
                         1.0  ;; part of datamodel to use
                         )
        _ (log/info "Evaluator score" score)

        ^RecommenderIRStatsEvaluator ir-evaluator (GenericRecommenderIRStatsEvaluator.)

        ir-stats (Object.)#_(.evaluate ir-evaluator
                            builder
                            nil
                            model
                            nil
                            2
                            GenericRecommenderIRStatsEvaluator/CHOOSE_THRESHOLD
                            1.0)]
    (log/info (bean ir-stats))))

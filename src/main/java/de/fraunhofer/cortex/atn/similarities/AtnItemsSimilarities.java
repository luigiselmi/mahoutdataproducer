package de.fraunhofer.cortex.atn.similarities;

import org.apache.mahout.cf.taste.impl.similarity.precompute.MultithreadedBatchItemSimilarities;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;

public class AtnItemsSimilarities {
  ItemBasedRecommender recommender;
  public void computeSimilarities() {
    MultithreadedBatchItemSimilarities sim = new MultithreadedBatchItemSimilarities(recommender, 1, 1);
    
  }

}

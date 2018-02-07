package de.fraunhofer.cortex.atn.similarities;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.similarity.precompute.FileSimilarItemsWriter;
import org.apache.mahout.cf.taste.impl.similarity.precompute.MultithreadedBatchItemSimilarities;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.similarity.precompute.SimilarItemsWriter;

import de.fraunhofer.cortex.atn.logs.SignalsDataModel;

public class AtnItemsSimilarities {
  
  public int computeSimilarities(File similaritiesFile) throws IOException, TasteException {
    File dataFile = new File("");
    SignalsDataModel dataModel = new SignalsDataModel(dataFile);
    ItemBasedRecommender recommender = new AtnItemBasedRecommender(dataFile);
    MultithreadedBatchItemSimilarities sim = new MultithreadedBatchItemSimilarities(recommender, 1, 1);
    int degreeOfParallelism = 1;
    int maxDurationInHours = 1;
    SimilarItemsWriter writer = new FileSimilarItemsWriter(similaritiesFile);
    int numSimilarities = sim.computeItemSimilarities(degreeOfParallelism, maxDurationInHours, writer);
    return numSimilarities; 
    
  }

}

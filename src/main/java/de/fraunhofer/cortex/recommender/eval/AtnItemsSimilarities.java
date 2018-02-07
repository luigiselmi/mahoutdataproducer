package de.fraunhofer.cortex.recommender.eval;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.precompute.FileSimilarItemsWriter;
import org.apache.mahout.cf.taste.impl.similarity.precompute.MultithreadedBatchItemSimilarities;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.precompute.BatchItemSimilarities;
import org.apache.mahout.cf.taste.similarity.precompute.SimilarItemsWriter;

import de.fraunhofer.cortex.recommender.model.SignalsDataModel;

public class AtnItemsSimilarities {
  
  /**
   * Computes the similarities from a data model and saves the result in a file.   
   * @param model
   * @param similaritiesFile
   * @param degreeOfParallelism
   * @param maxDurationInHours
   * @return
   * @throws IOException
   * @throws TasteException
   */
  public int computeSimilarities(SignalsDataModel model, 
                                 File similaritiesFile,
                                 int degreeOfParallelism,
                                 int maxDurationInHours) throws IOException, TasteException {
    
    ItemBasedRecommender recommender = new GenericItemBasedRecommender(model, new PearsonCorrelationSimilarity(model));
    BatchItemSimilarities similarities = new MultithreadedBatchItemSimilarities(recommender, 1, 1);
    SimilarItemsWriter writer = new FileSimilarItemsWriter(similaritiesFile);
    int numSimilarities = similarities.computeItemSimilarities(degreeOfParallelism, maxDurationInHours, writer);
    writer.close();
    return numSimilarities; 
    
  }
  
  /**
   * By default the itemIDs in the similarities are available in long. This method maps
   * the itemIDs to string.
   * @param similaritiesFile
   */
  public void mapSimilaritiesToStringIDs(File similaritiesFile) {
    //TODO
  }
  /**
   * Builds an item-based recommender from a similarities file. This is useful when the 
   * data is large.
   * @param similaritiesFile
   * @return
   * @throws TasteException 
   */
  public Recommender buildItemBasedRecommenderFromSimilarities(SignalsDataModel model, File similaritiesFile) throws TasteException {
    
    // 1) Read the similarities file 
    // 2) Load the similarities in a collection
    List<GenericItemSimilarity.ItemItemSimilarity> similarities = new ArrayList<GenericItemSimilarity.ItemItemSimilarity>();
    // 3) Create the similarity
    ItemSimilarity similarity = new GenericItemSimilarity(similarities);
    Recommender recommender = new CachingRecommender(new GenericItemBasedRecommender(model, similarity));
    return recommender;
  }
  
  public double evaluateItemBasedRecommender(SignalsDataModel model) throws TasteException {
    RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
    RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
      @Override
      public Recommender buildRecommender(DataModel model) throws TasteException {
        ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
        return new GenericItemBasedRecommender(model, similarity);
        
      }
    };
   
    double trainingPercentage = 0.95;
    double evaluationPercentage = 0.05;
    double score = evaluator.evaluate(recommenderBuilder, null, model, trainingPercentage, evaluationPercentage);
    return score;
  }

}

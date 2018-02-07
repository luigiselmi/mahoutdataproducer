package de.fraunhofer.cortex.recommender.eval;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import de.fraunhofer.cortex.recommender.model.SignalsDataModel;

public class AtnRecommenderEvaluator {
  
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

package de.fraunhofer.cortex.recommender.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.precompute.FileSimilarItemsWriter;
import org.apache.mahout.cf.taste.impl.similarity.precompute.MultithreadedBatchItemSimilarities;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.precompute.BatchItemSimilarities;
import org.apache.mahout.cf.taste.similarity.precompute.SimilarItemsWriter;

public class AtnItemsSimilarities {
  
  /**
   * Computes the similarities from a data model using Pearson correlation and saves 
   * the result in a file. The file contains records of item-item pairs with a value 
   * that represents their similarity.  
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
	int similarItemsPerItem = 1;
    BatchItemSimilarities similarities = new MultithreadedBatchItemSimilarities(recommender, similarItemsPerItem);
    //SimilarItemsWriter writer = new AtnFileSimilarItemsWriter(similaritiesFile, model);
    SimilarItemsWriter writer = new FileSimilarItemsWriter(similaritiesFile);
    int numSimilarities = similarities.computeItemSimilarities(degreeOfParallelism, maxDurationInHours, writer);
    writer.close();
    return numSimilarities; 
    
  }
  
  
  /**
   * Builds an item-based recommender from a similarities file. This is useful when the 
   * data is large. The similarities are records of item-item pairs associated to a value
   * that represents their similarity. 
   * @param similaritiesFile
   * @return
   * @throws TasteException 
   * @throws IOException 
   */
  public Recommender buildRecommenderFromSimilarities(SignalsDataModel model, File similaritiesFile) throws TasteException, IOException {
	List<GenericItemSimilarity.ItemItemSimilarity> similarities = getItemSimilarities(similaritiesFile);
	ItemSimilarity similarity = new GenericItemSimilarity(similarities);
    Recommender recommender = new GenericItemBasedRecommender(model, similarity);
    return recommender;
  }
  
  /**
   * Returns a list of items similarities as (long, long, double), from a file where they are represented 
   * in the same format.
   * @param similaritiesFile
   * @return
 * @throws IOException 
   */
  public List<GenericItemSimilarity.ItemItemSimilarity> getItemSimilarities(File similaritiesFile) throws IOException {
	List<GenericItemSimilarity.ItemItemSimilarity> similarities = new ArrayList<GenericItemSimilarity.ItemItemSimilarity>();
	List<String> lines = FileUtils.readLines(similaritiesFile, "UTF-8");
	for (String line: lines) {
		String [] fields = line.split(",");
		long item1 = Long.parseLong(fields[0]);
		long item2 = Long.parseLong(fields[1]);
		double similarityValue = Double.parseDouble(fields[2]);
		GenericItemSimilarity.ItemItemSimilarity similarity = new GenericItemSimilarity.ItemItemSimilarity(item1, item2, similarityValue);
		similarities.add(similarity);
	}
	
	return similarities;  
  }
  
  /**
   * Returns a list of items similarities as (long, long, double), from a file where they are represented 
   * as (string, string, double), in order to be used by a Mahout recommender.
   * @param similaritiesFile
   * @return
 * @throws IOException 
   */
  public List<GenericItemSimilarity.ItemItemSimilarity> getItemSimilaritiesFromString(SignalsDataModel model, File similaritiesFile) throws IOException {
	return null;  
  }
  

}

package de.fraunhofer.cortex.recommender.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
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

import de.fraunhofer.cortex.logs.atn.SignalsFileUtils;

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
   * the itemIDs to string and updates the similarities file. It also returns the list of
   * similarities. 
   * @param similaritiesFile
   * @throws TasteException 
   */
  public List<GenericItemSimilarity.ItemItemSimilarity> mapSimilaritiesToStringIDs(SignalsDataModel model, File similaritiesFile) throws IOException, TasteException { 
   List<String> linesString = new ArrayList<String>();
   List<GenericItemSimilarity.ItemItemSimilarity> similarities = new ArrayList<GenericItemSimilarity.ItemItemSimilarity>();
   List<String> linesSimilarities = FileUtils.readLines(similaritiesFile, "UTF-8");
   for(String line: linesSimilarities) {
     int lastDelimiterStart = line.lastIndexOf(SignalsFileUtils.COLON_DELIMTER);
     double similarityValue = Double.parseDouble(line.substring(lastDelimiterStart + 1)); 
     String subRecord = line.substring(0, lastDelimiterStart);
     int subRecordLastDelimiterStart = subRecord.lastIndexOf(SignalsFileUtils.COLON_DELIMTER);
     long itemID2 = Long.parseLong(subRecord.substring(subRecordLastDelimiterStart + 1));
     long itemID1 = Long.parseLong(subRecord.substring(0, subRecordLastDelimiterStart));
     String stringItemID1 = model.getItemIDAsString(itemID1);
     String stringItemID2 = model.getItemIDAsString(itemID2);
     GenericItemSimilarity.ItemItemSimilarity similarity = new GenericItemSimilarity.ItemItemSimilarity(itemID1, itemID2, similarityValue);
     linesString.add(stringItemID1 +
         SignalsFileUtils.COLON_DELIMTER + 
         stringItemID2 + SignalsFileUtils.COLON_DELIMTER +
         similarityValue);
  
     similarities.add(similarity);
   }
   
   String filePath = similaritiesFile.getPath();
   similaritiesFile.delete();
   FileUtils.writeLines(new File(filePath), "UTF-8", linesString);
   
   return similarities;
    
  }
  /**
   * Builds an item-based recommender from a similarities file. This is useful when the 
   * data is large.
   * @param similaritiesFile
   * @return
   * @throws TasteException 
   * @throws IOException 
   */
  public Recommender buildRecommenderFromSimilarities(SignalsDataModel model, List<GenericItemSimilarity.ItemItemSimilarity> similarities) throws TasteException, IOException {
    ItemSimilarity similarity = new GenericItemSimilarity(similarities);
    Recommender recommender = new CachingRecommender(new GenericItemBasedRecommender(model, similarity));
    return recommender;
  }

}

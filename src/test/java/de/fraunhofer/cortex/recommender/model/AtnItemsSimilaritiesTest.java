package de.fraunhofer.cortex.recommender.model;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity.ItemItemSimilarity;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.junit.Before;
import org.junit.Test;

import de.fraunhofer.cortex.recommender.model.AtnItemsSimilarities;
import de.fraunhofer.cortex.recommender.model.SignalsDataModel;

public class AtnItemsSimilaritiesTest {
  
  SignalsDataModel model;
  File signalsFile; 
  File similaritiesFile;

  @Before
  public void setUp() throws Exception {
    signalsFile = new File(this.getClass().getClassLoader().getResource("signals/mahout_example.csv").getFile());
    similaritiesFile = new File(this.getClass().getClassLoader().getResource("signals/mahout_similarities.csv").getFile());
    model = new SignalsDataModel(signalsFile);
  }
  
  
  @Test
  public void testComputeSimilarities() throws IOException, TasteException {
    File tmpSimilaritiesFile = new File(new File(System.getProperty("java.io.tmpdir")), "similarities.csv");
    int degreeOfParallelism = 1;
    int maxDurationInHours = 1;
    AtnItemsSimilarities similarities = new AtnItemsSimilarities();
    int totNumSimilarities = similarities.computeSimilarities(model, tmpSimilaritiesFile, degreeOfParallelism, maxDurationInHours);
    assertTrue(totNumSimilarities > 0);
  }
  
  @Test
  public void testGetItemSimilarities() throws IOException {
	AtnItemsSimilarities similarity = new AtnItemsSimilarities();
	List<GenericItemSimilarity.ItemItemSimilarity> similarities = similarity.getItemSimilarities(similaritiesFile);
	ItemItemSimilarity record1 = similarities.get(0);
	assertTrue(record1.getItemID1() == -3952245584515844984L);
  }
  
  @Test
  public void testBuildRecommenderFromSimilarities() throws IOException, TasteException {
	  AtnItemsSimilarities similarity = new AtnItemsSimilarities();
	  Recommender recommender = similarity.buildRecommenderFromSimilarities(model, similaritiesFile);
	  System.out.println("Num. users: " + model.getNumUsers());
	  System.out.println("Num. items: " + model.getNumItems());
	  LongPrimitiveIterator it = model.getUserIDs(); 
	  while (it.hasNext()) {
		long userID = it.next();
	    List<RecommendedItem> recommendations = recommender.recommend(userID, 2);
	    int numRecommendations = recommendations.size();
	    System.out.println("User ID: " + userID);
	    for (RecommendedItem recommendation: recommendations) {
	    	long recommendedItem = recommendation.getItemID();
		    System.out.println("recommended itemID: " + recommendedItem);	
	    }
	    PreferenceArray userPreferences = model.getPreferencesFromUser(userID);
	    long [] prefs = userPreferences.getIDs();
	    for(int i = 0; i < prefs.length; i++) {
	    	System.out.println("Preferences: " + prefs[i]);
	    } 
	  }	  
  }
  

}

package de.fraunhofer.cortex.recommender.model;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.junit.Before;
import org.junit.Test;

import de.fraunhofer.cortex.recommender.model.AtnItemsSimilarities;
import de.fraunhofer.cortex.recommender.model.SignalsDataModel;

public class AtnItemsSimilaritiesTest {
  
  SignalsDataModel model;
  File signalsFile; 

  @Before
  public void setUp() throws Exception {
    signalsFile = new File(this.getClass().getClassLoader().getResource("signals/signals_test.csv").getFile());
    model = new SignalsDataModel(signalsFile);
  }
  
  /*
  @Test
  public void testComputeSimilarities() throws IOException, TasteException {
    File similaritiesFile = new File(new File(System.getProperty("java.io.tmpdir")), "similarities.csv");
    AtnItemsSimilarities similarities = new AtnItemsSimilarities();
    similarities.computeSimilarities(model, similaritiesFile, 1, 1);
  }
  */
  /*
  @Test
  public void testMapSimilaritiesToStringIDs() throws IOException, TasteException {
    File similaritiesFile = new File(new File(System.getProperty("java.io.tmpdir")), "similarities.csv");
    AtnItemsSimilarities similarities = new AtnItemsSimilarities();
    similarities.mapSimilaritiesToStringIDs(model, similaritiesFile);
  }
  */

}

package de.fraunhofer.cortex.logs.atn;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.junit.Before;
import org.junit.Test;

import de.fraunhofer.cortex.logs.atn.SignalsFileUtils;
import de.fraunhofer.cortex.recommender.model.SignalsDataModel;

public class SignalDataModelTest {
  
  File signalsFile = null;
  SignalsDataModel model = null;

  @Before
  public void setUp() throws Exception {
    signalsFile = new File(this.getClass().getClassLoader().getResource("signals/signals_test.csv").getFile());
    model = new SignalsDataModel(signalsFile);
  }

  @Test
  public void testGetItemIDsFromUser() throws TasteException, IOException {
    FastIDSet itemIDs = model.getItemIDsFromUser(1);
    Iterator<Long> i = itemIDs.iterator();
    while(i.hasNext()) {
      long itemID = i.next();
      if(model.itemIdMigrator != null) {
        System.out.println("String Item ID: " + model.getItemIDAsString(itemID) + " ItemID = " + itemID);
      }
      else {
        System.out.println("MIGRATOR == NULL");
      }
      
    }
  }

  @Test
  public void testGetMaxPreference() {
    System.out.println("Max preference: " + model.getMaxPreference());
  }

  @Test
  public void testGetMinPreference() {
    System.out.println("Min preference: " + model.getMinPreference());
  }

}

package de.fraunhofer.cortex.logs.atn;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.fraunhofer.cortex.logs.atn.SignalsFileUtils;


public class SignalsFileUtilsTest {

  File signalsFile = null;
  
  @Before
  public void setUp() throws Exception {
    signalsFile = new File(this.getClass().getClassLoader().getResource("signals/mahout_example.csv").getFile());
  }

  @Test
  public void test() throws IOException {
    Set<String> stringIDs = SignalsFileUtils.getItemIDs(signalsFile);
    assertEquals(7, stringIDs.size());
  }

}

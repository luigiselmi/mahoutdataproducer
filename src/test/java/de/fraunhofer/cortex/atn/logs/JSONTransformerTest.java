package de.fraunhofer.cortex.atn.logs;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.fraunhofer.cortex.atn.logs.JSONTransformer;
import de.fraunhofer.cortex.atn.logs.SignalRecord;

public class JSONTransformerTest {

  private File viewFile;
  
  @Before
  public void setUp() throws Exception {
    viewFile = new File(this.getClass().getClassLoader().getResource("views/20161128.txt").getFile());
  }

  @Test
  public void test() throws IOException {
    List<String> lines = FileUtils.readLines(viewFile, "UTF-8");
    for(String line: lines) {
      SignalRecord record = JSONTransformer.parseComponentView(line);
      System.out.println("Atn Item ID: " + record.atnItemID);
      System.out.println("User ID: " + record.getUserID());
    }
  }

}

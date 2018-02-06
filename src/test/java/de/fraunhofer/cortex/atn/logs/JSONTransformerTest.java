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
  private File downloadFile;
  private File comparisonFile;
  private ApplicationConfig config;
  private JSONTransformer transformer;
  
  @Before
  public void setUp() throws Exception {
    viewFile = new File(this.getClass().getClassLoader().getResource("views/20161128.txt").getFile());
    downloadFile = new File(this.getClass().getClassLoader().getResource("downloads/20180113.txt").getFile());
    comparisonFile = new File(this.getClass().getClassLoader().getResource("comparisons/20180118.txt").getFile());
    config = new ApplicationConfig();
    config.setValueComparison(2.0);
    config.setValueDownload(2.0);
    config.setValueView(1.0);
    transformer = new JSONTransformer(config);
  }

  @Test
  public void testParseViews() throws IOException {
    List<String> lines = FileUtils.readLines(viewFile, "UTF-8");
    System.out.println("Test parsing view events");
    for(String line: lines) {
      SignalRecord record = transformer.parseViews(line);
      System.out.println("  User ID: " + record.getUserID());
      System.out.print("Item ID: " + record.atnItemID);
    }
  }
  
  @Test
  public void testParseDownloads() throws IOException {
    List<String> lines = FileUtils.readLines(downloadFile, "UTF-8");
    System.out.println("Test parsing download events");
    for(String line: lines) {
      SignalRecord record = transformer.parseDownloads(line);
      System.out.println(" User ID: " + record.getUserID());
      System.out.print("Item ID: " + record.atnItemID);
    }
  }
  
  @Test
  public void testParseComparisons() throws IOException {
    List<String> lines = FileUtils.readLines(comparisonFile, "UTF-8");
    System.out.println("Test parsing comparisons events");
    for(String line: lines) {
      List<SignalRecord> records = transformer.parseComparisons(line);
      for(SignalRecord r: records) {
        System.out.print("User ID: " + r.getUserID());
        System.out.println(" Item ID: " + r.getAtnItemID());
      }
    }
  }

}

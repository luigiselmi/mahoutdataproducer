package de.fraunhofer.cortex.logs.atn;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class SignalsFileUtils {
  
  public static final String COLON_DELIMTER = ",";
  
  /**
   * Copies the string item IDs into an array list
   * @param records
   * @return
   * @throws IOException 
   */
  public static Set<String> getItemIDs(File dataFile) throws IOException {
    Set<String> stringItemIDs = new HashSet<String>();
    List<String> lines = FileUtils.readLines(dataFile, "UTF-8");
    for(String line: lines) {
      int lastDelimiterStart = line.lastIndexOf(COLON_DELIMTER);
      String subRecord = line.substring(0, lastDelimiterStart);
      int subRecordLastDelimiterStart = subRecord.lastIndexOf(COLON_DELIMTER);
      String stringItemID = subRecord.substring(subRecordLastDelimiterStart + 1);
      stringItemIDs.add(stringItemID);
    }
    return stringItemIDs;
  }
  
  /**
   * Copies the users IDs into an array list
   * @param records
   * @return
   * @throws IOException 
   */
  public static Set<String> getUserIDs(File dataFile) throws IOException {
    Set<String> stringUserIDs = new HashSet<String>();
    List<String> lines = FileUtils.readLines(dataFile, "UTF-8");
    for(String line: lines) {
      int delimiterIndex = line.indexOf(COLON_DELIMTER);
      String stringItemID = line.substring(0, delimiterIndex);
      stringUserIDs.add(stringItemID);
    }
    return stringUserIDs;
  }
  
  public static int getNumberUserIDs(File dataFile) throws IOException {
	  Set<String> userIds = getUserIDs(dataFile);
	  return userIds.size();
  }
  
  public static int getNumberItemIDs(File dataFile) throws IOException {
	  Set<String> itemIds = getItemIDs(dataFile);
	  return itemIds.size();
  }
  
 
  public void printFile(String filePath) throws MalformedURLException, IOException {
    InputStream in = new URL( filePath ).openStream();
     try {
       InputStreamReader inR = new InputStreamReader( in );
       BufferedReader buf = new BufferedReader( inR );
       String line;
       while ( ( line = buf.readLine() ) != null ) {
         System.out.println( line );
       }
     } finally {
       in.close();
     }
  }
  

}

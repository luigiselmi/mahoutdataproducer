package de.fraunhofer.cortex.logs.atn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SignalsReader {
	
  static final Logger LOG = LoggerFactory.getLogger(SignalsReader.class);
  JSONParser transformer = null;
  ApplicationConfig config;
  
  public SignalsReader(ApplicationConfig config) throws IOException {
    this.config = config;
    transformer = new JSONParser(config);
  }
  
  /**
   * Extracts events of type views from the folder passed as argument and its subfolders 
   * @return
   * @throws IOException
   */
  public List<SignalRecord> parseRecursivelyViewFiles(File dir, IOFileFilter logFilter) throws IOException {
    List<SignalRecord> signals = new ArrayList<SignalRecord>();
    Collection<File> logFiles = FileUtils.listFiles(dir, logFilter, TrueFileFilter.INSTANCE);
    LOG.info("Number of files with view events: " + logFiles.size());
    for (File logFile: logFiles) {
      if (logFile.isFile()) {
        List<String> lines = FileUtils.readLines(logFile, "UTF-8");
        for(String line: lines) {
          SignalRecord signal = transformer.parseViews(line);
          signals.add(signal);
          
        }
      
      }
    }
    LOG.info("Number of events of type view: " + signals.size());
    return signals;  
  }
  
  /**
   * Extracts events of type download from the folder passed as argument and its subfolders 
   * @return
   * @throws IOException
   */
  public List<SignalRecord> parseRecursivelyDownloadFiles(File dir, IOFileFilter logFilter) throws IOException {
    List<SignalRecord> signals = new ArrayList<SignalRecord>();
    Collection<File> logFiles = FileUtils.listFiles(dir, logFilter, FileFilterUtils.trueFileFilter());
    LOG.info("Number of files with download events: " + logFiles.size());
    for (File logFile: logFiles) {
      if (logFile.isFile()) {
        List<String> lines = FileUtils.readLines(logFile, "UTF-8");
        for(String line: lines) {
          SignalRecord signal = transformer.parseDownloads(line);
          signals.add(signal);
          
        }
      
      }
    }
    LOG.info("Number of events of type download: " + signals.size());
    return signals;  
  }
  
  /**
   * Extracts events of type comparison from the folder passed as argument and its subfolders 
   * @return
   * @throws IOException
   */
  public List<SignalRecord> parseRecursivelyComparisonFiles(File dir, IOFileFilter logFilter) throws IOException {
    List<SignalRecord> signals = new ArrayList<SignalRecord>();
    Collection<File> logFiles = FileUtils.listFiles(dir, logFilter, FileFilterUtils.trueFileFilter());
    LOG.info("Number of files with comparison events: " + logFiles.size());
    for (File logFile: logFiles) {
      if (logFile.isFile()) {
        List<String> lines = FileUtils.readLines(logFile, "UTF-8");
        for(String line: lines) {
          List<SignalRecord> signalsFromOneUser = transformer.parseComparisons(line);
          signals.addAll(signalsFromOneUser);    
        }
      
      }
    }
    LOG.info("Number of events of type comparison: " + signals.size());
    return signals;  
  }
  
  /**
   * Copies the string item IDs into an array list
   * @param records
   * @return
   */
  public Set<String> getItemIDs(List<SignalRecord> records) {
    Set<String> itemIDs = new HashSet<String>();
    for(SignalRecord record: records) {
      String itemID = record.getAtnItemID();
      itemIDs.add(itemID);
    }
    return itemIDs;
  }
  /**
   * Returns the number of items in a set of records (userID, itemID, value)
   * @param records
   * @return
   */
  public int getNumItemIDs(List<SignalRecord> records) {
	  Set<String> itemIDs = getItemIDs(records);
	  return itemIDs.size();
  }
  
  /**
   * Copies the userIDs in a list of signal records
   * @param records
   * @return
   */
  public Set<Long> getUserIDs(List<SignalRecord> records) {
	  Set<Long> userIDs = new HashSet<Long>();
	  for(SignalRecord record: records) {
	    long userID = record.getUserID();
	    userIDs.add(userID);
	  }
	  return userIDs;
  }
  
  public int getNumUserIDs(List<SignalRecord> records) {
	  Set<Long> userIDs = getUserIDs(records);
	  return userIDs.size();
  }
  
  /**
   * Group the records by a key (userID and itemID). The value for each pair userId, itemID 
   * is the sum of all the values collected from the feedback 
   * @param records
   * @return
   * @throws IOException
   */
  public List<SignalRecord> groupRecordsByKey(List<SignalRecord> records) {
    List<SignalRecord> keyedSignals = new ArrayList<SignalRecord>();
    Map<String, List<SignalRecord>> recordsByKey = records.stream()
                    .collect(Collectors.groupingBy(SignalRecord::getKey));
    for (String key: recordsByKey.keySet()) {
      List<SignalRecord> groupRecords = recordsByKey.get(key);
      double value = groupRecords.stream().collect(Collectors.summingDouble(SignalRecord::getValue));
      SignalRecord record = groupRecords.get(0);
      SignalRecord signal = new SignalRecord(record.getUserID(), record.getAtnItemID(), value);
      keyedSignals.add(signal);
    }
    
    return keyedSignals;
    
  }
  /**
   * Normalize a list of signals.
   * @param records
   * @return
   */
  public List<SignalRecord> normalizeList(List<SignalRecord> records) {
    for(SignalRecord record: records) {
      double value = record.getValue();
      double normalizedValue = normalize(value);
      record.setValue(normalizedValue);    
    }
    return records;
    
  }
  /**
   * Normalize a signals value. If the value of an item computed from a user's feedback
   * is above the maximum it will be set to the maximum and then normalized in order to be between
   * -1 and 1. 
   * @param value
   * @return
   */
  public double normalize(double value) {
    value = ( value > config.getMaxValue() ) ? config.getMaxValue() : value;
    return (2*(value - config.getMinValue()) - (config.getMaxValue() - config.getMinValue())) / (config.getMaxValue() - config.getMinValue()); 
  }
  
  /**
   * Create a file of signals: userID, itemID, value.
   * @param feedbacks
   * @return
   * @throws FileNotFoundException
   */
  public void createSignalsFile(List<SignalRecord> records, String signalsFileName) throws FileNotFoundException {
    
      PrintWriter writer = new PrintWriter(new FileOutputStream(new File(signalsFileName)));
      for (SignalRecord r: records) {
        writer.println(r.getUserID() + "," + r.getAtnItemID() + "," + r.getValue());
      }
      writer.flush();
      writer.close();
      
  }

}

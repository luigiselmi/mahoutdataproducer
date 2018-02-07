package de.fraunhofer.cortex.logs.atn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;


public class SignalsReader {
  
  JSONTransformer transformer = null;
  ApplicationConfig config;
  
  public SignalsReader(ApplicationConfig config) throws IOException {
    this.config = config;
    transformer = new JSONTransformer(config);
  }
  
  /**
   * Extracts userIDs, itemIDs from the views events 
   * and sets the value to 1.0 from the user.
   * Stores each event in one record.  
   * navigation data.
   * @return
   * @throws IOException
   */
  public List<SignalRecord> readViewsFiles(File dir) throws IOException {
    List<SignalRecord> signals = new ArrayList<SignalRecord>();
    File[] logFiles = dir.listFiles();
    for (int i = 0; i < logFiles.length; i++) {
      if (logFiles[i].isFile()) {
        List<String> lines = FileUtils.readLines(logFiles[i], "UTF-8");
        for(String line: lines) {
          SignalRecord signal = transformer.parseViews(line);
          signals.add(signal);
          
        }
      
      }
    }
    return signals;
  }
  
  /**
   * Extracts userIDs, itemIDs from the downloads events 
   * and sets the value to 1.0 from the user.
   * Stores each event in one record.  
   * navigation data.
   * @return
   * @throws IOException
   */
  public List<SignalRecord> readDownloadsFiles(File dir) throws IOException {
    List<SignalRecord> signals = new ArrayList<SignalRecord>();
    File[] logFiles = dir.listFiles();
    for (int i = 0; i < logFiles.length; i++) {
      if (logFiles[i].isFile()) {
        List<String> lines = FileUtils.readLines(logFiles[i], "UTF-8");
        for(String line: lines) {
          SignalRecord signal = transformer.parseDownloads(line);
          signals.add(signal);
          
        }
      
      }
    }
    return signals;
  }
  
  /**
   * Extracts userIDs, itemIDs from the comparison events 
   * and sets the value to 1.0 from the user for each comparison.
   * One user generates one event for each item in a comparison. 
   * navigation data.
   * @return
   * @throws IOException
   */
  public List<SignalRecord> readComparisonsFiles(File dir) throws IOException {
    List<SignalRecord> signals = new ArrayList<SignalRecord>();
    File[] logFiles = dir.listFiles();
    for (int i = 0; i < logFiles.length; i++) {
      if (logFiles[i].isFile()) {
        List<String> lines = FileUtils.readLines(logFiles[i], "UTF-8");
        for(String line: lines) {
          List<SignalRecord> signalsFromOneUser = transformer.parseComparisons(line);
          signals.addAll(signalsFromOneUser);
          
        }
      
      }
    }
    return signals;
  }
  
  /**
   * Copies the string item IDs into an array list
   * @param records
   * @return
   */
  public Set<String> getStringItemIDs(List<SignalRecord> records) {
    Set<String> stringItemIDs = new HashSet<String>();
    for(SignalRecord record: records) {
      String stringItemID = record.getAtnItemID();
      stringItemIDs.add(stringItemID);
    }
    return stringItemIDs;
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
  public void createSignalsFile(List<SignalRecord> records, File signalsFile) throws FileNotFoundException {
    
      PrintWriter writer = new PrintWriter(new FileOutputStream(signalsFile));
      for (SignalRecord r: records) {
        writer.println(r.getUserID() + "," + r.getAtnItemID() + "," + r.getValue());
      }
      writer.flush();
      writer.close();
      
  }

}

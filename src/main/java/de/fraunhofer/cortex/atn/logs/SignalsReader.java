package de.fraunhofer.cortex.atn.logs;

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
  private File dir = null;
  
  public SignalsReader(File directory) {
    this.dir = directory;
  }
  /**
   * Extracts userIDs, itemIDs and sets the value to 1.0 from the user.
   * Stores each event in one record.  
   * navigation data.
   * @return
   * @throws IOException
   */
  public List<SignalRecord> readFiles() throws IOException {
    List<SignalRecord> signals = new ArrayList<SignalRecord>();
    File[] logFiles = dir.listFiles();
    for (int i = 0; i < logFiles.length; i++) {
      if (logFiles[i].isFile()) {
        List<String> lines = FileUtils.readLines(logFiles[i], "UTF-8");
        for(String line: lines) {
          SignalRecord signal = JSONTransformer.parseComponentView(line);
          signals.add(signal);
          System.out.print("Atn Item ID: " + signal.atnItemID);
          System.out.println(" User ID: " + signal.getUserID());
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
   * Group the records by a key (userID and itemID). The number of records 
   * per key counts as feedbacks.
   * @param records
   * @return
   * @throws IOException
   */
  public List<SignalRecord> groupRecordsByKey(List<SignalRecord> records) throws IOException {
    List<SignalRecord> keyedSignals = new ArrayList<SignalRecord>();
    Map<String, List<SignalRecord>> recordsByKey = records.stream()
                    .collect(Collectors.groupingBy(SignalRecord::getKey));
    for (String key: recordsByKey.keySet()) {
      List<SignalRecord> groupRecords = recordsByKey.get(key);
      SignalRecord record = groupRecords.get(0);
      SignalRecord feedback = new SignalRecord(record.getUserID(), record.getAtnItemID(), 1.0);
      feedback.setValue(groupRecords.size());
      keyedSignals.add(feedback);
      System.out.print("userID: " + feedback.getUserID());
      System.out.print(" itemID: " + feedback.getAtnItemID());
      System.out.println(" value: " + feedback.getValue());
    }
    
    return keyedSignals;
    
  }
  
  /**
   * Create a file of signals: userID, itemID, value.
   * @param feedbacks
   * @return
   * @throws FileNotFoundException
   */
  public void createSignalsFile(List<SignalRecord> feedbacks, File signalsFile) throws FileNotFoundException {
    
      if (signalsFile.exists()) {
        signalsFile.delete();
      }
      PrintWriter writer = new PrintWriter(new FileOutputStream(signalsFile));
      for (SignalRecord f: feedbacks) {
        writer.println(f.getUserID() + "," + f.getAtnItemID() + "," + f.getValue());
      }
      writer.flush();
      writer.close();
      
  }

}

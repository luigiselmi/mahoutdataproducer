package de.fraunhofer.cortex.logs.atn;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Before;
import org.junit.Test;

import de.fraunhofer.cortex.logs.atn.ApplicationConfig;
import de.fraunhofer.cortex.logs.atn.SignalRecord;
import de.fraunhofer.cortex.logs.atn.SignalsReader;

public class SignalsReadersTest {

  File dir = null;
  private ApplicationConfig config;
  private SignalsReader reader;
  
  @Before
  public void setUp() throws Exception {
    dir = new File(this.getClass().getClassLoader().getResource("views").getFile());
    config = new ApplicationConfig();
    config.setMaxValue(5.0);
    config.setMinValue(1.0);
    reader = new SignalsReader(config);
    
  }
  
  @Test
  public void testCreateSignalsFile() throws IOException {
	String signalsFilePath = System.getProperty("java.io.tmpdir") + "/signals.csv";
	String normalizedSignalsFilePath = System.getProperty("java.io.tmpdir") + "/normalized_signals.csv";
    List<SignalRecord> signals = reader.parseRecursivelyViewFiles(dir, TrueFileFilter.INSTANCE);
    List<SignalRecord> keyedSignals = reader.groupRecordsByKey(signals);
    reader.createSignalsFile(keyedSignals, signalsFilePath);
    List<SignalRecord> normalizedSignals = reader.normalizeList(keyedSignals);
    reader.createSignalsFile(normalizedSignals, normalizedSignalsFilePath);
  }
  
  @Test
  public void testFilterUserLogFiles() throws IOException {
	LogFileFilter logFilter = new LogFileFilter(20161128);
	String filteredSignalsFilePath = System.getProperty("java.io.tmpdir") + "/filtered_signals.csv";
    List<SignalRecord> signals = reader.parseRecursivelyViewFiles(dir, logFilter);
    List<SignalRecord> keyedSignals = reader.groupRecordsByKey(signals);
    List<SignalRecord> normalizedSignals = reader.normalizeList(keyedSignals);
    reader.createSignalsFile(normalizedSignals, filteredSignalsFilePath);
  }
  
  @Test
  public void testNormalize() {
    assertTrue(reader.normalize(4.0) == 0.5);
    assertTrue(reader.normalize(10.0) == 1.0);
  }

}

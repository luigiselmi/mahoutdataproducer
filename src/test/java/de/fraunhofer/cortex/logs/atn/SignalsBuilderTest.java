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
import de.fraunhofer.cortex.logs.atn.SignalsBuilder;

public class SignalsBuilderTest {

  File dir = null;
  private ApplicationConfig config;
  private SignalsBuilder builder;
  
  @Before
  public void setUp() throws Exception {
    dir = new File(this.getClass().getClassLoader().getResource("views").getFile());
    config = new ApplicationConfig();
    config.setMaxValue(5.0);
    config.setMinValue(1.0);
    builder = new SignalsBuilder(config);
    
  }
  
  @Test
  public void testCreateSignalsFile() throws IOException {
	String signalsFilePath = System.getProperty("java.io.tmpdir") + "/signals.csv";
	String normalizedSignalsFilePath = System.getProperty("java.io.tmpdir") + "/normalized_signals.csv";
    List<SignalRecord> signals = builder.parseRecursivelyViewFiles(dir, TrueFileFilter.INSTANCE);
    List<SignalRecord> keyedSignals = builder.groupRecordsByKey(signals);
    builder.createSignalsFile(keyedSignals, signalsFilePath);
    List<SignalRecord> normalizedSignals = builder.normalizeList(keyedSignals);
    builder.createSignalsFile(normalizedSignals, normalizedSignalsFilePath);
  }
  
  @Test
  public void testFilterUserLogFiles() throws IOException {
	LogFileFilter logFilter = new LogFileFilter(20161128);
	String filteredSignalsFilePath = System.getProperty("java.io.tmpdir") + "/filtered_signals.csv";
    List<SignalRecord> signals = builder.parseRecursivelyViewFiles(dir, logFilter);
    List<SignalRecord> keyedSignals = builder.groupRecordsByKey(signals);
    List<SignalRecord> normalizedSignals = builder.normalizeList(keyedSignals);
    builder.createSignalsFile(normalizedSignals, filteredSignalsFilePath);
  }
  
  @Test
  public void testNormalize() {
    assertTrue(builder.normalize(4.0) == 0.5);
    assertTrue(builder.normalize(10.0) == 1.0);
  }

}

package de.fraunhofer.cortex.logs.atn;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
    config = new ApplicationConfig();
    config.setMaxValue(5.0);
    config.setMinValue(1.0);
    reader = new SignalsReader(config);
    
  }

  @Test
  public void testReadViewsFiles() throws IOException {
    reader.readViewsFiles(dir);
  }
  
  @Test
  public void testKeyedSignals() throws IOException {
    List<SignalRecord> signals = reader.readViewsFiles(dir);
    List<SignalRecord> keyedSignals = reader.groupRecordsByKey(signals);
  }
  
  
  @Test
  public void testCreateSignalsFile() throws IOException {
    File signalsFile = new File(new File(System.getProperty("java.io.tmpdir")), "signals.csv");
    File normalizedSignalsFile = new File(new File(System.getProperty("java.io.tmpdir")), "normalized_signals.csv");
    List<SignalRecord> signals = reader.readViewsFiles(dir);
    List<SignalRecord> keyedSignals = reader.groupRecordsByKey(signals);
    reader.createSignalsFile(keyedSignals, signalsFile);
    List<SignalRecord> normalizedSignals = reader.normalizeList(keyedSignals);
    reader.createSignalsFile(normalizedSignals, normalizedSignalsFile);
  }
  
  @Test
  public void testNormalize() {
    assertTrue(reader.normalize(4.0) == 0.5);
    assertTrue(reader.normalize(10.0) == 1.0);
  }

}

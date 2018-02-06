package de.fraunhofer.cortex.atn.logs;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.fraunhofer.cortex.atn.logs.SignalRecord;
import de.fraunhofer.cortex.atn.logs.SignalsReader;

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
    reader = new SignalsReader(config);
    
  }

  @Test
  public void testReadViewsFiles() throws IOException {
    reader.readViewsFiles(dir);
  }
  
  @Test
  public void testKeyedSignals() throws IOException {
    List<SignalRecord> signals = reader.readViewsFiles(dir);
    List<SignalRecord> keyedSignals = SignalsReader.groupRecordsByKey(signals);
  }
  
  @Test
  public void testCreateSignalsFile() throws IOException {
    File signalsFile = new File(new File(System.getProperty("java.io.tmpdir")), "signals.csv");
    List<SignalRecord> signals = reader.readViewsFiles(dir);
    List<SignalRecord> keyedSignals = SignalsReader.groupRecordsByKey(signals);
    SignalsReader.createSignalsFile(keyedSignals, signalsFile);
  }

}

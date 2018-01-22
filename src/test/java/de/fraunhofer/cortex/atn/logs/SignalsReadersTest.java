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
  SignalsReader reader = null;
  
  @Before
  public void setUp() throws Exception {
    dir = new File(this.getClass().getClassLoader().getResource("views").getFile());
    reader = new SignalsReader(dir);
  }

  @Test
  public void testReadFiles() throws IOException {
    reader.readFiles();
  }
  
  @Test
  public void testKeyedSignals() throws IOException {
    List<SignalRecord> signals = reader.readFiles();
    List<SignalRecord> keyedSignals = reader.groupRecordsByKey(signals);
  }
  
  @Test
  public void testCreateSignalsFile() throws IOException {
    File signalsFile = new File(new File(System.getProperty("java.io.tmpdir")), "signals.csv");
    List<SignalRecord> signals = reader.readFiles();
    List<SignalRecord> keyedSignals = reader.groupRecordsByKey(signals);
    reader.createSignalsFile(keyedSignals, signalsFile);
  }

}

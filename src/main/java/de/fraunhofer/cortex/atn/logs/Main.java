package de.fraunhofer.cortex.atn.logs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.mahout.cf.taste.common.TasteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
	static final Logger LOG = LoggerFactory.getLogger(Main.class);
	private static String userLogsFolder;
	private static String signalsFilePath;
	
	public static void main(String[] args) throws IOException, TasteException {
	  if (args.length < 2) {
	    throw new NumberFormatException("The application needs:\n 1) the path to the log files \n 2) the path to the signals file");
	  }
	  
	  userLogsFolder = args[0];
	  signalsFilePath = args[1];
	  
	  Main main = new Main();
	  // 1) read config file
	  ApplicationConfig config = main.readConfiguration(); 
	  //userLogsFolder = config.getUserLogsFolder();
	  //signalsFilePath = config.getSignalsFile();
	  File dir = new File(userLogsFolder);
	  
	  SignalsReader signalsReader = new SignalsReader(dir);
	  // 2) read the log files
	  List<SignalRecord> signalsRecords = signalsReader.readFiles();
	  // 3) Count the signals: items a user has seen
	  List<SignalRecord> keyedSignals = signalsReader.groupRecordsByKey(signalsRecords);
	  // 4) save the signals in a file
	  File signalsFile = new File(signalsFilePath);
	  signalsReader.createSignalsFile(keyedSignals, signalsFile);
    
	}
	
	
	/**
	 * Reads the configuration file
	 * @return
	 * @throws IOException
	 */
	private ApplicationConfig readConfiguration() throws IOException {
	  ApplicationConfig config = new ApplicationConfig();
	  Properties prop = new Properties();
    InputStream configIs = Main.class.getClassLoader().getResourceAsStream("config.properties");
    prop.load(configIs);
    config.setUserLogsFolder( prop.getProperty("userlogs.views.folder") );
    config.setSignalsFile(new File(prop.getProperty("signals.file")));
    
    return config;
	}
	
	
}

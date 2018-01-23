package de.fraunhofer.cortex.atn.logs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
	
  static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
  static final String VIEWS_FOLDER_OPTION = "views";
	static final String DOWNLOADS_FOLDER_OPTION = "downloads";
	static final String MAHOUT_DATA_FILE = "output";
	
	private static Options options;
	
	public static void main(String[] args) throws IOException, TasteException {
	  
	  Options options = createOptions();
	  CommandLine cmd = getCommandLine(options, args);
	  String mahoutFileName = cmd.getOptionValue(MAHOUT_DATA_FILE);
	  File mahoutDataFile = new File(mahoutFileName);
	  if(! (cmd.hasOption(VIEWS_FOLDER_OPTION) | cmd.hasOption(DOWNLOADS_FOLDER_OPTION))) {
      help(options);
      System.exit(0);
    }
	  
	  List<SignalRecord> allSignals = new ArrayList<SignalRecord>();
	  
	  if(cmd.hasOption(VIEWS_FOLDER_OPTION)) {
	    File dir = new File(cmd.getOptionValue(VIEWS_FOLDER_OPTION));
	    // read the views log files
	    List<SignalRecord> viewsRecords = SignalsReader.readViewsFiles(dir);
	    allSignals.addAll(viewsRecords);
	  }
	  
	  if(cmd.hasOption(DOWNLOADS_FOLDER_OPTION)) {
      File dir = new File(cmd.getOptionValue(DOWNLOADS_FOLDER_OPTION));
      // read the downloads log files
      List<SignalRecord> downloadsRecords = SignalsReader.readDownloadsFiles(dir);
      allSignals.addAll(downloadsRecords);
    }
	  
	  List<SignalRecord> keyedSignals = SignalsReader.groupRecordsByKey(allSignals);
	  SignalsReader.createSignalsFile(keyedSignals, mahoutDataFile);
	  
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
	
	private static Options createOptions() {
	  options = new Options();
	  Option signalsFile = new Option("output", true, "path to the output file, mandatory");
	  signalsFile.setRequired(true);
	  Option viewsFolder = new Option("views", true, "folder containing the views log files");
	  Option downladsFolder = new Option("downloads", true, "folder containing the downloads log files");
	  options.addOption(signalsFile);
	  options.addOption(viewsFolder);
	  options.addOption(downladsFolder);
	  return options;
	  
	}
	
	private static CommandLine getCommandLine(final Options options, final String [] args) {
	  CommandLineParser parser = new DefaultParser();
	  CommandLine line = null;
    try {
      line = parser.parse(options, args);
    } 
    catch (ParseException pe) {
      System.out.println("Unable to process command line options: " + pe.getMessage());
    }
    
	  return line;
	}
	
	private static void help(final Options options) {
	  final HelpFormatter formatter = new HelpFormatter();
	  formatter.printHelp("User navigation data extractor. At least one folder containing the "
	      + "event data (e.g. views, downloads, ..) must be passed as an argument.", options);
	}
}

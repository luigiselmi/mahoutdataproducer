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
	static final String COMPARISONS_FOLDER_OPTION = "comparisons";
	static final String MAHOUT_DATA_FILE = "output";
	
	private static Options options;
	
	public static void main(String[] args) throws IOException, TasteException {
	  
	  Options options = createOptions();
	  CommandLine cmd = getCommandLine(options, args);
	  String mahoutFileName = cmd.getOptionValue(MAHOUT_DATA_FILE);
	  File mahoutDataFile = new File(mahoutFileName);
	  // at least one type of signals (views, downloads, comparisons) must be available
	  if(! (cmd.hasOption(VIEWS_FOLDER_OPTION) | 
	        cmd.hasOption(DOWNLOADS_FOLDER_OPTION) | 
	        cmd.hasOption(COMPARISONS_FOLDER_OPTION))) {
      help(options);
      System.exit(0);
    }
	  
	  List<SignalRecord> allSignals = new ArrayList<SignalRecord>();
	  ApplicationConfig config = readConfiguration();
	  SignalsReader reader = new SignalsReader(config);
	  
	  if(cmd.hasOption(VIEWS_FOLDER_OPTION)) {
	    File dir = new File(cmd.getOptionValue(VIEWS_FOLDER_OPTION));
	    // read the views log files
	    List<SignalRecord> viewsRecords = reader.readViewsFiles(dir);
	    allSignals.addAll(viewsRecords);
	  }
	  
	  if(cmd.hasOption(DOWNLOADS_FOLDER_OPTION)) {
      File dir = new File(cmd.getOptionValue(DOWNLOADS_FOLDER_OPTION));
      // read the downloads log files
      List<SignalRecord> downloadsRecords = reader.readDownloadsFiles(dir);
      allSignals.addAll(downloadsRecords);
    }
	  
	  if(cmd.hasOption(COMPARISONS_FOLDER_OPTION)) {
      File dir = new File(cmd.getOptionValue(COMPARISONS_FOLDER_OPTION));
      // read the comparisons log files
      List<SignalRecord> comparisonsRecords = reader.readComparisonsFiles(dir);
      allSignals.addAll(comparisonsRecords);
    }
	  
	  // Aggregate signals, i.e. records with same pair userID and itemID. 
	  // The total value is the sum of all the values
	  List<SignalRecord> keyedSignals = reader.groupRecordsByKey(allSignals);
	  
	  // normalize the values in the signals file
	  List<SignalRecord> normalizedSignals = reader.normalizeList(keyedSignals);
	  
	  // save signals in a file
	  reader.createSignalsFile(normalizedSignals, mahoutDataFile);
	  
	}
	
	private static Options createOptions() {
	  options = new Options();
	  Option signalsFile = new Option("output", true, "path to the output file, mandatory");
	  signalsFile.setRequired(true);
	  Option viewsFolder = new Option(VIEWS_FOLDER_OPTION, true, "folder containing the views log files");
	  Option downladsFolder = new Option(DOWNLOADS_FOLDER_OPTION, true, "folder containing the downloads log files");
	  Option comparisonsFolder = new Option(COMPARISONS_FOLDER_OPTION, true, "folder containing the comparisons log files");
	  options.addOption(signalsFile);
	  options.addOption(viewsFolder);
	  options.addOption(downladsFolder);
	  options.addOption(comparisonsFolder);
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
	
	/**
   * Reads the configuration file
   * @return
   * @throws IOException
   */
  
  private static ApplicationConfig readConfiguration() throws IOException {
    ApplicationConfig config = new ApplicationConfig();
    Properties prop = new Properties();
    InputStream configIs = JSONTransformer.class.getClassLoader().getResourceAsStream("config.properties");
    prop.load(configIs);
    double maxValue = Double.parseDouble(prop.getProperty("value.max"));
    double downloadValue = Double.parseDouble(prop.getProperty("value.download"));
    double viewValue = Double.parseDouble(prop.getProperty("value.view"));
    double comparisonValue = Double.parseDouble(prop.getProperty("value.comparison"));
    config.setMaxValue(maxValue);
    config.setValueDownload(downloadValue);
    config.setValueView(viewValue);
    config.setValueComparison(comparisonValue);
    return config;
  }
}

package de.fraunhofer.cortex.logs.atn;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.mahout.cf.taste.common.TasteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.cortex.recommender.model.SignalsDataModel;


public class Main {
	
  static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
  static final String VIEWS_FOLDER_OPTION = "views";
  static final String DOWNLOADS_FOLDER_OPTION = "downloads";
  static final String COMPARISONS_FOLDER_OPTION = "comparisons";
  static final String SIGNALS_FILE_OPTION = "output";
  static final String LOGS_FILTER_OPTION = "filter";
	
  private static Options options;
	
  public static void main(String[] args) throws IOException {
	  
    Options options = createOptions();
	CommandLine cmd = getCommandLine(options, args);
	String signalsFileName = cmd.getOptionValue(SIGNALS_FILE_OPTION);
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
	IOFileFilter logFilter = getLogsFilter(cmd.getOptionValue(LOGS_FILTER_OPTION));
	  
	if(cmd.hasOption(VIEWS_FOLDER_OPTION)) {
	  File dir = new File(cmd.getOptionValue(VIEWS_FOLDER_OPTION));
	  // read the views log files
	  if(dir.exists()) {
		  List<SignalRecord> viewsRecords = reader.parseRecursivelyViewFiles(dir, logFilter);
		  allSignals.addAll(viewsRecords);
	  }
	  else {
		  LOG.error("The folder " + cmd.getOptionValue(VIEWS_FOLDER_OPTION) + " doesn't exist.");
	  }
	}
	  
	if(cmd.hasOption(DOWNLOADS_FOLDER_OPTION)) {
      File dir = new File(cmd.getOptionValue(DOWNLOADS_FOLDER_OPTION));
      // read the downloads log files
      if(dir.exists()) {
    	  List<SignalRecord> downloadsRecords = reader.parseRecursivelyDownloadFiles(dir, logFilter);
    	  allSignals.addAll(downloadsRecords);
      }
      else {
		  LOG.error("The folder " + cmd.getOptionValue(DOWNLOADS_FOLDER_OPTION) + " doesn't exist.");
	  }
    }
	  
	if(cmd.hasOption(COMPARISONS_FOLDER_OPTION)) {
      File dir = new File(cmd.getOptionValue(COMPARISONS_FOLDER_OPTION));
      // read the comparisons log files
      if(dir.exists()) {
    	  List<SignalRecord> comparisonsRecords = reader.parseRecursivelyComparisonFiles(dir, logFilter);
    	  allSignals.addAll(comparisonsRecords);
      }
      else {
		  LOG.error("The folder " + cmd.getOptionValue(COMPARISONS_FOLDER_OPTION) + " doesn't exist.");
	  }
    }
	  
	// Aggregate signals, i.e. records with same pair userID and itemID. 
	// The total value is the sum of all the values
	List<SignalRecord> keyedSignals = reader.groupRecordsByKey(allSignals);
	
	int numItemIDs = reader.getNumItemIDs(keyedSignals);
	int numUserIDs = reader.getNumUserIDs(keyedSignals);
	LOG.info("Total number of item IDs: " + numItemIDs);
	LOG.info("Total number of user IDs: " + numUserIDs);
	LOG.info("Number of signals (unique userID and itemID): " + keyedSignals.size());
	  
	// normalize the values in the signals file
	List<SignalRecord> normalizedSignals = reader.normalizeList(keyedSignals);
	  
	// save the signals in a file
	reader.createSignalsFile(normalizedSignals, signalsFileName);
	  
	
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
	  Option logsFilter = new Option(LOGS_FILTER_OPTION, true, "filter out the users' log files whose name is lower");
	  options.addOption(logsFilter);
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
    InputStream configIs = Main.class.getClassLoader().getResourceAsStream("config.properties");
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
  /**
   * Returns a filter instance from the option specified as an argument.
   * The filter should be an integer of the form yyyymmdd (e.g. 20180509)
   * The methods doesn't check the format of the filter passed as argument.
   * @param strFilter
   * @return
   */
  private static IOFileFilter getLogsFilter(String strFilter) {
	  IOFileFilter filter = TrueFileFilter.INSTANCE;
	  if(strFilter != null | !"".equals(strFilter)) {
		  try {
			  int filterValue = Integer.parseInt(strFilter);
			  filter = new LogFileFilter(filterValue);
		  }
		  catch(NumberFormatException nfe) {
			  LOG.error("The filter " + strFilter + " is not valid and will not be used.");
		  }
	  }
	  else {
		  LOG.info("No filter has been passed. All the files in the folders will be used.");
	  }
	  return filter;
  }
  /**
   * Reads from a file, in the project root folder, the filter to be applied to the user log files. 
   * The filter must be an integer of the form yyyymmdd, e.g. 20180508.
   * If the file containing the filter doesn't exist it is created 
   * with the current date in it.
   * @param filterFileName
   * @return the filter to be used for the user log files.
 * @throws IOException 
   */
  private static int getFilterFileValue(String filterFileName) throws IOException {
	int filterValue;
	File filterFile = new File(filterFileName);
	if(filterFile.exists()) {
	 String filter = FileUtils.readFileToString(filterFile, "UTF-8");
	 filterValue = Integer.parseInt(filter);
	}
	else {
	 String filter = createFilter();
	 filterValue = Integer.parseInt(filter);
	 FileUtils.writeStringToFile(filterFile, filter, "UTF-8");
	}
	  
	return filterValue;
  }
  
  /**
   * Returns the current day as string with the format yyyymmdd.
   * @return
   */
  private static String createFilter() {
	LocalDate currentDate = LocalDate.now();
	String year = Integer.toString(currentDate.getYear());
	int month = currentDate.getMonthValue();
	String strMonth = (month < 10 ) ? "0" + Integer.toString(month): Integer.toString(month);
	int day = currentDate.getDayOfMonth();
	String strDay = (day < 10 ) ? "0" + Integer.toString(day): Integer.toString(day);
	return year + strMonth + strDay;
	  
  }
}

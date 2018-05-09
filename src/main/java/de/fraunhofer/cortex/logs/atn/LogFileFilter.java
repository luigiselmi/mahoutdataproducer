package de.fraunhofer.cortex.logs.atn;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
/**
 * This class filters the files whose name is a number 
 * of the format yyyymmdd (e.g. 20180512)
 * @author luigi
 *
 */
public class LogFileFilter implements IOFileFilter {
	
	private int filter;
	private int higherValue = 0;
	private final int FILTER_LENGTH = 8;
	
	public LogFileFilter(int filter) {
		this.filter = filter;
	}

	/**
	 * In order to be parsed the file name when transformed into an integer
	 * must be greater than the value of the filter.
	 */
	@Override
	public boolean accept(File logfile) {
		String logFileName = logfile.getName().substring(0, FILTER_LENGTH);
		int fileNameValue = Integer.parseInt(logFileName);
		boolean acceptFile = (fileNameValue >= filter) ? true: false;
		return acceptFile;
	}

	
	@Override
	public boolean accept(File dir, String logFileName) {
		int fileNameValue = Integer.parseInt(logFileName.substring(0, FILTER_LENGTH));
		boolean acceptFile = (fileNameValue >= filter) ? true: false;
		return acceptFile;
	}

}

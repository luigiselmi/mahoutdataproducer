package de.fraunhofer.cortex.atn.logs;

import java.io.File;

public class ApplicationConfig {
  
  private String userLogsFolder = null;
  
  private File signalsFile = null;
  
  
  public File getSignalsFile() {
    return signalsFile;
  }
  public void setSignalsFile(File signalsFile) {
    this.signalsFile = signalsFile;
  }
  
  public String getUserLogsFolder() {
    return userLogsFolder;
  }
  
  public void setUserLogsFolder(String userLogsFolder) {
    this.userLogsFolder = userLogsFolder;
  }
  
}

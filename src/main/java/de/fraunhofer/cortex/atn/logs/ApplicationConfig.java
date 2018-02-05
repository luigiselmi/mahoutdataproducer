package de.fraunhofer.cortex.atn.logs;

import java.io.IOException;

public class ApplicationConfig {
  
  // Max value for each record in the signals file
  private double maxValue = 100.0;
  //Values for each event
  private double valueDownload = 1.0;
  private double valueView = 1.0;
  private double valueComparison = 1.0;
 
  public double getMaxValue() {
    return maxValue;
  }
  public void setMaxValue(double maxValue) {
    this.maxValue = maxValue;
  }
  public double getValueDownload() {
    return valueDownload;
  }
  public void setValueDownload(double valueDownload) {
    this.valueDownload = valueDownload;
  }
  public double getValueView() {
    return valueView;
  }
  public void setValueView(double valueView) {
    this.valueView = valueView;
  }
  public double getValueComparison() {
    return valueComparison;
  }
  public void setValueComparison(double valueComparison) {
    this.valueComparison = valueComparison;
  }
  
  
}

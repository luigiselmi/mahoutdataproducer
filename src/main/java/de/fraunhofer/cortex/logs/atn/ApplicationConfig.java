package de.fraunhofer.cortex.logs.atn;

import java.io.IOException;

public class ApplicationConfig {
  
  // Max value for each record in the signals file
  private double maxValue = 5.0;
  //Min value for each record in the signals file
  private double minValue = 1.0;
 
  //Values for each event
  private double valueDownload = 4.0;
  private double valueView = 3.5;
  private double valueComparison = 4.0;
 
  public double getMaxValue() {
    return maxValue;
  }
  public void setMaxValue(double maxValue) {
    this.maxValue = maxValue;
  }
  public double getMinValue() {
    return minValue;
  }
  public void setMinValue(double minValue) {
    this.minValue = minValue;
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

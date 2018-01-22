package de.fraunhofer.cortex.atn.logs;


public class SignalRecord implements Comparable<SignalRecord> {
  
  long userID;
  String atnItemID;
  double value;
  String key;
  
  public SignalRecord(long userID, String atnItemID, double value) {
    if(userID < 0 || atnItemID == null)
      throw new NullPointerException();
    this.userID = userID;
    this.atnItemID = atnItemID;
    this.value = value;
    this.key = Long.toString(userID) + atnItemID;
  }
  
  public long getUserID() {
    return userID;
  }
  
  public String getAtnItemID() {
    return atnItemID;
  }
  
  public double getValue() {
    return value;
  }
  
  public void setValue(double value) {
    this.value = value;
  }
  
  public String getKey() {
    return key;
  }
  
  @Override
  public String toString() {
    return Long.toString(userID) + atnItemID;
  }
  
  /**
   * Two signals are equals when they have the same userID and itemID
   */
  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof SignalRecord))
      return false;
    SignalRecord s = (SignalRecord)obj;
    return (s.getUserID() == userID) && s.getAtnItemID().equals(atnItemID);
  }
  
  public int compareTo(SignalRecord s) {
    return key.compareTo(s.getKey());
  }
  
}

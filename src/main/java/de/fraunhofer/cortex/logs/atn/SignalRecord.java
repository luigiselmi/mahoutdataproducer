package de.fraunhofer.cortex.logs.atn;


public class SignalRecord implements Comparable<SignalRecord> {
  
  long userID;
  String atnItemID;
  double value;
  String family; //additional info for content-based filtering
  String style; //additional info for content-based filtering
  String familyPath; //additional info for content-based filtering
  String componentType; // one of family or familyPath
  String key;
  
  public SignalRecord(long userID, String atnItemID) {
    if(userID < 0 || atnItemID == null)
      throw new NullPointerException();
    this.userID = userID;
    this.atnItemID = atnItemID;
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
  
  public String getFamily() {
	return family;
  }

  public void setFamily(String family) {
	this.family = family;
  }

  public String getStyle() {
	return style;
  }

  public void setStyle(String style) {
	this.style = style;
  }
  
  public String getFamilyPath() {
	return familyPath;
  }

  public void setFamilyPath(String familyPath) {
	this.familyPath = familyPath;
  }
  
  public String getComponentType() {
	return componentType;
  }

  public void setComponentType(String componentType) {
	this.componentType = componentType;
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

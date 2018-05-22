package de.fraunhofer.cortex.logs.atn;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONParser {
	
  static final Logger LOG = LoggerFactory.getLogger(JSONParser.class);
  private ApplicationConfig config;
	
  public JSONParser(ApplicationConfig config) throws IOException {
	  this.config = config;
  }
	
  /**
   * Parse the json log data of a component's details seen by a user  	
   * @param line
   * @return
   */
  public SignalRecord parseViews(String line) {
	  JsonReader jsonReader = Json.createReader(new StringReader(line));
	  JsonObject json = jsonReader.readObject(); 
	  // userid
	  JsonObject user = json.get("user").asJsonObject();
	  long userID = Long.parseLong(user.getString("userId"));
      // itemid
      JsonObject originalComponent = json.get("originalComponet").asJsonObject();
      String atnItemID = originalComponent.getString("componentId");
      //String family = originalComponent.getString("family");
      //String style = originalComponent.getString("style");
      String familyPath = originalComponent.getString("familyPath");
      String componentType;
      if(familyPath.contains("/")) {
        String [] hierarchy = familyPath.split("/");
        componentType = hierarchy[hierarchy.length - 1];
      }
      else {
    	  componentType = familyPath;
      }
	  // value
      double value = config.getValueView();
      SignalRecord signal = new SignalRecord(userID, atnItemID);
      signal.setValue(value);
      // component type
      signal.setComponentType(componentType.trim()); // use the last category in the hierarchy
      
	  return signal;
	}
	
    /**
     * Parse the json log data of a component's document downloaded by a user
     */
	public SignalRecord parseDownloads(String line) {
      JsonReader jsonReader = Json.createReader(new StringReader(line));
      JsonObject json = jsonReader.readObject();
      JsonValue time = json.get("time");
      JsonValue sessionId = json.get("sessionId");
      JsonObject component = json.getJsonObject("componet");
      String atnItemID = component.getString("componetId");
      String family = component.getString("family");
      String componentType;
      if(family.contains("/")) {
    	  String [] hierarchy = family.split("/");
    	  componentType = hierarchy[0];
      }
      else {
    	  componentType = family;
      }
      JsonObject user = json.get("user").asJsonObject();
      long userID = Long.parseLong(user.getString("userId"));
      // value
      double value = config.getValueDownload();
      SignalRecord signal = new SignalRecord(userID, atnItemID);
      signal.setValue(value);
      // component type
      signal.setComponentType(componentType.trim());
      
      return signal;
  }
  /**
   * Parse the json log data of a user's comparison of two or more components 	
   * @param line
   * @return
   */
  public List<SignalRecord> parseComparisons(String line) {
    List<SignalRecord> signals = new ArrayList<SignalRecord>();
    JsonReader jsonReader = Json.createReader(new StringReader(line));
    JsonObject json = jsonReader.readObject();
    JsonValue time = json.get("time");
    JsonValue sessionId = json.get("sessionId");
    JsonObject user = json.get("user").asJsonObject();
    long userID = Long.parseLong(user.getString("userId"));
    JsonArray components = json.getJsonArray("components");
    for(int i = 0; i < components.size(); i++) {
      String atnItemId = null;
      String familyPath;
      String componentType = "Not Available";
      JsonObject component = components.get(i).asJsonObject();
      // extracts the component ID
      if(component.containsKey("componentId")) {
        atnItemId = component.getString("componentId");
      }
      // extracts the highest family 
      if(component.containsKey("familyPath")) {
        familyPath = component.getString("familyPath");
        if(familyPath.contains("/")) {
    	  String [] hierarchy = familyPath.split("/");
    	  componentType = hierarchy[hierarchy.length - 1];
        }
        else {
    	  componentType = familyPath;
        }
      }
      double value = config.getValueComparison();
      SignalRecord signal = new SignalRecord(userID, atnItemId);
      signal.setValue(value);
      signal.setComponentType(componentType.trim());
      signals.add(signal);
    }
    
    return signals;
  }
  
}

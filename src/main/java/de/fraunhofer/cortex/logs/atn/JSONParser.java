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
	
	
  public SignalRecord parseViews(String line) {
	  JsonReader jsonReader = Json.createReader(new StringReader(line));
	  JsonObject json = jsonReader.readObject(); 
	  // userid
	  JsonObject user = json.get("user").asJsonObject();
	  long userID = Long.parseLong(user.getString("userId"));
      // itemid
      JsonObject originalComponent = json.get("originalComponet").asJsonObject();
      String atnItemID = originalComponent.getString("componentId");
	  // value
      double value = config.getValueView();
      SignalRecord signal = new SignalRecord(userID, atnItemID, value);
	  return signal;
	}
	
	public SignalRecord parseDownloads(String line) {
      JsonReader jsonReader = Json.createReader(new StringReader(line));
      JsonObject json = jsonReader.readObject();
      JsonValue time = json.get("time");
      JsonValue sessionId = json.get("sessionId");
      JsonObject component = json.getJsonObject("componet");
      String atnItemID = component.getString("componetId");
      JsonValue family = component.get("family");
      JsonObject user = json.get("user").asJsonObject();
      long userID = Long.parseLong(user.getString("userId"));
      // value
      double value = config.getValueDownload();
      SignalRecord signal = new SignalRecord(userID, atnItemID, value);
      return signal;
  }
	
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
      JsonObject component = components.get(i).asJsonObject();
      String atnItemId = component.getString("componentId");
      //JsonValue family = component.get("family");
      double value = config.getValueComparison();
      SignalRecord signal = new SignalRecord(userID, atnItemId, value);
      signals.add(signal);
    }
    
    return signals;
  }
	
}

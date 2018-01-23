package de.fraunhofer.cortex.atn.logs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONTransformer {
	
	static final Logger LOG = LoggerFactory.getLogger(JSONTransformer.class);
	
	public static SignalRecord parseViews(String line) {
	  JsonReader jsonReader = Json.createReader(new StringReader(line));
	  JsonObject json = jsonReader.readObject(); 
	  // userid
	  JsonObject user = json.get("user").asJsonObject();
	  long userID = Long.parseLong(user.getString("userId"));
    // itemid
    JsonObject originalComponent = json.get("originalComponet").asJsonObject();
    String atnItemID = originalComponent.getString("componentId");
	  // value
    double value = 1.0;
    SignalRecord signal = new SignalRecord(userID, atnItemID, value);
	  return signal;
	}
	
	public static SignalRecord parseDownloads(String line) {
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
    double value = 1.0;
    SignalRecord signal = new SignalRecord(userID, atnItemID, value);
    return signal;
  }
	
  public static List<SignalRecord> parseComparisons(String line) {
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
      double value = 1.0;
      SignalRecord signal = new SignalRecord(userID, atnItemId, value);
      signals.add(signal);
    }
    
    return signals;
  }
	
	public void printFile(String filePath) throws MalformedURLException, IOException {
		InputStream in = new URL( filePath ).openStream();
		 try {
		   InputStreamReader inR = new InputStreamReader( in );
		   BufferedReader buf = new BufferedReader( inR );
		   String line;
		   while ( ( line = buf.readLine() ) != null ) {
		     System.out.println( line );
		   }
		 } finally {
		   in.close();
		 }
	}

}

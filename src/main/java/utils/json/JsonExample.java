package utils.json;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.json.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class JsonExample {

	private static String localpath = "src/main/resources/";

	public static void main(String[] args) {
		// I'm using JsonParser from com.google.gson
		JsonParser json = new JsonParser();
		Reader reader = null;
		try {
			// open file
			reader = new InputStreamReader(new FileInputStream(localpath + "example.json"));
		} catch (FileNotFoundException e1) {
			System.out.println("File not found.");
			System.out.println(e1.getMessage());
		}
		// json from the json file located in src/main/resources
		JsonElement jsonElemFromFile = json.parse(new JsonReader(reader));

		// validate if is a valid json object
		if (jsonElemFromFile.isJsonObject()) {
			System.out.println("Json is valid");
			JsonObject obj = jsonElemFromFile.getAsJsonObject();
			System.out.println(obj);

			// get elements from json, using every available method in gson for data type
			System.out.println(obj.get("String").getAsString());
			System.out.println(obj.get("Number").getAsInt());
			System.out.println(obj.get("Boolean").getAsBoolean());
			System.out.println(obj.get("Null").getAsJsonNull());
			System.out.println(obj.get("Array").getAsJsonArray().get(0));
			System.out.println(obj.get("Array").getAsJsonArray().get(1));
			System.out.println(obj.get("Object").getAsJsonObject().get("first_child"));
			// get nested element
			System.out.println(obj.get("Object").getAsJsonObject().get("second_child").getAsJsonObject().get("level"));
		}

		// json from / to string using com.google.gson
		JsonElement jsonfromStringUsingGSON = json.parse("{\"String\": \"this is a string\",\"Number\": 12345}");
		System.out.println(jsonfromStringUsingGSON.toString());

		// json from / to string using org.json
		JSONObject jsonObjectUsingOrgJson = new JSONObject("{\"String\": \"this is a string\",\"Number\": 12345}");
		System.out.println(jsonObjectUsingOrgJson.getString("String"));
		System.out.println(jsonObjectUsingOrgJson.getInt("Number"));
		System.out.println(jsonObjectUsingOrgJson.toString());
		
		// convert to XML using org.json.XML class
		String xml = XML.toString(jsonObjectUsingOrgJson, "root"); // xml needs a root element
		System.out.println(xml);

	}
}
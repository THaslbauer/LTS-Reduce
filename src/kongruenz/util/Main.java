package kongruenz.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

public class Main {

	/**
	 * Demonstrates how to parse an LTS string.
	 * Writes information about the LTS to standarf output.
	 */
	public static void ltsParsingDemo() {
		// we'll parse this string:
		// {"initialState":"a.b.(0 | 0) + (c?.0 | c!.0) \\ {c}","states":{"a.b.(0 | 0) + (c?.0 | c!.0) \\ {c}":{"transitions":[{"label":"a","detailsLabel":false,"target":"b.(0 | 0) \\ {c}"},{"label":"τ","detailsLabel":"c","target":"0 | 0 \\ {c}"}]},"b.(0 | 0) \\ {c}":{"transitions":[{"label":"b","detailsLabel":false,"target":"0 | 0 \\ {c}"}]},"0 | 0 \\ {c}":{"transitions":[]}}}
		// note that " and \ have to be escaped with \ below so they represent
		// this string in Java
		// see the source CCS term online:
		// http://pseuco.com/#/edit/remote/dzsk0qbxnbcm2mcumw5i
		String encodedLts = "{\"initialState\":\"a.b.(0 | 0) + (c?.0 | c!.0) \\\\ {c}\",\"states\":{\"a.b.(0 | 0) + (c?.0 | c!.0) \\\\ {c}\":{\"transitions\":[{\"label\":\"a\",\"detailsLabel\":false,\"target\":\"b.(0 | 0) \\\\ {c}\"},{\"label\":\"τ\",\"detailsLabel\":\"c\",\"target\":\"0 | 0 \\\\ {c}\"}]},\"b.(0 | 0) \\\\ {c}\":{\"transitions\":[{\"label\":\"b\",\"detailsLabel\":false,\"target\":\"0 | 0 \\\\ {c}\"}]},\"0 | 0 \\\\ {c}\":{\"transitions\":[]}}}";

		JsonObject ltsObject = Json.createReader(new StringReader(encodedLts))
				.readObject();

		String initialStateLabel = ltsObject.getString("initialState");

		System.out.println("The initial state is \"" + initialStateLabel
				+ "\".");

		JsonObject statesObject = ltsObject.getJsonObject("states");

		for (String state : statesObject.keySet()) {

			JsonObject stateObject = statesObject.getJsonObject(state);

			JsonArray transitionsArray = stateObject
					.getJsonArray("transitions");

			for (int i = 0; i < transitionsArray.size(); i++) {

				JsonObject transition = transitionsArray.getJsonObject(i);

				String label = transition.getString("label");

				// detailsLabel contains additional information
				// e.g.: which actions were synced and created the tau transition
				// This property is optional, so it might not exists.
				// You can ignore it for the project.
				// However, if you are adventurous, you can keep it
				// and add some helpful detailsLabels to the transitions in your output 
				String detailsLabel = null;
				try {
					detailsLabel = transition.getString("detailsLabel");
				} catch (ClassCastException e) {
					// ignore - detailsLabel = null
				}

				String target = transition.getString("target");

				System.out.println("Saw a transition from \""
						+ state
						+ "\" with \""
						+ label
						+ "\""
						+ (detailsLabel != null ? " [\"" + detailsLabel + "\"]"
								: "") + " to \"" + target + "\".");

			}

		}
	}

	
	/**
	 * Demonstrates how to serialize a LTS.
	 * Builds a JSON representation of a hardcoded transition system.
	 * 
	 * @return the JSON representation
	 */
	public static JsonObject ltsSerializationDemo() {
		// we'll try to construct this LTS:
		// 
		//   -------τ-------\
		//  /                v
		// 1 --a--> 2 --τ--> 3
		
		JsonObject statesObject = Json.createObjectBuilder()
				.add("1", Json.createObjectBuilder().add("transitions", Json.createArrayBuilder()
						.add(Json.createObjectBuilder().add("label", "a").add("detailsLabel", false).add("target", "2").build())
						.add(Json.createObjectBuilder().add("label", "τ").add("detailsLabel", false).add("target", "3").build())
						.build()).build())
				.add("2", Json.createObjectBuilder().add("transitions", Json.createArrayBuilder()
						.add(Json.createObjectBuilder().add("label", "τ").add("detailsLabel", false).add("target", "3").build())
						.build()).build())
				.add("3", Json.createObjectBuilder().add("transitions", Json.createArrayBuilder()
						// no transitions
						.build()).build())
				.build();
		
		JsonObject ltsObject = Json.createObjectBuilder()
				.add("initialState", "1")
				.add("states", statesObject)
				.build();
		
		System.out.println(ltsObject.toString());
		return ltsObject;
	}

	
	/**
	 * Demonstrates how to open a LTS in pseuCo.com
	 * 
	 * @param data
	 * 		The LTS as a JSON object.
	 */
	public static void openInBrowserDemo(JsonObject data) {
		PseuCoShare sharer = new PseuCoShare();
		
		try {
			System.out.print("Submitting file...");
			sharer.submitAndOpenLts(data);
			System.out.println(" Done.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Reads the standard input.
	 * 
	 * @return The text entered on standard input, without newlines.
	 * @throws IOException
	 */
	public static String readStandardInput() throws IOException {
	    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	    
	    StringBuilder builder = new StringBuilder();
	    
	    String s;
	    while ((s = in.readLine()) != null && s.length() != 0) { // read until end or empty line
	    	// reading this way strips empty lines, but they are not needed anyway
	    	builder.append(s);
	    }
	
		return builder.toString(); 
	}
	
	
	/*public static void main(String[] args) throws IOException {
		if (args.length == 1 && args[0].equals("-i")) {
			// started with command line argument -i
			
			// read the input
			String input = readStandardInput();
			
			// TODO do the minimization
			String output = input; // this may not write to standard output!
			
			// output the result on standard output
			System.out.println(output);
		} else {
			// other command line arguments
			
			ltsParsingDemo();
			openInBrowserDemo(ltsSerializationDemo());
		}		
	}*/

}

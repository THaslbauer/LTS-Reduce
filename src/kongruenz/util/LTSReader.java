package kongruenz.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import kongruenz.LTS;
import kongruenz.objects.LabeledEdge;
import kongruenz.objects.Vertex;


/**This class provides 
 * 
 * */
public class LTSReader {

	private String input;
	private LTS output;

	/**
	 * Translates the input String into an LTS
	 * 
	 * @return The LTS represented by the given input String[]
	 * @author Jere
	 * 
	 * 
	 * */
	public LTS generateLTSfromJSON() {

		if (output != null)
			return output;
		
		// -----------------------------------------------------------------//
		// --------------Variables used in this method----------------------//
		
		String LTSText = input;
		Set<Vertex> allstates = new HashSet<Vertex>();
		Set<LabeledEdge> transitions = new HashSet<LabeledEdge>();

		// ----------------------------------------------------------------------//
		// --------------------Initialize Object and get the initial State-----------//

		JsonObject lts = Json.createReader(new StringReader(LTSText))
				.readObject();

		Vertex start = new Vertex(lts.getString("initialState"));

		// ---------------------------------------------------------------------------------------------------//
		// --------------------gather states and transitions in order to add them to their respective Set--------//

		JsonObject states = lts.getJsonObject("states");

		for (String state : states.keySet()) {

			allstates.add(new Vertex(state));

			JsonObject stateObject = states.getJsonObject(state);
			JsonArray transitionsArray = stateObject
					.getJsonArray("transitions");

			for (int i = 0; i < transitionsArray.size(); i++) {

				JsonObject transition = transitionsArray.getJsonObject(i);

				String label = transition.getString("label");
				String target = transition.getString("target");

				transitions.add(new LabeledEdge(state, target, label));
			}

		}

		output = new LTS(allstates, transitions, start);
		return output;
	}

	/**
	 * This method is needed to circumvent the issues with symbols which need
	 * escaping, that one runs into when using the args array. Due to the fact
	 * that pseuCo adds a few empty lines to every JSON export, this method
	 * skips these lines and jumps straight to the important text part.
	 * 
	 * @return returns the String
	 * @throws UnsupportedEncodingException  in case UTF-8 is not supported
	 * */
	public static String getString() throws UnsupportedEncodingException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));

		String str = "";
		while (str.equals("")) {
			try {
				str = br.readLine();
			} catch (IOException e) {
				throw new IllegalArgumentException();
			}
		}

		return str;

	}

	// -------------------------------------------------------------------//
	// -----------Getters, Setters and Constructor -----------------------//
	// -------------------------------------------------------------------//

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public void setOutput(LTS output) {
		this.output = output;
	}

	public LTSReader(String input) {

		this.input = input;
		output = null;

	}

}

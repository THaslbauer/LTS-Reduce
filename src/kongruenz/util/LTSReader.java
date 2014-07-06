package kongruenz.util;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import kongruenz.LTS;
import kongruenz.objects.LabeledEdge;
import kongruenz.objects.Vertex;

public class LTSReader {

	
	private String[] input;
	private LTS output;

	
	/**
	 * Translates the input String[] into an LTS
	 * 
	 * @return The LTS represented by the given input String[]
	 * 
	 * 
	 * 
	 * */
	
	public LTS getOutput() {
		
		
		if(output != null)
			return output;
		
		
		
		if (!input[0].equals("-i")) {
			
			throw new IllegalArgumentException("Not an LTS");
		}
		
		//-----------------------------------------------------------------//
		//--------------Variables used in this method----------------------//
		
		String LTSText = new String();
		Set<Vertex> allstates = new HashSet<Vertex>();
		Set<LabeledEdge> transitions = new HashSet<LabeledEdge>();
		
		//-------------------------------------------------------------------//
		//--------------Concat input in order to get one big string----------//
		
		for (int i = 1 ; i < input.length ; i++){
			
			LTSText = LTSText + " " + input[i];
		}
		
		
		//----------------------------------------------------------------------//
		//--------------------Initialize Object and get initial State-----------//
		
		JsonObject lts = Json.createReader(new StringReader(LTSText)).readObject();
		
		Vertex start = new Vertex(lts.getString("initialState"));
		
		
		//---------------------------------------------------------------------------------------------------//
		//--------------------gather states and transitions in order to add them to the respective Set--------//
		
		JsonObject states = lts.getJsonObject("states");
		
		for (String state : states.keySet()) {
			
			allstates.add( new Vertex(state));
			
			JsonObject stateObject = states.getJsonObject(state);
			JsonArray transitionsArray = stateObject.getJsonArray("transitions");
			
			for (int i = 0; i < transitionsArray.size() ; i++){
				
				JsonObject transition = transitionsArray.getJsonObject(i);
				
				String label = transition.getString("label");
				String target = transition.getString("target");
				
				transitions.add(new LabeledEdge(state, target, label));
			}
			
			
			
		}
		
		output = new LTS(allstates, transitions, start);
		return output;
	}
	
	//-----------Getters, Setters and Constructor -----------------------//
	
	
	public String[] getInput() {
		return input;
	}


	public void setInput(String[] input) {
		this.input = input;
	}


	public void setOutput(LTS output) {
		this.output = output;
	}

	

	public LTSReader(String[] input){
		
		this.input = input;
		output = null;
		
	}
	
	
}

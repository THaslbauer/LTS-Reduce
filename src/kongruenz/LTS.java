package kongruenz;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import kongruenz.objects.Action;
import kongruenz.objects.Vertex;
import kongruenz.objects.LabeledEdge;

/** 
 * Basic implementation of a LTS, extends a Graph.
 * @author Thomas
 *
 */
public class LTS extends Graph{
	private Set<Action> act;
	final private Vertex start;

	/**
	 * Constructor expects the standard arguments for an LTS
	 * @param states
	 * @param transitions
	 * @param start
	 */
	public LTS (Collection<Vertex> states, Collection<LabeledEdge> transitions, Vertex start) {
		super(transitions, states);
		act = new HashSet<Action>();
		this.start = start;
		for(LabeledEdge trans : this.edges){
			act.add(trans.getLabel());
		}
	}


	/**
	 * Finds out if the weak Transition with Action act connects start and end
	 * @param start
	 * @param end
	 * @param act
	 * @return
	 */
	public boolean taureachableWith(Vertex start, Vertex end, Action act){
		this.initSearch();
		if(start.equals(end) && act.equals(Action.TAU))
			return true;
		for(LabeledEdge trans : this.edgesByAction.get(act)){
			//TODO remove
			System.out.println(searcher.getPreWithTau(trans.getStart()));
			System.out.println(searcher.getPostWithTau(trans.getEnd()));
			if(trans.getStart().equals(start) && trans.getEnd().equals(end))
				return true;
			if(trans.getStart().equals(start) && searcher.getPostWithTau(trans.getEnd()).contains(end))
				return true;
			if(searcher.getPreWithTau(trans.getStart()).contains(start) && trans.getEnd().equals(end))
				return true;
			if(searcher.getPreWithTau(trans.getStart()).contains(start)&&searcher.getPostWithTau(trans.getEnd()).contains(end))
				return true;
		}
		return false;
	}
	
	/**
	 * Finds all states, that can reach the "start" state with the specified action.
	 * WARNING: the start state WILL be included if tau is the specified action.
	 * @param start
	 * @param act
	 * @return
	 */
	public Set<Vertex> weakPre(Vertex start, Action act){
		this.initSearch();
		Set<Vertex> pre = new HashSet<>();
		if(act.equals(Action.TAU)){
			pre.add(start);
			pre.addAll(searcher.getPreWithTau(start));
			return pre;
		}
		for(LabeledEdge trans : this.edgesByEnd.get(start)){
			if(trans.getLabel().equals(act)){
				pre.add(trans.getStart());
				pre.addAll(searcher.getPreWithTau(trans.getStart()));
			}
		}
		for(Vertex v : searcher.getPreWithTau(start)){
			for(LabeledEdge trans : this.edgesByEnd.get(v)){
				if(trans.getLabel().equals(act)){
					pre.add(trans.getStart());
					pre.addAll(searcher.getPreWithTau(trans.getStart()));
				}
			}
		}
		return pre;
	}

	/*
	private boolean taureachableWith(Vertex start, Vertex end, Action act, Set<Vertex> visited){
		if(reachableWith(start, end, act))
			return true;
		Set<Vertex> reach = post(start);
		boolean found = false;
		for(Vertex state : reach){
//			synchronized(visited){
			//TODO: erroneous code, what if this happens: 1 to 1 with b, 1 to 2 with tau, looking for 1 to 2 with b
				if(!(visited.contains(state))){
					if(getTransitions(start, state).contains(new LabeledEdge(start, state, Action.TAU))){
						visited.addAll(reach);
						found |= taureachableWith(state, end, act, visited);
					}
					if(getTransitions(start, state).contains(new LabeledEdge(start, state, act))){
						found |= taureachableWith(state, end, Action.TAU);
					}
				}
//			}
		}
		return found;
	}*/
	

	public Vertex getStart(){
		return start;
	}
	
	@Override
	public Set<Action>getActions(){
		act = super.getActions();
		return act;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((this.vertices == null) ? 0 : this.vertices.hashCode());
		result = prime * result
				+ ((this.edges == null) ? 0 : this.edges.hashCode());
		return result;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LTS other = (LTS) obj;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		if (this.vertices == null) {
			if (other.vertices != null)
				return false;
		} else if (!this.vertices.equals(other.vertices))
			return false;
		if (this.edges == null) {
			if (other.edges != null)
				return false;
		} else if (!this.edges.equals(other.edges))
			return false;
		return true;
	}
	
	public JsonObject ToJson(){
		
		
		JsonObjectBuilder statesBuilder = Json.createObjectBuilder();
		
		for (Vertex state : vertices) {
			
			JsonArrayBuilder transBuilder = Json.createArrayBuilder();
			
			for (LabeledEdge edge : edges){
				
				if (edge.getStart().equals(state)) {
					
					transBuilder = transBuilder.add(Json.createObjectBuilder().add("label", edge.getLabel().getAction()).add("detailsLabel", false).add("target", edge.getEnd().getName()));
				}
			}
			
			statesBuilder = statesBuilder.add(state.getName(), Json.createObjectBuilder().add("transitions",transBuilder.build()));
		}
		
		JsonObject LTS = Json.createObjectBuilder().add("initialState" , start.getName()).add("states", statesBuilder.build()).build();
		
		return LTS;
	}
	
}

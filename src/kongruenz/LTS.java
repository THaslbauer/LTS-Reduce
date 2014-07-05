package kongruenz;

import java.util.Collection;
import java.util.Set;

import kongruenz.objects.Action;
import kongruenz.objects.Vertex;
import kongruenz.objects.LabeledEdge;

public class LTS extends Graph{
	private Set<Action> act;
	final private Vertex start;

	public LTS (Collection<Vertex> states, Collection<LabeledEdge> transitions, Vertex start) {
		super(transitions, states);
		this.start = start;
		for(LabeledEdge trans : this.edges){
			act.add(trans.getLabel());
		}
	}

	public static LTS reduce(LTS lts){
		//TODO implement
		throw new UnsupportedOperationException();
	}

	
	public boolean taureachableWith(Vertex start, Vertex end, Action act){
		if (reachableWith(start, end, act))
			return true;
		Set<Vertex> reach = post(start);
		boolean found = false;
//		synchronized(reach){
			for(Vertex state : reach){
				if(!state.equals(start)) {
					if(getTransitions(start, state).contains(new LabeledEdge(start, state, Action.TAU)))
						found |= taureachableWith(state, end, act, reach);
					if(getTransitions(start, state).contains(new LabeledEdge(start, state, act)))
						found |= taureachableWith(state, end, Action.TAU, reach);
				}
			}
//		}
		return found;
	}
	
	private boolean taureachableWith(Vertex start, Vertex end, Action act, Set<Vertex> visited){
		if(reachableWith(start, end, act))
			return true;
		Set<Vertex> reach = post(start);
		boolean found = false;
		for(Vertex state : reach){
//			synchronized(visited){
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
	}
	
	public boolean bisimilarTo(LTS lts){
		//TODO implement
		throw new UnsupportedOperationException();
	}

	public Vertex getStart(){
		return start;
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
	
}

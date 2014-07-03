package bisim;

import java.util.LinkedList;
import java.util.List;

public class LTS {
	private List<State> states;
	private List<Transition> transitions;
	private State start;

	public LTS (List<State> states, List<Transition> transitions, State start) {
		this.states = states;
		this.transitions = transitions;
		this.start = start;
	}

	public static LTS reduce(LTS lts){
		//TODO implement
		throw new UnsupportedOperationException();
	}
	
	public List<State> reaches(State start){
		List<State> reach = new LinkedList<State>();
		reach.add(start);
		for(Transition trans : transitions){
			if(start.equals(trans.getStart()))
				reach.add(trans.getEnd());
		}
		return reach;
	}
	
	public boolean reachable(State start, State end){
		List<State> reach = this.reaches(start); 
		return reach.contains(end);
	}
	
	public boolean reachableWith(State start, State end, Action act){
		List<State> reach = this.reaches(start);
		if (reach.contains(end)){
			List<Transition> transitions = getTransitions();
			for(Transition trans : transitions){
				if(trans.getAction().equals(act))
					return true;
			}
		}
		if(start.equals(end) && act.equals(Action.TAU))
			return true;
		return false;
	}
	
	public boolean taureachableWith(State start, State end, Action act){
		if (reachableWith(start, end, act))
			return true;
		List<State> reach = reaches(start);
		boolean found = false;
		for(State state : reach){
			if(!state.equals(start) && getTransitions(start, state).contains(Action.TAU))
				found |= taureachableWith(state, end, act, reach);
		}
		return found;
	}
	
	private boolean taureachableWith(State start, State end, Action act, List<State> visited){
		if(reachableWith(start, end, act))
			return true;
		List<State> reach = reaches(start);
		boolean found = false;
		for(State state : reach){
			if(!(reach.contains(state))){
				if(getTransitions(start, state).contains(Action.TAU))
					found |= taureachableWith(state, end, act, reach);
				if(getTransitions(start, state).contains(act))
					found |= taureachableWith(state, end, Action.TAU);
			}
		}
		return found;
	}
	
	public List<Transition> getTransitions(State start, State end){
		List<Transition> paths = new LinkedList<Transition>();
		for(Transition trans : transitions){
			if(trans.getStart().equals(start) && trans.getEnd().equals(end))
				paths.add(trans);
		}
		return paths;
	}
	public boolean bisimilarTo(LTS lts){
		//TODO implement
		throw new UnsupportedOperationException();
	}
	public List<State> getStates() {
		return states;
	}

	public List<Transition> getTransitions() {
		return transitions;
	}

	public State getStart(){
		return start;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((states == null) ? 0 : states.hashCode());
		result = prime * result
				+ ((transitions == null) ? 0 : transitions.hashCode());
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
		if (states == null) {
			if (other.states != null)
				return false;
		} else if (!states.equals(other.states))
			return false;
		if (transitions == null) {
			if (other.transitions != null)
				return false;
		} else if (!transitions.equals(other.transitions))
			return false;
		return true;
	}
	
}

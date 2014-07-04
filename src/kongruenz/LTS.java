package kongruenz;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LTS {
	private Set<State> states;
	private Set<Transition> transitions;
	private Set<Action> act;
	private State start;

	public LTS (List<State> states, List<Transition> transitions, State start) {
		this.states = new HashSet<State>();
		if(!this.states.addAll(states))
			throw new IllegalArgumentException();
		this.transitions = new HashSet<Transition>();
		if(!this.transitions.addAll(transitions))
				throw new IllegalArgumentException();
		this.start = start;
		for(Transition trans : this.transitions){
			act.add(trans.getAction());
		}
	}

	public static LTS reduce(LTS lts){
		//TODO implement
		throw new UnsupportedOperationException();
	}
	
	public Set<State> post(State start){
		Set<State> reach = new HashSet<State>();
		reach.add(start);
		for(Transition trans : transitions){
			if(start.equals(trans.getStart()))
				reach.add(trans.getEnd());
		}
		return reach;
	}
	
	public boolean reachable(State start, State end){
		Set<State> reach = this.post(start); 
		return reach.contains(end);
	}
	
	public boolean reachableWith(State start, State end, Action act){
		Set<State> reach = this.post(start);
		if (reach.contains(end)){
			Set<Transition> transitions = getTransitions();
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
		Set<State> reach = post(start);
		boolean found = false;
//		synchronized(reach){
			for(State state : reach){
				if(!state.equals(start)) {
					if(getTransitions(start, state).contains(new Transition(start, state, Action.TAU)))
						found |= taureachableWith(state, end, act, reach);
					if(getTransitions(start, state).contains(new Transition(start, state, act)))
						found |= taureachableWith(state, end, Action.TAU, reach);
				}
			}
//		}
		return found;
	}
	
	private boolean taureachableWith(State start, State end, Action act, Set<State> visited){
		if(reachableWith(start, end, act))
			return true;
		Set<State> reach = post(start);
		boolean found = false;
		for(State state : reach){
//			synchronized(visited){
				if(!(visited.contains(state))){
					if(getTransitions(start, state).contains(new Transition(start, state, Action.TAU))){
						visited.addAll(reach);
						found |= taureachableWith(state, end, act, visited);
					}
					if(getTransitions(start, state).contains(new Transition(start, state, act))){
						found |= taureachableWith(state, end, Action.TAU);
					}
				}
//			}
		}
		return found;
	}
	
	public Set<Transition> getTransitions(State start, State follower){
		Set<Transition> paths = new HashSet<Transition>();
		for(Transition trans : transitions){
			if(trans.getStart().equals(start) && trans.getEnd().equals(follower))
				paths.add(trans);
		}
		return paths;
	}
	public boolean bisimilarTo(LTS lts){
		//TODO implement
		throw new UnsupportedOperationException();
	}
	public Set<State> getStates() {
		return states;
	}

	public Set<Transition> getTransitions() {
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

package bisim;

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
	
	public boolean reachable(State start, State end){
		//TODO implement
		throw new UnsupportedOperationException();
	}
	
	public boolean taureachable(State start, State end){
		//TODO implement
		throw new UnsupportedOperationException();
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

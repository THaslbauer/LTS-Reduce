package bisim;

import java.util.List;

public class LTS {
	private List<State> states;
	private List<Transition> transitions;
	private List<Action> act;

	public LTS (List<State> states, List<Transition> transitions, List<Action> act) {
		this.states = states;
		this.transitions = transitions;
		this.act = act;
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

	public List<Action> getAct() {
		return act;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((act == null) ? 0 : act.hashCode());
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
		if (act == null) {
			if (other.act != null)
				return false;
		} else if (!act.equals(other.act))
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

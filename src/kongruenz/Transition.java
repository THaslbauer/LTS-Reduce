package kongruenz;

public class Transition {
	final private State start;
	final private State end;
	final private Action action;
	
	public Transition(State start, State end){
		if(start == null || end == null)
			throw new IllegalArgumentException("Start and End need to be specified!");
		this.start = start;
		this.end = end;
		this.action = new Action();
	}
	
	public Transition(State start, State end, Action action){
		if(start == null || end == null)
			throw new IllegalArgumentException("Start and End need to be specified!");
		if(action == null)
			action = Action.TAU;
		this.start = start;
		this.end = end;
		this.action = action;
	}
	
	public Transition(String start, String end, String action){
		if(start == null || end == null)
			throw new IllegalArgumentException("Start and End need to be specified!");
		if(action == null)
			action = "tau";
		this.start = new State(start);
		this.end = new State(end);
		this.action = new Action(action);
	}

	public State getStart() {
		return start;
	}

	public State getEnd() {
		return end;
	}

	public Action getAction() {
		return action;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		Transition other = (Transition) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return start.toString()+" -> "+end.toString()+" with "+action.toString();
	}
	
}

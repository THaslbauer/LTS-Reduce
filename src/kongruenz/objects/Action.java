package kongruenz.objects;

public class Action {
	final private String action;
	static final public Action TAU = new Action();
	
	
	public Action(){
		this.action = "tau";
	}
	
	public Action (String name) {
		if(name == null)
			name = "tau";
		this.action = name;
	}
	
	public String getAction(){
		return this.action;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
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
		Action other = (Action) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		return true;
	}
	
}

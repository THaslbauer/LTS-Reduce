package kongruenz.objects;

/**
 * Basic implementation of the label Action for the LabeledEdges of our LTS.
 * Probably a bit overkill.
 * @author Thomas
 *
 */
public class Action {
	final private String action;
	static final public Action TAU = new Action("tau");
	
	/**
	 * Constructor, wants a name.
	 * @param name
	 * @throws IllegalArgumentException if name string was null.
	 */
	public Action (String name) {
		if(name == null)
			throw new IllegalArgumentException();
		this.action = name;
	}
	
	/**
	 * returns the name string.
	 * @return
	 */
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
	@Override
	public String toString(){
		return this.getAction();
	}
	
}

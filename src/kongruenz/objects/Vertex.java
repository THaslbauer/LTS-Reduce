package kongruenz.objects;


/**
 * A Basic representation of a Vertex as an object.
 * Just wraps a string.
 * @author Thomas
 *
 */
public class Vertex {
	final private String name;
	
	
	/**
	 * Takes name of Vertex as string.
	 * @param name
	 */
	public Vertex(String name) {
		if(name == null)
			throw new IllegalArgumentException("State needs a name!");
		this.name = name;
	}
	
	/**
	 * Returns the name.
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Vertex other = (Vertex) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}

package kongruenz.objects;


public class LabeledEdge {
	final private Vertex start;
	final private Vertex end;
	final private Action label;
	
	public LabeledEdge(Vertex start, Vertex end){
		if(start == null || end == null)
			throw new IllegalArgumentException("Start and End need to be specified!");
		this.start = start;
		this.end = end;
		this.label = new Action();
	}
	
	public LabeledEdge(Vertex start, Vertex end, Action label){
		if(start == null || end == null)
			throw new IllegalArgumentException("Start and End need to be specified!");
		if(label == null)
			label = Action.TAU;
		this.start = start;
		this.end = end;
		this.label = label;
	}
	
	public LabeledEdge(String start, String end, String action){
		if(start == null || end == null)
			throw new IllegalArgumentException("Start and End need to be specified!");
		if(action == null)
			action = "tau";
		this.start = new Vertex(start);
		this.end = new Vertex(end);
		this.label = new Action(action);
	}

	public Vertex getStart() {
		return start;
	}

	public Vertex getEnd() {
		return end;
	}

	public Action getLabel() {
		return label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
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
		LabeledEdge other = (LabeledEdge) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
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
		return start.toString()+" -> "+end.toString()+" with "+label.toString();
	}
	
}

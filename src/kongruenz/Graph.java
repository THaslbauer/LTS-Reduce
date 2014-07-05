package kongruenz;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import kongruenz.objects.Action;
import kongruenz.objects.LabeledEdge;
import kongruenz.objects.Vertex;

public abstract class Graph {
	final protected Set<LabeledEdge> edges;
	final protected Set<Vertex> vertices;
	
	public Graph(Collection<LabeledEdge> edges, Collection<Vertex> vertices) {
		this.edges = new HashSet<>(edges);
		this.vertices = new HashSet<>(vertices);
	}
	
	public Set<LabeledEdge> getEdges() {
		return Collections.unmodifiableSet(edges);
	}
	
	public Set<Vertex> getVertices() {
		return Collections.unmodifiableSet(vertices);
	}
	
	
	//TODO: look into the methods using this if removing the start vertex itself causes problems
	public Set<Vertex> post(Vertex start){
		Set<Vertex> reach = new HashSet<Vertex>();
		reach.add(start);
		for(LabeledEdge trans : this.edges){
			if(start.equals(trans.getStart()))
				reach.add(trans.getEnd());
		}
		return reach;
	}
	
	public Set<Vertex> pre(Vertex start){
		Set<Vertex> reach = new HashSet<Vertex>();
		for(LabeledEdge trans : this.edges){
			if(start.equals(trans.getEnd()))
				reach.add(trans.getStart());
		}
		return reach;
	}
	
	public boolean reachable(Vertex start, Vertex end){
		Set<Vertex> reach = this.post(start); 
		return reach.contains(end);
	}	
	
	public boolean reachableWith(Vertex start, Vertex end, Action act){
		Set<Vertex> reach = this.post(start);
		if (reach.contains(end)){
			Set<LabeledEdge> edges = getEdges();
			for(LabeledEdge trans : edges){
				if(trans.getLabel().equals(act)
						&& trans.getStart().equals(start) && trans.getEnd().equals(end))
					return true;
			}
		}
		if(start.equals(end) && act.equals(Action.TAU))
			return true;
		return false;
	}
	
	public Set<LabeledEdge> getTransitions(Vertex start, Vertex follower){
		Set<LabeledEdge> paths = new HashSet<LabeledEdge>();
		for(LabeledEdge trans : this.edges){
			if(trans.getStart().equals(start) && trans.getEnd().equals(follower))
				paths.add(trans);
		}
		return paths;
	}
}

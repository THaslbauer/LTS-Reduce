package kongruenz;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kongruenz.objects.Action;
import kongruenz.objects.LabeledEdge;
import kongruenz.objects.Vertex;

public abstract class Graph {
	final protected Set<LabeledEdge> edges;
	final protected Set<Vertex> vertices;
	final protected Map<Vertex, Set<LabeledEdge>> edgesByStart;
	final protected Map<Vertex, Set<LabeledEdge>> edgesByEnd;
	
	public Graph(Collection<LabeledEdge> edges, Collection<Vertex> vertices) {
		this.edges = new HashSet<>(edges);
		this.vertices = new HashSet<>(vertices);
		this.edgesByStart = new HashMap<>();
		this.edgesByEnd = new HashMap<>();
		//TODO Debug code
		Set<LabeledEdge> bla = new HashSet<>();
		for(Vertex vertex : vertices){
			this.edgesByStart.put(vertex, bla);
			this.edgesByEnd.put(vertex, new HashSet<LabeledEdge>());
		}
		for(LabeledEdge trans : edges){
			this.edgesByStart.get(trans.getStart()).add(trans);
			this.edgesByEnd.get(trans.getEnd()).add(trans);
		}
		//TODO Debug code
		for(Vertex key : edgesByEnd.keySet()){
			System.out.println(edgesByEnd.get(key));
		}
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
/*		for(LabeledEdge trans : this.edges){
			if(start.equals(trans.getStart()))
				reach.add(trans.getEnd());
		}*/
		if(this.edgesByStart.get(start) == null)
			return reach;
		for(LabeledEdge trans : this.edgesByStart.get(start)){
			reach.add(trans.getEnd());
		}
		return reach;
		
	}
	
	public Set<Vertex> pre(Vertex start){
		Set<Vertex> reach = new HashSet<Vertex>();
/*		for(LabeledEdge trans : this.edges){
			if(start.equals(trans.getEnd()))
				reach.add(trans.getStart());
		}*/
		//TODO Debug Code
		System.out.println("start the for loop "+edgesByEnd.get(start)+" at Vertex "+start);
		if(this.edgesByEnd.get(start) == null)
			return reach;
		for(LabeledEdge trans : this.edgesByEnd.get(start)){
			//TODO Debug Code
			System.out.println("doing for loop "+trans);
			assert(trans.getStart()!= null);
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

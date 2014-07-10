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

/**
 * An Implementation of a Graph
 * @author Thomas
 *
 */
public abstract class Graph {
	final protected Set<LabeledEdge> edges;
	final protected Set<Vertex> vertices;
	final protected Map<Vertex, Set<LabeledEdge>> edgesByStart;
	final protected Map<Vertex, Set<LabeledEdge>> edgesByEnd;
	final protected Map<Action, Set<LabeledEdge>> edgesByAction;
	
	/**
	 * The Constructor. Takes a Collection of Vertices and a Collection of LabeledEdges connecting those Vertices
	 * @param edges
	 * @param vertices
	 */
	public Graph(Collection<LabeledEdge> edges, Collection<Vertex> vertices) {
		this.edges = new HashSet<>(edges);
		this.vertices = new HashSet<>(vertices);
		this.edgesByStart = new HashMap<>();
		this.edgesByEnd = new HashMap<>();
		this.edgesByAction = new HashMap<>();
		for(Vertex vertex : vertices){
			this.edgesByStart.put(vertex, new HashSet<LabeledEdge>());
			this.edgesByEnd.put(vertex, new HashSet<LabeledEdge>());
		}
		for(LabeledEdge trans : edges){
			this.edgesByStart.get(trans.getStart()).add(trans);
			this.edgesByEnd.get(trans.getEnd()).add(trans);
			if(edgesByAction.get(trans.getLabel()) == null)
				edgesByAction.put(trans.getLabel(), new HashSet<LabeledEdge>());
			this.edgesByAction.get(trans.getLabel()).add(trans);
		}
	}
	
	public Set<LabeledEdge> getEdges() {
		return Collections.unmodifiableSet(edges);
	}
	
	public Set<Vertex> getVertices() {
		return Collections.unmodifiableSet(vertices);
	}
	
	/**
	 * 
	 *@return A set that contains every action present in this LTS
	 *
	 *@author Jeremias
	 * */
	public Set<Action> getActions(){
		
		Set<Action> actions = new HashSet<Action>();
		
		for (LabeledEdge edge : edges){
			
			actions.add(edge.getLabel());
		}
		
		return actions;
	}
	
	
	//TODO: look into the methods using this if removing the start vertex itself causes problems
	/**
	 * Returns the direct followers of a given vertex
	 * @param start The Vertex to calculate Post of
	 * @return
	 */
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
	
	/**
	 * Returns the direct predecessors of a given vertex
	 * @param start The vertex to calculate the predecessor of
	 * @return
	 */
	public Set<Vertex> pre(Vertex start){
		Set<Vertex> reach = new HashSet<Vertex>();
/*		for(LabeledEdge trans : this.edges){
			if(start.equals(trans.getEnd()))
				reach.add(trans.getStart());
		}*/
		if(this.edgesByEnd.get(start) == null)
			return reach;
		for(LabeledEdge trans : this.edgesByEnd.get(start)){
			reach.add(trans.getStart());
		}
		return reach;
	}
	
	/**
	 * Looks up if the end vertex is in Post(start)
	 * @param start
	 * @param end
	 * @return
	 */
	public boolean reachable(Vertex start, Vertex end){
		Set<Vertex> reach = this.post(start); 
		return reach.contains(end);
	}	
	
	/**
	 * Looks up if the end vertex is in Post(start) and can be reached with Action act
	 * @param start
	 * @param end
	 * @param act
	 * @return
	 */
	public boolean reachableWith(Vertex start, Vertex end, Action act){
		Set<LabeledEdge> reach = edgesByStart.get(start);
		if(reach.contains(new LabeledEdge(start, end, act)))
			return true;
		if(start.equals(end) && act.equals(Action.TAU))
			return true;
		return false;
	}
	
	/**
	 * Lists the transitions with the vertex start as the start and the vertex follower as the end
	 * @param start
	 * @param follower
	 * @return
	 */
	public Set<LabeledEdge> getTransitions(Vertex start, Vertex follower){
		Set<LabeledEdge> paths = new HashSet<LabeledEdge>();
		for(LabeledEdge trans : edgesByStart.get(start)){
			if(trans.getEnd().equals(follower))
				paths.add(trans);
		}
		return paths;
	}
}

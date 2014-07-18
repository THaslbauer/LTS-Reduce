package kongruenz.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.lang.Runtime;

import kongruenz.Graph;
import kongruenz.objects.Action;
import kongruenz.objects.LabeledEdge;
import kongruenz.objects.Vertex;
import kongruenz.util.Communicator;


/**
 * Concurrent search object for nodes in a labeled directed graph. 
 * @author Thomas
 *
 */
public class GraphSearch {
private final Graph graph;
private static final int workercount = Runtime.getRuntime().availableProcessors()+1;
private final Map<Vertex, VertexWithPrePost> vertices;

/**
 * Takes a Graph to create the search object for
 * @param graph the Graph we want to search through
 */
public GraphSearch(final Graph graph){
	this.graph = graph;
	
	final CountDownLatch counter = new CountDownLatch(graph.getVertices().size());
	ThreadPoolExecutor threads = new ThreadPoolExecutor(workercount, workercount,
			10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	
	//initialize a Map that matches each vertex v to its weak Pre- and Post-States with tau.
	//v is only contained in Pre or Post if it can reach itself via selfloop.
	vertices = new HashMap<>();
	for(Vertex v : graph.getVertices()){
		vertices.put(v, new VertexWithPrePost(v));
	}
	
	//FIRST: map to each Vertex v Post(v, τ) via a simple Runnable.
	for(Vertex v : graph.getVertices()){
		final Vertex w = v;
		synchronized(threads){
			threads.execute(new Runnable(){
				public void run(){
					for(LabeledEdge trans : graph.getEdgesWithStart(w)){
						if(trans.getLabel().equals(Action.TAU))
							vertices.get(w).addPost(trans.getEnd());
					}
					counter.countDown();
				}
			});
		}
	}
	try{
		counter.await();
	}
	catch(InterruptedException e){
		System.err.println(e.getStackTrace());
	}
	Communicator comm = new Communicator(threads);
	
	//Proliferating for each vertex v Pre(v, τ)
	for(Vertex v : graph.getVertices()){
		Set<Vertex>preTau = new HashSet<>();
		for(LabeledEdge trans : graph.getEdgesWithEnd(v)){
			if(trans.getLabel().equals(Action.TAU))
				preTau.add(trans.getStart());
		}
		comm.moreWorkToDo();
		synchronized(threads){
			threads.execute(new Proliferator(v, preTau, threads, comm, true));
		}
	}
	if(comm.waitForDone()){
		threads.shutdownNow();
		throw new UnsupportedOperationException();
	}
	
	//For each vertex v: add its weak Pre(v, τ) (calculated in the loop before) to its weak post nodes.
	for(Vertex v: graph.getVertices()){
		final Vertex w = v;
		final Communicator fcomm = comm;
		synchronized(threads){
			threads.execute(new Runnable(){
				public void run(){
					fcomm.moreWorkToDo();
					for(Vertex u : vertices.get(w).getPost()){
						vertices.get(u).addPre(vertices.get(w).getPre());
					}
					fcomm.lessWorkToDo();
				}
			});
		}
	}
	if(comm.waitForDone()){
		threads.shutdownNow();
		throw new UnsupportedOperationException();
	}
	threads.shutdown();
}

public Set<Vertex> getPreWithTau(Vertex vertex){
	return this.vertices.get(vertex).getPre();
}

public Set<Vertex> getPostWithTau(Vertex vertex){
	return this.vertices.get(vertex).getPost();
}

/**
 * The Proliferator Runnable adds a set of states to the pre or post nodes of a given states
 * and pushes new Proliferators for each state that is post or pre (other direction than before) if the set to add changed something.
 * @author Thomas
 *
 */
private class Proliferator implements Runnable{
	private Vertex vert;
	private Set<Vertex> verticesToAdd;
	private ThreadPoolExecutor threads;
	private Communicator comm;
	private final boolean pre;
	public Proliferator(Vertex v, Set<Vertex> verticesToAdd,
			ThreadPoolExecutor threads, Communicator comm, boolean pre){
		this.vert = v;
		this.verticesToAdd = verticesToAdd;
		this.threads = threads;
		this.comm = comm;
		this.pre = pre;
	}
	public void run(){
		if(pre){
			//if something was added, proliferate to post nodes
			if(vertices.get(vert).addPre(verticesToAdd)){
				for(Vertex v : vertices.get(vert).getPost()){
					comm.moreWorkToDo();
					synchronized(threads){
						threads.execute(new Proliferator(v, verticesToAdd, threads, comm, pre));
					}
				}
			}
		}
		else{
			//if something was added, proliferate to post nodes
			if(vertices.get(vert).addPost(verticesToAdd)){
				for(Vertex v : vertices.get(vert).getPre()){
					comm.moreWorkToDo();
					synchronized(threads){
						threads.execute(new Proliferator(v, verticesToAdd, threads, comm, pre));
					}
				}
			}
		}
		comm.lessWorkToDo();
	}
}

public synchronized void sysout(String input){
	System.err.println(input);
}

private class VertexWithPrePost {
	private Vertex vert;
	private Set<Vertex> preTau;
	private Set<Vertex> postTau;
	
	public VertexWithPrePost(Vertex vert){
		this.vert = vert;
		this.preTau = new HashSet<>();
		this.postTau = new HashSet<>();
	}
	
	synchronized Set<Vertex> getPre(){
		return preTau;
	}
	
	synchronized Set<Vertex> getPost(){
		return postTau;
	}
	
	synchronized boolean addPre(Set<Vertex> pre){
		return preTau.addAll(pre);
	}
	
	synchronized boolean addPre(Vertex pre){
		return preTau.add(pre);
	}
	
	synchronized boolean addPost(Set<Vertex> post){
		return postTau.addAll(post);
	}
	
	synchronized boolean addPost(Vertex post){
		return postTau.add(post);
	}
}
}

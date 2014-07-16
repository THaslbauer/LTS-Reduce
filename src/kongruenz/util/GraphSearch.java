package kongruenz.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
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
private static final boolean FORWARD = true;
private final Map<Vertex, VertexWithPrePost> vertices;

/**
 * Takes a Graph to create the search object for
 * @param graph the Graph we want to search through
 */
public GraphSearch(final Graph graph){
	this.graph = graph;
	final CountDownLatch counter = new CountDownLatch(graph.getVertices().size());
	vertices = new HashMap<>();
	ThreadPoolExecutor threads = new ThreadPoolExecutor(workercount, workercount,
			10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	for(Vertex v : graph.getVertices()){
		vertices.put(v, new VertexWithPrePost(v));
	}
	for(Vertex v : graph.getVertices()){
		final Vertex w = v;
		synchronized(threads){
			threads.execute(new Runnable(){
				public void run(){
					for(LabeledEdge trans : graph.getEdgesWithStart(w)){
						if(trans.getLabel().equals(Action.TAU))
							vertices.get(w).addPost(trans.getEnd());
					}
					//TODO remove
					sysout("added tau-post to "+w+": "+vertices.get(w).getPost());
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
	//TODO remove
	sysout("proliferating forward");
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
	comm.waitForDone();
	//TODO remove
	sysout("proliferating backwards");
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
	comm.waitForDone();
	threads.shutdown();
}

public Set<Vertex> getPreWithTau(Vertex vertex){
	return this.vertices.get(vertex).getPre();
}

public Set<Vertex> getPostWithTau(Vertex vertex){
	return this.vertices.get(vertex).getPost();
}

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
			if(vertices.get(vert).addPre(verticesToAdd)){
				//TODO remove
				sysout("proliferating pre of "+vert+": "+verticesToAdd);
				for(Vertex v : vertices.get(vert).getPost()){
					comm.moreWorkToDo();
					synchronized(threads){
						threads.execute(new Proliferator(v, verticesToAdd, threads, comm, pre));
					}
				}
			}
		}
		else{
			if(vertices.get(vert).addPost(verticesToAdd)){
				//TODO remove
				sysout("proliferating post of "+vert+": "+verticesToAdd);
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
	
	/**
	 * WARNING: USE WITH LOTS OF CARE
	 */
	synchronized void clearPost(){
		postTau.clear();
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

package kongruenz.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import kongruenz.Graph;
import kongruenz.objects.Action;
import kongruenz.objects.LabeledEdge;
import kongruenz.objects.Vertex;

public class GraphSearch {
private final Graph graph;
private static final int workercount = 2;
private static final boolean FORWARD = true;

public GraphSearch(Graph graph){
	this.graph = graph;
}

public boolean findForward(Vertex start, Vertex target, Action path){
	Worker[] workers = new Worker[workercount];
	Communicator comm = new Communicator();
	//Condition with comm.isDone()
	if(graph.reachableWith(start, target, path))
		return true;
	comm.addToToVisit(graph.post(start));
	comm.addToVisited(graph.post(start));
	//TODO remove
	System.out.println("starting workers");
	for(int i = 0; i<workercount; i++){
		workers[i] = new Worker(comm, target, FORWARD);
		workers[i].start();
	}
	//TODO remove
	System.out.println("main now sleeping");
	comm.waitForDone();
	System.out.println("seems to be done");
	for(int i = 0; i < workercount; i++){
		workers[i].interrupt();
	}
	//TODO remove
	System.out.println("now done");
	return comm.wasFound();
}

public boolean findBackwards(Vertex start, Vertex target, Action path){
	Worker[] workers = new Worker[workercount];
	Communicator comm = new Communicator();
	//Condition with comm.isDone()
	if(graph.pre(start).contains(target)){
		Set<LabeledEdge> edges = graph.getEdges();
		for(LabeledEdge trans : edges){
			if(trans.getLabel().equals(path)
					&& trans.getStart().equals(target) && trans.getEnd().equals(start))
				return true;
		}
	}
	comm.addToToVisit(graph.pre(start));
	comm.addToVisited(graph.pre(start));
	//TODO remove
	System.out.println("starting workers");
	for(int i = 0; i<workercount; i++){
		workers[i] = new Worker(comm, target, !FORWARD);
		workers[i].start();
	}
	//TODO remove
	System.out.println("main now sleeping");
	comm.waitForDone();
	for(int i = 0; i < workercount; i++){
		workers[i].interrupt();
	}
	//TODO remove
	System.out.println("now done");
	return comm.wasFound();
}

private class Worker extends Thread {
	
	private Communicator comm;
	private final Vertex target;
	private final boolean forward;
	
	public Worker(Communicator comm, Vertex target, boolean forward){
		this.comm = comm;
		this.target = target;
		this.forward = forward;
	}

	@Override
	public void run() {
		//TODO remove
		System.out.println("worker initialized");
		while(comm.workToDo()){
			//TODO remove
			System.out.println("working");
			comm.checkIn();
			Vertex v = comm.getVertexToVisit();
			if(v != null){
				if(reachableWith(v, target, Action.TAU))
					comm.found();
				else {
					for(Vertex check : getPrePost(v)){
						if(!comm.wasVisited(check) && reachableWith(v, check, Action.TAU)){
								comm.addToToVisit(check);
								comm.addToVisited(check);
						}
					}
				}
			}
			comm.checkOut();
		}
		//TODO remove
		System.out.println("worker done");
	}
	
	private boolean reachableWith(Vertex start, Vertex end, Action path){
		if(forward)
			return graph.reachableWith(start, end, path);
		else {
			if(graph.pre(start).contains(end)){
				Set<LabeledEdge> edges = graph.getEdges();
				for(LabeledEdge trans : edges){
					if(trans.getLabel().equals(path)
							&& trans.getStart().equals(end) && trans.getEnd().equals(start))
						return true;
				}
			}
			return false;
		}
	}
	private Set<Vertex> getPrePost(Vertex start){
		if(forward)
			return graph.post(start);
		else
			return graph.pre(start);
	}
}

private class Communicator {
	private List<Vertex> toVisit;
	private Set<Vertex> visited;
	private Semaphore status;
	private boolean found = false;
	
	public Communicator(){
		toVisit = new LinkedList<Vertex>();
		visited = new HashSet<Vertex>();
		status = new Semaphore(workercount);
	}
	
	synchronized public void addToToVisit(Collection<Vertex> vertices){
		for(Vertex v : vertices){
			if(!toVisit.contains(v))
				toVisit.add(v);
		}
		notifyAll();
	}
	
	synchronized public void addToToVisit(Vertex vertex){
		toVisit.add(vertex);
		notifyAll();
	}
	
	synchronized public Vertex getVertexToVisit(){
		if(!toVisit.isEmpty()){
			Vertex v = toVisit.get(0);
			toVisit.remove(0);
			return v;
		}
		else
			return null;
	}
	
	synchronized public boolean wasVisited(Vertex v){
		return visited.contains(v);
	}
	
	synchronized public void addToVisited(Collection<Vertex> vertices){
		visited.addAll(vertices);
	}
	
	synchronized public void addToVisited(Vertex vertex){
		visited.add(vertex);
	}
	
	synchronized public void checkIn(){
		try{
		status.acquire();
		}
		catch(Exception e){}
	}
	
	synchronized public void checkOut(){
		try{
			status.release();
		}
		catch(Exception e){}
		notifyAll();
	}
	
	synchronized public void found() {
		found = true;
	}
	
	synchronized public void waitForDone(){
		//TODO fucked up condition for termination not working
//		if(found){
//			notifyAll();
//			return;
//		}
//		else{
			while(!((toVisit.size() == 0) && (status.availablePermits() == workercount))){
				notifyAll();
				try{
					wait();
				}
				catch (Exception e){}
			}
			notifyAll();
//		}
	}
	
	synchronized public boolean workToDo(){
		if(toVisit.size() != 0 && !found)
			return true;
		while(toVisit.size() == 0){
			if(status.availablePermits() == workercount){
				notifyAll();
				return false;
			}
			else{
				notifyAll();
				try{
					wait();
				}
				catch (InterruptedException e){
					return false;
				}
			}
		}
		return true;
	}
	
	synchronized public boolean wasFound(){
		return found;
	}
	
}
}
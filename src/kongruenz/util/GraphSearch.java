package kongruenz.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import kongruenz.Graph;
import kongruenz.objects.Action;
import kongruenz.objects.LabeledEdge;
import kongruenz.objects.Vertex;

public class GraphSearch {
private final Graph graph;
private static final int workercount = 3;
private static final boolean FORWARD = true;

public GraphSearch(Graph graph){
	this.graph = graph;
}

public boolean findForward(Vertex start, Vertex target, Action path){
	//TODO implement
	Worker[] workers = new Worker[workercount];
	Lock syncer = new ReentrantLock();
	Communicator comm = new Communicator();
	//Condition with comm.isDone()
	Condition finished = syncer.newCondition();
	if(graph.reachableWith(start, target, path))
		return true;
	comm.addToToVisit(graph.post(start));
	comm.addToVisited(graph.post(start));
	for(int i = 0; i<workercount; i++){
		workers[i] = new Worker(comm, finished, target, FORWARD);
	}
	comm.waitForDone();
	return comm.wasFound();
}

public boolean findBackwards(Vertex start, Vertex target, Action path){
	//TODO implement
	return false;
}

private class Worker extends Thread {
	
	private Communicator comm;
	private Condition wakeUp;
	private final Vertex target;
	private final boolean forward;
	
	public Worker(Communicator comm, Condition wakeUp, Vertex target, boolean forward){
		this.comm = comm;
		this.wakeUp = wakeUp;
		this.target = target;
		this.forward = forward;
	}

	@Override
	public void run() {
		while(!comm.workToDo()){
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
			wakeUp.signalAll();
			try{
				wakeUp.await();
			}
			catch(InterruptedException e){
				break;
			}
		}
	}
	
	private boolean reachableWith(Vertex start, Vertex end, Action path){
		if(forward)
			return graph.reachableWith(start, end, path);
		else {
			if(graph.pre(start).contains(end)){
				Set<LabeledEdge> edges = graph.getEdges();
				for(LabeledEdge trans : edges){
					if(trans.getLabel().equals(path)
							&& trans.getStart().equals(start) && trans.getEnd().equals(end))
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
	}
	
	synchronized public void addToToVisit(Vertex vertex){
		toVisit.add(vertex);
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
		while (!(((status.availablePermits() == 0)&&(toVisit.size() == 0)) || found)){
			try{
			wait();
			}
			catch(InterruptedException e){}
		}
	}
	
	synchronized public boolean workToDo(){
		while(toVisit.size()==0){
			if(status.availablePermits() != 5){
				try{
					wait();
				}
				catch(InterruptedException e){
					return false;
				}
			}
			else
				return false;
		}
		return true;
	}
	
	synchronized public boolean wasFound(){
		return found;
	}
	
}
}
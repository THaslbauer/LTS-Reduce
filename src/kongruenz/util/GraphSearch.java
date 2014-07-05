package kongruenz.util;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import kongruenz.Graph;
import kongruenz.objects.Action;
import kongruenz.objects.Vertex;

public class GraphSearch {
private final Graph graph;
private final int workercount = 3;
private final boolean FORWARD = true;

public GraphSearch(Graph graph){
	this.graph = graph;
}

public boolean findForward(Vertex start, Vertex target, Action path){
	//TODO implement
	Set<Vertex> visited = new HashSet<Vertex>();
	Set<Vertex> toVisit = new HashSet<Vertex>();
	Semaphore states = new Semaphore(workercount);
	Worker[] workers = new Worker[workercount];
	Lock syncer = new ReentrantLock();
	//Condition with (Semaphore isn't used (no worker still working)) & (toVisit.size() == 0)
	Condition finished = syncer.newCondition();
	if(graph.reachableWith(start, target, path))
		return true;
	for(Vertex v : graph.post(start)){
		toVisit.add(v);
		visited.add(v);
	}
	for(int i = 0; i<workercount; i++){
		workers[i] = new Worker(visited, toVisit, states, FORWARD, finished);
	}
	int tovisitcount;
	synchronized(toVisit){
		tovisitcount = toVisit.size();
	}
	while(states.availablePermits() < workercount || tovisitcount != 0){
		try {
			finished.await();
		}
		catch(InterruptedException e){}
		synchronized(toVisit) {tovisitcount = toVisit.size();}
	}
	return false;
}

public boolean findBackwards(Vertex start, Vertex target, Action path){
	//TODO implement
	return false;
}

private class Worker extends Thread {
	private Set<Vertex> visited, toVisit;
	private Semaphore state;
	private final boolean forward;
	private boolean[] done;
	
	public Worker(Set<Vertex> visited, Set<Vertex> toVisit,
			Semaphore state, boolean forward, Condition wakeEveryone) {
		this.visited = visited;
		this.toVisit = toVisit;
		this.state = state;
		this.forward = forward;
	}

	@Override
	public void run() {
		try {
		state.acquire();
		}
		catch(InterruptedException e) {
			return;
		}
		while(state.availablePermits() < workercount || (toVisit.size() != 0)) {
			
		}
	}
	
}
}
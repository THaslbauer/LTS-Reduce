package kongruenz.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import kongruenz.LTS;
import kongruenz.Partition;
import kongruenz.objects.Action;
import kongruenz.objects.LabeledEdge;
import kongruenz.objects.Vertex;

public class Minimizer {
	private ThreadPoolExecutor threads;
	private int workercount = Runtime.getRuntime().availableProcessors()+2;
	
	
	public Minimizer(){
		threads = new ThreadPoolExecutor(this.workercount, this.workercount, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}
	
	public LTS minimize(LTS toMinimize, Partition p){
		return this.collapse(toMinimize, p);
	}
	
	/**
	 * Collapses a given LTS to a smaller one by combining the Vertices blockwise.
	 * @param toMinimize The LTS to collapse
	 * @param p The Partition that defines the collapse operation.
	 * @return
	 */
	private LTS collapse(LTS toMinimize, Partition p){
		CountDownLatch counter = new CountDownLatch(p.getBlocks().size());
		int vertexNumber = 0;
		Map<Vertex, String> stateName = new ConcurrentHashMap<>(toMinimize.getVertices().size(), 0.75f, workercount);
		for(Set<Vertex> vertices : p.getBlocks()){
			//Using the MapUpDater to map each Vertex in a Block to a common name.
			threads.execute(new MapUpDater(vertices, stateName, Integer.toString(vertexNumber), counter));
			vertexNumber++;
		}
		try{
			counter.await();
		}
		catch (InterruptedException e){
			this.shutdown();
			this.threads = new ThreadPoolExecutor(this.workercount, this.workercount, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
			return null;
		}
		//TODO remove
		System.err.println("name mapping now done, mapping is:");
		for(Vertex v : stateName.keySet()){
			System.err.println(v+" to "+stateName.get(v));
		}
		Communicator comm = new Communicator(threads);
		GraphMonitor graphMon = new GraphMonitor();
		comm.moreWorkToDo();
		//TODO remove
		System.err.println("now collapsing");
		threads.execute(new LTSGenerator(threads, comm, graphMon, toMinimize.getStart(), toMinimize, stateName));
		comm.waitForDone();
		//TODO remove
		System.err.println("edges are:\n"+graphMon.getEdges());
		System.err.println("vertices are:\n"+graphMon.getVertices());
		return new LTS(graphMon.getVertices(), graphMon.getEdges(), new Vertex(stateName.get(toMinimize.getStart())));
	}
	
	private LTS reduceEdges(LTS lts){
		GraphSearch searcher = new GraphSearch(lts);
		CountDownLatch counter = new CountDownLatch(lts.getActions().size());
		for(Action a : lts.getActions()){
			
		}
		throw new UnsupportedOperationException();
	}
	
	public void shutdown(){
		threads.shutdownNow();
		try{
			threads.awaitTermination(20, TimeUnit.SECONDS);
		}
		catch(InterruptedException e){
			throw new UnsupportedOperationException("Can't interrupt termination");
		}
	}
	
	
	/**
	 * Simple Runnable that updates a Map with the Vertices and the name you gave to it.
	 * The map should be concurrent if you want to use this concurrently.
	 * @author thomas
	 *
	 */
	private class MapUpDater implements Runnable{
		private Map<Vertex, String> map;
		private Set<Vertex> vertices;
		private CountDownLatch counter;
		private String name;
		
		public MapUpDater(Set<Vertex> verticesToAdd, Map<Vertex,
				String> mapToUpdate, String name, CountDownLatch counter){
			this.vertices = verticesToAdd;
			this.map = mapToUpdate;
			this.counter = counter;
			this.name = name;
		}
		
		public void run(){
			for(Vertex v : vertices){
				System.out.println("mapping "+v+" to "+name);
				boolean hopeNotThis = map.put(v, name) == null;
				assert(hopeNotThis);
				hopeNotThis = !hopeNotThis;
			}
			counter.countDown();
		}
	}
	
	/**
	 * Runnable that walks over a given LTS and writes a collapsed version to a GraphMonitor via concurrent execution.
	 * @author thomas
	 *
	 */
	private class LTSGenerator implements Runnable{
		private ThreadPoolExecutor threads;
		private Communicator comm;
		private GraphMonitor graph;
		private Vertex start;
		private LTS lts;
		private Map<Vertex, String> namingKey;
		
		public LTSGenerator(ThreadPoolExecutor threads, Communicator comm,
				GraphMonitor graph, Vertex start, LTS lts, Map<Vertex, String>namingKey){
			this.threads = threads;
			this.comm = comm;
			this.graph = graph;
			this.start = start;
			this.lts = lts;
			this.namingKey = namingKey;
		}
		
		/**
		 * Add all edges to the given GraphMonitor that aren't tau-self-loops.
		 * Tau-Self-Loops do get added if they are from the LTS-start-state.
		 * Adds the edges with start- and end-vertex according to the namingKey map.
		 */
		public void run(){
			String startName = namingKey.get(start);
			//TODO remove
			System.err.println(start+" mapped to "+startName);
			for(LabeledEdge trans : lts.getEdgesWithStart(start)){
				Vertex from = new Vertex(startName);
				Vertex to = new Vertex(namingKey.get(trans.getEnd()));
				if(!(from.equals(to) && !this.start.equals(lts.getStart())
						&& trans.getLabel().equals(Action.TAU))){
					//TODO remove
					System.err.println("going from "+from+" to "+to+" with "+trans.getLabel());
					graph.updateMonitor(new LabeledEdge(from, to, trans.getLabel()));
				}
				if(!graph.visited(trans.getEnd())){
					comm.moreWorkToDo();
					threads.execute(new LTSGenerator(threads, comm, graph, trans.getEnd(), lts, namingKey));
				}
			}
			comm.lessWorkToDo();
		}
		
	}

	/**
	 * Monitor that wraps the Vertex- and LabeledEdge-Set of a Graph that is to be built.
	 * Also monitors the visited nodes of the original LTS.
	 * @author thomas
	 *
	 */
	private class GraphMonitor {
		private Set<Vertex> vertices;
		private Set<LabeledEdge> edges;
		private Set<Vertex> visitedUncombinedVertices;
		
		public GraphMonitor(){
			vertices = new HashSet<>();
			edges = new HashSet<>();
			visitedUncombinedVertices = new HashSet<>();
		}
		
		/**
		 * Updates the Monitors sets.
		 * @param edge The LabeledEdge that should get added to the final graph, the start and end vertices get added as well.
		 * @return True if something was updated
		 */
		public boolean updateMonitor(LabeledEdge edge){
			//TODO remove
			System.err.println("adding edge "+edge);
			boolean a =  vertices.add(edge.getStart());
			boolean b = vertices.add(edge.getEnd());
			boolean c = edges.add(edge);
			return a||b||c;
		}
		
		/**
		 * Checks if Vertex v of the original LTS has been visited and if not marks it as visited.
		 * @param v
		 * @return True if v was visited already.
		 */
		public boolean visited(Vertex v){
			return !visitedUncombinedVertices.add(v);
		}
		
		public Set<LabeledEdge> getEdges(){
			return Collections.unmodifiableSet(edges);
		}
		
		public Set<Vertex> getVertices(){
			return Collections.unmodifiableSet(vertices);
		}
	}
	
	private class EdgeCombiner implements Runnable{
		private GraphSearch searcher;
		private LTS lts;
		private CountDownLatch counter;
		private Action a;
		
		public EdgeCombiner(GraphSearch searcher, LTS lts, CountDownLatch counter, Action a){
			this.searcher = searcher;
			this.lts = lts;
			this.counter = counter;
			this.a = a;
		}
		
	}
}

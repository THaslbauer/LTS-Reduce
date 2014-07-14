package kongruenz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import kongruenz.objects.Action;
import kongruenz.objects.LabeledEdge;
import kongruenz.objects.Vertex;

public class Partition {

	private Set<Set<Vertex>> P;
	private List<Set<Vertex>> toDo_list;
	private LTS lts;

	public Partition(LTS lts) {

		this.lts = lts;
		P = new CopyOnWriteArraySet<Set<Vertex>>();
		P.add(lts.getModVertices());

		toDo_list = new ArrayList<Set<Vertex>>();
		toDo_list.add(lts.getModVertices());
	}

	synchronized public LTS getLTS() {
		return lts;
	}

	/**
	 * Generates a new LTS from the current partition, represented by field P,
	 * using the vertices and transitions from the LTS. E.g. if A --"a"--> B is
	 * a valid transition in the LTS, then the new LTS will have a transition
	 * with "a" going from whatever block contains A to whatever block contains
	 * B. Additionally, in the new LTS the vertices get their names from
	 * numbers( 0,...,n) where "n" is the amount of blocks in the partition.
	 * Only use this in conjunction with a Reduce task, as this method wont do anything unless
	 * the toDo-list isEmpty;
	 * @returns A new LTS
	 * @author Jeremias
	 * 
	 * 
	 * */
	synchronized public LTS generateLTSfromPartition() {

		
		while(!isDone()){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Map<Set<Vertex>, Vertex> set_vertex_map = new HashMap<Set<Vertex>, Vertex>();
		Set<Vertex> new_states = new HashSet<Vertex>();

		//initialize the new states
		int i = 0;
		for (Set<Vertex> block : P) {

			Vertex j = new Vertex(Integer.toString(i));
			set_vertex_map.put(block, j);
			new_states.add(j);

			i++;
		}

		Map<Vertex, Set<Vertex>> vertex_set_map = new HashMap<Vertex, Set<Vertex>>();

		for (Vertex vertex : lts.getVertices()) {

			for (Set<Vertex> block : P) {

				if (block.contains(vertex)) {
					
					//TODO: vertex_set_map.put(vertex,block);
					assert (vertex_set_map.put(vertex, block) == null);
				}
			}
		}

		Set<LabeledEdge> newEdges = new HashSet<LabeledEdge>();

		for (LabeledEdge edge : lts.getEdges()) {
			if (edge.getLabel() != Action.TAU
					|| edge.getStart() == lts.getStart())
				newEdges.add(new LabeledEdge(set_vertex_map.get(vertex_set_map
						.get(edge.getStart())), set_vertex_map
						.get(vertex_set_map.get(edge.getEnd())), edge
						.getLabel()));
		}

		return new LTS(new_states, newEdges, set_vertex_map.get(vertex_set_map
				.get(lts.getStart())));

	}

	/**
	 * Generates a new Partition with the same LTS as this one, but with a new P
	 * field
	 * 
	 * @param newP
	 *            The set with which to replace everything in the old partition
	 * @return The new partition
	 * 
	 * @author Jeremias
	 * */
	synchronized public Partition generateNewPartition(Set<Vertex> newP) {

		Partition newPartition = new Partition(lts);

		Set<Set<Vertex>> toBeUsed = new HashSet<Set<Vertex>>();
		toBeUsed.add(newP);
		newPartition.P = toBeUsed;
		return newPartition;
	}

	synchronized public boolean isDone(){
		
		return toDo_list.isEmpty();
	}
	
	
	
	
	
	
	/**
	 * This method forms the union of two partitions' P fields
	 * 
	 * @param partition
	 *            The partition to be added to this one
	 * 
	 * @author Jere
	 * */
	synchronized public void unite(Partition partition) {

		this.P.addAll(partition.P);
	}

	/**
	 * Returns the first block in the toDo-list and waits if none are present
	 * 
	 * @throws InterruptedException
	 *             ()
	 * 
	 * 
	 * */
	synchronized public void removeBlock_fromList(Set<Vertex> block) {

		toDo_list.remove(block);
		notifyAll();

	}

	synchronized public Set<Set<Vertex>> getBlocks() {

		return P;
	}

	/**
	 * Adds a block to the toDo-list
	 * 
	 * @param The
	 *            block to add
	 * 
	 * */
	synchronized public void putBlock(Set<Vertex> block)
			throws InterruptedException {

		while (!(toDo_list.size() < Integer.MAX_VALUE)) {
			wait();
		}

		toDo_list.add(block);

	}

	/**
	 * Replaces one of the blocks in the Partition with two others
	 * 
	 * @param block0
	 *            The block to be replaced
	 * @param block1
	 *            One of the two blocks to be added
	 * @param block2
	 *            One of the two blocks to be added
	 * 
	 * 
	 * */

	synchronized public void replaceBlock(Set<Vertex> block0,
			Set<Vertex> block1, Set<Vertex> block2) throws InterruptedException {

		P.remove(block0);
		P.add(block1);
		P.add(block2);
		putBlock(block1);
		putBlock(block2);
		removeBlock_fromList(block0);
		notifyAll();
	}

	
	synchronized public String toString(){
		
		while(!isDone()){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return P.toString();
	}
}

package kongruenz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import kongruenz.objects.Vertex;
import kongruenz.util.Minimizer;


/**
 * This class is used to represent a Set that, if in its final state, contains sets of Vertices that are congruent to all others in their respective set.
 * It starts off as only one set of vertices, but using a ForkJoinPool and ReduceTask it can be iterated upon, further improving the Partition.
 * Its fields are used as follows:
 * 
 * @P: The actual Set of sets of vertices.
 * @lts: The LTS this Partition originally belonged to.
 * @toDo_list: Methods such as generateLTSfromPartition() rely on the Partition being completely iterated upon, meaning every set contained therein only has Vertices that are congruent to each other.
 * In order to make sure the methods wait for the Partition to be done, this toDo_list contains all blocks that still need to be looked at.
 * 
 * 
 * */
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
	 * Only use this in conjunction with a Reduce task, as this method wont do anything usefull unless
	 * this Partition has been iterated upon to completion.
	 * @returns A new LTS
	 * @author Thomas & Jere
	 * 
	 * 
	 * */
	synchronized public LTS generateLTSfromPartition() {
		while(!isDone()){
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.err.println(e.getStackTrace());
				return null;
			}
		}
		Minimizer minimizer = new Minimizer();
		LTS returnLTS = minimizer.minimize(lts, this);
		minimizer.shutdown();
		return returnLTS;
	}

	synchronized public boolean isDone(){
		
		return toDo_list.isEmpty();
	}

	/**
	 * Removes one block from the toDo_list and then notifies all Threads.
	 * The notifying is important for termination purposes.
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
	 * Adds a block to the toDo-list. Waits if the list is full, i.e. size == Integer.MAX_VALUE.
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
	 * @throws InterruptedException
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

	/**Used for debugging purposes only, not needed in the final program.
	 * 
	 * */
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

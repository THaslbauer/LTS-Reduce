package kongruenz;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import kongruenz.objects.Action;
import kongruenz.objects.Vertex;

public class ReduceTask extends RecursiveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Partition partition;
	private Set<Vertex> block;

	public ReduceTask(Partition p, Set<Vertex> block) {

		this.partition = p;
		this.block = block;

	}

	
	/**Goes through the algorithm with one fixed block and makes changes to the partition.
	 * This method is run when the ReducedTask is invoked or forked.
	 * 
	 * @author Jere
	 * 
	 * */
	@Override
	public void compute() {

		boolean split = false;
		Set<Vertex> block1 = new HashSet<Vertex>();
		Set<Vertex> block2 = new HashSet<Vertex>();
		
		//---------------go through all blocks in the partition using the algorithm---------------------//
		//------------------------------------------------------------------------------------------------//

		here: for (Action action : partition.getLTS().getActions()) {

			for (Set<Vertex> test_block : partition.getBlocks()) {

				Set<Vertex> pre_states = new HashSet<Vertex>();

				for (Vertex state : test_block) {

					pre_states
							.addAll(partition.getLTS().weakPre(state, action));

				}

				//-------------------create block1 = Intersection(Pre(a,B'), B) and block2 = Exclusion(B,Pre(a,B'))
				
				block1 = new HashSet<Vertex>(pre_states);
				block1.retainAll(block);
				block2 = new HashSet<Vertex>(block);
				block2.removeAll(pre_states);

				//---------------see if the splitting condition is fulfilled--------------------------//
				
				if (!(block1.isEmpty() || block2.isEmpty())) {

					split = true;
					break here;

				}
			}

		}

		//--------------if the block has to be split, replace it and re-check the blocks that have transitions to it------//
		//----------------------------------------------------------------------------------------------------------------//
		
		if (split) {

			//---------------replace block---------------//
			
			try {
				partition.replaceBlock(block, block1, block2);
			} catch (InterruptedException e) {

			}

			//--------------find out which blocks to look at again-------------//
			
			Set<Set<Vertex>> preBlocks = getPreBlocksOfBlock(block);

			//--------------add these blocks to the queue and fork a reduce task for every one------//
			
			for (Set<Vertex> block : preBlocks) {

				if (!block.equals(block1) && !block.equals(block2) && !block.equals(this.block) && block.size() > 1 && (!partition.inQ(block) || partition.isBeingWorkedOn(block))){
				
				try {
					partition.putBlock(block);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ReduceTask reduce0 = new ReduceTask(partition, block);
				reduce0.fork();

				}
			}
			
			//------------fork one new ReduceTask for block1 and start a second one for block2 on this thread------//

			if (!(block1.size() == 1)){
			ReduceTask reduce1 = new ReduceTask(partition, block1);
			reduce1.fork();

			}
			
			else {
				System.err.println(block.toString());
				partition.removeBlock_fromList(block1);
				
			}
			
			if (!(block2.size() == 1)){
			ReduceTask reduce2 = new ReduceTask(partition, block2);
			reduce2.compute();

			}
			
			else {
				System.err.println(block.toString());
				partition.removeBlock_fromList(block2);
			}
			
		}

		//---------------in case the block is not to be split, remove it from the toDo_list---------------------//

		else {
			System.err.println(block.toString());
			partition.removeBlock_fromList(block);
			

		}
		return;
	}

	/**
	 * After splitting a block, the reduction algorithm requires you to look
	 * again at all blocks that can transition to the split one. Given a block,
	 * this method returns all blocks that need to be re-checked.
	 * 
	 * @param block
	 *            The block that's about to be split.
	 * @return All blocks that need looking at after it's been split.
	 * 
	 * 
	 * */
	private Set<Set<Vertex>> getPreBlocksOfBlock(Set<Vertex> block) {

		Set<Vertex> presOfBlock = new HashSet<Vertex>(); // Pres of the argument
															// block
		Set<Set<Vertex>> preBlocks = new HashSet<Set<Vertex>>();// Set of blocks
																// that need to
																// be checked

		// get the pres of block

		for (Action action : partition.getLTS().getActions()) {

			for (Vertex vertex : block) {

				presOfBlock.addAll(partition.getLTS().weakPre(vertex, action));
			}
		}

		// get the preBlocks to be returned

		for (Set<Vertex> pBlock : partition.getBlocks()) {

			for (Vertex vertex : presOfBlock) {

				if (pBlock.contains(vertex)) {

					preBlocks.add(pBlock);
				}
			}

		}

		return preBlocks;

	}

}

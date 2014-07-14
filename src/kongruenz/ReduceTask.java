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

	@Override
	public void compute() {

		boolean split = false;
		Set<Vertex> block1 = new HashSet<Vertex>();
		Set<Vertex> block2 = new HashSet<Vertex>();
		
		here: for (Action action : partition.getLTS().getActions()) {

			for (Set<Vertex> test_block : partition.getBlocks()) {

				Set<Vertex> pre_states = new HashSet<Vertex>();

				for (Vertex state : test_block) {

					pre_states
							.addAll(partition.getLTS().weakPre(state, action));

				}

				block1 = new HashSet<Vertex>(pre_states);
				block1.retainAll(block);
				block2 = new HashSet<Vertex>(block);
				block2.removeAll(pre_states);

				if (!(block1.isEmpty() || block2.isEmpty())) {

					split = true;
					break here;

				}
			}

		}

		if (split) {

			try {
				partition.replaceBlock(block, block1, block2);
			} catch (InterruptedException e) {

			}
			
			Set<Set<Vertex>> preBlocks = getPreBlocksOfBlock(block);
			
			for (Set<Vertex> block : preBlocks){
				
				try {
					partition.putBlock(block);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ReduceTask reduce0 = new ReduceTask(partition, block);
				reduce0.fork();
				
			}

			ReduceTask reduce1 = new ReduceTask(partition, block1);
			reduce1.fork();

			ReduceTask reduce2 = new ReduceTask(partition, block2);
			reduce2.compute();

		}

		else {
			System.out.println(block.toString());
			partition.removeBlock_fromList(block);
			synchronized (partition) {
				System.out.println("Notifying");
				partition.notifyAll();
			}

		}
		return;
	}

	
	
	private Set<Set<Vertex>> getPreBlocksOfBlock(Set<Vertex> block) {

		Set<Vertex> presOfBlock = new HashSet<Vertex>(); //Pres of the argument block
		Set<Set<Vertex>> preBlocks = new HashSet<Set<Vertex>>();//Set of blocks that need to be checked

		//get the pres of block
		
		for (Action action : partition.getLTS().getActions()) {

			for (Vertex vertex : block) {

				presOfBlock.addAll(partition.getLTS().weakPre(vertex, action));
			}
		}

		//get the preBlocks to be returned
		
		for (Set<Vertex> pBlock : partition.getBlocks()) {
			
			for (Vertex vertex : presOfBlock){
				
				if (pBlock.contains(vertex)){
					
					preBlocks.add(pBlock);
				}
			}

		}
		
		return preBlocks;

	}

}

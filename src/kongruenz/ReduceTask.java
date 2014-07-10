package kongruenz;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

import javax.annotation.Generated;

import kongruenz.objects.Action;
import kongruenz.objects.Vertex;

public class ReduceTask extends RecursiveTask<Partition> {

	
	private Partition partition;
	
	
	public ReduceTask(Partition p){
		
		this.partition = p;
	}


	

	@Override
	protected Partition compute() {
		
		boolean split = false;
		Set<Vertex> block1, block2;
		
		for(Action action : partition.getLTS().getActions()){
			//TODO: implement check_Block_with_action
				
				if (block1 != Collections.EMPTY_SET && block2 != Collections.EMPTY_SET){
					
					split = true;
					break;
					
				}
				
	}
		
		if (split) {
			
			ReduceTask reduce1 = new ReduceTask(partition.generateNewPartition(block1));
			reduce1.fork();
			ReduceTask reduce2 = new ReduceTask(partition.generateNewPartition(block2));
			
			Partition newPartition = reduce2.compute();
			newPartition.unite((Partition) reduce1.join());
			return newPartition;
		}
		
		return partition;
		
	}

}

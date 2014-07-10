package kongruenz;

import java.util.concurrent.ForkJoinPool;

import kongruenz.util.LTSReader;


public class Reduce {

	public static void main(String[] args) {

		ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
		LTSReader Reader = new LTSReader(args);
		Partition partition0 = new Partition(Reader.generateLTSfromJSON());
		ReduceTask reduce0 = new ReduceTask(partition0);
		
		Partition reducedPartition = pool.invoke(reduce0);
		
		//TODO: finish the main method
	}

}

package kongruenz;

import java.util.concurrent.ForkJoinPool;

import kongruenz.util.Main;
import kongruenz.util.LTSReader;


public class Reduce {

	public static void main(String[] args) throws InterruptedException {
		
		ForkJoinPool pool = new ForkJoinPool();
		LTSReader Reader = new LTSReader(args);
		Partition partition = new Partition(Reader.generateLTSfromJSON());
		ReduceTask reduce0 = new ReduceTask(partition, partition.getLTS().getModVertices() );
		
		pool.invoke(reduce0);
		pool.shutdown();
		
		System.out.println(pool.getActiveThreadCount());
		System.out.println(partition.toString());
		
		LTS reducedLTS = partition.generateLTSfromPartition();
		
		System.out.println(pool.getActiveThreadCount());
	 	System.out.println(pool.isTerminated());
	 	System.out.println(pool.isShutdown());
	 	System.out.println("\u03C4");
	 	
	 	
	 	
	 			
		Main.openInBrowserDemo(reducedLTS.ToJson());
		//TODO: finish the main method
	}

}

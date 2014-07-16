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
		
		System.err.println(pool.getActiveThreadCount());
		System.err.println(partition.toString());
		
		LTS reducedLTS = partition.generateLTSfromPartition();
		
		System.err.println(pool.getActiveThreadCount());
	 	System.err.println(pool.isTerminated());
	 	System.err.println(pool.isShutdown());
	 	System.err.println("\u03C4");
	 	
	 	System.out.println(reducedLTS.toString());
	 	
	 	if (args.length == 1){
	 		
	 		if (args[0].equals("-d")){
	 			Main.openInBrowserDemo(reducedLTS.ToJson());
	 		}
	 	}
		
		//TODO: finish the main method
	}

}

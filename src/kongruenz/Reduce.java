package kongruenz;

import java.util.concurrent.ForkJoinPool;

import com.pseuco.project.Main;

import kongruenz.util.LTSReader;


public class Reduce {

	public static void main(String[] args) throws InterruptedException {

		ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
		LTSReader Reader = new LTSReader(args);
		Partition partition = new Partition(Reader.generateLTSfromJSON());
		ReduceTask reduce0 = new ReduceTask(partition, partition.getLTS().getModVertices() );
		
		pool.invoke(reduce0);
		
		
		System.out.println(partition.toString());
		//LTS reducedLTS = partition.generateLTSfromPartition();
		//Main.openInBrowserDemo(reducedLTS.ToJson());
		//TODO: finish the main method
	}

}

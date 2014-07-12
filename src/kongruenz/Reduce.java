package kongruenz;

import java.util.concurrent.ForkJoinPool;

import com.pseuco.project.Main;

import kongruenz.util.LTSReader;


public class Reduce {

	public static void main(String[] args) {

		ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
		LTSReader Reader = new LTSReader(args);
		Partition partition0 = new Partition(Reader.generateLTSfromJSON());
		ReduceTask reduce0 = new ReduceTask(partition0);
		
		Partition reducedPartition = pool.invoke(reduce0);
		
		LTS reducedLTS = reducedPartition.generateLTSfromPartition();
		Main.openInBrowserDemo(reducedLTS.ToJson());
		//TODO: finish the main method
	}

}

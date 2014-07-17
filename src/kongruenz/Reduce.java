package kongruenz;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ForkJoinPool;

import kongruenz.util.Main;
import kongruenz.util.LTSReader;
import kongruenz.util.Utf8IO;

public class Reduce {

	public static void main(String[] args) throws InterruptedException,
			UnsupportedEncodingException {

		// ------------------------check args----------------------------//
		// --------------------------------------------------------------//
		if (args.length < 1) {

			throw new IllegalArgumentException("Not an LTS");

		} else if (!args[0].equals("-i") && !args[0].equals("-d")) {

			throw new IllegalArgumentException("Not an LTS");
		}

		// -----------------------get the LTS from stdin------------------//
		// ---------------------------------------------------------------//

		ForkJoinPool pool = new ForkJoinPool();
		System.err.println("Enter LTS here:");
		LTSReader Reader = new LTSReader(LTSReader.getString());

		// ---------create a new Partition to iterate over----------------//
		// ---------------------------------------------------------------//

		Partition partition = new Partition(Reader.generateLTSfromJSON());

		// -------Iterates over the Partition using the algorithm---------//
		// ---------------------------------------------------------------//

		ReduceTask reduce0 = new ReduceTask(partition, partition.getLTS()
				.getModVertices());

		pool.invoke(reduce0);
		pool.shutdown();

		// ------------Minimizes and prints the reduced LTS----------------//
		// ----------------------------------------------------------------//

		LTS reducedLTS = partition.generateLTSfromPartition();

		Utf8IO.writeStdout(reducedLTS.ToJson().toString());

		// -------if the program is run with -d instead of -i as argument, it
		// will upload it to pseuCo.com-----//
		// ----------------------------------------------------------------------------------------------------//

		if (args[0].equals("-d")) {
			Main.openInBrowserDemo(reducedLTS.ToJson());
		}
	}

}

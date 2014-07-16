package reduceTaskTests;

import kongruenz.LTS;
import kongruenz.Partition;
import kongruenz.ReduceTask;
import kongruenz.util.LTSReader;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReduceTests {
	
	private static LTS testLTS;
	private static ReduceTask reducer;
	
	
	@BeforeClass
	public static void setUpBeforeClass(){
		
		LTSReader reader = new LTSReader(LTSReader.getString());
		testLTS = reader.generateLTSfromJSON();
		reducer = new ReduceTask(new Partition(testLTS), testLTS.getModVertices());
		
	}
	
	@Before
	public void setUp(){
		
	}
	
	@Test
	public void Test() {
		
		reducer.compute();
	}

}

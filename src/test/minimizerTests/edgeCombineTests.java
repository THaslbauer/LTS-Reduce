package test.minimizerTests;

import static org.junit.Assert.*;
import isomorph.IsoChecker;
import kongruenz.LTS;
import kongruenz.util.LTSReader;
import kongruenz.util.Main;
import kongruenz.util.Minimizer;

import org.junit.Test;

public class edgeCombineTests {

	@Test
	public void edgeCombineTest1() {
		String input = "{\"initialState\":\"X\",\"states\":{\"0\":{\"transitions\":[]},\"X\":{\"transitions\":[{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"X\"},{\"label\":\"a\",\"detailsLabel\":false,\"target\":\"0\"},{\"label\":\"a\",\"detailsLabel\":false,\"target\":\"Y\"}]},\"Y\":{\"transitions\":[{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"0\"},{\"label\":\"b\",\"detailsLabel\":false,\"target\":\"Y\"}]}}}";
		LTSReader reader = new LTSReader(input);
		Minimizer min = new Minimizer();
		LTS out = min.reduceEdges(reader.generateLTSfromJSON());
		IsoChecker.assertIsomorphic(out.ToJson().toString(), "{\"initialState\":\"X\",\"states\":{\"0\":{\"transitions\":[]},\"X\":{\"transitions\":[{\"label\":\"a\",\"detailsLabel\":false,\"target\":\"Y\"},{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"X\"}]},\"Y\":{\"transitions\":[{\"label\":\"b\",\"detailsLabel\":false,\"target\":\"Y\"},{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"0\"}]}}}");
//		Main.openInBrowserDemo(out.ToJson());
	}

	@Test
	public void edgeCombineTest2() {
		String input = "{\"initialState\":\"X\",\"states\":{\"0\":{\"transitions\":[]},\"X\":{\"transitions\":[{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"X\"},{\"label\":\"b\",\"detailsLabel\":false,\"target\":\"0\"},{\"label\":\"a\",\"detailsLabel\":false,\"target\":\"Y\"}]},\"Y\":{\"transitions\":[{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"0\"},{\"label\":\"b\",\"detailsLabel\":false,\"target\":\"Y\"}]}}}";
		LTSReader reader = new LTSReader(input);
		Minimizer min = new Minimizer();
		LTS out = min.reduceEdges(reader.generateLTSfromJSON());
		IsoChecker.assertIsomorphic(out.ToJson().toString(), "{\"initialState\":\"X\",\"states\":{\"0\":{\"transitions\":[]},\"X\":{\"transitions\":[{\"label\":\"b\",\"detailsLabel\":false,\"target\":\"0\"},{\"label\":\"a\",\"detailsLabel\":false,\"target\":\"Y\"},{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"X\"}]},\"Y\":{\"transitions\":[{\"label\":\"b\",\"detailsLabel\":false,\"target\":\"Y\"},{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"0\"}]}}}");
//		Main.openInBrowserDemo(out.ToJson());
	}
	
	@Test
	public void edgeCombineTest3() {
		String input = "{\"initialState\":\"X\",\"states\":{\"0\":{\"transitions\":[]},\"X\":{\"transitions\":[{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"X\"},{\"label\":\"b\",\"detailsLabel\":false,\"target\":\"0\"},{\"label\":\"a\",\"detailsLabel\":false,\"target\":\"Y\"}]},\"Y\":{\"transitions\":[{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"0\"},{\"label\":\"b\",\"detailsLabel\":false,\"target\":\"Y\"},{\"label\":\"b\",\"detailsLabel\":false,\"target\":\"0\"}]}}}";
		LTSReader reader = new LTSReader(input);
		Minimizer min = new Minimizer();
		LTS out = min.reduceEdges(reader.generateLTSfromJSON());
		IsoChecker.assertIsomorphic(out.ToJson().toString(), "{\"initialState\":\"X\",\"states\":{\"0\":{\"transitions\":[]},\"X\":{\"transitions\":[{\"label\":\"b\",\"detailsLabel\":false,\"target\":\"0\"},{\"label\":\"a\",\"detailsLabel\":false,\"target\":\"Y\"},{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"X\"}]},\"Y\":{\"transitions\":[{\"label\":\"b\",\"detailsLabel\":false,\"target\":\"Y\"},{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"0\"}]}}}");
//		Main.openInBrowserDemo(out.ToJson());
	}
	
	@Test
	public void edgeCombineTest4() {
		String input = "{\"initialState\":\"X\",\"states\":{\"0\":{\"transitions\":[]},\"X\":{\"transitions\":[{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"X\"},{\"label\":\"b\",\"detailsLabel\":false,\"target\":\"0\"},{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"Y\"}]},\"Y\":{\"transitions\":[{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"0\"},{\"label\":\"b\",\"detailsLabel\":false,\"target\":\"Y\"}]}}}";
		LTSReader reader = new LTSReader(input);
		Minimizer min = new Minimizer();
		LTS out = min.reduceEdges(reader.generateLTSfromJSON());
		IsoChecker.assertIsomorphic(out.ToJson().toString(), "{\"initialState\":\"X\",\"states\":{\"0\":{\"transitions\":[]},\"X\":{\"transitions\":[{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"X\"},{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"Y\"}]},\"Y\":{\"transitions\":[{\"label\":\"b\",\"detailsLabel\":false,\"target\":\"Y\"},{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"0\"}]}}}");
//		Main.openInBrowserDemo(out.ToJson());
	}
	
	@Test
	public void edgeCombineTest5() {
		String input = "{\"initialState\":\"X\",\"states\":{\"0\":{\"transitions\":[]},\"X\":{\"transitions\":[{\"label\":\"a\",\"detailsLabel\":false,\"target\":\"0\"},{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"a.τ.0\"}]},\"a.τ.0\":{\"transitions\":[{\"label\":\"a\",\"detailsLabel\":false,\"target\":\"τ.0\"}]},\"τ.0\":{\"transitions\":[{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"0\"}]}}}";
		LTSReader reader = new LTSReader(input);
		Minimizer min = new Minimizer();
		LTS out = min.reduceEdges(reader.generateLTSfromJSON());
		IsoChecker.assertIsomorphic(out.ToJson().toString(), "{\"initialState\":\"X\",\"states\":{\"0\":{\"transitions\":[]},\"a.τ.0\":{\"transitions\":[{\"label\":\"a\",\"detailsLabel\":false,\"target\":\"τ.0\"}]},\"X\":{\"transitions\":[{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"a.τ.0\"}]},\"τ.0\":{\"transitions\":[{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"0\"}]}}}");
//		Main.openInBrowserDemo(out.ToJson());
	}
	
	@Test
	public void edgeCombineTest6() {
		String input = "{\"initialState\":\"X\",\"states\":{\"X\":{\"transitions\":[{\"label\":\"a\",\"detailsLabel\":false,\"target\":\"X\"},{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"Y\"}]},\"Y\":{\"transitions\":[{\"label\":\"a\",\"detailsLabel\":false,\"target\":\"τ.X\"}]},\"τ.X\":{\"transitions\":[{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"X\"}]}}}";
		LTSReader reader = new LTSReader(input);
		Minimizer min = new Minimizer();
		LTS out = min.reduceEdges(reader.generateLTSfromJSON());
		IsoChecker.assertIsomorphic(out.ToJson().toString(), "{\"initialState\":\"X\",\"states\":{\"τ.X\":{\"transitions\":[{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"X\"}]},\"X\":{\"transitions\":[{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"Y\"}]},\"Y\":{\"transitions\":[{\"label\":\"a\",\"detailsLabel\":false,\"target\":\"τ.X\"}]}}}");
//		Main.openInBrowserDemo(out.ToJson());
	}
	
	@Test
	public void edgeCombineTest7() {
		String input = "{\"initialState\":\"X\",\"states\":{\"0\":{\"transitions\":[]},\"X\":{\"transitions\":[{\"label\":\"a\",\"detailsLabel\":false,\"target\":\"0\"},{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"Y\"},{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"X\"},{\"label\":\"τ\",\"detailsLabel\":false,\"target\":\"0\"}]},\"Y\":{\"transitions\":[{\"label\":\"b\",\"detailsLabel\":false,\"target\":\"0\"}]}}}";
		LTSReader reader = new LTSReader(input);
		Minimizer min = new Minimizer();
		LTS out = min.reduceEdges(reader.generateLTSfromJSON());
		IsoChecker.assertIsomorphic(out.ToJson().toString(), input);
//		Main.openInBrowserDemo(out.ToJson());
	}
}

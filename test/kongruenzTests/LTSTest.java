package kongruenzTests;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import kongruenz.LTS;
import kongruenz.objects.Action;
import kongruenz.objects.Vertex;
import kongruenz.objects.LabeledEdge;
import kongruenz.util.GraphSearch;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class LTSTest {
	
	private static LTS linearTwoNodes;
	private static LTS splitTwoNodesFromStart;
	private static LTS linearThreeNodesTauAtFront;
	private static LTS linearThreeNodesTauInBack;
	private static LTS linearThreeNodesOnlyTau;
	private static Vertex start;
	private static Vertex end1;
	private static Vertex end2;
	private static Vertex middle;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		start = new Vertex("Start");
		end1 = new Vertex("End1");
		end2 = new Vertex("End2");
		middle = new Vertex("Middle");
		List<Vertex> states = new LinkedList<Vertex>();
		states.add(start);
		states.add(end1);
		List<LabeledEdge> labeledEdges = new LinkedList<LabeledEdge>();
		labeledEdges.add(new LabeledEdge(start, end1, new Action("a")));
		linearTwoNodes = new LTS(new LinkedList<Vertex>(states),
				new LinkedList<LabeledEdge>(labeledEdges), start);
		states.add(end2);
		labeledEdges.add(new LabeledEdge(start, end2, new Action("a")));
		splitTwoNodesFromStart = new LTS(new LinkedList<Vertex>(states),
				new LinkedList<LabeledEdge>(labeledEdges), start);
		states.remove(end2);
		labeledEdges.clear();
		states.add(middle);
		labeledEdges.add(new LabeledEdge(start, middle, Action.TAU));
		labeledEdges.add(new LabeledEdge(middle, end1, new Action("a")));
		linearThreeNodesTauAtFront = new LTS(new LinkedList<Vertex>(states),
				new LinkedList<LabeledEdge>(labeledEdges), start);
		labeledEdges.clear();
		labeledEdges.add(new LabeledEdge(start, middle, new Action("a")));
		labeledEdges.add(new LabeledEdge(middle, end1, Action.TAU));
		linearThreeNodesTauInBack = new LTS(new LinkedList<Vertex>(states),
				new LinkedList<LabeledEdge>(labeledEdges), start);
		labeledEdges.clear();
		labeledEdges.add(new LabeledEdge(start, middle));
		labeledEdges.add(new LabeledEdge(middle, end1));
		linearThreeNodesOnlyTau = new LTS(states, labeledEdges, start);
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testReaches() {
		Collection<Vertex> nodes = linearTwoNodes.post(start);
		assertTrue("List for linearTwoNodes should only contain 'Start' and 'End1', was "+nodes.toString(),
				nodes.size() == 2 && nodes.contains(end1) && nodes.contains(start));
		nodes = splitTwoNodesFromStart.post(start);
		assertTrue("List for splitTwoNodesFromStart should only contain 'Start', 'End1' and 'End2', was "
				+nodes.toString(),
				nodes.size() == 3 && nodes.contains(end1) && nodes.contains(end2) && nodes.contains(start));
	}
	
	@Test
	public void testReachableWith() {
		boolean reaches = linearTwoNodes.reachableWith(start, end1, new Action("a"));
		assertTrue(reaches);
	}
	
	@Test
	public void testGetTransitions() {
		Collection<LabeledEdge> trans = linearThreeNodesTauAtFront.getTransitions(start, middle);
		assertTrue(trans.contains(new LabeledEdge(start, middle, Action.TAU)));
	}
	
	@Test
	public void testTaureachableWith() {
		boolean reaches = false;
		reaches = linearTwoNodes.taureachableWith(start, start, Action.TAU);
		assertTrue("Has to find itself with tau", reaches);
		reaches = linearThreeNodesTauAtFront.taureachableWith(start, end1, new Action("a"));
		assertTrue("Has to jump over tau in front", reaches);
		reaches = linearThreeNodesTauInBack.taureachableWith(start, end1, new Action("a"));
		assertTrue("Has to jump over tau in back", reaches);
	}
	
	@Test(timeout = 10000)
	public void testGraphSearch() {
		GraphSearch searcher = new GraphSearch(linearThreeNodesOnlyTau);
		assertTrue(searcher.findForward(start, end1, Action.TAU));
	}
}

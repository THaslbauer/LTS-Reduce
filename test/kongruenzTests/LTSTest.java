package kongruenzTests;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import kongruenz.Action;
import kongruenz.LTS;
import kongruenz.State;
import kongruenz.Transition;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class LTSTest {
	
	private LTS linearTwoNodes;
	private LTS splitTwoNodesFromStart;
	private LTS linearThreeNodesTauAtFront;
	private LTS linearThreeNodesTauInBack;
	private State start;
	private State end1;
	private State end2;
	private State middle;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
		start = new State("Start");
		end1 = new State("End1");
		List<State> states = new LinkedList<State>();
		states.add(start);
		states.add(end1);
		List<Transition> transitions = new LinkedList<Transition>();
		transitions.add(new Transition(start, end1, new Action("a")));
		linearTwoNodes = new LTS(new LinkedList<State>(states),
				new LinkedList<Transition>(transitions), start);
		end2 = new State("End2");
		states.add(end2);
		transitions.add(new Transition(start, end2, new Action("a")));
		splitTwoNodesFromStart = new LTS(new LinkedList<State>(states),
				new LinkedList<Transition>(transitions), start);
		states.remove(end2);
		transitions.clear();
		middle = new State("Middle");
		states.add(middle);
		transitions.add(new Transition(start, middle, Action.TAU));
		transitions.add(new Transition(middle, end1, new Action("a")));
		linearThreeNodesTauAtFront = new LTS(new LinkedList<State>(states),
				new LinkedList<Transition>(transitions), start);
		transitions.clear();
		transitions.add(new Transition(start, middle, new Action("a")));
		transitions.add(new Transition(middle, end1, Action.TAU));
		linearThreeNodesTauInBack = new LTS(new LinkedList<State>(states),
				new LinkedList<Transition>(transitions), start);
	}

	@Test
	public void testReaches() {
		Collection<State> nodes = linearTwoNodes.post(start);
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
		Collection<Transition> trans = linearThreeNodesTauAtFront.getTransitions(start, middle);
		assertTrue(trans.contains(new Transition(start, middle, Action.TAU)));
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

}

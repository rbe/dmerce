/*
 * Datei angelegt am 14.10.2003
 */
package com.wanci.dmerce.test.workflow;

import junit.framework.TestCase;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.exceptions.WorkflowRuntimeException;
import com.wanci.dmerce.workflow.AlwaysTrueCondition;
import com.wanci.dmerce.workflow.DoNothingAction;
import com.wanci.dmerce.workflow.State;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.dmerce.workflow.TransitionFactory;
import com.wanci.dmerce.workflow.Workflow;
import com.wanci.dmerce.workflow.WorkflowContext;

/**
 * @author Masanori Fujita
 */
public class StateTest extends TestCase {

	private Workflow workflow;
	private State state;

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		this.state = new State(workflow, "State 1");
	}

	/**
	 * Constructor for StateTest.
	 * @param arg0
	 */
	public StateTest(String arg0) {
		super(arg0);
		this.workflow = new Workflow("Test-Workflow");
	}

	public void testState() {
		State state = new State(workflow, "Teststate");
		assertEquals(true, state != null);
		
		boolean fehlerBekommen;

		// Als Workflow null übergeben		
		fehlerBekommen = true;
		try {
			state = new State(null, "Teststate");
		}
		catch (AssertionError e) {
			fehlerBekommen = true;
		}
		assertTrue(fehlerBekommen);
		
		// Als State-ID null übergeben
		fehlerBekommen = true;
		try {
			state = new State(workflow, null);
		}
		catch (AssertionError e) {
			fehlerBekommen = true;
		}
		assertTrue(fehlerBekommen);
		
		// Als State-ID "" übergeben
		fehlerBekommen = true;
		try {
			state = new State(workflow, "");
		}
		catch (AssertionError e) {
			fehlerBekommen = true;
		}
		assertTrue(fehlerBekommen);
		
	}

	public void testGetWorkflow() {
		assertEquals(workflow, state.getWorkflow());
	}

	public void testAddGetTransitions() {
		State dest = new State(workflow, "Dest-State");
		try {
			Transition transition1 = TransitionFactory.createNoActionTransition(state, dest);
			assertEquals(1, state.getTransitions().size());
			assertEquals(transition1, (Transition) state.getTransitions().get(0));
			Transition transition2 = TransitionFactory.createNoActionTransition(state, dest);
			assertEquals(2, state.getTransitions().size());
			assertEquals(transition2, (Transition) state.getTransitions().get(1));
		} catch (WorkflowConfigurationException e) {
			fail(e.getMessage());
		}
	}

	public void testGetStateId() {
		assertEquals("State 1", state.getStateId());
	}

	public void testIsEndpoint() {
		State endpoint = new State(workflow, "Endpoint");
		State dest = new State(workflow, "Dest-State");
		assertTrue(endpoint.isEndpoint());
		try {
			Transition transition1 = TransitionFactory.createNoActionTransition(endpoint, dest);
			assertTrue(!endpoint.isEndpoint());
		} catch (WorkflowConfigurationException e) {
			fail(e.getMessage());
		}
	}

	public void testProcess() {
		// Test ohne Übergänge
		try {
			state.process(new WorkflowContext());
			fail("Ein Zustand ohne ausgehende Übergänge muss einen Fehler zurückgeben.");
		} catch (WorkflowRuntimeException e) {
			// bleibt leer, weil der Fehler hier explizit abgefangen werden soll. 
		} catch (WorkflowConfigurationException e) {
			fail(e.getMessage());
		}
		// Test mit gültigen Übergängen
		try {
			State destState = new State(workflow, "Dest-State");
			Transition transition = new Transition(state, destState, "Transition 1");
			new AlwaysTrueCondition(transition);
			new DoNothingAction(transition);
			WorkflowContext context = new WorkflowContext();
			state.process(context);
			assertSame(destState, context.getCurrentState());
		} catch (WorkflowRuntimeException e) {
			fail(e.getMessage()); 
		} catch (WorkflowConfigurationException e) {
			fail(e.getMessage());
		}
	}

}

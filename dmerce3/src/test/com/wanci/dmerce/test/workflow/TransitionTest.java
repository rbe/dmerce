/*
 * Datei angelegt am 29.09.2003
 */
package com.wanci.dmerce.test.workflow;

import junit.framework.TestCase;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.exceptions.WorkflowRuntimeException;
import com.wanci.dmerce.workflow.AlwaysTrueCondition;
import com.wanci.dmerce.workflow.Condition;
import com.wanci.dmerce.workflow.DoNothingAction;
import com.wanci.dmerce.workflow.NegativeCondition;
import com.wanci.dmerce.workflow.State;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.dmerce.workflow.Workflow;
import com.wanci.dmerce.workflow.WorkflowContext;

/**
 * @author Masanori Fujita
 */
public class TransitionTest extends TestCase {

	protected Workflow workflow;
	protected State state1;
	protected State state2;
	
	protected void setUp() throws Exception {
		super.setUp();
		workflow = new Workflow("Workflow 1");
		state1 = new State(workflow, "State 1");
		state2 = new State(workflow, "State 2");
	}

	public void testTransitionWithParent() {
		try {
			Transition transition = new Transition(state1, "Transition 1");
			assertSame(state1, transition.getParentState());
			
			try {
				transition.getDestinationState();
				fail();
			}
			catch (WorkflowConfigurationException e) {}
			catch (AssertionError e) {}

			try {
				transition.getCondition();
				fail();
			} catch (WorkflowConfigurationException e) {}

			try {
				transition.getAction();
				fail();
			} catch (WorkflowConfigurationException e) {}
			
			transition.setDestinationState(state2);			
			assertSame(state2, transition.getDestinationState());
			
			new AlwaysTrueCondition(transition);
			transition.getCondition();
			
			new DoNothingAction(transition);
			transition.getAction();
			
		} catch (WorkflowConfigurationException e) {
			fail(e.getMessage());
		}
	}

	public void testTransitionWithParentAndDestination() {
		try {
			Transition transition = new Transition(state1, state2, "Transition 1");
			assertSame(state1, transition.getParentState());
			assertSame(state2, transition.getDestinationState());
			
			try {
				transition.getCondition();
				fail();
			} catch (WorkflowConfigurationException e) {}

			try {
				transition.getAction();
				fail();
			} catch (WorkflowConfigurationException e) {}
			
			new AlwaysTrueCondition(transition);
			transition.getCondition();
			
			new DoNothingAction(transition);
			transition.getAction();
			
		} catch (WorkflowConfigurationException e) {
			fail(e.getMessage());
		}
	}

	public void testTransitionWithParentAndDestinationId() {
		try {
			Transition transition = new Transition(state1, "State 2", "Transition 1");
			assertSame(state1, transition.getParentState());
			assertSame(state2, transition.getDestinationState());
			
			try {
				transition.getCondition();
				fail();
			} catch (WorkflowConfigurationException e) {}

			try {
				transition.getAction();
				fail();
			} catch (WorkflowConfigurationException e) {}
			
			new AlwaysTrueCondition(transition);
			transition.getCondition();
			
			new DoNothingAction(transition);
			transition.getAction();
			
		} catch (WorkflowConfigurationException e) {
			fail(e.getMessage());
		}
	}

	public void testTransitionWithParentAndDestinationIdFail() {
		try {
			Transition transition = new Transition(state1, "State 5", "Transition 1");
			try {
				transition.getDestinationState();
				fail("Der Zustand 5 existiert nicht und dürfte nicht gefunden werden.");
			}
			catch (Exception e) {}
			catch (AssertionError e) {}
		} catch (WorkflowConfigurationException e) {
			fail(e.getMessage());
		}
	}

	public void testGetWorkflow() {
		try {
			Transition transition = new Transition(state1, state2, "Transition 1");
			assertSame(workflow, transition.getWorkflow());
		} catch (WorkflowConfigurationException e) {
			fail(e.getMessage());
		}
	}

	public void testIsSatisfied() {
		try {
			Transition transition = new Transition(state1, state2, "Transition 1");
			new AlwaysTrueCondition(transition);
			assertTrue(transition.isSatisfied(new WorkflowContext()));
		} catch (WorkflowConfigurationException e) {
			fail(e.getMessage());
		}
	}

	public void testExecute() {
		try {
			Transition transition = new Transition(state1, state2, "Transition 1");
			// Test mit Allways-True-Condition
			Condition aCondition = new AlwaysTrueCondition(transition);
			new DoNothingAction(transition);
			int result = transition.process(new WorkflowContext());
			assertEquals(Transition.FIRE, result);
			// Test mit negierter Allways-True-Condition
			Condition bCondition = new NegativeCondition(aCondition);
			transition.setCondition(bCondition);
			result = transition.process(new WorkflowContext());
			assertEquals(Transition.SKIP, result);
		} catch (WorkflowConfigurationException e) {
			fail(e.getMessage());
		} catch (WorkflowRuntimeException e) {
			fail(e.getMessage());
		}
	}

}

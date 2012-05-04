/*
 * Datei angelegt am 29.09.2003
 */
package com.wanci.dmerce.test.workflow;

import junit.framework.TestCase;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.workflow.Action;
import com.wanci.dmerce.workflow.DoNothingAction;
import com.wanci.dmerce.workflow.State;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.dmerce.workflow.Workflow;

/**
 * @author Masanori Fujita
 */
public class ActionTest extends TestCase {

	protected Workflow workflow;
	protected State state1;
	protected State state2;
	protected Transition transition;
	protected Action action;

	protected void setUp() throws Exception {
		super.setUp();
		workflow = new Workflow("Workflow 1");
		state1 = new State(workflow, "State 1");
		state2 = new State(workflow, "State 2");
		transition = new Transition(state1, state2, "Transition 1");
		// Einfachsten Action-Typ verwenden
		action = new DoNothingAction(transition);
	}

	public void testGetParentTransition() {
		assertSame(transition, action.getParentTransition());
	}

	public void testGetParentState() {
		assertSame(state1, action.getParentState());
	}

	public void testGetDestinationState() {
		try {
			assertSame(state2, action.getDestinationState());
		} catch (WorkflowConfigurationException e) {
			fail(e.getMessage());
		}
	}

	public void testGetWorkflow() {
		assertSame(workflow, action.getWorkflow());
	}

}

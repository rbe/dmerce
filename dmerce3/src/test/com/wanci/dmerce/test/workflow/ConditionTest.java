/*
 * Datei angelegt am 29.09.2003
 */
package com.wanci.dmerce.test.workflow;

import junit.framework.TestCase;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.workflow.AlwaysTrueCondition;
import com.wanci.dmerce.workflow.Condition;
import com.wanci.dmerce.workflow.State;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.dmerce.workflow.Workflow;

/**
 * @author Masanori Fujita
 */
public class ConditionTest extends TestCase {
	
	protected Workflow workflow;
	protected State state1;
	protected State state2;
	protected Transition transition;
	protected Condition condition;
	
	protected void setUp() throws Exception {
		super.setUp();
		workflow = new Workflow("Workflow 1");
		state1 = new State(workflow, "State 1");
		state2 = new State(workflow, "State 2");
		transition = new Transition(state1, state2, "Transition 1");
		condition = new AlwaysTrueCondition(transition);
	}

	public void testGetParentTransition() {
		assertSame(transition, condition.getParentTransition());
	}

	public void testGetParentState() {
		assertSame(state1, condition.getParentState());
	}

	public void testGetDestinationState() {
		try {
			assertSame(state2, condition.getDestinationState());
		} catch (WorkflowConfigurationException e) {
			fail(e.getMessage());
		}
	}

	public void testGetWorkflow() {
		assertSame(workflow, condition.getWorkflow());
	}

}

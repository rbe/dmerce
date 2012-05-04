/*
 * Datei angelegt am 14.10.2003
 */
package com.wanci.dmerce.test.workflow;

import junit.framework.TestCase;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.exceptions.WorkflowRuntimeException;
import com.wanci.dmerce.workflow.AlwaysTrueCondition;
import com.wanci.dmerce.workflow.State;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.dmerce.workflow.TransitionFactory;
import com.wanci.dmerce.workflow.Workflow;
import com.wanci.dmerce.workflow.WorkflowContext;

/**
 * @author Masanori Fujita
 */
public class TransitionFactoryTest extends TestCase {

	protected Workflow workflow;
	protected State state1;
	protected State state2;
	
	protected void setUp() throws Exception {
		super.setUp();
		workflow = new Workflow("Workflow 1");
		state1 = new State(workflow, "State 1");
		state2 = new State(workflow, "State 2");
	}

	public void testCreateNoActionTransition() {
		try {
			Transition t = TransitionFactory.createNoActionTransition(state1, state2);
			assertSame(state1, t.getParentState());
			assertSame(state2, t.getDestinationState());
			new AlwaysTrueCondition(t);
			t.process(new WorkflowContext());
		} catch (WorkflowConfigurationException e) {
			fail(e.getMessage());
		} catch (WorkflowRuntimeException e) {
			fail(e.getMessage());
		}
	}

	/*
	 * Class to test for Transition createNoConditionTransition(State)
	 */
	public void testCreateNoConditionTransitionState() {
		try {
			Transition t = TransitionFactory.createNoConditionTransition(state1);
			assertSame(state1, t.getParentState());
		} catch (WorkflowConfigurationException e) {
			fail(e.getMessage());
		}
	}

	/*
	 * Class to test for Transition createNoConditionTransition(State, State)
	 */
	public void testCreateNoConditionTransitionStateState() {
		try {
			Transition t = TransitionFactory.createNoConditionTransition(state1, state2);
			assertSame(state1, t.getParentState());
			assertSame(state2, t.getDestinationState());
		} catch (WorkflowConfigurationException e) {
			fail(e.getMessage());
		}
	}

}

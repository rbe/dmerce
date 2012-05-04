/*
 * Datei angelegt am 13.10.2003
 */
package com.wanci.dmerce.test.workflow;

import junit.framework.TestCase;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.exceptions.WorkflowRuntimeException;
import com.wanci.dmerce.workflow.AlwaysTrueCondition;
import com.wanci.dmerce.workflow.DoNothingAction;
import com.wanci.dmerce.workflow.State;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.dmerce.workflow.Workflow;
import com.wanci.dmerce.workflow.WorkflowContext;

/**
 * @author Masanori Fujita
 */
public class WorkflowTest extends TestCase {
	
	private Workflow workflow;
	 
	public void testGetWorkflowId() {
		workflow = new Workflow("Workflow 1");		
		assertEquals("Workflow 1", workflow.getWorkflowId());
	}

	public void testWorkflow() {
		workflow = new Workflow("Workflow 1");		
		assertEquals(true, workflow != null);
		
		boolean fehlerBekommen = false;
		try {
			new Workflow(null);
		}
		catch (AssertionError e) {
			fehlerBekommen = true;
		}
		assertEquals(true, fehlerBekommen);
	}

	public void testAddGetState() {
		workflow = new Workflow("Workflow 1");		
		// Hinzufügen und Holen von States prüfen
		assertEquals(0, workflow.getStateCount());
		State state1 = new State(workflow, "State 1");
		assertEquals(1, workflow.getStateCount());
		State state2 = new State(workflow, "State 2");
		assertEquals(2, workflow.getStateCount());
		assertEquals(state1, workflow.getState("State 1"));
		assertEquals(state2, workflow.getState("State 2"));

		// Prüfen, ob ordentliche Fehlermeldungen geworfen werden
		boolean fehlerBekommen;
		
		// Fehler bei Abrufen nicht existenter Zustände
		fehlerBekommen = false;
		try {
			workflow.getState("State 3");
		}
		catch (AssertionError e) {
			fehlerBekommen = true;
		}
		assertEquals(true, fehlerBekommen);
		
		// Fehler bei Abrufen eines Zustandes null
		fehlerBekommen = false;
		try {
			workflow.getState(null);
		}
		catch (AssertionError e) {
			fehlerBekommen = true;
		}
		assertEquals(true, fehlerBekommen);

		// Fehler bei Abrufen eines Zustandes ""
		fehlerBekommen = false;
		try {
			workflow.getState("");
		}
		catch (AssertionError e) {
			fehlerBekommen = true;
		}
		assertEquals(true, fehlerBekommen);
	}

	public void testProcess() {
		try {
			workflow = new Workflow("Workflow 1");
			State state1 = new State(workflow, "State 1");
			State state2 = new State(workflow, "State 2"); 		
			Transition transition = new Transition(state1, state2, "Transition 1");
			new AlwaysTrueCondition(transition);
			new DoNothingAction(transition);
			 		
			WorkflowContext context = new WorkflowContext();
			context.setCurrentState(state1);
			workflow.process(context);
			assertSame(state2, context.getCurrentState());
		} catch (WorkflowRuntimeException e) {
			fail(e.getMessage());
		} catch (WorkflowConfigurationException e) {
			fail(e.getMessage());
		}
	}
	
}

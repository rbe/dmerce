/*
 * Datei angelegt am 29.09.2003
 */
package com.wanci.dmerce.test.workflow;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.workflow.AlwaysTrueCondition;
import com.wanci.dmerce.workflow.WorkflowContext;

/**
 * @author Masanori Fujita
 */
public class AlwaysTrueConditionTest extends ConditionTest {
	
	protected void setUp() throws Exception {
		super.setUp();
		this.condition = new AlwaysTrueCondition(transition);
	}

	public void testIsSatisfied() {
		try {
			assertEquals(true, condition.isSatisfied(new WorkflowContext()));
		} catch (WorkflowConfigurationException e) {
		}
	}

}

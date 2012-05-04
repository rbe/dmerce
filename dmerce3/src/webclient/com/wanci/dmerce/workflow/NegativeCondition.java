/*
 * Datei angelegt am 20.10.2003
 */
package com.wanci.dmerce.workflow;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;

/**
 * Eine Condition, die die enthaltene Condition negiert. 
 * @author Masanori Fujita
 */
public class NegativeCondition extends Condition {
	
	private Condition condition;
	
	public NegativeCondition(Condition condition) {
		assert condition != null : "Übergebene Condition darf nicht null sein.";
		this.condition = condition;
	}

	public boolean isSatisfied(WorkflowContext context) throws WorkflowConfigurationException {
		return ! condition.isSatisfied(context);
	}

}

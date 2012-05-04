/*
 * Datei angelegt am 29.09.2003
 */
package com.wanci.dmerce.workflow;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;

/**
 * Repr�sentiert eine Null-Aktion, d.h eine Dummy-Aktion, die dazu
 * verwendet wird, die Aktion in einer Transition auszulassen.
 * @author Masanori Fujita
 */
public class DoNothingAction extends Action {
	
	public DoNothingAction(Transition parent) throws WorkflowConfigurationException {
		super(parent);
		setDescription("Diese Aktion f�hrt nichts aus.");
	}

	public void execute(WorkflowContext context) {
		return;
	}

}

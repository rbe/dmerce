/*
 * Datei angelegt am 29.09.2003
 */
package com.wanci.dmerce.workflow;

/**
 * Eine Dummy-Bedingung, die dazu verwendet wird, eine Bedingungsprüfung
 * zu übergehen.
 * @author Masanori Fujita
 */
public class AlwaysTrueCondition extends Condition {
	
	public AlwaysTrueCondition(Transition parent) {
		super(parent);
		setDescription("Diese Bedingung ist immer erfüllt.");
	}

	/*
	 * Gibt immer True zurück.
	 */
	public boolean isSatisfied(WorkflowContext context) {
		return true;
	}

}

/*
 * Datei angelegt am 03.11.2003
 */
package com.wanci.dmerce.workflow.webapp;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.workflow.Condition;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.dmerce.workflow.WorkflowContext;

/**
 * Allgemeine Condition-Klasse für Web-Anwendungen.
 * @author Masanori Fujita
 */
public abstract class WebappCondition extends Condition {

	/**
	 * Standard-Konstruktor
	 */
	public WebappCondition() {
		super();
	}

	/**
	 * Konstruktor, der die Condition direkt mit einer Transition
	 * verbindet.
	 * @param parent
	 */
	public WebappCondition(Transition parent) {
		super(parent);
	}
		
	
	/**
	 * Durch Überschreiben der geerbten Methode wird sichergestellt,
	 * dass nur Kontexte vom Typ WebappWorkflowContext übergeben werden.
	 */
	public boolean isSatisfied(WorkflowContext context) throws WorkflowConfigurationException {
		assert WebappWorkflowContext.class.isAssignableFrom(context.getClass()) : "Der übergebene Kontext muss vom Typ WebappWorkflowContext sein.";
		return isSatisfied((WebappWorkflowContext) context);
	}

	/**
	 * Abstrakte Methode, die von allen Webapp-Conditions überschrieben
	 * werden muss.
	 */
	public abstract boolean isSatisfied(WebappWorkflowContext context) throws WorkflowConfigurationException ;
	
}

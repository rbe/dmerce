/*
 * Datei angelegt am 13.10.2003
 */
package com.wanci.dmerce.workflow.webapp;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.exceptions.WorkflowRuntimeException;
import com.wanci.dmerce.workflow.Action;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.dmerce.workflow.WorkflowContext;

/**
 * Eine WebappAction erweitert die generische Action-Klasse eines
 * Workflows, um Methoden anzubieten, die Webanwendungen besser
 * unterstützen.
 * 
 * @author Masanori Fujita
 */
public abstract class WebappAction extends Action {

	public WebappAction(Transition parent) throws WorkflowConfigurationException {
		super(parent);
	}
	
	/**
	 * Implementierung der generischen execute-Methode. Sie delegiert
	 * den Aufruf nach dem Casting des Kontextes an die eigene execute-Methode
	 * weiter.
	 */
	public void execute(WorkflowContext context) throws WorkflowRuntimeException, WorkflowConfigurationException {
		assert WebappWorkflowContext.class.isAssignableFrom(context.getClass()) : "Übergebener Kontext ist nicht vom Typ WebappWorkflowContext oder von ihm abeleitet.";
		execute((WebappWorkflowContext) context);
	}
	
	/**
	 * Spezialisierte execute-Methode, die in Webanwendungen den
	 * speziellen WorkflowContext übergeben bekommt.
	 * @param context
	 */
	protected abstract void execute(WebappWorkflowContext context) throws WorkflowRuntimeException, WorkflowConfigurationException;

}

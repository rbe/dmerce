/*
 * Datei angelegt am 14.11.2003
 */
package com.wanci.dmerce.workflow.webapp;

import java.util.Iterator;
import java.util.Map;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.exceptions.WorkflowRuntimeException;
import com.wanci.dmerce.workflow.Transition;

/**
 * Action, die fest vorgegebene Werte in den Kontext schreibt.
 * @author Masanori Fujita
 */
public class SetContextValuesAction extends WebappAction {
	
	Map paramMap;

	/**
	 * Erzeugt die Action mit den später im Kontext zu setzenden Werten
	 * @param parameters Map mit Key (String), Value (String)
	 * @throws WorkflowConfigurationException
	 */
	public SetContextValuesAction(Transition parent, Map parameters) throws WorkflowConfigurationException {
		super(parent);
		this.paramMap = parameters;
	}

	/**
	 * Überträgt die im Konstruktor festgelegten Werte in den Kontext.
	 */
	public void execute(WebappWorkflowContext context) throws WorkflowRuntimeException {
		Iterator it = paramMap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			context.put(key, paramMap.get(key));
		}
	}

}

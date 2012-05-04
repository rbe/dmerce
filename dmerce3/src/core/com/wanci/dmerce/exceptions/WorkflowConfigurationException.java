/*
 * Datei angelegt am 29.09.2003
 */
package com.wanci.dmerce.exceptions;

/**
 * Diese Exception wird vom Workflow-Engine verwendet, um unerwartete
 * Fehler bei der Initialisierung von Workflows zu melden.
 * @author Masanori Fujita
 */
public class WorkflowConfigurationException extends Exception {

	/**
	 * @param string
	 */
	public WorkflowConfigurationException(String string) {
		super(string);
	}
	
}

/*
 * Datei angelegt am 03.11.2003
 */
package com.wanci.dmerce.workflow.webapp;

import com.wanci.dmerce.workflow.Transition;

/**
 * Eine Condition, die pr�ft, ob eine bestimmte Kontext-Variable
 * gesetzt ist.
 * @author Masanori Fujita
 */
public class WebappContextVarSetCondition extends WebappCondition {
	
	private String strVarName;

	/**
	 * Initialisiert diese Bedingung mit dem Namen der abzupr�fenden Kontextvariable
	 * @param parent Eltern-Transition
	 * @param varName Name der Kontext-Variable, die vorhanden sein muss, damit
	 * 		diese Condition greift.
	 */
	public WebappContextVarSetCondition(Transition parent, String varName) {
		super(parent);
		assert varName != null && !varName.equals("") : "Der �bergebene Variablenname darf nicht null oder leer sein.";
		strVarName = varName;
		setDescription("Diese Bedingung greift genau dann, wenn eine Kontextvariable mit dem Namen "+varName+" gesetzt ist.");
	}

	/**
	 * @return true, falls die Variable im Kontext gesetzt ist, false sonst.
	 */
	public boolean isSatisfied(WebappWorkflowContext context) {
		Object o = context.get(strVarName);
		return o != null;
	}

}

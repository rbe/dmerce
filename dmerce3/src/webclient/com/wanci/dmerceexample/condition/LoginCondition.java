package com.wanci.dmerceexample.condition;

import com.wanci.dmerce.exceptions.FieldNotFoundException;
import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.dmerce.workflow.webapp.WebappCondition;
import com.wanci.dmerce.workflow.webapp.WebappWorkflowContext;

/**
 * Beispiel Klasse zur Prüfung von Login Daten. 
 * dies ist eine einfache Klasse die eine Login-Bedingung darstellt.
 * Es wird als Passwort der umgedrehte Loginname erwartet. 
 * 
 * Die Felder zum Login und Passwort sind im WebappWorkflowContext abgelegt
 * 
 * @author Masanori Fujita
 */
public class LoginCondition extends WebappCondition {

	/**
	 * constructor
	 * @param parent
	 */
	public LoginCondition(Transition parent) {
		super(parent);
	}

	/**
	 * prüfroutine
	 * 
	 * @return Bedingung erfüllt oder nicht
	 */
	public boolean isSatisfied(WebappWorkflowContext context) throws WorkflowConfigurationException {
		String username;
		String passwort;
		try {
			username = context.getFormField("login","login","name").getValue();
			passwort = context.getFormField("login","login","passwort").getValue();
		} catch (FieldNotFoundException e) {
			WorkflowConfigurationException wce = new WorkflowConfigurationException(e.getMessage());
			wce.setStackTrace(e.getStackTrace());
			throw wce;
		}
		
		// Aus Spaß: Das passwort muss dem umgedrehten Usernamen entsprechen.
		// An dieser Stelle muss eigentlich eine DB-Abfrage erfolgen.
		char[] arrayChar = username.toCharArray();
		char[] arrayRes = new char[arrayChar.length];
		int j = 0;
		for (int i = arrayChar.length-1; i >= 0; i--) {
			arrayRes[j++] = arrayChar[i];
		}
		
		return String.valueOf(arrayRes).equals(passwort);
	}

}

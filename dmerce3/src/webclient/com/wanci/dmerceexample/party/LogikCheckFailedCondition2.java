/*
 * Datei angelegt am 17.11.2003
 */
package com.wanci.dmerceexample.party;

import com.wanci.dmerce.exceptions.FieldNotFoundException;
import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.taglib.form.FormField;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.dmerce.workflow.webapp.WebappCondition;
import com.wanci.dmerce.workflow.webapp.WebappWorkflowContext;

/**
 * @author Masanori Fujita
 */
public class LogikCheckFailedCondition2 extends WebappCondition {

	public LogikCheckFailedCondition2(Transition parent) {
		super(parent);
	}

	public boolean isSatisfied(WebappWorkflowContext context) throws WorkflowConfigurationException {
		boolean result = false;
		// Alkohol am Steuer verbieten
		FormField feld;
		try {
			feld = context.getFormField("begleitunginfo","einladung","trinken2");
		} catch (FieldNotFoundException e) {
			WorkflowConfigurationException wce = new WorkflowConfigurationException(e.getMessage());
			wce.setStackTrace(e.getStackTrace());
			throw wce;
		}
		feld.clearErrorMsgs();
		if (feld.getValue().equals("4")) {
			feld.addErrorMsgs("Böse, böse. Alkohol am Steuer wird nicht akzeptiert!");
			result |= true;
		}
		// Wenn kein Trinker, dann nur Soft-Getränke erlauben
		if (feld.getValue().equals("2") | feld.getValue().equals("3")) {
			FormField bier;
			FormField wein;
			FormField hartes;
			try {
				bier = context.getFormField("begleitunginfo","einladung","bier");
				wein = context.getFormField("begleitunginfo","einladung","wein");
				hartes = context.getFormField("begleitunginfo","einladung","hartes");
			} catch (FieldNotFoundException e) {
				WorkflowConfigurationException wce = new WorkflowConfigurationException(e.getMessage());
				wce.setStackTrace(e.getStackTrace());
				throw wce;
			}
			if (bier.getValue().equals("true") | wein.getValue().equals("true") | hartes.getValue().equals("true")) {
				feld.addErrorMsgs("Bier, Wein und Hartes Zeug sind auch alkoholische Getränke.");
				result |= true;
			}
		}
		return result;
	}

}

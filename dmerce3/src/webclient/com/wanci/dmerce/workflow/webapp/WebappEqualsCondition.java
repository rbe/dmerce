/*
 * Datei angelegt am 03.11.2003
 */
package com.wanci.dmerce.workflow.webapp;

import java.util.List;

import com.wanci.dmerce.exceptions.FieldNotFoundException;
import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.taglib.form.FormField;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.dmerce.workflow.xmlbridge.CONDITION;
import com.wanci.dmerce.workflow.xmlbridge.FORMFIELD;
import com.wanci.dmerce.workflow.xmlbridge.PARAMETER;
import com.wanci.java.LangUtil;

/**
 * @author Masanori Fujita
 */
public class WebappEqualsCondition extends WebappCondition {
	
	private static boolean DEBUG = true;
	
	private String strPageId;
	private String strFormId;
	private String strFieldId;
	private String strExpectedValue;
	
	public WebappEqualsCondition(Transition transition, CONDITION conditionToReadFrom) {
		super(transition);
		// Formularfeld auslesen
		List listFormfield = conditionToReadFrom.getFormfield();
		assert listFormfield != null : "Fehler in Condition für Transition "+transition.getId()+": In einer EqualsCondition muss ein <formfield ... > angegeben werden.";
		assert listFormfield.size() == 1 : "Fehler in Condition für Transition "+transition.getId()+": In einer EqualsCondition darf nur genau ein <formfield ... > angegeben werden.";
		assert ((FORMFIELD) listFormfield.get(0)).getPageid() != null : "Fehler in Condition für Transition "+transition.getId()+": In einer EqualsCondition muss eine <formfield pageid=\"..\" > angegeben werden.";
		strPageId = ((FORMFIELD) listFormfield.get(0)).getPageid();
		strFormId = ((FORMFIELD) listFormfield.get(0)).getFormid();
		strFieldId = ((FORMFIELD) listFormfield.get(0)).getName();
		// Parameter auslesen
		List listParameters = conditionToReadFrom.getParameter();
		assert listParameters != null : "Fehler in Condition für Transition "+transition.getId()+": In einer EqualsCondition muss ein <parameter name=\"expected\" ... > angegeben werden.";
		assert listParameters.size() == 1 : "Fehler in Condition für Transition "+transition.getId()+": In einer EqualsCondition darf nur genau ein <parameter ... > angegeben werden.";
		assert ((PARAMETER) listParameters.get(0)).getName().equals("expected") : "Fehler in Condition für Transition "+transition.getId()+": In einer EqualsCondition muss ein <parameter name=\"expected\" ... > angegeben werden.";
		strExpectedValue = ((PARAMETER) listParameters.get(0)).getValue();
		// Beschreibung setzen
		setDescription("Prüft, ob das Formularfeld "+strPageId+"."+strFormId+"."+strFieldId+" dem Wert "+strExpectedValue+" entspricht.");
	}

	public boolean isSatisfied(WebappWorkflowContext context) throws WorkflowConfigurationException {
		FormField formfield;
		try {
			formfield = context.getFormField(strPageId, strFormId, strFieldId);
		} catch (FieldNotFoundException e) {
			WorkflowConfigurationException wce = new WorkflowConfigurationException(e.getMessage());
			wce.setStackTrace(e.getStackTrace());
			throw wce;
		}
		LangUtil.consoleDebug(DEBUG, "WebappEqualsCondition: pageid="+strPageId+", formid="+strFormId+", fieldname="+strFieldId+", Wert="+formfield.getValue() );
		return formfield.getValue().equals(strExpectedValue);
	}

}

/*
 * Datei angelegt am 06.11.2003
 */
package com.wanci.dmerce.workflow.webapp;

import com.wanci.dmerce.formvalidation.DefaultValidatorGroup;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.java.LangUtil;

/**
 * Diese Bedingung ist genau dann erfüllt, wenn die Prüfung
 * der FormFields fehlschlägt. Die Negation der Bedingung ist
 * sinnvoll, um durch diese Bedingung wieder auf die ursprüngliche
 * Seite zurückzukehren.
 * 
 * @author Masanori Fujita
 */
public class ValidationFailedCondition extends WebappCondition {
	
	private DefaultValidatorGroup validatorGroup;

	/**
	 * Erzeugt eine Instanz dieser Bedingung
	 */
	public ValidationFailedCondition(Transition parent) {
		super(parent);
		this.validatorGroup = DefaultValidatorGroup.getInstance();
		setDescription("Diese Bedingung ist genau dann erfüllt, wenn die Validierung der Formularfelder fehlschlägt.");
	}

	public boolean isSatisfied(WebappWorkflowContext context) {
		LangUtil.consoleDebug(true, "ValidationFailedCondition: hole FormFields zur Page-ID "+((WebappState) context.getCurrentState()).getPageId());
		LangUtil.consoleDebug(true, "Anzahl ermittelter FormFields: " + new Integer(context.getFormFields(((WebappState) context.getCurrentState()).getPageId()).size()));
		return ! validatorGroup.validate(context.getFormFields(((WebappState) context.getCurrentState()).getPageId()));
	}

}

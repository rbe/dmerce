/*
 * Datei angelegt am 14.11.2003
 */
package com.wanci.dmerce.formvalidation;

import com.wanci.dmerce.taglib.form.FormField;

/**
 * Pr�ft, ob die H�chstanzahl ausgew�hlter Elemente eingehalten wird.
 * 
 * @author Masanori Fujita
 */
public class MaxSelectedValidator implements Validator {

	public boolean isResponsibleFor(FormField field) {
		return field.getConstraints().get("maxselected") != null;
	}

	public boolean validate(FormField field) {
		try {
			int countChecked;
			if (field.getValues() == null)
				countChecked = 0;
			else
				countChecked = field.getValues().length;
			// Maxselected-Value auslesen
			int max = Integer.valueOf((String) field.getConstraints().get("maxselected")).intValue();
			if (countChecked <= max) {
				return true;
			} else {
				if (max == 1) {
					field.addErrorMsgs("Im Feld " + field.getDescription() + " darf maximal ein Element ausgew�hlt werden.");
				} else {
					field.addErrorMsgs(
						"Im Feld " + field.getDescription() + " d�rfen maximal " + max + " Elemente ausgew�hlt werden.");
				}
				return false;
			}
		} catch (NumberFormatException ne) {
			field.addErrorMsgs("Constraint-Definition f�r 'maxselected' fehlerhaft. Bitte in der forms.xml pr�fen.");
			return false;
		}
	}

}

/*
 * Datei angelegt am 14.11.2003
 */
package com.wanci.dmerce.formvalidation;

import com.wanci.dmerce.taglib.form.FormField;

/**
 * Prüft, ob die Mindestanzahl ausgewählter Elemente eingehalten wird.
 * @author Masanori Fujita
 */
public class MinSelectedValidator implements Validator {

	public boolean isResponsibleFor(FormField field) {
		return field.getConstraints().get("minselected") != null;
	}

	public boolean validate(FormField field) {
		try {
			int countChecked;
			if (field.getValues() == null)
				countChecked = 0;
			else
				countChecked = field.getValues().length;
			// Minselected-Value
			int min = Integer.valueOf((String) field.getConstraints().get("minselected")).intValue();
			if (countChecked >= min) {
				return true;
			} else {
				if (min == 1) {
					field.addErrorMsgs("Im Feld " + field.getDescription() + " muss mindestens ein Element ausgewählt werden.");
				} else {
					field.addErrorMsgs(
						"Im Feld " + field.getDescription() + " müssen mindestens " + min + " Elemente ausgewählt werden.");
				}
				return false;
			}
		} catch (NumberFormatException ne) {
			field.addErrorMsgs("Constraint-Definition für 'maxselected' fehlerhaft. Bitte in der forms.xml prüfen.");
			return false;
		}
	}

}

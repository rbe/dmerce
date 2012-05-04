/*
 * Created on Sep 8, 2003
 */
package com.wanci.dmerce.formvalidation;

import com.wanci.dmerce.taglib.form.FormField;

/**
 * MaxInclusiveValidator prüft ein Textfeld in dem eine Zahl eingegeben wurde und
 * diese Zahl mit der Zahl im forms.xml Dokument vergleicht.
 * Ist die Zahl im Formular nicht kleiner oder gleich der Zahl im forms.xml Dokument
 * wird eine Fehlermeldung im FormField ArrayList errorMsgs hinzugefügt.
 *
 * @author mm
 * @version $Id: MaxInclusiveValidator.java,v 1.8 2003/10/08 12:58:06 mm Exp $
 */
public class MaxInclusiveValidator implements Validator {

	/**
	 * @see com.wanci.dmerce.formvalidation.Validator#isResponsibleFor(com.wanci.dmerce.taglib.form.FormField)
	 */
	public boolean isResponsibleFor(FormField field) {
		return field.getConstraints().containsKey("maxinclusive");
	}

	/**
	 * @return true, wenn die Zahl im Formular kleiner oder gleich der Zahl ist, die
	 * 			in forms.xml gesetzt ist.
	 * @see com.wanci.dmerce.formvalidation.Validator#validate(com.wanci.dmerce.taglib.form.FormField)
	 */
	public boolean validate(FormField field) {

		String stringMaxInclusive =
			(String) field.getConstraints().get("maxinclusive");

		try {

			Double maxinclusive = new Double(stringMaxInclusive);
			Double fieldValue = new Double(field.getValue());
			if (maxinclusive.compareTo(fieldValue) >= 0)
				return true;
			else {
				field.addErrorMsgs(
					"Das Feld "
						+ field.getName()
						+ " darf den Wert "
						+ stringMaxInclusive
						+ " nicht überschreiten.");
				return false;
			}

		}
		catch (Exception e) {
			field.addErrorMsgs(
				"Constraint-Definition für 'maxinclusive' fehlerhaft. Bitte in der forms.xml prüfen.");
			return false;
		}

	}
}

/*
 * Created on Sep 8, 2003
 */
package com.wanci.dmerce.formvalidation;

import com.wanci.dmerce.taglib.form.FormField;

/**
 * MinInclusiveValidator pr�ft ein Textfeld in dem eine Zahl eingegeben wurde und
 * diese Zahl mit der Zahl im forms.xml Dokument vergleicht.
 * Ist die Zahl im Formular nicht gr�sser oder gleich der Zahl im forms.xml Dokument
 * wird eine Fehlermeldung im FormField ArrayList errorMsgs hinzugef�gt.
 *
 * @author mm
 * @version $Id: MinInclusiveValidator.java,v 1.5 2003/09/22 09:58:43 pg Exp $
 */
public class MinInclusiveValidator implements Validator {

	/**
	 * @see com.wanci.dmerce.formvalidation.Validator#isResponsibleFor(com.wanci.dmerce.taglib.form.FormField)
	 */
	public boolean isResponsibleFor(FormField field) {
		return field.getConstraints().containsKey("mininclusive");
	}

	/**
	 * @return true, wenn die Zahl im Formular gr�sser oder gleich der Zahl ist, die in
	 * 			forms.xml gesetzt ist.
	 * @see com.wanci.dmerce.formvalidation.Validator#validate(com.wanci.dmerce.taglib.form.FormField)
	 */
	public boolean validate(FormField field) {

		String stringMinInclusive =
			(String) field.getConstraints().get("mininclusive");

		try {

			Double mininclusive = new Double(stringMinInclusive);
			Double fieldValue = new Double(field.getValue());
			if (mininclusive.compareTo(fieldValue) <= 0)
				return true;
			else {
				field.addErrorMsgs(
					"Das Feld "
						+ field.getName()
						+ " darf den Wert "
						+ stringMinInclusive
						+ " nicht unterschreiten.");
				return false;
			}

		} catch (Exception e) {
			field.addErrorMsgs(
				"Constraint-Definition f�r 'mininclusive' fehlerhaft. Bitte in der forms.xml pr�fen.");
			return false;
		}

	}
}

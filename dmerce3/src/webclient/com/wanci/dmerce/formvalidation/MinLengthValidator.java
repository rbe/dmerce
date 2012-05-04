/*
 * Created on Sep 8, 2003
 */
package com.wanci.dmerce.formvalidation;

import com.wanci.dmerce.taglib.form.FormField;

/**
 * Die Klasse MinLengthValidator prüft das Feld minlength ob die minimale Anzahl der
 * Zeichen unterschritten worden ist oder nicht.
 * Falls die minimale Zeichenlänge unterschritten wurde, wird eine Fehlermeldung
 * für dieses Feld erzeugt.
 *
 * @author mm
 * @version $Id: MinLengthValidator.java,v 1.7 2003/10/20 15:45:45 mm Exp $
 */
public class MinLengthValidator implements Validator {

	/**
	 * @see com.wanci.dmerce.formvalidation.Validator#isResponsibleFor(com.wanci.dmerce.taglib.form.FormField)
	 */
	public boolean isResponsibleFor(FormField field) {
		return field.getConstraints().containsKey("minlength");
	}

	/**
	 * @return true, wenn das Textfeld mehr Zeichen enthält als in forms.xml
	 * 			angegeben sind.
	 * @see com.wanci.dmerce.formvalidation.Validator#validate(com.wanci.dmerce.taglib.form.FormField)
	 */
	public boolean validate(FormField field) {

		String stringMinLength =
			(String) field.getConstraints().get("minlength");

		try {

			int minlength = new Integer(stringMinLength).intValue();
			int fieldLength = field.getValue().length();

			if (minlength <= fieldLength)
				return true;
			else {
				field.addErrorMsgs(
					"Das Feld "
						+ field.getName()
						+ " muss mindestens  "
						+ field.getConstraints().get("minlength").toString()
						+ " Zeichen enthalten.");
				return false;
			}

		} catch (Exception e) {
			field.addErrorMsgs(
				"Constraint-Definition für 'minlength' fehlerhaft. Bitte in der forms.xml prüfen.");
			return false;
		}

	}

}

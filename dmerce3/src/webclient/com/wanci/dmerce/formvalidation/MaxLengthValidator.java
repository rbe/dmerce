/*
 * Created on Sep 8, 2003
 */
package com.wanci.dmerce.formvalidation;

import com.wanci.dmerce.taglib.form.FormField;

/**
 * Die Klasse MaxLengthValidator pr�ft das Feld maxlength ob die maximale Anzahl der Zeichen �berschritten worden sind
 * oder nicht. Falls die maximale Zeichenl�nge �berschritten wurde, wird eine Fehlermeldung f�r dieses Feld erzeugt.
 * 
 * @author mm
 * @version $Id: MaxLengthValidator.java,v 1.9 2003/11/14 16:32:56 mf Exp $
 */
public class MaxLengthValidator implements Validator {

	/**
	 * @see com.wanci.dmerce.formvalidation.Validator#isResponsibleFor(com.wanci.dmerce.taglib.form.FormField)
	 */
	public boolean isResponsibleFor(FormField field) {
		return field.getConstraints().containsKey("maxlength");
	}

	/**
	 * @return true, wenn das Textfeld nicht mehr Zeichen enth�lt als in forms.xml angegeben sind.
	 * @see com.wanci.dmerce.formvalidation.Validator#validate(com.wanci.dmerce.taglib.form.FormField)
	 */
	public boolean validate(FormField field) {

		String stringMaxLength = (String) field.getConstraints().get("maxlength");

		try {

			int maxlength = new Integer(stringMaxLength).intValue();
			int fieldLength = ((String) field.getValue()).length();

			if (maxlength >= fieldLength)
				return true;
			else {
				field.addErrorMsgs(
					"Das Feld "
						+ field.getName()
						+ " darf h�chstens "
						+ field.getConstraints().get("maxlength").toString()
						+ " Zeichen enthalten.");
				return false;
			}

		} catch (Exception e) {
			field.addErrorMsgs("Constraint-Definition f�r 'maxlength' fehlerhaft. Bitte in der forms.xml pr�fen.");
			return false;
		}

	}

}

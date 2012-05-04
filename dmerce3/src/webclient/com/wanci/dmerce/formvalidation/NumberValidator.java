/*
 * Created on Sep 8, 2003
 */
package com.wanci.dmerce.formvalidation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wanci.dmerce.taglib.form.FormField;

/**
 * Die Klasse NumberValidator prüft ob das Feld eine Nummer darstellt.
 * Falls Überprüfung fehlschlägt, wird eine Fehlermeldung
 * für dieses Feld erzeugt.
 *
 * @author mm
 * @version $Id: NumberValidator.java,v 1.5 2003/10/20 15:45:45 mm Exp $
 */
public class NumberValidator implements Validator {

	/**
	 * normale Verantwortlichkeitsprüfung 
	 */
	public boolean isResponsibleFor(FormField field) {
		return (field.getType().equals("number"));
	}

	/**
	 * @return true, ob Wert eine Nummer ist
	 * 			die Überprüfung geschieht mittels regex
	 * @see com.wanci.dmerce.formvalidation.Validator#validate(com.wanci.dmerce.taglib.form.FormField)
	 */
	public boolean validate(FormField field) {
		String value = field.getValue();

		//create regular expression pattern
		//erlaubt: nur zahlen (beliebig)
		//         zahlen (beliebig)  Komma  mind. eine Zahl
		//	       zahlen (beliebig)  Punkt  mind. eine Zahl
		Pattern p = Pattern.compile("^\\d*$|^\\d*,\\d+$|^\\d*\\.\\d+$");
		//create Matcher with value as string
		Matcher m = p.matcher(value);

		//check if pattern matches input value       
		if (m.matches())
			return true;
		else {
			//fehlermeldung erzeugen
			field.addErrorMsgs(
				"Das Feld " + field.getName() + " muss eine gültige Zahl sein");
			return false;
		} //else
	} //validate
}
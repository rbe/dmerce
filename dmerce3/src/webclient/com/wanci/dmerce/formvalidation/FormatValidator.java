/*
 * Created on Sep 8, 2003
 */
package com.wanci.dmerce.formvalidation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wanci.dmerce.taglib.form.FormField;

/**
 * Die Klasse FormatValidator prüft 
 * ob der Wert auf den angegebenen Regex matcht 
 * Falls das Format nicht passt, wird eine Fehlermeldung
 * für dieses Feld erzeugt.
 *
 * @author mm
 * @version $Id: FormatValidator.java,v 1.19 2003/11/14 16:32:56 mf Exp $
 */
public class FormatValidator implements Validator {

	/**
	 * Übliche Methode um die Verantwortlichkeit festzustellen
	 * @see com.wanci.dmerce.formvalidation.Validator#isResponsibleFor(com.wanci.dmerce.taglib.form.FormField)
	 */
	public boolean isResponsibleFor(FormField field) {
		return (field.getConstraints().containsKey("format") && (!field.getValue().equals("")));
	}

	/**
	 * @return true, wenn das format auf den wert matcht
	 */
	public boolean validate(FormField field) {

		//value aus dem field-Objekt holen
		String value = field.getValue();

		//create regular expression pattern
		Pattern p = Pattern.compile((String) field.getConstraints().get("format"));
		//create Matcher with value as string
		Matcher m = p.matcher(value);

		//check if pattern matches input value
		if (m.matches()) {
			return true;
		} else {
			Object checkDesc = field.getConstraintsDesc().get("format");
			//message hinzufügen
			if (checkDesc != null && !checkDesc.equals("")) {
				field.addErrorMsgs("Das Feld " + field.getDescription() + " sollte diesem Format entsprechen: " + (String) field.getConstraintsDesc().get("format"));
			} else {
				field.addErrorMsgs("Das Feld " + field.getDescription() + " entspricht nicht dem vorgegebenen Format");
			}
			return false;
		}
	}
}
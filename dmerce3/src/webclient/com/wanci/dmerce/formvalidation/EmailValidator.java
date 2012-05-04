/*
 * Created on Sep 8, 2003
 */
package com.wanci.dmerce.formvalidation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wanci.dmerce.taglib.form.FormField;

/**
 * Die Klasse EmailValidator pr�ft ob das Feld eine Email enth�lt
 * Falls �berpr�fung fehlschl�gt, wird eine Fehlermeldung
 * f�r dieses Feld erzeugt.
 *
 * @author mm
 * @version $Id: EmailValidator.java,v 1.5 2003/11/17 13:31:10 pg Exp $
 */
public class EmailValidator implements Validator {

	/**
	 * normale Verantwortlichkeitspr�fung 
	 */
	public boolean isResponsibleFor(FormField field) {
		return (
			field.getType().equals("email")
				&& (field.isRequired() || !field.getValue().equals("")));
	}

	/**
	 * validate pr�ft ob der Field-Value eine email darstellt.
	 * 
	 * zur Zeit wird die Pr�fung �ber einen Regexp ausgef�hrt
	 * w�nschenswert ist eine �berpr�fung der existenz der EmailAdresse
	 * das ist aber meines wissens (pg) nicht m�glich, bzw. doch m�glich aber wird nicht unterst�tzt
	 * 
	 * Die �berpr�fung auf korrektes Email format ist recht schwach, 
	 * hier k�nnte man noch einen schickeren Regexp bauen
	 * 
	 * @return true, falls Wert eine email ist
	 */
	public boolean validate(FormField field) {
		String value = field.getValue();

		//create regular expression pattern
		//erlaubt: beliebige Zeichen, dann @, dann beliebige Zeichen, dann Punkt, dann beliebige Zeichen
		Pattern p = Pattern.compile(".+@..+\\...+");
		//create Matcher with value as string
		Matcher m = p.matcher(value);

		//check if pattern matches input value       
		if (m.matches())
			return true;
		else {
			Object checkDesc = field.getConstraintsDesc().get(field.getName());
			//fehlermeldung erzeugen
			if (checkDesc != null && !checkDesc.equals(""))
				field.addErrorMsgs(
					"Das Feld "
						+ field.getName()
						+ " ist keine g�ltige email im Format: "
						+ (String) field.getConstraintsDesc().get(
							"format"));
			else
				field.addErrorMsgs(
					"Das Feld "
						+ field.getName()
						+ " ist keine g�ltige email.");
			return false;
		} //else
	} //validate
}
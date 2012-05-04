/*
 * Created on 08.09.2003
 *
 */
package com.wanci.dmerce.formvalidation;

import com.wanci.dmerce.taglib.form.FormField;

/**
 * @author mf
 * @version $Id: Validator.java,v 1.1 2003/09/08 11:03:55 pg Exp $
 *
 */
public interface Validator {

	public boolean isResponsibleFor(FormField field);
	
	/** Prüft, ob der Wert des Formfields aufgrund der Constraints im
	 * forms.xml gültig sind. Bei Verletzungen von Constraints wird eine Fehlermeldung
	 * im FormField-Objekt gesetzt.
	 * @param field Das zu untersuchende Formular-Feld
	 * @return true, falls die Eingabe gültig ist, false sonst
	 */ 
	public boolean validate (FormField field);
	
}

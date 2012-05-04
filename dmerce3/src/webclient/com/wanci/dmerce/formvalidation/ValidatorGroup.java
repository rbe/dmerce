/*
 * Created on 08.09.2003
 *
 */
package com.wanci.dmerce.formvalidation;

import java.util.Collection;

/**
 * @author mf
 * @version $Id: ValidatorGroup.java,v 1.1 2003/09/08 11:03:55 pg Exp $
 *
 */
public interface ValidatorGroup {

	public void addValidator(Validator validator);
	
	public void removeValidator(Validator validator);
	
	/**
	 * Durchläuft die übergebenen FormField-Objekte und wendet die passenden
	 * Validator an, um die Eingabe zu prüfen.
	 * @param formfields Eine Collection von FormField-Objekten
	 * @return true, falls Eingabe ok, false sonst
	 */
	public boolean validate(Collection formfields);
	
}

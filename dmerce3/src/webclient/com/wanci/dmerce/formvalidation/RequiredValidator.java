/*
 * Created on Sep 10, 2003
 */
package com.wanci.dmerce.formvalidation;

import com.wanci.dmerce.taglib.form.FormField;

/**
 * RequiredValidator sucht nach dem Attribute required im Element field.
 * Wenn das Attribute gefunden wurde, wird geprüft, ob der Benutzer einen
 * Wert eingefügt hat.
 *
 * @author mm
 * @version $Id: RequiredValidator.java,v 1.10 2003/10/07 10:07:59 mf Exp $
 */
public class RequiredValidator implements Validator {
    
    /**
     * @see com.wanci.dmerce.formvalidation.Validator#isResponsibleFor(com.wanci.dmerce.taglib.form.FormField)
     */
    public boolean isResponsibleFor(FormField field) {
        //prüfen ob das Formularfeld benötigt wird
        return field.isRequired();
    }
    
    /**
     * @return true, wenn das Feld benötigt wird und das Textfeld ausgefüllt ist.
     * @see com.wanci.dmerce.formvalidation.Validator#validate(com.wanci.dmerce.taglib.form.FormField)
     */
    public boolean validate(FormField field) {
        
        if (field.getValue() == null || field.getValue().equals("")) {
            field.addErrorMsgs("Das Feld " + field.getDescription() + " darf nicht leer sein.");
            return false;
        } else
            return true;
        
    }
}

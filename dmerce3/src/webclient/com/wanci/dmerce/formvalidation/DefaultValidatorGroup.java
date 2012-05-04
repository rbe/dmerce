/*
 * Created on 08.09.2003
 *
 */
package com.wanci.dmerce.formvalidation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.wanci.dmerce.taglib.form.FormField;
import com.wanci.dmerce.workflow.Workflow;
import com.wanci.java.LangUtil;

/**
 * @author pg
 * @version $Id: DefaultValidatorGroup.java,v 1.8 2004/03/09 17:13:39 rb Exp $
 *
 */
public class DefaultValidatorGroup implements ValidatorGroup {
    
    private ArrayList validators;
    private static DefaultValidatorGroup instance;
    
    /**
     * Privater Konstruktor, da Singleton-Pattern.
     * Hier werden auch alle vorhandenen Validatoren integriert.
     */
    private DefaultValidatorGroup() {
        validators = new ArrayList();
		//add all validators
		addValidator(new MaxInclusiveValidator());
		addValidator(new MaxLengthValidator());
		addValidator(new MinInclusiveValidator());
		addValidator(new MinLengthValidator());
		addValidator(new RequiredValidator());
		addValidator(new ScaleValidator());
		addValidator(new NumberValidator());
		addValidator(new FormatValidator());
		addValidator(new EmailValidator());
		addValidator(new MinSelectedValidator());
		addValidator(new MaxSelectedValidator());
		addValidator(new DateValidator());
    }
    
    /**
     * Statischer Konstruktor, der zu Beginn einmal erzeugt wird.
     */
    static {
    	LangUtil.consoleDebug(Workflow.DEBUG, "Initialisiere DefaultValidatorGroup...");
    	instance = new DefaultValidatorGroup();
    }
    
    public static DefaultValidatorGroup getInstance() {
    	return instance;
    }
    
    public void addValidator(Validator validator) {
        if (!validators.contains(validator)) {
            validators.add(validator);
        }
    }
    
    public void removeValidator(Validator validator) {
        validators.remove(validator);
    }

    /**
     * Methode, die alle Validatoren durchläuft und die übergebenen
     * FormFields überprüft.
     */
    public boolean validate(Collection formFields) {
        Iterator it = formFields.iterator();
        boolean success = true;
        Validator val;
        FormField field;
        while (it.hasNext()) {
            field = (FormField) it.next();
            field.clearErrorMsgs();
            for (int i = 0; i < validators.size(); i++) {
                val = (Validator) validators.get(i);
                if (val.isResponsibleFor(field))
                    success &= val.validate(field);
            }
        }
        return success;
    }
    
}

/*
 * Created on Sep 10, 2003
 */
package com.wanci.dmerce.formvalidation;

import java.util.Map;
import java.util.StringTokenizer;

import com.wanci.dmerce.taglib.form.FormField;

/**
 * PrecisionValidator prüft die Nachkommastellen in Textfeldern.
 *
 * @author mm
 * @version $Id: ScaleValidator.java,v 1.4 2003/12/03 16:36:00 mf Exp $
 */
public class ScaleValidator implements Validator {
    
    /**
     * @see com.wanci.dmerce.formvalidation.Validator#isResponsibleFor(com.wanci.dmerce.taglib.form.FormField)
     */
    public boolean isResponsibleFor(FormField field) {
        return field.getConstraints().containsKey("scale");
    }
    
    /**
     * @return true, wenn die Nachkommastelle in constraints mit der vom Benutzer
     * 			eingegebene Zahl
     *
     * @see com.wanci.dmerce.formvalidation.Validator#validate(com.wanci.dmerce.taglib.form.FormField)
     */
    public boolean validate(FormField field) {
        
        Map constraints = field.getConstraints();
        
        Integer integerScale = new Integer((String) constraints.get("scale"));
        
        int intScale = 0;
        
        if (integerScale != null) {
            intScale = integerScale.intValue();
        }
        else {
            field.addErrorMsgs("Das Mapping für 'scale' ist fehlerhaft. Bitte prüfen.");
            return false;
        }
        
        String value = field.getValue();
        StringTokenizer st = new StringTokenizer(value, ",");
        
        if ((st.countTokens() > 1) && (intScale == 0)) {
            field.addErrorMsgs("Die Anzahl an Nachkommastellen darf nicht grösser sein als " + intScale);
            return false;
        }
        
        if (st.countTokens() < 2)
            return true;
        
        st.nextToken();
        String stringAfterComma = st.nextToken();
        
        if (stringAfterComma.length() > intScale) {
            field.addErrorMsgs("Die Anzahl an Nachkommastellen darf nicht grösser sein als " + intScale);
            return false;
        }
        else {
            return true;
        }
        
    }
    
}

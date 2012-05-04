/*
 * Created on Mar 9, 2004
 *  
 */
package com.wanci.dmerce.formvalidation;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.dmerce.taglib.form.FormField;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: DateValidator.java,v 1.4 2004/03/15 19:54:14 rb Exp $
 *  
 */
public class DateValidator implements Validator {

    private boolean DEBUG = false;

    private boolean DEBUG2 = false;

    public DateValidator() {

        try {

            DEBUG = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
                    "debug");
            DEBUG2 = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
                    "core.debug");

        }
        catch (XmlPropertiesFormatException e) {
        }

    }

    public boolean isResponsibleFor(FormField field) {
        return field.getType().equals("date");
    }

    public boolean validate(FormField field) {

        String inputString = field.getValue();

        LangUtil.consoleDebug(DEBUG2, this, field.getName() + ": "
                + field.isRequired() + "/" + field.getValue());

        String format = field.getDisplayFormat();
        SimpleDateFormat sdf;

        if (format != null && !format.equals("")) {
            sdf = new SimpleDateFormat(format);
        }
        else {
            sdf = new SimpleDateFormat();
        }

        // Sonderfall: Feld ist nicht required und es wurde nichts eingegeben
        if (!field.isRequired()
                && (inputString == null || inputString.equals(""))) {
            return true;
        }

        try {
            sdf.parse(inputString);
        }
        catch (ParseException e) {

            // Sonderfall: wenn bereits eine Fehlermeldung dadurch vorhanden
            // ist, dass das Feld leer ist, muss nicht noch das Format
            // angemeckert werden
            if (!field.isRequired()
                    && (field != null || inputString.equals("")))
                field.addErrorMsgs("Das Feld " + field.getName()
                        + " muss das folgende Format besitzen: " + format);

            return false;

        }

        return true;

    }
}

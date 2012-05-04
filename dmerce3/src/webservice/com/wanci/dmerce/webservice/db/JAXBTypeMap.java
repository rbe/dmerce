/*
 * SQLTypeMap.java
 * 
 * Created on September 11, 2003, 9:51 AM
 */
package com.wanci.dmerce.webservice.db;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.java.LangUtil;

/**
 * Type-Mapping für SQL- und Java-Typen
 * 
 * @author mf
 */
public class JAXBTypeMap {

    private boolean DEBUG = false;

    private boolean DEBUG2 = false;

    private DateFormat dateFormat;

    DatatypeConverter converter;

    /** Creates a new instance of SQLTypeMap */
    public JAXBTypeMap() {

        try {

            DEBUG = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
                "debug");
            DEBUG2 = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
                "core.debug");

        }
        catch (XmlPropertiesFormatException e) {
        }

        LangUtil.consoleDebug(DEBUG2, this, "Initializing JAXB");

        try {

            JAXBContext.newInstance("com.wanci.dmerce.webservice.db.xmlbridge");
            dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL,
                DateFormat.FULL, java.util.Locale.GERMANY);

        }
        catch (JAXBException e) {
            e.printStackTrace();
        }

        LangUtil.consoleDebug(DEBUG2, this, "Initialized");

    }

    /**
     * Mappings: JDBC-Typ -> JAXB-Typ (QResult-Typ) String -> String (String)
     * BigDecimal -> double (Double) BigInteger -> BigInteger (BigInteger) Long ->
     * BigInteger Integer -> long (Integer) Timestamp -> java.util.Date
     * (Calendar) java.sql.Date -> java.util.Date (Calendar) java.sql.Time ->
     * java.util.Date (Calendar) oracle.sql.Timestamp -> java.util.Date
     * (Calendar)
     */
    public String toString(Object o) {

        String s;

        //LangUtil.consoleDebug(DEBUG2, this, "Called");

        if (o.getClass().equals(java.lang.String.class))
            s = (String) o;
        else if (o.getClass().equals(java.math.BigDecimal.class)) {

            // Wird zu einem Double konvertiert, falls es Nachkommastellen
            // gibt.
            BigDecimal number = (BigDecimal) o;

            LangUtil.consoleDebug(DEBUG2, this,
                "toString(): BigDecimal: value=" + number + " scale="
                    + number.scale());

            if (number.scale() <= 0 && number.scale() != -127) {

                s = DatatypeConverter.printInteger(BigInteger.valueOf(number
                    .longValue()));

            }
            else
                s = DatatypeConverter.printDouble(number.doubleValue());

        }
        else if (o.getClass().equals(java.lang.Long.class))
            s = DatatypeConverter.printLong(((Long) o).longValue());
        else if (o.getClass().equals(java.math.BigInteger.class))
            s = DatatypeConverter.printInteger((BigInteger) o);
        else if (o.getClass().equals(java.lang.Integer.class))
            s = DatatypeConverter.printLong(((Integer) o).longValue());
        else if (o.getClass().equals(java.sql.Timestamp.class)) {

            java.util.Date date = new java.util.Date();
            date.setTime(((java.sql.Timestamp) o).getTime());
            s = dateFormat.format(date);

        }
        else if (o instanceof oracle.sql.TIMESTAMP) {

            java.util.Date date = new java.util.Date();

            try {
                date.setTime(((oracle.sql.TIMESTAMP) o).dateValue().getTime());
            }
            catch (SQLException e) {
                // TODO: SQLException bei dateValue() ist zwar Unsinn, aber
                // muss irgendwie weiter nach oben gereicht werden.
                if (DEBUG2)
                    e.printStackTrace();
            }

            s = dateFormat.format(date);

        }
        else if (o.getClass().equals(java.sql.Date.class)) {

            java.util.Date date = new java.util.Date();
            date.setTime(((java.sql.Date) o).getTime());
            s = dateFormat.format(date);

        }
        else if (o.getClass().equals(java.sql.Time.class)) {

            java.util.Date date = new java.util.Date();
            date.setTime(((java.sql.Time) o).getTime());
            s = dateFormat.format(date);

        }
        else {

            // Als letzte Lösung einfach toString() ausführen, aber auf der
            // Konsole warnen.
            LangUtil.consoleDebug(true, this,
                "toString(): WARNING: Will not convert class "
                    + o.getClass().getName()
                    + ". Just calling .toString() -> '" + o.toString() + "'");

            s = o.toString();

        }

        LangUtil.consoleDebug(DEBUG2, this, "toString(): Processed class '"
            + o.getClass().getName() + "'");

        return s;

    }

    public Class getMappedType(Object o, int scale) {
        return getMappedType(o.getClass(), scale);
    }

    /**
     * @param c
     *            Typ des Objektes, das durch den Aufruf getObject auf dem
     *            JDBC-Resultset geholt werden kann.
     * @return Typ des Objektes, das im QResult enthalten sein wird.
     */
    public Class getMappedType(Class c, int scale) {

        Class mappedClass;

        if (c.equals(java.math.BigDecimal.class)) {

            LangUtil.consoleDebug(DEBUG2, this,
                "getMappedType(): BigDecimal: scale=" + scale);

            if (scale == 0 && scale != -127)
                mappedClass = java.lang.Integer.class;
            else
                // BigDecimals werden auf Doubles gemapped, falls
                // Nachkommastellen vorhanden sind
                mappedClass = java.lang.Double.class;

        }
        else if (c.equals(java.sql.Timestamp.class))
            mappedClass = java.util.Calendar.class;
        else if (c.equals(java.sql.Date.class))
            mappedClass = java.util.Calendar.class;
        else if (c.equals(java.sql.Time.class))
            mappedClass = java.util.Calendar.class;
        else if (c.equals(oracle.sql.TIMESTAMP.class))
            mappedClass = java.util.Calendar.class;
        else if (c.equals(java.lang.Long.class))
            mappedClass = java.lang.Integer.class;
        else if (c.equals(java.lang.Byte.class))
            mappedClass = java.lang.Integer.class;
        else
            mappedClass = c;

        LangUtil.consoleDebug(DEBUG2, this, "getMappedType(): Mapped class "
            + c.getName() + " with scale " + scale + " to class '"
            + mappedClass.getName() + "'");

        return mappedClass;

    }

    /**
     * Konvertiert den Input-String aus dem JAXB-Objekt in ein Objekt mit dem
     * angegebenen Typ in den für das QResult-Objekt.
     * 
     * @param input
     *            In der Map
     */
    public Object toQResultObject(String input, Class destinationType)
        throws DmerceException {

        Object r = null;
        String msg;

        LangUtil.consoleDebug(DEBUG2, this,
            "toQResultObject(): Detecting type of input '" + input + "'."
                + " Should become '" + destinationType.getName() + "'");

        if (destinationType.equals(java.lang.Boolean.class)) {

            if (input.equals("0"))
                r = new Boolean(false);
            else if (input.equals("1"))
                r = new Boolean(true);
            else if (input.equals("false"))
                r = new Boolean(false);
            else if (input.equals("true"))
                r = new Boolean(true);

            msg = "Detected java.lang.Boolean";

        }
        else if (destinationType.equals(java.lang.Double.class)) {

            LangUtil.consoleDebug(DEBUG2, this,
                "toQResultObject(): Destination type is 'Double'");

            if (input == null || input.equals(""))
                r = null;
            else
                r = new Double(input);

            msg = "Detected java.lang.Double";

        }
        else if (destinationType.equals(java.lang.Integer.class)) {

            LangUtil.consoleDebug(DEBUG2, this,
                "toQResultObject(): Destination type is 'Integer'");

            try {
                if (input == null || input.equals(""))
                    r = null;
                else
                    r = new Integer(input);

            }
            catch (NumberFormatException e) {

                LangUtil.consoleDebug(true, this,
                    "toQResultObject(): Caught NumberFormatException while converting '"
                        + input + "' len=" + input.length()
                        + " to 'Integer'. Setting value to 0.");

                r = new Integer(0);

            }

            msg = "Detected java.lang.Integer";

        }
        else if (destinationType.equals(java.math.BigInteger.class)) {

            LangUtil.consoleDebug(DEBUG2, this,
                "toQResultObject(): Destination type is 'BigInteger'");

            if (input == null || input.equals(""))
                r = null;
            else
                r = DatatypeConverter.parseInteger(input);

            msg = "Detected java.math.BigInteger";

        }
        else if (destinationType.equals(java.lang.String.class)) {

            LangUtil.consoleDebug(DEBUG2, this,
                "toQResultObject(): Destination type is 'String'");

            r = input;
            msg = "Detected java.lang.String";

        }
        else if (destinationType.equals(java.util.Calendar.class)) {

            LangUtil.consoleDebug(DEBUG2, this,
                "toQResultObject(): Destination type is 'Calendar'");

            if (!input.equals("")) {

                try {

                    java.util.Date parsedDate = dateFormat.parse(input);
                    Calendar cal = Calendar
                        .getInstance(java.util.Locale.GERMANY);
                    cal.setTime(parsedDate);

                    r = cal;

                }
                catch (ParseException e) {
                    throw new DmerceException(e.toString());
                }

            }
            else
                r = null;

            msg = "Detected java.util.Calendar";

        }
        else {

            msg = "toQResultObject(): Cannot map unknown type '"
                + input.getClass().getName() + "' (value='" + input + "') to '"
                + destinationType.getName() + "'";

            LangUtil.consoleDebug(DEBUG2, this, msg);

            throw new DmerceException(msg);

        }

        LangUtil.consoleDebug(DEBUG2, this, "toQResultObject(): " + msg
            + " for value '" + input + "'. Returning " + r);

        return r;

    }

    /**
     * Mapping für die Serverseite. Diese Methode konvertiert das über JAXB
     * erhaltene Objekt wieder in den passenden JDBC-Typ.
     */
    public Object toJDBCObject(Object o) throws DmerceException {

        LangUtil.consoleDebug(DEBUG2, this,
            "toJDBCObject(): Converting object of class '"
                + (o != null ? o.getClass().getName() : "null") + "'");

        if (o == null)
            return null;
        else if (o instanceof Boolean) {

            LangUtil
                .consoleDebug(DEBUG2, this,
                    "Converting java.lang.Boolean value "
                        + ((Boolean) o).booleanValue()
                        + " to java.math.BigDecimal");

            if (((Boolean) o).booleanValue())
                return new BigDecimal(1);
            else
                return new BigDecimal(0);

        }
        else if (o instanceof java.lang.Double) {

            LangUtil.consoleDebug(DEBUG2, this,
                "Converting java.lang.Double value "
                    + ((Double) o).doubleValue() + " to java.math.Bigdecimal");

            return new BigDecimal(((Double) o).doubleValue());

        }
        else if (o instanceof java.lang.Integer) {

            LangUtil.consoleDebug(DEBUG2, this,
                "Converting java.lang.Integer value" + ((Integer) o).intValue()
                    + " to java.math.BigDecimal");

            return new BigDecimal(((Integer) o).doubleValue());

        }
        else if (o instanceof java.math.BigInteger) {

            LangUtil.consoleDebug(DEBUG2, this,
                "Converting java.math.BigInteger value"
                    + ((BigInteger) o).intValue() + " to java.math.BigDecimal");

            return new BigDecimal(((BigInteger) o).doubleValue());

        }
        else if (o instanceof java.lang.String) {

            LangUtil.consoleDebug(DEBUG2, this,
                "Will not convert String value " + o);

            return o;

        }
        else if (o instanceof java.util.Calendar) {

            LangUtil.consoleDebug(DEBUG2, this,
                "Converting java.util.Calender value "
                    + ((Calendar) o).toString() + " to java.sql.Timestamp");

            return new java.sql.Timestamp(((Calendar) o).getTime().getTime());

        }
        else if (o instanceof java.util.Date) {

            LangUtil.consoleDebug(DEBUG2, this,
                "Converting java.util.Date value "
                    + ((java.util.Date) o).toString() + " to java.sql.Date");

            return new java.sql.Date(((java.util.Date) o).getTime());

        }
        else {

            String msg = "Cannot convert unknown type '"
                + o.getClass().getName() + "' into JDBC datatype";

            LangUtil.consoleDebug(DEBUG2, this, msg);

            throw new DmerceException(msg);

        }
    }

    /**
     * Erzeugt aus den Formular-Strings dem Feld-Typen aus der forms.xml
     * entsprechende Java-Objekte, die an den SQLWebservice weitergereicht
     * werden können.
     * 
     * @param value
     * @param typeString
     * @return
     */
    public Object toJAXBObject(String value, String typeString,
        String optionalFormat) throws DmerceException {

        LangUtil.consoleDebug(DEBUG2, this, "toJAXBObject(): Converting "
            + typeString + "/" + value + "/" + optionalFormat);

        Object result = null;

        if (value != null) {

            if (typeString.equals("string"))
                result = value;
            else if (typeString.equals("boolean"))
                result = new Boolean(value);
            else if (typeString.equals("number")) {
                try {
                    result = new Double(value);
                }
                catch (NumberFormatException e) {
                    result = new Double(0);
                }
            }
            else if (typeString.equals("date")) {

                LangUtil.consoleDebug(DEBUG2, this,
                    "toJAXBObject(): Parsing date: " + value);

                if (!value.equals("")) {

                    SimpleDateFormat sdf;
                    if (optionalFormat != null)
                        sdf = new SimpleDateFormat(optionalFormat);
                    else
                        sdf = new SimpleDateFormat();

                    try {
                        result = sdf.parse(value);
                    }
                    catch (ParseException e) {

                        throw new DmerceException("Cannot parse date '" + value
                            + "': " + e);

                    }

                }

            }
            else {

                String msg = "Unsupported type '" + typeString
                    + "' of a form field containing the value '" + value + "'";

                LangUtil.consoleDebug(DEBUG2, this, msg);

                throw new DmerceException(msg);

            }

        }

        return result;

    }

}
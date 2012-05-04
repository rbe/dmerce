/*
 * Created on Aug 20, 2003
 */
package com.wanci.dmerce.webservice.db;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Repr�sentiert einen Datensatz (Zeile) der QResult-Ergebnismente.
 * 
 * @author Masanori Fujita
 * @version $Id: QRow.java,v 1.2 2004/05/16 19:44:06 rb Exp $
 */
public class QRow implements Serializable {

    private Map fields;

    private QTypeInfo typeinfo;

    /**
     * Standard-Konstruktor f�r eine QRow. Mit Hilfe der �bergebenen QTypeInfo
     * wird sp�ter sichergestellt, dass nur g�ltige Werte in die QRow
     * aufgenommen werden.
     */
    public QRow(QTypeInfo typeinfo) {
        fields = new HashMap();
        this.typeinfo = typeinfo;
    }

    /**
     * Gibt Key/Value-Paare eines Datensatzes zur�ck.
     * 
     * @return Datensatz als Key-Value-Paare
     */
    public Map getFields() {
        return fields;
    }

    /**
     * Setzt den Wert eines Feldes. Es wird dabei gepr�ft, ob die
     * Spaltenbezeichnung und der Typ des Wert-Objektes zul�ssig sind.
     * 
     * @param colname
     *            Name des zu setzenden Feldes
     * @param o
     *            zu setzender Wert
     */
    public void setValue(String colname, Object o) throws QColumnException,
        QColumnTypeException {
        if (typeinfo.isValidColumnName(colname)) {
            if (typeinfo.isValidValue(colname, o))
                fields.put(colname, o);
            else
                throw new QColumnTypeException(colname, typeinfo
                    .getColumnType(colname), o.getClass());
        }
        else
            throw new QColumnException(colname);
    }

    /**
     * Gibt das Wert-Objekt des angegebenen Feldes zur�ck.
     */
    public Object getValue(String fieldname) {
        return fields.get(fieldname);
    }

    /**
     * Gibt das QTypeInfo-Objekt zur�ck.
     */
    public QTypeInfo getTypeInfo() {
        return this.typeinfo;
    }

    public String toString() {
        String result = "";
        Iterator it = fields.values().iterator();
        Object nextValue;
        while (it.hasNext()) {
            nextValue = it.next();
            if (nextValue != null) {
                if (java.util.Calendar.class.isAssignableFrom(nextValue
                    .getClass())) { // Bei Datum ansprechend formatieren.
                    DateFormat format = DateFormat.getDateTimeInstance();
                    result += format.format(((Calendar) nextValue).getTime())
                        + " ";
                }
                else
                    result += nextValue.toString() + " ";
            }
            else
                result += "null ";
        }
        return result;
    }

}

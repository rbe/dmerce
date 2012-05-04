/*
 * QTypeInfo.java
 *
 * Created on August 25, 2003, 10:56 AM
 */

package com.wanci.dmerce.webservice.db;

import java.util.ArrayList;
import java.util.List;

import com.wanci.java.LangUtil;

/**
 * Diese Klasse hält die Typinformationen für die einzelnen QRow's in einem
 * QResultset.
 * 
 * @author Masanori Fujita
 */
public class QTypeInfo {

    private boolean DEBUG = false;

    private List typeinfo;

    private List colnames;

    /** Creates a new instance of QTypeInfo */
    public QTypeInfo() {
        this.typeinfo = new ArrayList();
        this.colnames = new ArrayList();
    }

    /**
     * Hilfsmethode zum Hinzufügen einer Spalteninformation. Die Typeinfo wird
     * am Ende der Liste angehängt.
     * 
     * @param colInfo
     *            Typinformation einer Spalte
     */
    public void addColumnInfo(QColumnInfo colInfo) {
        typeinfo.add(colInfo);
        colnames.add(colInfo.getName());
    }

    /**
     * Hilfsmethode zum Hinzufügen einer Spalteninformation. Die Typeinfo wird
     * an der angegebenen Position in der Liste eingefügt.
     * 
     * @param colInfo
     *            Typinformation einer Spalte
     */
    public void addColumnInfo(int index, QColumnInfo colInfo) {
        typeinfo.add(index, colInfo);
        colnames.add(index, colInfo.getName());
    }

    /**
     * Entfernt die Spalteninformation an der angegebenen Position.
     */
    public void removeColumnInfo(int index) {
        typeinfo.remove(index);
        colnames.remove(index);
    }

    /**
     * Gibt die Typinformation der Spalte an der angegebenen Position zurück.
     */
    public QColumnInfo getColumnInfo(int index) {
        return (QColumnInfo) typeinfo.get(index);
    }

    /**
     * Gibt die Bezeichnung der Spalte an der angegebenen Position zurück.
     */
    public String getColumnName(int index) {
        return (String) colnames.get(index);
    }

    /**
     * Gibt den Java-Typ der Spalte mit der angegebenen Bezeichnung zurück.
     */
    public Class getColumnType(String colname) throws QColumnException {
        LangUtil.consoleDebug(DEBUG, "getColumnType(" + colname + ")");
        if (isValidColumnName(colname)) {
            return getColumnInfo(colnames.indexOf(colname)).getType();
        }
        else
            throw new QColumnException(colname);
    }

    /**
     * Prüft, ob das übergebene Wert-Objekt vom Typ her mit der angegebenen
     * Spalte vereinbar ist.
     */
    public boolean isValidValue(String fieldname, Object o) {
        LangUtil.consoleDebug(DEBUG, "isValidValue(" + fieldname + ", " + o
            + ")");
        if (o != null) {
            QColumnInfo search = new QColumnInfo(fieldname, o.getClass());
            return typeinfo.contains(search);
        }
        else
            return true;
    }

    /**
     * Prüft, ob eine Spalte mit der angegebenen Bezeichnung existiert.
     */
    public boolean isValidColumnName(String colname) {
        return colnames.contains(colname);
    }

    /**
     * Gibt die Anzahl der Spalten zurück.
     */
    public int getColumnCount() {
        return typeinfo.size();
    }

    /**
     * Gibt einen Aussagekräftigen Textblock als String zurück, der die
     * QColumnInfos dieser QTypeInfo näher beschreibt.
     */
    public String toString() {
        String result = "[QTypeInfo, containing following QColumnInfos:\n";
        for (int i = 0; i < getColumnCount(); i++) {
            result += "\t" + getColumnInfo(i) + "\n";
        }
        result += "]";
        return result;
    }

    /**
     * Prüft, ob das übergebene QTypeInfo-Objekt inhaltlich mit dieser Instanz
     * übereinstimmt
     */
    public boolean equals(Object o) {
        QTypeInfo q = null;
        try {
            q = (QTypeInfo) o;
        }
        catch (ClassCastException e) {
            return false;
        }
        boolean matching = true;
        for (int i = 0; i < getColumnCount(); i++) {
            matching &= getColumnInfo(i).equals(q.getColumnInfo(i));
        }
        return matching;
    }
}

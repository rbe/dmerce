/*
 * QTypeInfo.java
 *
 * Created on August 25, 2003, 10:48 AM
 */

package com.wanci.dmerce.webservice.db;

import java.io.Serializable;

/**
 * Ein QColumnInfo enthält die Bezeichnung und den Typ einer Spalte einer QRow.
 * @author  Masanori Fujita
 */
public class QColumnInfo implements Serializable {
    
    private boolean DEBUG = true;
    
    private String fieldname;
    
    private Class type;
    
    /** Creates a new instance of QTypeInfo */
    public QColumnInfo(String fieldname, Class type) {
        this.fieldname = fieldname;
        this.type = type;
    }
    
    /**
     * Gibt den Namen der Spalte zurück.
     */
    public String getName() {
        return fieldname;
    }
    
    /**
     * Gibt den Java-Objekt-Typ der Spalte zurück.
     */
    public Class getType() {
        return type;
    }
    
    /**
     * Vergleicht zwei QColumnInfo's anhand der enthaltenen Felder.
     */
    public boolean equals(Object o) {
        QColumnInfo colInfo = null;
        try {
            colInfo = (QColumnInfo) o;
        }
        catch (ClassCastException e) {
            return false;
        }
        if (colInfo.getName().equals(getName()) & colInfo.getType().isAssignableFrom(getType()))
            return true;
        else
            return false;
    }
    
    /**
     * Gibt einen aussagekräftigen String als Beschreibung der QColumnInfo zurück.
     * Der String hat das Format:<br/>
     * <pre>[QColumnInfo, column name: SPALTE_A, column type: String]</pre>
     */
    public String toString() {
        return "[QColumnInfo, column name: "+getName()+", column type: "+getType().getName()+"]";
    }
    

}

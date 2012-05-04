/*
 * QColumnTypeException.java
 *
 * Created on August 25, 2003, 11:28 AM
 */

package com.wanci.dmerce.webservice.db;

/**
 * Exception, die darauf hinweist, dass das verwendete Wert-Objekt vom Typ her
 * nicht mit der angegebenen Spalte vereinbar ist.
 * @author  Masanori Fujita
 */
public class QColumnTypeException extends java.lang.Exception {
    
    private Class expectedType;
    private Class foundType;
    private String fieldname;
    
    /**
     * Constructs an instance of <code>QColumnTypeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public QColumnTypeException(String columnName, Class expected, Class found) {
        super("Wrong column type. Expected: "+expected.getName()+", Found: "+found.getName());
        this.expectedType = expected;
        this.foundType = found;
    }
    
    /**
     * Erwarteter Typ für die angegebene Spalte.
     */
    public Class getExpectedType() {
        return expectedType;
    }
    
    /**
     * Verwendeter Typ für die angegebene Spalte.
     */
    public Class getFoundType() {
        return foundType;
    }
}

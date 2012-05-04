/*
 * QColumnException.java
 *
 * Created on August 25, 2003, 11:33 AM
 */

package com.wanci.dmerce.webservice.db;

/**
 * Exception-Klasse, die darauf hinweist, dass eine falsche Spaltenbezeichnung
 * verwendet worden ist. Meist ist bei Auftreten dieser Exception die angegebene
 * Spalte nicht vorhanden oder ist falsch geschrieben.
 * @author  Masanori Fujita
 */
public class QColumnException extends java.lang.Exception {
    
    /**
     * Constructs an instance of <code>QColumnException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public QColumnException(String columnname) {
        super("Wrong column name: "+columnname);
    }
}

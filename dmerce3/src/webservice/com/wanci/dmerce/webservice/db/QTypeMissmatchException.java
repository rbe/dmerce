/*
 * QTypeMissmatchException.java
 *
 * Created on August 26, 2003, 9:53 AM
 */

package com.wanci.dmerce.webservice.db;

/**
 *
 * @author  pg
 */
public class QTypeMissmatchException extends java.lang.Exception {

    private QTypeInfo foundTypeInfo;
    private QTypeInfo expectedTypeInfo;
    
    /**
     * Constructs an instance of <code>QTypeMissmatchException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public QTypeMissmatchException(QTypeInfo expectedTypeInfo, QTypeInfo foundTypeInfo) {
        super("The following type information\n"+foundTypeInfo+"\ndoes not match the expected type information\n"+expectedTypeInfo);
        this.expectedTypeInfo = expectedTypeInfo;
        this.foundTypeInfo = foundTypeInfo;
    }
}

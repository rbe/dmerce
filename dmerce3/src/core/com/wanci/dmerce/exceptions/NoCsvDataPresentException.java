/*
 * Created on Jul 24, 2003
 *
 */
package com.wanci.dmerce.exceptions;

/**
 * @author rb
 * @version $Id: NoCsvDataPresentException.java,v 1.1 2003/08/26 13:07:53 mm Exp $
 *
 * Ausnahme, wenn die Klasse CsvData keine Daten hat,
 * aber danach gefragt wird (waehre sonst eine
 * ArrayIndexOutOfBoundsException)
 * 
 */
public class NoCsvDataPresentException extends DmerceException {

    public NoCsvDataPresentException() {
    }

    public NoCsvDataPresentException(String message) {
        super(message);
    }

}

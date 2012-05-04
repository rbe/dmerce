/*
 * ValidatorException.java
 *
 * Created on September 30, 2003, 2:27 PM
 */

package com.wanci.dmerce.exceptions;

/**
 *
 * @author  rb
 */
public class ValidatorException extends DmerceException {
    
    /** Creates a new instance of ValidatorException */
    public ValidatorException() {
    }
    
    public ValidatorException(String message) {
        super(message);
    }
    
}
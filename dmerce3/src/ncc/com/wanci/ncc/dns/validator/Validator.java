/*
 * Validator.java
 *
 * Created on September 30, 2003, 2:24 PM
 */

package com.wanci.ncc.dns.validator;

import com.wanci.dmerce.exceptions.ValidatorException;

/**
 *
 * @author  rb
 */
public interface Validator {
    
    void validate() throws ValidatorException;
   
}
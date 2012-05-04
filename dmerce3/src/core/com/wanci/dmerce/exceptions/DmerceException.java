/*
 * Created on 28.07.2003
 *
 */
package com.wanci.dmerce.exceptions;

/**
 * @author rb
 * @version $Id: DmerceException.java,v 1.2 2004/03/03 14:50:54 mf Exp $
 *
 *
 *
 */
public class DmerceException extends Exception {

	public DmerceException() {
	}

	public DmerceException(String message) {
		super(message);
	}
	
	public DmerceException(Exception e) {
		super(e.getMessage());
		this.setStackTrace(e.getStackTrace());
	}
    
}
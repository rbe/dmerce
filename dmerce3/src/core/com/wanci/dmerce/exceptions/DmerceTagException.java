/*
 * Created on May 20, 2004
 *
 */
package com.wanci.dmerce.exceptions;


/**
 * @author rb
 * @version $Id: DmerceTagException.java,v 1.1 2004/05/26 17:02:57 rb Exp $
 *
 */
public class DmerceTagException extends DmerceException {

	public DmerceTagException() {
	}

	public DmerceTagException(String message) {
		super(message);
	}
	
	public DmerceTagException(Exception e) {
		super(e.getMessage());
		this.setStackTrace(e.getStackTrace());
	}
	
}
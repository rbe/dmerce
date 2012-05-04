/*
 * Created on 11.07.2003
 *
 */
package com.wanci.ncc.qluster;

/**
 * @author rb
 * @version $Id: InterConnect.java,v 1.1 2004/02/02 09:41:50 rb Exp $
 *
 *
 *
 */
public interface InterConnect {
	
	/**
	 * 
	 *
	 */
	void initialize();
	
	/**
	 * 
	 * @return
	 */
	boolean check();
	
	/**
	 * 
	 * @param intervalInMilliseconds
	 */
	void setCheckInterval(int intervalInMilliseconds);

}
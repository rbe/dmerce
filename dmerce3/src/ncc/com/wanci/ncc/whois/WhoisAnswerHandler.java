/*
 * Created on 23.01.2004
 *  
 */
package com.wanci.ncc.whois;

/**
 * @author rb
 * @version ${Id}
 *  
 */
public interface WhoisAnswerHandler {

	/**
	 * When a whois server referres to another
	 * 
	 * @return
	 */
	String getReferralWhoisServer();

	/**
	 * Is the domain freely available to anyone?
	 * 
	 * @return
	 */
	boolean isDomainAvailable();

}
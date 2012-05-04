/*
 * LdapLog.java
 *
 * Created on September 17, 2003, 4:44 PM
 */

package com.wanci.customer.callando;

/**
 * 
 * @author rb
 */
public class LdapLogEntry {

	boolean actionOk = false;

	/**
	 * Possibly error codes and their meaning add: 268 = already exists delete:
	 * 332 = object does not exist changepw: 432 = object does not exist
	 */
	int errorCode = 0;

	String action;

	int username;

	String password;

	/** Creates a new instance of LdapLog */
	public LdapLogEntry(boolean actionOk, int errorCode, String action,
			int username, String password) {

		this.actionOk = actionOk;
		this.errorCode = errorCode;
		this.action = action;
		this.username = username;
		this.password = password;

	}

	public boolean isActionOk() {
		return actionOk;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getAction() {
		return action;
	}

	public int getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
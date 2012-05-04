/*
 * Created on Aug 13, 2003
 *  
 */
package com.wanci.customer.callando;

import com.wanci.java.PasswordGenerator;

/**
 * @author rb
 * @version $Id: Account.java,v 1.6 2003/12/09 12:59:36 rb Exp $
 * 
 * 
 *  
 */
public class Account {

	int databaseId;

	int customerId;

	int uid;

	String password;

	/**
	 * Flag that indicates that account should be created and modifications
	 * need to be transmitted to mediaWays
	 */
	private boolean create = false;

	/**
	 * Flag that indicates that account was created
	 */
	private boolean created = false;

	/**
	 * Flag that indicates that password of account should be changed and
	 * modifications need to be transmitted to mediaWays
	 */
	private boolean changePassword = false;

	/**
	 * Flag that indicates that account should be deleted and modifications
	 * need to be transmitted to mediaWays
	 */
	private boolean delete = false;

	/**
	 * Flag that indicates that account was deleted
	 */
	private boolean deleted = false;

	/**
	 * Flag that indicates this account exists but is free and usable to new
	 * customers
	 */
	private boolean free = false;

	/**
	 * Flag used when synchronizing accounts from mediaWays with our database
	 */
	private boolean existsAtMediaWays = false;

	/**
	 * Flag used when account was processed by us and data was sent to
	 * mediaWays but answer was not read
	 */
	boolean inProgress = false;

	/**
	 * Last message from mediaWays
	 */
	String message = "no message";

	/**
	 * Initiliaze account with uid and password
	 */
	public Account(int uid, String password) {
		setUid(uid);
		setPassword(password);
	}

	/**
	 * Returns action (string for LDAP at mediaWays) that has to take place for
	 * an Account
	 */
	public String getAction() {

		if (isDelete()) {

			if (uid > 0)
				return "delete\t" + uid;

		}
		else if (isChangePassword()) {

			if (uid > 0 && password != null)
				return "changepw\t" + uid + "\t" + password;

		}
		else if (isCreate()) {

			if (uid > 0 && password != null)
				return "add\t" + uid + "\t" + password + "\tCallando\tCallando";

		}

		return null;

	}

	public int getCustomerId() {
		return customerId;
	}

	public int getDatabaseId() {
		return databaseId;
	}

	public String getMediaWaysMessage() {
		return message;
	}

	public int getUid() {
		return uid;
	}

	public String getPassword() {
		return password;
	}

	public boolean isChangePassword() {
		return changePassword;
	}

	public boolean isCreate() {
		return create;
	}

	public boolean isCreated() {
		return created;
	}

	public boolean isDelete() {
		return delete;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public boolean isExistsAtMediaWays() {
		return existsAtMediaWays;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public boolean isFree() {
		return free;
	}

	/**
	 * Sets a flag that this account needs to be created
	 */
	public void setCreate() {
		create = true;
	}

	/**
	 * Sets a flag that this account needs to change its password
	 */
	public void setChangePassword() {
		changePassword = true;
		password = PasswordGenerator.getInstance().get();
	}

	/**
	 * Set flags that account was created at mediaWays
	 */
	public void setCreated() {
		create = false;
		created = true;
	}

	/**
	 * Sets the id of the customer that this account belongs to
	 */
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public void setDatabaseId(int databaseId) {
		this.databaseId = databaseId;
	}

	/**
	 * Sets a flag that this account should be deleted
	 */
	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	/**
	 * Set flags that account was deleted at mediaWays
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
		if (deleted)
			delete = false;
	}

	/**
	 * Sets a flag that indicates that this account really exists at mediaWays
	 * (used while synchronizing database)
	 */
	public void setExistsAtMediaWays(boolean existsAtMediaWays) {
		this.existsAtMediaWays = existsAtMediaWays;
	}

	/**
	 * Sets flag that indicates account "is free" and can be used e.g. for new
	 * customers
	 */
	public void setFree(boolean free) {
		this.free = free;
	}

	/**
	 *  
	 */
	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

	public void setMediaWaysMessage(String message) {
		this.message = message;
	}

	/**
	 * Setter for UID
	 */
	public void setUid(int uid) {
		this.uid = uid;
	}

	/**
	 * Setter for password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Set flag that password was changed and should not changed anymore
	 */
	public void setPasswordChanged() {
		changePassword = false;
	}

}
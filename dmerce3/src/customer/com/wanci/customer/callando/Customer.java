/*
 * Customer.java
 * 
 * Created on September 9, 2003, 2:32 PM
 */

package com.wanci.customer.callando;

import java.util.Date;

/**
 * A customer of callando
 * 
 * @author rb
 */
public class Customer {

	int databaseId;

	String firstName;

	String lastName;

	String email;

	boolean welcomeEmailSent;

	int statusId;

	boolean newCustomer = false;

	int uid = 0;

	Date contractStartDate;

	Date accountStartDate;

	Date accountEndDate;

	int resellerId;

	/**
	 * Flag that indicates that customer is processed and we are waiting for an
	 * answer from mediaWays
	 */
	boolean inProgress;

	/**
	 * Number of month that contract runs. If accountEndDate is reached we add
	 * the number of month to the actual accountEndDate
	 */
	private int runtimeInMonth;

	/**
	 * @param databaseId
	 * @param firstName
	 * @param lastName
	 * @param statusId
	 */
	public Customer(int databaseId, String firstName, String lastName,
			int statusId) {

		this.databaseId = databaseId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.statusId = statusId;

	}

	/**
	 * @param databaseId
	 * @param firstName
	 * @param lastName
	 * @param statusId
	 * @param uid
	 */
	public Customer(int databaseId, String firstName, String lastName,
			int statusId, int uid) {

		this.databaseId = databaseId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.statusId = statusId;
		this.uid = uid;

	}

	/**
	 * @param databaseId
	 * @param firstName
	 * @param lastName
	 * @param statusId
	 * @param uid
	 * @param accountStartDate
	 * @param accountEndDate
	 * @param runtimeInMonth
	 */
	public Customer(int databaseId, String firstName, String lastName,
			int statusId, int uid, Date accountStartDate, Date accountEndDate,
			int runtimeInMonth) {

		this.databaseId = databaseId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.statusId = statusId;
		this.uid = uid;
		this.accountStartDate = accountStartDate;
		this.accountEndDate = accountEndDate;
		this.runtimeInMonth = runtimeInMonth;

	}

	public void calculateEndDate() {
	}

	public int getAccountUid() {
		return uid;
	}

	public Date getAccountStartDate() {
		return accountStartDate;
	}

	public Date getAccountEndDate() {
		return accountEndDate;
	}

	public Date getContractStartDate() {
		return contractStartDate;
	}

	public int getDatabaseId() {
		return databaseId;
	}

	public String getEmail() {
		return email;
	}

	public boolean isWelcomeEmailSent() {
		return welcomeEmailSent;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public int getResellerId() {
		return resellerId;
	}

	public int getStatusId() {
		return statusId;
	}

	public boolean isInProgress() {
		return inProgress;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}

	public void setAccountUid(int uid) {
		this.uid = uid;
	}

	public void setAccountEndDate(Date date) {
		this.accountEndDate = date;
	}

	public void setAccountStartDate(Date date) {
		this.accountStartDate = date;
	}

	public void setContractStartDate(Date date) {
		this.contractStartDate = date;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setWelcomeEmailSent(boolean emailSent) {
		this.welcomeEmailSent = emailSent;
	}

	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public void setResellerId(int resellerId) {
		this.resellerId = resellerId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public String toString() {
		return "'" + getDatabaseId() + " " + getLastName() + ", "
				+ getFirstName() + ", " + getEmail() + "'";
	}

}
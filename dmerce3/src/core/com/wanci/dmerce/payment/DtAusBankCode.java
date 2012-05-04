/*
 * Created on 14.02.2004
 *  
 */
package com.wanci.dmerce.payment;

/**
 * @author rb
 * @version $Id: DtAusBankCode.java,v 1.1 2004/05/16 19:44:07 rb Exp $
 * 
 * Bank Code Information
 *  
 */
public class DtAusBankCode {

	int bankNumber;
	String bankCode = "";
	String bankName;
	boolean outdated = false;
	int newBankNumber; // Lfd. Nr.

	/**
	 * Constructor
	 * 
	 * @param bankNumber
	 * @param bankCode
	 * @param bankName
	 * @param outdated
	 */
	public DtAusBankCode(
		int bankNumber,
		String bankCode,
		String bankName,
		boolean outdated) {

		this.bankNumber = bankNumber;
		if (!bankCode.equals("00000000"))
			this.bankCode = bankCode;
		this.bankName = bankName;
		this.outdated = outdated;

	}

	/**
	 * Constructor
	 * 
	 * @param bankCode
	 * @param bankName
	 * @param outdated
	 * @param newBankCode
	 */
	public DtAusBankCode(
		int bankNumber,
		String bankCode,
		String bankName,
		boolean outdated,
		int newBankNumber) {

		this.bankNumber = bankNumber;
		if (!bankCode.equals("00000000"))
			this.bankCode = bankCode;
		this.bankName = bankName;
		this.outdated = outdated;
		this.newBankNumber = newBankNumber;

	}

	/**
	 * Return bank code
	 * 
	 * @return
	 */
	public String getBankCode() {
		return bankCode;
	}

	/**
	 * Return bank name
	 * 
	 * @return
	 */
	public String getBankName() {
		return bankName;
	}

	/**
	 * @return
	 */
	public int getBankNumber() {
		return bankNumber;
	}

	/**
	 * Get number of new bank in case of deletion of bank code
	 * 
	 * @return
	 */
	public int getNewBankNumber() {
		return newBankNumber;
	}

	/**
	 * Is bank code outdated?
	 * 
	 * @return
	 */
	public boolean isOutdated() {
		return outdated;
	}

	public void setOutdated(boolean outdated) {
		this.outdated = outdated;
	}

	public void setNewBankNumber(int newBankNumber) {
		this.newBankNumber = newBankNumber;
	}

}
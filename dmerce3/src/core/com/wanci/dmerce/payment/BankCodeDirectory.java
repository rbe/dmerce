/*
 * Created on 14.02.2004
 *  
 */
package com.wanci.dmerce.payment;

import java.util.Iterator;
import java.util.Vector;

/**
 * @author rb
 * @version $Id: BankCodeDirectory.java,v 1.2 2004/05/16 19:44:07 rb Exp $
 * 
 * Bank Code Directory
 *  
 */
public class BankCodeDirectory {

	Vector bankCodes = new Vector();
	int count = 0;

	/**
	 * Constructor
	 *  
	 */
	public BankCodeDirectory() {
	}

	/**
	 * Add bank code
	 * 
	 * @param bankCode
	 */
	public void add(DtAusBankCode bankCode) {
		bankCodes.add(bankCode);
		count++;
	}

	/**
	 * Does bank code object exist in directory?
	 * 
	 * @param bankCode
	 * @return
	 */
	public boolean exists(DtAusBankCode bankCode) {

		boolean e = false;

		Iterator i = bankCodes.iterator();
		while (i.hasNext()) {

			DtAusBankCode b = (DtAusBankCode) i.next();
			if (b.getBankCode().equals(bankCode.getBankCode()))
				e = true;
		}

		return e;

	}

	/**
	 * Get bank code object for bank code 'bankCode'
	 * 
	 * @param bankCode
	 * @return
	 */
	public DtAusBankCode getBankCode(String bankCode) {

		DtAusBankCode bc = null;
		Iterator i = bankCodes.iterator();
		while (i.hasNext()) {

			DtAusBankCode b = (DtAusBankCode) i.next();
			if (b.getBankCode().equals(bankCode))
				bc = b;
		}

		return bc;

	}

	/**
	 * Get bank code object for bank code with 'bankNumber' (lfd. Nr.)
	 * 
	 * @param bankNumber
	 * @return
	 */
	public DtAusBankCode getBankCode(int bankNumber) {

		DtAusBankCode bc = null;
		Iterator i = bankCodes.iterator();
		while (i.hasNext()) {

			DtAusBankCode b = (DtAusBankCode) i.next();
			if (b.getBankNumber() == bankNumber)
				bc = b;
		}

		return bc;

	}

	/**
	 * Return count of bank codes in directory
	 * 
	 * @return
	 */
	public int getCount() {
		return count;
	}

}
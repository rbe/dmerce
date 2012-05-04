/*
 * Created on 01.07.2003
 */
package com.wanci.dmerce.qxrp;

import java.util.Vector;

/**
 * Implementation of interface
 * @author pg
 * @version $Id: RequestExecuter.java,v 1.1 2003/08/26 13:07:53 mm Exp $
 *
 */
public class RequestExecuter implements RequestExecuterInterface {

	/**
	 * @see com.wanci.dmerce.qxrp.RequestExecuterInterface#executeTransactions()
	 */
	public void executeTransactions() {

	}

	/**
	 * @see com.wanci.dmerce.qxrp.RequestExecuterInterface#executeTransaction(int)
	 */
	public int executeTransaction(int transactionId) {
		return 0;
	}

	/**
	 * @see com.wanci.dmerce.qxrp.RequestExecuterInterface#rollbackTransaction(int)
	 */
	public int rollbackTransaction(int transactionId) {
		return 0;
	}

	/**
	 * @see com.wanci.dmerce.qxrp.RequestExecuterInterface#logTransaction(java.util.Vector)
	 */
	public void logTransaction(Vector v) {

	}

	public static void main(String[] args) {
		RequestExecuter re = new RequestExecuter();
		
		re.executeTransactions();
	}
}

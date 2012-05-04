/*
 * Created on 01.07.2003
 */
package com.wanci.dmerce.qxrp;

import java.util.Vector;

/**
 * the request executer interface
 * 
 * it looks for open requests in the requests table and executes them
 * should be called regularly
 *  
 * @author pg
 * @version $Id: RequestExecuterInterface.java,v 1.1 2003/08/26 13:07:53 mm Exp $
 *
 */
public interface RequestExecuterInterface {

	/**
	 * get all open transactions from database and execute them
	 */
	void executeTransactions();
	
	/**
	 * execute one transaction from database and report status
	 * if this fails, perform update of status, log, then call rollback
	 * @param transactionId
	 * @return
	 */
	int executeTransaction(int transactionId);
	
	/**
	 * rollback a failed transaction
	 * @param v
	 */
	int rollbackTransaction(int transactionId);
	
	/**
	 * write a log entry to the database
	 * @param v
	 */
	void logTransaction(Vector v);

}

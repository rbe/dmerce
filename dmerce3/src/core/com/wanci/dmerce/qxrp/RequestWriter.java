/*
 * Created on 01.07.2003
 */
package com.wanci.dmerce.qxrp;

import java.sql.SQLException;

import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;

/**
 * writer for a transaction request
 * 
 * this class should be small enough to be called from many places
 * to write a transactionrequest to the database
 * 
 * @author pg
 * @version $Id: RequestWriter.java,v 1.3 2003/09/30 14:27:37 pg Exp $
 *
 */
public class RequestWriter {

	/**
	 * debug status
	 */
	//private boolean DEBUG = false;

	/**
	 * database handler
	 */
	private Database jdbcDatabase;

	public RequestWriter() {
		try {
			jdbcDatabase = DatabaseHandler.getDatabaseConnection("dmerce_ncc");
			jdbcDatabase.openConnection();
		} catch (SQLException e) {
			//what to do here is write the transaction to another place
			// for backing up, then try again later 
			e.printStackTrace();
		}
	}
	
	
	public static void main(String args[]) {

	}
}

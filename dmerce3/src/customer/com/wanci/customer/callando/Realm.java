/*
 * Created on 04.07.2004
 *
 */
package com.wanci.customer.callando;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import com.wanci.dmerce.cli.ArgumentParser;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: Realm.java,v 1.3 2004/08/10 15:26:49 rb Exp $
 *  
 */
public class Realm {

	/**
	 * Debug flags
	 */
	private boolean DEBUG = Configuration.getInstance().DEBUG;

	private boolean DEBUG2 = Configuration.getInstance().DEBUG2;

	/**
	 * Argument parser
	 */
	private ArgumentParser argParser = Configuration.getInstance().argParser;

	/**
	 * Pool of accounts for a certain realm
	 */
	private AccountPool accountPool;

	/**
	 * Customers of a certain realm
	 */
	private Customers customers;

	/**
	 * Transfer of new requests
	 */
	private FtpTransfer ftpTransfer;

	/**
	 * Read answer for processed requests from mediaWays
	 */
	private ReadAnswer readAnswer;

	/**
	 * Process customers/account pool and transmit data to mediaWays
	 */
	private ProcessRequests processRequests;

	/**
	 * Database id of realm
	 */
	private int realmId;

	/**
	 * Prefix of realm
	 */
	private String realmPrefix;

	private String realmLoginPrefix;

	private String realmLoginSuffix;

	/**
	 * JDBC connection
	 */
	private Database jdbcDatabase;

	/**
	 * Constructor
	 * 
	 * @param realmId
	 * @param jdbcDatabase
	 */
	public Realm(int realmId, String realmPrefix, String realmLoginPrefix,
			String realmLoginSuffix) throws SQLException {

		LangUtil.consoleDebug(DEBUG2, this,
				".<init>: Initializing realm: realmId=" + realmId
						+ " realmPrefix=" + realmPrefix);

		this.realmId = realmId;
		this.realmPrefix = realmPrefix;
		this.realmLoginPrefix = realmLoginPrefix;
		this.realmLoginSuffix = realmLoginSuffix;

		// JDBC database connection
		this.jdbcDatabase = getDatabaseConnection();

		// Account pool of realm
		LangUtil.consoleDebug(DEBUG2, this,
				".<init>: Initializing account pool");
		accountPool = new AccountPool(this);
		accountPool.loadAccountsFromDatabase();

		// Customers
		LangUtil.consoleDebug(DEBUG2, this, ".<init>: Initializing customers");
		//customers = new Customers(jdbcDatabase, accountPool);
		customers = new Customers(this);
		customers.convertTimestampsToDate();
		customers.loadCustomersFromDatabase();

		LangUtil.consoleDebug(DEBUG2, this, ".<init>: Realm '" + realmPrefix
				+ "' initialized");

	}

	/**
	 * Get database connection named after realmprefix
	 * 
	 * @return
	 */
	public Database getDatabaseConnection() {

		Configuration cfg = Configuration.getInstance();
		
		if (cfg.jdbcDatabase == null)
			cfg.jdbcDatabase = jdbcDatabase = DatabaseHandler
					.getDatabaseConnection(realmPrefix);

		return cfg.jdbcDatabase;

	}

	public int getRealmId() {
		return realmId;
	}

	public String getRealmLoginPrefix() {
		return realmLoginPrefix;
	}

	public String getRealmLoginSuffix() {
		return realmLoginSuffix;
	}

	public String getRealmPrefix() {
		return realmPrefix;
	}

	public FtpTransfer getFtpTransfer() {
		return ftpTransfer;
	}

	public AccountPool getAccountPool() {
		return accountPool;
	}

	public Customers getCustomers() {
		return customers;
	}

	/**
	 * Set parameters for FTP transfer to mediaWays and initialize classes
	 * FtpTransfer and ReadAnswer
	 * 
	 * @param ftpHost
	 * @param ftpUser
	 * @param ftpPwd
	 */
	public void setTransferFtpAccount(String ftpHost, String ftpUser,
			String ftpPwd) {

		// Initialize class for FTP transfer
		ftpTransfer = new FtpTransfer(this, ftpHost, ftpUser, ftpPwd);

		// Initialize class to process requests
		processRequests = new ProcessRequests(this);

		// Initialize class to read answer from mediaWays
		readAnswer = new ReadAnswer(this);

	}

	public void processRequests() throws IOException, SQLException {

		// Open database connection, process rquests
		// and close connection
		jdbcDatabase.openConnection();
		processRequests.go();
		jdbcDatabase.closeConnection();

	}

	public void readAnswer() throws FileNotFoundException, IOException,
			SQLException {

		// Open database connection, process rquests
		// and close connection
		jdbcDatabase.openConnection();
		readAnswer.go();
		jdbcDatabase.closeConnection();

	}

}
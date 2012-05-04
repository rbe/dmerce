/*
 * ReadAnswer.java
 * 
 * Created on September 17, 2003, 3:20 PM
 */

package com.wanci.customer.callando;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.enterprisedt.net.ftp.FTPException;
import com.wanci.dmerce.cli.ArgumentParser;
import com.wanci.java.LangUtil;

/**
 * Read answer from mediaWays
 * 
 * @author rb
 * @version $Id$
 */
public class ReadAnswer {

	/**
	 * Debug flag
	 */
	private boolean DEBUG = Configuration.getInstance().DEBUG;

	private boolean DEBUG2 = Configuration.getInstance().DEBUG2;

	/**
	 * Argument parser
	 */
	private ArgumentParser argParser = Configuration.getInstance().argParser;

	private boolean simulate = false;

	private AccountPool accountPool;

	private Customers customers;

	private Vector readEntries = new Vector();

	private FtpTransfer ftpTransfer;

	/**
	 * Initializes ReadAnswer
	 * 
	 * @param accountPool
	 *            An AccountPool instance, .loadAccountFromDatabase() must
	 *            already be called
	 * @param customer
	 *            An instance of Customers, .convertTimestampsToDate() and
	 *            .loadCustomersFromDatabase() must already be called
	 */
	public ReadAnswer(Realm realm) {

		this.accountPool = realm.getAccountPool();
		this.customers = realm.getCustomers();
		this.ftpTransfer = realm.getFtpTransfer();

		LangUtil.consoleDebug(DEBUG2, this, ".<init>: Initialized for realm "
				+ realm.getRealmId() + "/" + realm.getRealmPrefix());

	}

	/**
	 * Reads the answer from mediaWays (from a ByteArrayInputStream) that
	 * getFile() returns.
	 * 
	 * Format:
	 * 
	 * OK|FAIL ERROR-CODE TIME ACTION (with USERNAME [PASSWORD])
	 *  
	 */
	public void readAnswer(BufferedReader bufferedReader) throws IOException {

		String s;

		while ((s = bufferedReader.readLine()) != null) {

			LangUtil.consoleDebug(DEBUG, "Processing: '" + s + "'");

			StringTokenizer st = new StringTokenizer(s);
			StringBuffer action = new StringBuffer();

			boolean ok = st.nextToken().equalsIgnoreCase("ok") ? true : false;
			int errorCode = new Integer(st.nextToken()).intValue();
			st.nextToken(); // Time
			String act = st.nextToken();
			action.append(act); // add, delete, changepw
			String username = st.nextToken();
			action.append(" " + username);
			String password = "";
			if (act.equals("add") || act.equals("changepw"))
				password = st.nextToken();
			action.append(" " + password);

			// Rest of the line is appended to action string
			while (st.hasMoreTokens())
				action.append(" " + st.nextToken());

			/*
			 * System.out.println(ok + " " + errorCode + " " + action.toString() + " "
			 */

			readEntries.add(new LdapLogEntry(ok, errorCode, action.toString(),
					new Integer(username).intValue(), password));

		}

	}

	/**
	 * Set error message for an account when LDAP action at mediaWays failed
	 * 
	 * @param account
	 * @param l
	 */
	private void setAccountErrorMessage(Account account, LdapLogEntry l) {

		account.setMediaWaysMessage("ACTION NOT SUCCESSFUL: GOT #"
				+ l.getErrorCode() + " AFTER " + l.getAction());

	}

	/**
	 * @param customer
	 * @param account
	 * @param l
	 */
	private void processCustomerId1(Customer customer, Account account,
			LdapLogEntry l) {

		String action = l.getAction();

		// Set customer to status id 2 when account was
		// created or password was changed successfully
		if (action.indexOf("add") >= 0 || action.indexOf("changepw") >= 0) {

			LangUtil.consoleDebug(DEBUG, "Account " + account.getUid()
					+ " was processed successfully (" + action + ")."
					+ " Setting customer to status 2.");

			account.setFree(false);
			account.setPassword(l.getPassword());
			customer.setInProgress(false);
			customers.setCustomerStatusId(customer, 2);

		}

	}

	/**
	 * @param customer
	 * @param account
	 * @param l
	 */
	private void processCustomerId345(Customer customer, Account account,
			LdapLogEntry l) {

		LangUtil.consoleDebug(DEBUG,
				"Password of account was changed successfully."
						+ " Putting account " + account.getUid()
						+ " back into pool.");

		account.setFree(true);
		account.setPassword(l.getPassword());

		accountPool.unassignAccountFromCustomer(account, customer
				.getDatabaseId());

	}

	/**
	 * @param customer
	 * @param account
	 * @param l
	 */
	private void processCustomerId6(Customer customer, Account account,
			LdapLogEntry l) {

		account.setPassword(l.getPassword());
		customer.setInProgress(false);
		customers.setCustomerStatusId(customer, 2);

		LangUtil.consoleDebug(DEBUG, "Password of account " + account.getUid()
				+ " was changed successfully. Customer "
				+ customer.getDatabaseId() + " was set to status 2.");

	}

	/**
	 * Account was successfully deleted
	 * 
	 * @param account
	 */
	private void deleteAccount(Account account) {

		account.setDeleted(true);

		LangUtil.consoleDebug(DEBUG, "Account " + account.getUid()
				+ " was deleted.");

	}

	/**
	 * Processes the answer read from mediaWays by FTP and updates accounts in
	 * the database
	 */
	public void processAnswer() throws SQLException {

		Iterator i = readEntries.iterator();

		LangUtil.consoleDebug(DEBUG, "Processing " + readEntries.size()
				+ " LDAP log entries");

		while (i.hasNext()) {

			LdapLogEntry l = (LdapLogEntry) i.next();
			int uid = l.getUsername();
			String action = l.getAction();

			Account account = accountPool.getAccountByUid(uid);

			// We found the account in the pool
			if (account != null) {

				LangUtil.consoleDebug(DEBUG, "Processing account " + uid);
				account.setInProgress(false);

				// Retrieve customer of account
				int cid = accountPool.getCustomerIdForAccountUid(uid);
				Customer customer = customers.getCustomerById(cid);
				if (customer == null) {
					try {
						customer = customers.getCustomerByIdFromDatabase(cid);
					} catch (SQLException e) {
					}
				}

				int customerStatusId = 0;
				if (customer == null)
					LangUtil.consoleDebug(DEBUG2, this,
							"Cannot find customer for account uid " + uid);
				else
					customerStatusId = customer.getStatusId();

				if (!l.isActionOk())
					setAccountErrorMessage(account, l);
				else if (action.indexOf("add") >= 0
						|| action.indexOf("changepw") >= 0) {

					account.setMediaWaysMessage("ACTION SUCCESSFUL: " + action);
					account.setInProgress(false);

					// New customer
					if (customerStatusId == 1)
						processCustomerId1(customer, account, l);
					// Customer was locked (3), has canceled (4)
					// or was deleted (5)
					else if (customerStatusId == 3 || customerStatusId == 4
							|| customerStatusId == 5)
						processCustomerId345(customer, account, l);
					// Customer should get/got a new password
					else if (customerStatusId == 6)
						processCustomerId6(customer, account, l);

					if (!simulate)
						customers.updateCustomerInDatabase(customer);

				} else if (action.indexOf("delete") >= 0)
					deleteAccount(account);

				if (!simulate)
					accountPool.updateAccountInDatabase(account);

			} else {

				LangUtil.consoleDebug(DEBUG2, this, "Account object for UID "
						+ uid + " was null");

			}

		}

	}

	/**
	 * Set flag for simulation: we won't modify the database
	 * 
	 * @param simulate
	 */
	public void setSimulate(boolean simulate) {
		this.simulate = simulate;
	}

	/**
	 * 
	 *  
	 */
	public void go() throws IOException, FileNotFoundException, SQLException {

		ByteArrayInputStream bais;

		if (!argParser.hasArgument("-f")) {

			if (argParser.hasArgument("-s"))
				setSimulate(true);

			try {

				if (argParser.hasArgument("-i")) {

					LangUtil.consoleDebug(DEBUG, "Fetching file '"
							+ argParser.getString("-i") + "' from ftp server");

					bais = ftpTransfer.getFile(argParser.getString("-i"));
				} else {

					LangUtil.consoleDebug(DEBUG,
							"Fetching recent answer from ftp server");

					bais = ftpTransfer.getRecentFile();
				}
				// Write answer to file
				//IOUtil.writeBufferToFile(new BufferedReader( new
				// InputStreamReader(bais)), new File("mw-" +
				// FtpTransfer.getDateAndNumberForRetrieval()));
				// Read and process answer
				readAnswer(new BufferedReader(new InputStreamReader(bais)));
				processAnswer();
			} catch (FTPException e) {

				LangUtil.consoleDebug(DEBUG,
						"Could not read answer from ftp server: "
								+ e.getCause() + ": " + e.getMessage());
			}
		} else if (argParser.hasArgument("-f")) {

			LangUtil.consoleDebug(DEBUG, "Reading answer from file "
					+ argParser.getString("-f"));

			readAnswer(new BufferedReader(new FileReader(argParser
					.getString("-f"))));
			processAnswer();
		}

	}

	/**
	 * @param args
	 * @throws DmerceException
	 * @throws IOException
	 * @throws FTPException
	 * @throws SQLException
	 */
	/*
	 * public static void main(String[] args) throws DmerceException,
	 * IOException, FTPException, SQLException {
	 * 
	 * boolean DEBUG = true;
	 * 
	 * ArgumentParser argParser = new ArgumentParser(args); argParser.add("-f",
	 * "", "Use local file to read answer."); argParser.add("-h", null, "Display
	 * help."); argParser.add("-i", "", "Use remote file to read answer.");
	 * argParser.add("-s", null, "Simulate. Don't modify database.");
	 * argParser.add("-q", null, "Be quiet."); argParser.parse();
	 * 
	 * if (argParser.hasArgument("-h")) { argParser.usage(); System.exit(2); }
	 * 
	 * if (argParser.hasArgument("-q")) DEBUG = false; else
	 * Boot.printCopyright("1[DSL] - READ ANSWER FROM mediaWays");
	 * 
	 * LangUtil.consoleDebug(DEBUG, "START");
	 * 
	 * Database jdbcDatabase = DatabaseHandler
	 * .getDatabaseConnection("callando"); jdbcDatabase.openConnection();
	 * LangUtil.consoleDebug(DEBUG, "Connected to database");
	 * 
	 * LangUtil.consoleDebug(DEBUG, "Initializing account pool"); AccountPool ap =
	 * new AccountPool(jdbcDatabase); ap.loadAccountsFromDatabase();
	 * 
	 * LangUtil.consoleDebug(DEBUG, "Loading customers from database");
	 * Customers c = new Customers(jdbcDatabase, ap);
	 * c.convertTimestampsToDate(); c.loadCustomersFromDatabase();
	 * 
	 * LangUtil.consoleDebug(DEBUG, "Reading answer from mediaWays"); ReadAnswer
	 * ra = new ReadAnswer(ap, c);
	 * 
	 * ByteArrayInputStream bais;
	 * 
	 * if (!argParser.hasArgument("-f")) {
	 * 
	 * if (argParser.hasArgument("-s")) ra.setSimulate(true);
	 * 
	 * try {
	 * 
	 * if (argParser.hasArgument("-i")) {
	 * 
	 * LangUtil.consoleDebug(DEBUG, "Fetching file '" +
	 * argParser.getString("-i") + "' from ftp server");
	 * 
	 * bais = FtpTransfer.getFile(argParser.getString("-i")); } else {
	 * 
	 * LangUtil.consoleDebug(DEBUG, "Fetching recent answer from ftp server");
	 * 
	 * bais = FtpTransfer.getRecentFile(); } // Write answer to file
	 * //IOUtil.writeBufferToFile(new BufferedReader( new
	 * InputStreamReader(bais)), new File("mw-" +
	 * FtpTransfer.getDateAndNumberForRetrieval())); // Read and process answer
	 * ra.readAnswer(new BufferedReader(new InputStreamReader(bais)));
	 * ra.processAnswer(); } catch (FTPException e) {
	 * 
	 * LangUtil.consoleDebug(DEBUG, "Could not read answer from ftp server: " +
	 * e.getCause() + ": " + e.getMessage()); } } else if
	 * (argParser.hasArgument("-f")) {
	 * 
	 * LangUtil.consoleDebug(DEBUG, "Reading answer from file " +
	 * argParser.getString("-f"));
	 * 
	 * ra.readAnswer(new BufferedReader(new FileReader(argParser
	 * .getString("-f")))); ra.processAnswer(); }
	 * 
	 * jdbcDatabase.closeConnection(); LangUtil.consoleDebug(DEBUG,
	 * "Disconnected from database");
	 * 
	 * LangUtil.consoleDebug(DEBUG, "STOP"); }
	 */
}
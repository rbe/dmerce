/*
 * ProcessRequests.java
 * 
 * Created on September 7, 2003, 4:10 PM
 */

package com.wanci.customer.callando;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import com.enterprisedt.net.ftp.FTPException;
import com.wanci.dmerce.cli.ArgumentParser;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: ProcessRequests.java,v 1.18 2004/08/11 19:30:13 rb Exp $
 */
public class ProcessRequests {

	/**
	 * Debug flag
	 */
	private boolean DEBUG = Configuration.getInstance().DEBUG;

	private boolean DEBUG2 = Configuration.getInstance().DEBUG2;

	/**
	 * The realm we work on
	 */
	private Realm realm;

	private FtpTransfer ftpTransfer;

	private ArgumentParser argParser = Configuration.getInstance().argParser;

	private AccountPool accountPool;

	private Customers customers;

	private FileWriter fileWriter;

	private StringBuffer fileContent = new StringBuffer();

	private int processedStatusId1 = 0;

	private int processedStatusId2 = 0;

	private int processedStatusId3 = 0;

	private int processedStatusId4 = 0;

	private int processedStatusId5 = 0;

	private int processedStatusId6 = 0;

	/**
	 * Accounts that were processed successfully and need flag "inprogress" in
	 * database
	 */
	private Vector processedAccounts = new Vector();

	private Email email;

	private int sentMails = 0;

	/**
	 * Creates a new instance of ProcessRequests
	 * 
	 * @param accountPool
	 *            An AccountPool instance, .loadAccountFromDatabase() must
	 *            already be called
	 * @param customer
	 *            An instance of Customers, .convertTimestampsToDate() and
	 *            .loadCustomersFromDatabase() must already be called
	 */
	public ProcessRequests(Realm realm) {

		this.realm = realm;
		this.ftpTransfer = realm.getFtpTransfer();
		this.accountPool = realm.getAccountPool();
		this.customers = realm.getCustomers();

	}

	/**
	 * Assign an account to a customer. First try to get a free and unused
	 * account from pool. If the pool does not have an account, create a new
	 * one. The new account is added to vector processAccount for further
	 * processing.
	 * 
	 * @param customer
	 */
	Account assignAccountToCustomer(Customer customer) {

		int customerId = customer.getDatabaseId();
		String customerLastName = customer.getLastName();
		String customerFirstName = customer.getFirstName();

		// Does a account exist that is not assign to a customer?
		Account account = accountPool.getFreeAccountFromPool();

		// We found a free account in the pool; assign it to customer
		if (account != null) {

			account.setFree(false);
			account.setInProgress(true);
			//if (!argParser.hasArgument("-s"))
			accountPool.assignAccountToCustomer(account, customerId);
			if (!argParser.hasArgument("-p"))
				account.setChangePassword();

			LangUtil.consoleDebug(DEBUG2, this, "Got free account "
					+ account.getUid() + " from pool");

		} else {

			// No free account in the pool; create a new one
			account = accountPool.createAccount();
			account.setFree(false);
			account.setInProgress(true);
			accountPool.assignAccountToCustomer(account, customer
					.getDatabaseId());
			accountPool.registerAccount(account);

			LangUtil.consoleDebug(DEBUG, "Created new account "
					+ account.getUid() + " (account was registered in pool)");

		}

		if (account == null)
			LangUtil.consoleDebug(DEBUG,
					"ERROR! No account (null) assigned to customer '"
							+ customer.toString() + "'");
		else {

			LangUtil.consoleDebug(DEBUG, "Successfully assigned account "
					+ account.getUid() + " to customer '" + customer.toString()
					+ "'");

			processedAccounts.add(account);

		}

		return account;

	}

	/**
	 * @param customer
	 * @param account
	 */
	void sendRegistrationEmail(Customer customer, Account account) {

		if (!argParser.hasArgument("-e")) {

			LangUtil.consoleDebug(DEBUG, "Sending registration mail to for "
					+ customer.toString());

			//email = new Email(argParser.getArgs());
			email = new Email(realm);
			LangUtil.consoleDebug(DEBUG, this, "Constructed new email object "
					+ email.toString());

			try {

				email.sendRegistrationEmail(customer, account);
				sentMails++;

				customer.setWelcomeEmailSent(true);

			} catch (SQLException e) {
				LangUtil.consoleDebug(DEBUG,
						"Could not send registration confirmation to "
								+ customer.toString() + ": " + e.getCause()
								+ ": " + e.getMessage());
			}

		} else
			LangUtil.consoleDebug(DEBUG, "Won't send email to customer "
					+ customer.toString());

	}

	/**
	 * @param customer
	 * @param account
	 */
	void sendAccountEmail(Customer customer, Account account) {

		if (!argParser.hasArgument("-e")) {

			LangUtil.consoleDebug(DEBUG, "Sending account mail for "
					+ customer.toString());

			//email = new Email(argParser.getArgs());
			email = new Email(realm);
			LangUtil.consoleDebug(DEBUG, this, "Constructed new email object "
					+ email.toString());

			try {

				email.sendAccountEmail(customer, account);
				sentMails++;

			} catch (SQLException e) {
				LangUtil.consoleDebug(DEBUG, "Could not send account email to "
						+ customer.toString() + ": " + e.getCause() + ": "
						+ e.getMessage());
			}

		} else
			LangUtil.consoleDebug(DEBUG, "Won't send email to customer "
					+ customer.toString());
	}

	/**
	 * Process new customers which need an acoount
	 */
	int processCustomersWithStatusId1() {

		int processed = 0;

		LangUtil.consoleDebug(DEBUG, "Processing customers with status id 1."
				+ " This customers should get an account."
				+ " They should get a welcome email or I will try to assign an"
				+ " existing, unused account from the pool"
				+ " or create a new one.");

		Iterator customerIterator = customers.getCustomersByStatusID(1);
		while (customerIterator.hasNext()) {

			Customer customer = (Customer) customerIterator.next();
			int customerId = customer.getDatabaseId();
			String customerLastName = customer.getLastName();
			String customerFirstName = customer.getFirstName();
			int accountUid = customer.getAccountUid();
			Date contractStartDate = customer.getContractStartDate();
			int dateCompare = contractStartDate.compareTo(Calendar
					.getInstance().getTime());
			Account account = null;

			LangUtil.consoleDebug(DEBUG, "Processing new customer '"
					+ customerId + " " + customerLastName + ", "
					+ customerFirstName + "' with contract start date '"
					+ contractStartDate + "': dateCompare=" + dateCompare);

			/*
			 * Wenn StartDate <= heute, dann Willkommensemail mit Account
			 * 
			 * Wenn StartDate == heute und Willkommensemail gesendet, dann Mail
			 * mit Accountdaten
			 * 
			 * Wenn StartDate > heute, dann Willkommensemail
			 *  
			 */

			// Assign account when dateCompare == -1 or == 0
			// StartDate is today or in the past
			if (accountUid == 0 && (dateCompare == -1 || dateCompare == 0))
				account = assignAccountToCustomer(customer);

			if (account != null && account.getUid() == -1) {
				System.err.println("Uhoh! UID = -1");
				continue;
			}

			// Send mail!?
			if (!argParser.hasArgument("-m")) {

				// Send welcome mail when StartDate is today
				// or in the future and we did not send a welcome mail
				if (!customer.isWelcomeEmailSent()) {
					sendRegistrationEmail(customer, account);
				}
				// Send account email when StartDate is in the past and
				// we sent a welcome mail
				// else if (customer.isWelcomeEmailSent() && dateCompare == -1)
				// {
				else if (customer.isWelcomeEmailSent() && dateCompare == -1) {
					sendAccountEmail(customer, account);
				}

			}

			if (!argParser.hasArgument("-s")) {

				if (account != null)
					customers.setCustomerStatusId(customer, 2);

				customers.updateCustomerInDatabase(customer);

			}

			processed++;

		}

		processedStatusId1 = processed;

		return processed;

	}

	/**
	 * Process existing customers
	 */
	int processCustomersWithStatusId2() {

		int processed = 0;

		LangUtil
				.consoleDebug(
						DEBUG,
						"Processing customers with status id 2."
								+ " I will assign an account to customers which don't have one.");

		Iterator customerIterator = customers.getCustomersByStatusID(2);
		while (customerIterator.hasNext()) {

			Customer customer = (Customer) customerIterator.next();
			int customerId = customer.getDatabaseId();
			int uid = customer.getAccountUid();

			if (!customer.isNewCustomer() && uid == 0) {

				//Account account = accountPool.getAccountByUid(uid);
				LangUtil.consoleDebug(DEBUG, "Existing customer '" + customerId
						+ " " + customer.getLastName() + ", "
						+ customer.getFirstName()
						+ "' does not have an account. Will assign one.");

				Account account = assignAccountToCustomer(customer);

				if (!argParser.hasArgument("-m"))
					sendAccountEmail(customer, account);

				processed++;

			}

		}

		processedStatusId2 = processed;

		return processed;

	}

	/**
	 * Process customers that should be locked: password of their account should
	 * be changed
	 */
	int processCustomersWithStatusId3() {

		int processed = 0;

		LangUtil.consoleDebug(DEBUG, "Processing customers with status id 3."
				+ " This customers should be locked."
				+ " I will change the passwords of"
				+ " their accounts and put them back into the pool.");

		Iterator customerIterator = customers.getCustomersByStatusID(3);
		while (customerIterator.hasNext()) {

			Customer customer = (Customer) customerIterator.next();
			int customerId = customer.getDatabaseId();
			int uid = customer.getAccountUid();

			if (!customer.isNewCustomer() && uid > 0) {

				Account account = accountPool.getAccountByUid(uid);
				LangUtil.consoleDebug(DEBUG, "Account for customer '"
						+ customerId + " " + customer.getLastName() + ", "
						+ customer.getFirstName() + "' is " + uid
						+ ". Should be locked.");

				account.setChangePassword();
				account.setFree(true);
				account.setInProgress(true);

				accountPool.unassignAccountFromCustomer(account, customerId);

				processedAccounts.add(account);

				processed++;

			}

		}

		processedStatusId3 = processed;

		return processed;

	}

	/**
	 * Customers that canceled give their account back to the pool
	 */
	int processCustomersWithStatusId4() {

		int processed = 0;

		LangUtil
				.consoleDebug(
						DEBUG,
						"Processing customers with status id 4."
								+ " This customers have canceled and their accounts go back into the"
								+ " pool. I will change the passwords of the accounts.");

		Iterator customerIterator = customers.getCustomersByStatusID(4);
		while (customerIterator.hasNext()) {

			Customer customer = (Customer) customerIterator.next();
			int customerId = customer.getDatabaseId();
			int uid = customer.getAccountUid();

			if (!customer.isNewCustomer() && uid > 0) {

				Account account = accountPool.getAccountByUid(uid);
				LangUtil.consoleDebug(DEBUG, "Account for customer '"
						+ customerId + " " + customer.getLastName() + ", "
						+ customer.getFirstName() + "' is " + uid
						+ ". Should be cancelled at "
						+ customer.getAccountEndDate() + ".");

				account.setChangePassword();
				account.setFree(true);
				account.setInProgress(true);

				accountPool.unassignAccountFromCustomer(account, customerId);

				processedAccounts.add(account);

				processed++;

			}

		}

		processedStatusId4 = processed;

		return processed;

	}

	/**
	 *  
	 */
	int processCustomersWithStatusId5() {

		int processed = 0;

		LangUtil.consoleDebug(DEBUG, "Processing customers with status id 5."
				+ " This customers were deleted and their accounts go back"
				+ " into the pool. I will change the passwords of the"
				+ " accounts.");

		Iterator customerIterator = customers.getCustomersByStatusID(5);
		while (customerIterator.hasNext()) {

			Customer customer = (Customer) customerIterator.next();
			int customerId = customer.getDatabaseId();
			int uid = customer.getAccountUid();

			if (!customer.isNewCustomer() && uid > 0) {

				Account account = accountPool.getAccountByUid(uid);
				LangUtil.consoleDebug(DEBUG, "Account for customer '"
						+ customerId + " " + customer.getLastName() + ", "
						+ customer.getFirstName() + "' is " + uid
						+ ". Customer should be deleted.");

				account.setChangePassword();
				account.setFree(true);
				account.setInProgress(true);

				accountPool.unassignAccountFromCustomer(account, customerId);

				processedAccounts.add(account);

				processed++;

			}

		}

		processedStatusId5 = processed;

		return processed;

	}

	/**
	 * Change passwords of accounts for customers with status id 6
	 */
	int processCustomersWithStatusId6() {

		int processed = 0;

		LangUtil.consoleDebug(DEBUG, "Processing customers with status id 6."
				+ " I will change the passwords of their accounts.");

		Iterator customerIterator = customers.getCustomersByStatusID(6);
		while (customerIterator.hasNext()) {

			Customer customer = (Customer) customerIterator.next();
			int customerId = customer.getDatabaseId();
			int uid = customer.getAccountUid();

			if (!customer.isNewCustomer() && uid > 0) {

				Account account = accountPool.getAccountByUid(uid);
				LangUtil.consoleDebug(DEBUG, "Account for customer '"
						+ customerId + " " + customer.getLastName() + ", "
						+ customer.getFirstName() + "' is " + uid);

				account.setChangePassword();
				account.setFree(false);
				account.setInProgress(true);

				processedAccounts.add(account);

				processed++;

			}

		}

		processedStatusId6 = processed;

		return processed;

	}

	/**
	 * Walk through all customers and process requests
	 * 
	 * @return int Number of successfully processed customers
	 */
	public int processAllCustomers() {

		int processed = 0;

		boolean p1 = false;
		boolean p2 = false;
		boolean p3 = false;
		boolean p4 = false;
		boolean p5 = false;
		boolean p6 = false;

		if (argParser.hasArgument("-o")) {

			if (!argParser.containsValue("-o", "none")) {

				if (argParser.containsValue("-o", "1"))
					p1 = true;
				if (argParser.containsValue("-o", "2"))
					p2 = true;
				if (argParser.containsValue("-o", "3"))
					p3 = true;
				if (argParser.containsValue("-o", "4"))
					p4 = true;
				if (argParser.containsValue("-o", "5"))
					p5 = true;
				if (argParser.containsValue("-o", "6"))
					p6 = true;

			}

		} else {

			p1 = true;
			p2 = true;
			p3 = true;
			p4 = true;
			p5 = true;
			p6 = true;

		}

		if (p3)
			processed += processCustomersWithStatusId3();
		//if (p4)
		//processed += processCustomersWithStatusId4();
		if (p5)
			processed += processCustomersWithStatusId5();
		if (p6)
			processed += processCustomersWithStatusId6();
		if (p1)
			processed += processCustomersWithStatusId1();
		if (p2)
			processed += processCustomersWithStatusId2();

		// will be checked in
		// updateProcessAccounts()/accountPool.updateAccounts() to show what
		// database modifications will be done if (!argParser.hasArgument("-s"))
		updateProcessedAccounts();

		return processed;

	}

	/**
	 * Delete all accounts having CustomerID = 0 and preserve
	 * Reseller.MaxFreeAccounts accounts
	 * 
	 * @param preserveAccounts
	 *            Parameter needed to preserve accounts for customers which
	 *            StartDate has not been reached
	 */
	void checkPool(int preserveAccounts) {

		int freeAccountsInPool = accountPool.countFreeAccounts();
		Reseller r = new Reseller();

		LangUtil.consoleDebug(DEBUG, "Checking account pool");

		try {

			r.loadResellersFromDatabase();
			int maxFreeAccounts = r.getReseller("callando")
					.getMaxFreeAccounts();
			int neededFreeAccounts = maxFreeAccounts; // + preserveAccounts;
			int accountsToBeDeleted = freeAccountsInPool - neededFreeAccounts;

			if (freeAccountsInPool > neededFreeAccounts) {

				LangUtil.consoleDebug(DEBUG,
						"We have more free accounts in the pool ("
								+ freeAccountsInPool + ") than we want ("
								+ maxFreeAccounts + ") plus needed accounts"
								+ " for new customers (" + preserveAccounts
								+ " = " + neededFreeAccounts
								+ ") for new customers. Deleting "
								+ accountsToBeDeleted + ".");

				int j = 1;
				Iterator freeAccounts = accountPool.getFreeAccountsFromPool();
				while (freeAccounts.hasNext()) {

					Account a = (Account) freeAccounts.next();

					if (j > neededFreeAccounts) {
						a.setDelete(true);
					} else
						a.setDelete(false);

					j++;

				}

			}

		} catch (Exception e) {
			LangUtil.consoleDebug(DEBUG,
					"Sorry, cannot check the account pool:" + e.getCause()
							+ ": " + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * Updates account information and sets the flag "inprogress" to 1 of all
	 * accounts that were successfully processed (data was sent to mediaways)
	 * and for which we are waiting for an answer of mediaWays
	 *  
	 */
	void updateProcessedAccounts() {

		Iterator i = processedAccounts.iterator();
		while (i.hasNext()) {

			Account account = (Account) i.next();

			LangUtil.consoleDebug(DEBUG, "Account in progress: "
					+ account.getUid());

			try {
				accountPool.updateAccountInDatabase(account);
			} catch (SQLException e) {
				LangUtil.consoleDebug(DEBUG, "Could not update account "
						+ account.getUid() + " in database: " + e.getCause()
						+ ": " + e.getMessage());
			}

		}

	}

	/**
	 * 
	 *  
	 */
	void updateProcessedCustomers() {
	}

	/**
	 * Process requests and trasmit data
	 *  
	 */
	void go() throws IOException {
		int processedCustomers = processAllCustomers();
		checkPool(processedStatusId1);

		GenerateMediaWaysFile gmf = new GenerateMediaWaysFile(accountPool);
		gmf.processAllAccounts();
		String content = gmf.getFileContent();

		if (content.length() > 1) {

			FileWriter fw = new FileWriter(realm.getRealmPrefix() + "-"
					+ ftpTransfer.getDateAndNumberForTransmission());
			fw.write(content);
			fw.close();

			if (!argParser.hasArgument("-s") && !argParser.hasArgument("-n")) {

				LangUtil.consoleDebug(DEBUG, "Transmitting data to mediaWays");

				try {

					ftpTransfer.putFile(new ByteArrayInputStream(content
							.getBytes()));

				} catch (FTPException fe) {
					LangUtil.consoleDebug(DEBUG,
							"Could not transmit data to mediaways: "
									+ fe.getCause() + ": " + fe.getMessage());
				}

			} else {

				LangUtil.consoleDebug(DEBUG,
						"Will not transmit data to mediaWays");

			}

		}

	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	/*
	 * public static void main(String[] args) throws SQLException,
	 * DmerceException, IOException, FTPException {
	 * 
	 * boolean DEBUG = true; // Parse command line arguments ArgumentParser
	 * argParser = new ArgumentParser(args); argParser.add("-e", null, "Don't
	 * send emails to customers."); argParser .add("-o", "", "Process customers
	 * with given status ids (comma separated) only."); argParser .add("-p",
	 * null, "Do not change passwords when assigning accounts to customers.");
	 * argParser.add("-m", null, "Don't send mails."); argParser.add("-n", null,
	 * "Don't transmit data to mediaWays."); argParser.add("-q", null, "Be
	 * quiet."); argParser.add("-s", null, "Simulate. Don't modify database.");
	 * argParser.add("-h", null, "Display help."); argParser.parse(); // Display
	 * help if (argParser.hasArgument("-h")) { argParser.usage();
	 * System.exit(2); }
	 * 
	 * if (argParser.hasArgument("-q")) DEBUG = false; else
	 * Boot.printCopyright("1[DSL] - PROCESS REQUESTS FROM DATABASE");
	 * 
	 * LangUtil.consoleDebug(DEBUG, "START");
	 * 
	 * if (argParser.hasArgument("-s")) LangUtil.consoleDebug(DEBUG,
	 * "Simulating!");
	 * 
	 * Database d = DatabaseHandler.getDatabaseConnection("callando");
	 * d.openConnection();
	 * 
	 * AccountPool ap = new AccountPool(d); ap.loadAccountsFromDatabase();
	 * 
	 * Customers c = new Customers(d, ap); c.convertTimestampsToDate();
	 * c.loadCustomersFromDatabase();
	 * 
	 * ProcessRequests pr = new ProcessRequests(ap, c, argParser); if
	 * (!argParser.hasArgument("-q")) Configuration.getInstance().DEBUG = true;
	 * int processedCustomers = pr.processAllCustomers();
	 * pr.checkPool(pr.processedStatusId1);
	 * 
	 * GenerateMediaWaysFile gmf = new GenerateMediaWaysFile(ap);
	 * gmf.processAllAccounts(); String content = gmf.getFileContent();
	 * 
	 * if (content.length() > 1) {
	 * 
	 * FileWriter fw = new FileWriter("callandodsl-" +
	 * FtpTransfer.getDateAndNumberForTransmission()); fw.write(content);
	 * fw.close();
	 * 
	 * if (!argParser.hasArgument("-s") && !argParser.hasArgument("-n")) {
	 * 
	 * LangUtil.consoleDebug(DEBUG, "Transmitting data to mediaWays");
	 * 
	 * try {
	 * 
	 * FtpTransfer.putFile(new ByteArrayInputStream(content .getBytes())); }
	 * catch (FTPException fe) { LangUtil.consoleDebug(DEBUG, "Could not
	 * transmit data to mediaways: " + fe.getCause() + ": " + fe.getMessage()); } }
	 * else {
	 * 
	 * LangUtil.consoleDebug(DEBUG, "Will not transmit data to mediaWays"); } }
	 * 
	 * d.closeConnection();
	 * 
	 * LangUtil.consoleDebug(DEBUG, "Sucessfully processed " +
	 * processedCustomers + " customers."); LangUtil.consoleDebug(DEBUG, "Sent " +
	 * pr.sentMails + " mails.");
	 * 
	 * LangUtil.consoleDebug(DEBUG, "STOP"); }
	 */
}
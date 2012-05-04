/*
 * SynchronizeDatabases.java
 * 
 * Created on September 17, 2003, 5:49 PM
 */

package com.wanci.customer.callando;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import com.wanci.dmerce.cli.ArgumentParser;
import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.java.LangUtil;

/**
 * Synchronizes data present at mediaWays with our database
 * 
 * All usernames found in the file have to exist in the database (table
 * accounts). If they exist the flag "existsatmediaways" is set to 1 else set to
 * 0.
 * 
 * All accounts that have a value of 0 can be deleted WHEN ALL FILES/ ACCOUNTS
 * FROM MEDIAWAYS WERE SYNCHRONIZED!
 * 
 * @version $Id: SynchronizeDatabases.java,v 1.2 2004/07/16 13:36:31 rb Exp $
 * @author rb
 */
public class SynchronizeDatabases {

	/**
	 * Debug flag
	 */
	private boolean DEBUG = Configuration.getInstance().DEBUG;

	ArgumentParser ap;

	Database jdbcDatabase;

	AccountPool accountPool;

	/** Creates a new instance of SynchronizeDatabases */
	public SynchronizeDatabases(Database jdbcDatabase, AccountPool accountPool,
			ArgumentParser ap) {

		this.jdbcDatabase = jdbcDatabase;
		this.accountPool = accountPool;
		this.ap = ap;

	}

	/**
	 * Synchronizes usernames from a file (one line per username) with the
	 * database
	 */
	private void synchronizeFileWithDatabse(String filename)
			throws IOException, FileNotFoundException, SQLException {

		BufferedReader br = new BufferedReader(new FileReader(
				new File(filename)));
		String username;
		String stmt;

		while ((username = br.readLine()) != null) {

			stmt = "SELECT COUNT(*) FROM Accounts WHERE Username = " + username;
			ResultSet rs = jdbcDatabase.executeQuery(stmt);
			rs.next();
			int count = rs.getInt(1);
			if (count > 0) {

				LangUtil.consoleDebug(DEBUG, "Processing account " + username
						+ " exists " + count + " times.");

				Account account = accountPool.getAccountByUid(new Integer(
						username).intValue());

				if (account != null) {

					account.setExistsAtMediaWays(true);

					if (!ap.hasArgument("-s")) {

						accountPool.updateAccountInDatabase(account);

						LangUtil.consoleDebug(DEBUG,
								"Account exists at mediaWays and " + username
										+ " was updated in database");

					} else
						LangUtil
								.consoleDebug(
										DEBUG,
										"Account "
												+ username
												+ " exists at mediaWays and would be updated in database");

				} else {
					LangUtil.consoleDebug(DEBUG, "Account " + username
							+ " does not exists in the pool");
				}

			} else {
				LangUtil.consoleDebug(DEBUG, "Account " + username
						+ " does not exist in database!");
			}

		}

	}

	/**
	 * @throws SQLException
	 */
	public void processAtMediaWaysUnexistingAccounts() throws SQLException {

		Vector v = accountPool.getAtMediaWaysUnexistingAccounts();
		Iterator i = v.iterator();
		LangUtil.consoleDebug(true, "Found " + v.size()
				+ " accounts that exist in our database"
				+ " but do not exist at mediaWays");

		while (i.hasNext()) {

			Account account = (Account) i.next();

			LangUtil.consoleDebug(true, "Account " + account.getUid()
					+ " does not exist at mediaWays");

			if (!ap.hasArgument("-s")) {

				if (ap.hasArgument("-p")) {
					accountPool.purgeAccount(account);
				} else {

					int customerId = account.getCustomerId();

					accountPool
							.unassignAccountFromCustomer(account, customerId);

					LangUtil.consoleDebug(DEBUG, "Account " + account.getUid()
							+ " was deregistered from customer " + customerId);

				}

				accountPool.updateAccountInDatabase(account);
				LangUtil.consoleDebug(DEBUG, "Account " + account.getUid()
						+ " was updated in database");

			}

		}

	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws DmerceException
	 * @throws FileNotFoundException
	 * @throws SQLException
	 */
	public static void main(String[] args) throws IOException, DmerceException,
			FileNotFoundException, SQLException {

		boolean DEBUG = true;

		ArgumentParser argParser = new ArgumentParser(args);
		argParser.add("-1", null, "Realm callandodsl");
		argParser.add("-2", null, "Realm callandoflat");
		argParser.add("-s", null, "Simulate. Don't modify database.");
		argParser.add("-p", null,
				"Purge accounts that do not exist at mediaWays.");
		argParser.add("-q", null, "Be quiet.");
		argParser.parse();

		if (argParser.hasArgument("-q"))
			DEBUG = false;
		else
			Boot
					.printCopyright("1[DSL] - SYNCHRONIZE ACCOUNTS AT MEDIAWAYS WITH DATABASE");

		LangUtil.consoleDebug(DEBUG, "START");

		if (argParser.hasArgument("-s"))
			LangUtil.consoleDebug(DEBUG, "Simulating!");

		Realm realm = null;
		if (argParser.hasArgument("-1"))
			realm = new Realms().getRealm("callandodsl");
		else if (argParser.hasArgument("-2"))
			realm = new Realms().getRealm("callandoflat");

		Database jdbcDatabase = realm.getDatabaseConnection();

		AccountPool accountPool = realm.getAccountPool();

		SynchronizeDatabases smwd = new SynchronizeDatabases(jdbcDatabase,
				accountPool, argParser);

		File[] parsedFiles = new File(".").listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.startsWith("callando-")
						&& name.indexOf(".parsed.txt") > 0)
					return true;
				else
					return false;
			}
		});

		if (parsedFiles.length > 0) {

			for (int i = 0; i < parsedFiles.length; i++) {
				String filename = parsedFiles[i].getName();
				LangUtil.consoleDebug(DEBUG, "Processing file " + filename);
				smwd.synchronizeFileWithDatabse(filename);
			}

			smwd.processAtMediaWaysUnexistingAccounts();

		} else
			LangUtil
					.consoleDebug(DEBUG,
							"No files 'callando-*.parsed.txt' found for synchronization");

		jdbcDatabase.closeConnection();

		LangUtil.consoleDebug(DEBUG, "STOP");

	}

}
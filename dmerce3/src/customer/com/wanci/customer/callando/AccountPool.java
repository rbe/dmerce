/*
 * Created on Aug 13, 2003
 *  
 */
package com.wanci.customer.callando;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import com.wanci.dmerce.jdbcal.Database;
import com.wanci.java.LangUtil;
import com.wanci.java.PasswordGenerator;

/**
 * @author rb
 * @version $Id: AccountPool.java,v 1.15 2004/08/11 19:30:13 rb Exp $
 * 
 * Manages a pool of accounts and uses database as persitant storage
 *  
 */
public class AccountPool {

	private boolean DEBUG = Configuration.getInstance().DEBUG;

	private boolean DEBUG2 = Configuration.getInstance().DEBUG2;

	private Database jdbcDatabase;

	private Realm realm;

	private int freeAccounts = 0;

	private int usedAccounts = 0;

	/**
	 * List containing all accounts of this pool
	 */
	private LinkedList accounts = new LinkedList();

	/**
	 *  
	 */
	public AccountPool(Realm realm) {
		this.realm = realm;
		this.jdbcDatabase = realm.getDatabaseConnection();
	}

	/**
	 * Registers an account with a customer
	 */
	public void assignAccountToCustomer(Account account, int customerId) {

		account.setCustomerId(customerId);

		/*
		 * if (account != null && customerId > 0) {
		 * 
		 * String stmt = "UPDATE Accounts" + " SET CustomerID = " + customerId + "
		 * WHERE ID = " + account.getDatabaseId(); try {
		 * jdbcDatabase.executeUpdate(stmt); } catch (SQLException e) {
		 * e.printStackTrace(); }
		 * 
		 * LangUtil.consoleDebug( DEBUG, "Registered account " +
		 * account.getUid() + " with customer " + customerId + ": " + stmt); }
		 */

	}

	/**
	 * Creates a new account
	 */
	public Account createAccount() {

		int lowestFreeUid = getLowestFreeUid();
		Account account = new Account(lowestFreeUid, PasswordGenerator
				.getInstance().get());
		account.setCreate();

		return account;

	}

	/**
	 * @return
	 */
	public int countFreeAccounts() {

		int count = 0;

		Iterator accountsIterator = accounts.iterator();
		while (accountsIterator.hasNext()) {
			Account a = (Account) accountsIterator.next();
			if (a.isFree())
				count++;
		}

		return count;

	}

	/**
	 * Delete an account, deregister it form the pool, the database and at
	 * mediaWays
	 */
	public void deleteAccount(Account account) throws SQLException {

		account.setDelete(true);

		LangUtil.consoleDebug(DEBUG, "Account" + account.getUid()
				+ " was scheduled for deletion");

	}

	/**
	 * Deregister an account form pool
	 */
	public void deregisterAccount(Account account) throws SQLException {

		if (account != null) {

			//if (!exists(account))
			accounts.remove(account);

			LangUtil.consoleDebug(DEBUG2, "Account deregistered from pool: "
					+ account.getUid());

		}

	}

	/**
	 * Does account already exist (search by instance)?
	 */
	public boolean accountExists(Account account) {

		Iterator accountsIterator = accounts.iterator();
		while (accountsIterator.hasNext()) {

			Account a = (Account) accountsIterator.next();

			if (a == account)
				return true;

		}

		return false;

	}

	/**
	 * Does account already exist (search by UID)?
	 */
	public boolean accountExists(int uid) {

		Iterator accountsIterator = accounts.iterator();
		while (accountsIterator.hasNext()) {

			Account a = (Account) accountsIterator.next();

			if (a.getUid() == uid)
				return true;

		}

		return false;

	}

	/**
	 *  
	 */
	public Account getAccountByUid(int uid) {

		Iterator accountsIterator = accounts.iterator();
		while (accountsIterator.hasNext()) {

			Account a = (Account) accountsIterator.next();
			if (a.getUid() == uid)
				return a;

		}

		return null;

	}

	/**
	 * Returns an iterator about all accounts in the pool
	 */
	public Iterator getAccountIterator() {
		return accounts.iterator();
	}

	/**
	 * Returns the database id of a customer
	 */
	public int getCustomerIdForAccountUid(int uid) {

		Iterator accountsIterator = accounts.iterator();
		while (accountsIterator.hasNext()) {

			Account a = (Account) accountsIterator.next();
			if (a.getUid() == uid)
				return a.getCustomerId();

		}

		return -1;

	}

	/**
	 * @return
	 */
	public Iterator getDeletedAccountsFromPool() {

		Vector deletedAccounts = new Vector();

		Iterator accountsIterator = accounts.iterator();
		while (accountsIterator.hasNext()) {
			Account a = (Account) accountsIterator.next();
			if (a.isDelete())
				deletedAccounts.add(a);
		}

		return deletedAccounts.iterator();

	}

	/**
	 * @return
	 */
	public Iterator getFreeAccountsFromPool() {

		Vector freeAccounts = new Vector();

		Iterator accountsIterator = accounts.iterator();
		while (accountsIterator.hasNext()) {
			Account a = (Account) accountsIterator.next();
			if (a.isFree())
				freeAccounts.add(a);
		}

		return freeAccounts.iterator();

	}

	/**
	 * Looks for a free account in the pool and returns it
	 */
	public Account getFreeAccountFromPool() {

		Iterator accountsIterator = accounts.iterator();
		while (accountsIterator.hasNext()) {

			Account a = (Account) accountsIterator.next();
			if (a.isFree())
				return a;

		}

		return null;

	}

	/**
	 * Looks for accounts that are in pool but do not exist at mediaWays (used
	 * while sychronizing)
	 */
	public Vector getAtMediaWaysUnexistingAccounts() {

		Vector v = new Vector();
		Iterator accountsIterator = accounts.iterator();
		while (accountsIterator.hasNext()) {

			Account a = (Account) accountsIterator.next();
			if (!a.isExistsAtMediaWays())
				v.add(a);

		}

		//return v.iterator();
		return v;

	}

	/**
	 * Returns highest used UID from pool
	 */
	public int getHighestUid() {

		int actualAccountUid = 0;

		Iterator accountsIterator = accounts.iterator();
		while (accountsIterator.hasNext()) {

			Account a = (Account) accountsIterator.next();
			int uid = new Integer(a.getUid()).intValue();
			if (actualAccountUid < uid)
				actualAccountUid = uid;

		}

		return actualAccountUid;

	}

	private int lastLowestFreeUid = 0;

	/**
	 * Returns lowest free UID from pool
	 */
	public int getLowestFreeUid() {

		int actualAccountUid;
		int maybeNewUid;
		int minUid = 100000;
		int maxUid = 500000;

		Iterator accountsIterator = accounts.iterator();

		// We don't have any accounts so start with minUid
		if (!accountsIterator.hasNext())
			return minUid;

		// Walk through all existing accounts and look for
		// a free/unused UID
		while (accountsIterator.hasNext()) {

			Account a = (Account) accountsIterator.next();
			actualAccountUid = new Integer(a.getUid()).intValue();
			maybeNewUid = actualAccountUid + 1;

			LangUtil.consoleDebug(DEBUG2, "Looking for lowest free UID: "
					+ minUid + "/" + maxUid + "/" + actualAccountUid + "/"
					+ "!accountExists(" + maybeNewUid + ")");

			if (actualAccountUid >= minUid && actualAccountUid <= maxUid
					&& !accountExists(maybeNewUid))
				return maybeNewUid;

		}

		return -1;

	}

	/**
	 * Load all accounts from database into pool
	 * 
	 * If an account has no assign customer id, the account can be treated as
	 * free/usable for new customers
	 *  
	 */
	public void loadAccountsFromDatabase() throws SQLException {

		String stmt = "SELECT a.ID, a.Username, a.Password, a.CustomerID,"
				+ " a.existsatmediaways, a.lastmediawaysmsg"
				+ " FROM Accounts a" + " WHERE realmid = " + realm.getRealmId()
				+ " ORDER BY Username";
		LangUtil.consoleDebug(DEBUG2, this, "Loading accounts: STMT=" + stmt);
		ResultSet rs = jdbcDatabase.executeQuery(stmt);

		while (rs.next()) {

			Account a = new Account(new Integer(rs.getString(2)).intValue(), rs
					.getString(3));
			a.setDatabaseId(rs.getInt(1));

			a.setCustomerId(rs.getInt(4));
			if (rs.getInt(4) == 0) {
				a.setFree(true);
				freeAccounts++;
			} else {
				a.setCreated();
				usedAccounts++;
			}

			a.setPassword(rs.getString(3));
			a.setCustomerId(rs.getInt(4));
			a.setExistsAtMediaWays((rs.getInt(5) == 1 ? true : false));
			a.setMediaWaysMessage(rs.getString(6));

			registerAccount(a);

		}

		rs.close();

		LangUtil.consoleDebug(DEBUG, "Loaded " + usedAccounts
				+ " used accounts" + " and " + freeAccounts
				+ " free/usable accounts from database");

	}

	/**
	 * Purge an account: delete it from database, deregister it form the pool
	 * (used while sychronizing with mediaWays, the account is NOT deleted at
	 * mediaWays via LDAP!)
	 */
	public void purgeAccount(Account account) throws SQLException {

		String stmt = "DELETE FROM Accounts WHERE ID = "
				+ account.getDatabaseId();
		jdbcDatabase.executeUpdate(stmt);

		deregisterAccount(account);

		LangUtil.consoleDebug(DEBUG, "Account '" + account.getUid()
				+ "' in realm " + realm.getRealmId() + " was purged!!!");

	}

	/**
	 * Registers a new account in pool
	 */
	public void registerAccount(Account account) {

		if (account != null) {

			if (!accountExists(account))
				accounts.add(account);

			LangUtil.consoleDebug(DEBUG2, "Account " + account.getUid()
					+ " was registered in pool");

		}

	}

	/**
	 * Removes assignment between account and customer (set customer id = 0)
	 */
	public void unassignAccountFromCustomer(Account account, int customerId) {

		account.setCustomerId(0);

		/*
		 * String stmt = "UPDATE Accounts" + " SET CustomerID = 0" + " WHERE ID = " +
		 * account.getDatabaseId() + " AND CustomerID = " + customerId;
		 * 
		 * try { jdbcDatabase.executeUpdate(stmt); } catch (SQLException e) {
		 * e.printStackTrace(); }
		 */

	}

	/**
	 * Get ID for account from database
	 * 
	 * @param account
	 * @return @throws
	 *         IllegalArgumentException
	 * @throws SQLException
	 */
	public int getIdFromDatabase(Account account)
			throws IllegalArgumentException, SQLException {

		int id = 0;
		String stmtGetId = "SELECT ID FROM Accounts WHERE Username = '"
				+ account.getUid() + "' AND realmid = '" + realm.getRealmId()
				+ "'";
		ResultSet rsGetId = jdbcDatabase.executeQuery(stmtGetId);

		try {

			rsGetId.next();
			id = rsGetId.getInt(1);

		} catch (SQLException e) {
		}

		return id;

	}

	/**
	 * Updates an account in the database: the actual settings of an account
	 * object are written immediately to database
	 */
	public void updateAccountInDatabase(Account account) throws SQLException {

		int accountId = getIdFromDatabase(account);
		String stmt;

		// Account is new and souhld be created in database
		// else the account may be deleted
		if (account.isCreate()) {

			LangUtil.consoleDebug(DEBUG2, this,
					"--> account.isCreate() == true");

			// Test whether account already exists in database or not
			boolean accountAlreadyExists = false;
			String testStmt = "SELECT ID FROM Accounts WHERE Username = '"
					+ account.getUid() + "' AND realmid = "
					+ realm.getRealmId();
			ResultSet rs = jdbcDatabase.executeQuery(testStmt);
			while (rs.next()) {
				accountAlreadyExists = true;
			}

			// Account does not exist; create it
			if (!accountAlreadyExists) {

				stmt = "INSERT INTO Accounts"
						+ " (active, Username, Password, CustomerID, realmid, inprogress)"
						+ " VALUES (1, " + account.getUid() + ", '"
						+ account.getPassword() + "', "
						+ account.getCustomerId() + ", " + realm.getRealmId()
						+ ", 1)";

				LangUtil.consoleDebug(DEBUG2, this, "Account '"
						+ account.getUid() + "' does not exist in database."
						+ " Creating account: " + stmt);

			} else {

				// Account exists; update data
				stmt = "UPDATE Accounts" + " SET Password = '"
						+ account.getPassword() + "', CustomerID = "
						+ account.getCustomerId() + ", inprogress = "
						+ (account.isInProgress() ? 1 : 0)
						+ " WHERE Username = '" + account.getUid() + "'"
						+ " AND realmid = " + realm.getRealmId();

				LangUtil.consoleDebug(DEBUG2, this, "Account '"
						+ account.getUid() + "' already exists in database."
						+ " Updating account: STMT=" + stmt);

			}

			try {

				if (!Configuration.getInstance().argParser.hasArgument("-s")) {

					LangUtil.consoleDebug(DEBUG2, this,
							"!argParser.hasArgument(\"-s\")");

					jdbcDatabase.executeUpdate(stmt);

				} else
					LangUtil.consoleDebug(DEBUG2, this,
							"Won't modify database: STMT=" + stmt);

				int databaseId = getIdFromDatabase(account);
				LangUtil.consoleDebug(DEBUG, "New account has id: "
						+ databaseId);

				account.setDatabaseId(databaseId);

			} catch (SQLException e) {

				LangUtil.consoleDebug(DEBUG,
						"Could not insert account into database: "
								+ e.getCause() + ": " + e.getMessage());

			}

		} else if (account.isDeleted()) {

			LangUtil.consoleDebug(DEBUG2, this, "--> account.isDeleted()");

			purgeAccount(account);

		} else if (accountId > 0) {

			LangUtil.consoleDebug(DEBUG2, this, "--> accountId > 0");

			stmt = "UPDATE Accounts" + " SET CustomerID = "
					+ account.getCustomerId() + ", password = '"
					+ account.getPassword() + "', realmid = "
					+ realm.getRealmId() + ", existsatmediaways = "
					+ (account.isExistsAtMediaWays() ? 1 : 0)
					+ ", lastmediawaysmsg = '" + account.getMediaWaysMessage()
					+ "', inprogress = " + (account.isInProgress() ? 1 : 0)
					+ ", active = 1" + " WHERE ID = " + accountId;

			if (!Configuration.getInstance().argParser.hasArgument("-s")) {

				LangUtil.consoleDebug(DEBUG2, this,
						"!argParser.hasArgument(\"-s\")");

				LangUtil.consoleDebug(DEBUG2, this, "Updating account "
						+ account.getUid() + " in database: " + stmt);

				jdbcDatabase.executeUpdate(stmt);

			} else
				LangUtil.consoleDebug(DEBUG2, this,
						"Won't modify database: STMT=" + stmt);

		}

	}
}
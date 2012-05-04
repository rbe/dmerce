/*
 * Customer.java
 * 
 * Created on September 8, 2003, 5:52 PM
 */

package com.wanci.customer.callando;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;

import com.wanci.dmerce.jdbcal.Database;
import com.wanci.java.LangUtil;

/**
 * @author rb
 */
public class Customers {

	/**
	 * Debug flag
	 */
	private boolean DEBUG = Configuration.getInstance().DEBUG;

	private boolean DEBUG2 = Configuration.getInstance().DEBUG2;

	private Realm realm;

	Database jdbcDatabase;

	LinkedList customers = new LinkedList();

	AccountPool accountPool;

	final int AFTER_TODAY = 1;

	final int BEFORE_TODAY = 2;

	final int NO_DATE_RANGE = 3;

	/** Creates a new instance of Customers */
	public Customers(Realm realm) {
		this.realm = realm;
		this.jdbcDatabase = realm.getDatabaseConnection();
		this.accountPool = realm.getAccountPool();

		LangUtil.consoleDebug(DEBUG2, this,
				".<init>: Initializing customers for realm "
						+ realm.getRealmId() + "/" + realm.getRealmPrefix());

	}

	/**
	 * Convert dmerce v1/v2 timestamps to MySQL DATE-field
	 */
	public void convertTimestampsToDate() throws SQLException {

		String stmt = "UPDATE Customer"
				+ " SET StartDateDate = FROM_UNIXTIME(StartDate),"
				+ " AccountStartDate = FROM_UNIXTIME(AccountStart),"
				+ " AccountEndDate = FROM_UNIXTIME(AccountEnd)";
		//+ " WHERE AccountStartDate IS NULL"
		//+ " OR AccountEndDate IS NULL";
		jdbcDatabase.executeUpdate(stmt);

		LangUtil.consoleDebug(DEBUG, "Converted timestamps of table 'Customer'"
				+ " to date fields");

	}

	/**
	 * Returns an iterator for all customers
	 */
	public Iterator getIterator() {
		return customers.iterator();
	}

	/**
	 * Returns an iterator holding all customers having a status id
	 */
	public Customer getCustomerById(int id) {

		Iterator i = getIterator();
		while (i.hasNext()) {

			Customer customer = (Customer) i.next();
			if (customer.getDatabaseId() == id)
				return customer;

		}

		return null;

	}

	/**
	 * Returns an iterator holding all customers having a status id
	 */
	public Iterator getCustomersByStatusID(int statusId) {

		LinkedList l = new LinkedList();
		Iterator customerIterator = customers.iterator();
		while (customerIterator.hasNext()) {

			Customer c = (Customer) customerIterator.next();
			if (c.getStatusId() == statusId)
				l.add(c);

		}

		return l.iterator();

	}

	/**
	 * Loads existing customers (already have an account) with certain status id
	 * from database into memory
	 * 
	 * @param statusId
	 * @param dateRange
	 *            Should we load customers with AccountEndDate before or after
	 *            today? Use constants AFTER_TODAY (>=) and BEFORE_TODAY ( <=)
	 */
	public void loadCustomersByStatusId(int statusId, int dateRange) {

		StringBuffer stmt = new StringBuffer(
				"SELECT c.ID, c.FirstName, c.LastName, c.StatusID,"
						+ " c.StartDateDate, c.AccountStartDate, c.AccountEndDate,"
						+ " c.ResellerID, a.Username, c.Email, c.welcomeemailsent"
						+ " FROM Customer c, Accounts a, realms r"
						+ " WHERE c.active = 1" + " AND a.active = 1"
						+ " AND c.inprogress = 0" + " AND c.ID = a.CustomerID"
						+ " AND a.realmid = r.id" + " AND r.id = "
						+ realm.getRealmId() + " AND c.StatusID = " + statusId);
		if (dateRange == AFTER_TODAY)
			stmt.append(" AND AccountEndDate >= SYSDATE()");
		else if (dateRange == BEFORE_TODAY)
			stmt.append(" AND AccountEndDate <= SYSDATE()");
		stmt.append(" ORDER BY ID");

		int i = 0;

		try {

			ResultSet rs = jdbcDatabase.executeQuery(stmt.toString());

			while (rs.next()) {

				Customer customer = new Customer(rs.getInt(1), rs.getString(2),
						rs.getString(3), rs.getInt(4), new Integer(rs
								.getString(9)).intValue());
				customer.setContractStartDate(rs.getDate(5));
				customer.setAccountStartDate(rs.getDate(6));
				customer.setAccountEndDate(rs.getDate(7));
				customer.setResellerId(rs.getInt(8));
				customer.setEmail(rs.getString(10));
				customer
						.setWelcomeEmailSent((rs.getInt(11) == 1 ? true : false));

				customers.add(customer);

				i++;

			}

			rs.close();

		} catch (SQLException e) {
			LangUtil
					.consoleDebug(DEBUG, "Loading customers with status id "
							+ statusId
							+ "throwed an error with SQL statement: " + stmt);
			e.printStackTrace();
		}

		LangUtil.consoleDebug(DEBUG, "Loaded " + i
				+ " existing customers (status ID " + statusId + ")"
				+ " with accounts from database");

	}

	/**
	 * Load existing and active (= should own an account) customers from
	 * database
	 * 
	 * @throws SQLException
	 */
	void loadCustomersByStatusId2() throws SQLException {
		/*
		 * String stmt = "SELECT c.ID, c.FirstName, c.LastName, c.StatusID," + "
		 * c.StartDateDate, c.AccountStartDate, c.AccountEndDate," + "
		 * c.ResellerID, c.Email, c.welcomeemailsent" + " FROM Customer c,
		 * Accounts a, realms r" + " WHERE a.CustomerID = c.ID" + " AND
		 * a.realmid = r.id" + " AND r.id = " + realm.getRealmId() + " AND
		 * c.active = 1" + " AND c.StatusID = 2" + " ORDER BY ID";
		 */
		String stmt = "SELECT c.ID, c.FirstName, c.LastName, c.StatusID,"
				+ " c.StartDateDate, c.AccountStartDate, c.AccountEndDate,"
				+ " c.ResellerID, c.Email, c.welcomeemailsent"
				+ " FROM Customer c, Product p" + " WHERE c.ProductID = p.ID"
				+ " AND c.active = 1" + " AND c.StatusID = 2"
				+ " AND p.realmid = " + realm.getRealmId() + " ORDER BY ID";

		int customersWithAccount = 0;
		int customersWithoutAccount = 0;

		try {

			ResultSet rs = jdbcDatabase.executeQuery(stmt);
			while (rs.next()) {

				Customer customer = new Customer(rs.getInt(1), rs.getString(2),
						rs.getString(3), rs.getInt(4));
				customer.setContractStartDate(rs.getDate(5));
				customer.setAccountStartDate(rs.getDate(6));
				customer.setAccountEndDate(rs.getDate(7));
				customer.setResellerId(rs.getInt(8));
				customer.setEmail(rs.getString(9));
				customer
						.setWelcomeEmailSent((rs.getInt(10) == 1 ? true : false));

				// Active customers that do not have an account
				ResultSet rs2 = jdbcDatabase.executeQuery("SELECT ID"
						+ " FROM Accounts" + " WHERE CustomerID = "
						+ rs.getInt(1));
				boolean b = false;
				while (rs2.next()) {
					b = true;
				}

				if (!b) {
					customers.add(customer);
					customersWithoutAccount++;
				} else
					customersWithAccount++;

			}

			rs.close();

		} catch (SQLException e) {
			
			LangUtil.consoleDebug(DEBUG, "Loading customers with status id 2 "
					+ "throwed an error with SQL statement: " + stmt + ": "
					+ e.getCause() + ": " + e.getMessage());
			System.exit(2);
			
		}

		LangUtil.consoleDebug(DEBUG, "Loaded " + customersWithoutAccount
				+ " existing customers (status ID 2)"
				+ " without accounts and " + customersWithAccount
				+ " with accounts from database");

	}

	/**
	 * Load new customers (with status id 1)
	 * 
	 * @throws SQLException
	 */
	public void loadCustomersByStatusId1() throws SQLException {

		// + " WHERE c.StartDateDate <= SYSDATE()"
		/*
		 * String stmt = "SELECT c.ID, c.FirstName, c.LastName, c.StatusID," + "
		 * c.StartDateDate, c.AccountStartDate, c.AccountEndDate," + "
		 * c.ResellerID, c.Email, c.welcomeemailsent," + " a.Username,
		 * a.Password" + " FROM Customer c LEFT JOIN Accounts a ON c.ID =
		 * a.CustomerID" + " WHERE c.StatusID = 1" + " AND c.inprogress = 0" + "
		 * ORDER BY ID";
		 */
		String stmt = "SELECT c.ID, c.FirstName, c.LastName, c.StatusID,"
				+ " c.StartDateDate, c.AccountStartDate, c.AccountEndDate,"
				+ " c.ResellerID, c.Email, c.welcomeemailsent,"
				+ " p.ID as ProductID, p.OriginalName"
				+ " FROM Customer c, Product p" + " WHERE c.StatusID = 1"
				+ " AND c.inprogress = 0" + " AND c.ProductID = p.ID"
				+ " AND p.realmid = " + realm.getRealmId() + " ORDER BY c.ID";

		ResultSet rs = jdbcDatabase.executeQuery(stmt);
		int i = 0;

		while (rs.next()) {

			Customer customer = new Customer(rs.getInt(1), rs.getString(2), rs
					.getString(3), rs.getInt(4));
			customer.setNewCustomer(true);
			customer.setContractStartDate(rs.getDate(5));
			customer.setResellerId(rs.getInt(8));
			customer.setEmail(rs.getString(9));
			customer.setWelcomeEmailSent((rs.getInt(10) == 1 ? true : false));

			customers.add(customer);

			i++;

		}

		rs.close();

		LangUtil.consoleDebug(DEBUG, "Loaded " + i
				+ " existing customers (status ID 1) without accounts"
				+ " for realm " + realm.getRealmId() + " from database");

	}

	/**
	 * Retrieve certain customer from database by ID
	 * 
	 * @return
	 */
	public Customer getCustomerByIdFromDatabase(int id) throws SQLException {

		Customer customer = null;

		/*
		 * Incl. Realm! String stmt = "SELECT c.ID, c.FirstName, c.LastName,
		 * c.StatusID," + " c.StartDateDate, c.AccountStartDate,
		 * c.AccountEndDate," + " c.ResellerID, c.Email, c.welcomeemailsent," + "
		 * a.Username, a.Password" + " FROM Customer c, Accounts a, realms r" + "
		 * WHERE c.ID = " + id + " AND a.CustomerID = c.ID" + " AND a.realmid =
		 * r.id";
		 */
		String stmt = "SELECT c.ID, c.FirstName, c.LastName, c.StatusID,"
				+ " c.StartDateDate, c.AccountStartDate, c.AccountEndDate,"
				+ " c.ResellerID, c.Email, c.welcomeemailsent,"
				+ " a.Username, a.Password"
				+ " FROM Customer c LEFT JOIN Accounts a ON c.ID = a.CustomerID"
				+ " WHERE c.ID = " + id;

		ResultSet rs = jdbcDatabase.executeQuery(stmt);
		if (rs.next()) {

			customer = new Customer(rs.getInt(1), rs.getString(2), rs
					.getString(3), rs.getInt(4));
			customer.setNewCustomer(true);
			customer.setContractStartDate(rs.getDate(5));
			customer.setResellerId(rs.getInt(8));
			customer.setEmail(rs.getString(9));
			customer.setWelcomeEmailSent((rs.getInt(10) == 1 ? true : false));
			customer.setAccountUid(rs.getInt(11));

		}

		return customer;

	}

	/**
	 * Load all customers from database into memory
	 * 
	 * @throws SQLException
	 */
	public void loadCustomersFromDatabase() throws SQLException {

		loadCustomersByStatusId1();
		loadCustomersByStatusId2();
		loadCustomersByStatusId(3, NO_DATE_RANGE);
		//loadCustomersByStatusId(4, BEFORE_TODAY);
		loadCustomersByStatusId(5, NO_DATE_RANGE);
		loadCustomersByStatusId(6, NO_DATE_RANGE);

	}

	/**
	 * Changes the status ID of a customer in database
	 */
	public void setCustomerStatusId(Customer customer, int statusId) {
		customer.setStatusId(statusId);
	}

	/**
	 * Write data of customer into database
	 * 
	 * @param customer
	 */
	public void updateCustomerInDatabase(Customer customer) {

		if (customer != null) {

			int statusId = customer.getStatusId();
			int inProgress = customer.isInProgress() ? 1 : 0;
			int welcomeEmailSent = customer.isWelcomeEmailSent() ? 1 : 0;

			String stmt = "UPDATE Customer" + " SET StatusID = " + statusId
					+ ", inprogress = " + inProgress
					+ ", AccountStartDate = SYSDATE()"
					+ ", welcomeemailsent = " + welcomeEmailSent
					+ " WHERE ID = " + customer.getDatabaseId();

			try {
				jdbcDatabase.executeUpdate(stmt);
			} catch (SQLException e) {
				LangUtil.consoleDebug(DEBUG,
						"Could not set status id of customer '"
								+ customer.getDatabaseId() + " "
								+ customer.getLastName() + ", "
								+ customer.getFirstName() + "'");
			}

			LangUtil.consoleDebug(DEBUG, "Updated customer '"
					+ customer.getDatabaseId() + " " + customer.getLastName()
					+ ", " + customer.getFirstName() + "' (status=" + statusId
					+ ", inprogress=" + inProgress + ", welcomeEmailSent="
					+ welcomeEmailSent + ") in database.");

		} else
			LangUtil.consoleDebug(DEBUG,
					"Won't change customer: does not exist (object == null)!");

	}

}
/*
 * Created on Nov 29, 2003
 *  
 */
package com.wanci.customer.callando;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.wanci.dmerce.cli.ArgumentParser;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.dmerce.mail.SendMail;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: Email.java,v 1.18 2004/08/10 15:26:49 rb Exp $
 *  
 */
public class Email {

	/**
	 * Debug flag
	 */
	private boolean DEBUG = Configuration.getInstance().DEBUG;

	private boolean DEBUG2 = Configuration.getInstance().DEBUG2;

	private Database jdbcDatabase;

	//private String[] args;

	private ArgumentParser argParser = Configuration.getInstance().argParser;

	private Realm realm;

	private boolean switchS = false;

	private boolean switchE = false;

	private boolean sendRegMail = false;

	private boolean sendAccMail = false;

	private SendMail s;

	/**
	 * @param args
	 */
	public Email(Realm realm) {

		LangUtil.consoleDebug(DEBUG2, this, "Initializing");

		setArgumentParser(argParser);
		this.realm = realm;

		s = new SendMail("localhost");

		jdbcDatabase = realm.getDatabaseConnection();

	}

	/**
	 * Set headers for email (depending on command line switches)
	 * 
	 * @param customer
	 */
	void setEmailHeaders(Customer customer) {

		LangUtil.consoleDebug(DEBUG2, this, "setEmailHeaders()");

		try {

			s.setFromHeader("anmeldung@callando.de");
			if (!switchS && !switchE) {

				LangUtil.consoleDebug(DEBUG, "No switch -s or -e: " + "To: "
						+ customer.getEmail() + " Bcc: support@callando.de");

				s.setToHeader(customer.getEmail());
				s.addBccHeader("support@callando.de");
				s.addBccHeader("r.bensmann@1ci.de");

			} else if (!switchS && switchE) {

				LangUtil.consoleDebug(DEBUG,
						"No switch -s but switch -e: To: support@callando.de");

				s.setToHeader("support@callando.de");
				s.addBccHeader("r.bensmann@1ci.de");

			} else if (switchS) {

				LangUtil.consoleDebug(DEBUG, "Switch -s and no switch -e: "
						+ "To: r.bensmann@1ci.de");

				s.setToHeader("r.bensmann@1ci.de");

			}

		} catch (AddressException e1) {
			e1.printStackTrace();
		} catch (MessagingException e1) {
			e1.printStackTrace();
		}

		LangUtil.consoleDebug(DEBUG2, this, "setEmailHeaders(): done");

	}

	/**
	 * Convert integer 1/2 to gender male/female
	 * 
	 * @param gender
	 * @return String
	 */
	private String genderToString(int gender) {

		String s;

		if (gender == 1)
			s = "Sehr geehrter Herr ";
		else if (gender == 2)
			s = "Sehr geehrte Frau ";
		else
			s = "Sehr geehrte Frau/sehr geehrter Herr ";

		return s;

	}

	/**
	 * @param customer
	 */
	public void sendRegistrationEmail(Customer customer, Account account)
			throws SQLException {

		LangUtil.consoleDebug(DEBUG2, this, "sendRegistrationEmail()");

		StringBuffer sb = new StringBuffer();
		boolean rowOk = false;

		String stmt = "SELECT c.Gender, c.Street, c.ZipCode, c.City,"
				+ " c.Phone, c.Email, c.AccountNumber, c.AccountOwner,"
				+ " c.BankCode, c.Bank, c.ProductID, rp.Name, rp.Price, "
				+ " rp.InstallationCost, r.Text2, r.Text3"
				+ " FROM Customer c, ResellerProduct rp, Reseller r"
				+ " WHERE c.ID = " + customer.getDatabaseId()
				+ " AND c.ResellerID = rp.ResellerID"
				+ " AND c.ProductID = rp.ProductID"
				+ " AND c.ResellerID = r.ID";

		LangUtil.consoleDebug(DEBUG2, this, "Querying database: " + stmt);

		try {

			//jdbcDatabase.openConnection();

			ResultSet rs = jdbcDatabase.executeQuery(stmt);
			rowOk = rs.next();

			LangUtil.consoleDebug(DEBUG2, this,
					"sendRegistrationEmail(): rowOk=" + rowOk);

			// Got a row from database
			if (rowOk) {

				LangUtil
						.consoleDebug(DEBUG,
								"Got result from database. Generating registration mail...");

				sb.append(genderToString(rs.getInt("Gender")) + " "
						+ customer.getFirstName() + " "
						+ customer.getLastName() + ",\n\n"
						+ rs.getString("Text2")
						+ "Folgende Daten haben wir von Ihnen erhalten:\n\n"
						+ "Ihr gewählter Tarif:\t\t" + rs.getString("Name")
						+ "\n" + "Ihr monatlicher Preis:\t\t"
						+ rs.getString("Price") + " EURO\n"
						+ "Einmalige Einrichtungsgebühr:\t"
						+ rs.getString("InstallationCost") + " EURO\n\n"
						+ "Name, Vorname:\t\t\t" + customer.getLastName()
						+ ", " + customer.getFirstName() + "\n"
						+ "Adresse:\t\t\t" + rs.getString("Street") + "\n"
						+ "\t\t\t\t" + rs.getString("ZipCode") + " "
						+ rs.getString("City") + "\n" + "Telefon:\t\t\t"
						+ rs.getString("Phone") + "\n" + "Email:\t\t\t\t"
						+ rs.getString("Email") + "\n\n" + "Kontonummer:\t\t\t"
						+ rs.getString("AccountNumber") + "\n"
						+ "Kontoinhaber:\t\t\t" + rs.getString("AccountOwner")
						+ "\n" + "Bankleitzahl:\t\t\t"
						+ rs.getString("BankCode") + "\n" + "Bank:\t\t\t\t"
						+ rs.getString("Bank") + "\n\n"
						+ "gewünschtes Einrichtungsdatum:\t"
						+ customer.getContractStartDate() + "\n\n");

				if (account != null) {

					sb
							.append("Folgende Benutzerdaten wurden Ihnen zugeteilt:\n\n"
									+ "Benutzername:\t\t\t"
									+ realm.getRealmLoginPrefix()
									+ "/"
									+ account.getUid()
									+ realm.getRealmLoginSuffix()
									+ "\n"
									+ "Passwort:\t\t\t"
									+ account.getPassword()
									+ "\n\n");

				}

				sb.append(rs.getString("Text3") + "\n");

				//jdbcDatabase.closeConnection();

			} else
				LangUtil.consoleDebug(true, "No result from database!");

		} catch (SQLException e) {
			e.printStackTrace();
		}

		LangUtil.consoleDebug(DEBUG2, this, "sendRegistrationEmail(): rowOk="
				+ rowOk);

		// Got a row from database
		if (rowOk) {

			setEmailHeaders(customer);

			LangUtil.consoleDebug(DEBUG, "Sending mail to "
					+ s.getToHeaderString());

			try {

				s.setSubject("callando GmbH - Neuanmeldung");
				s.addBody(sb.toString(), s.TEXTPLAIN);

				s.sendMail();
				LangUtil.consoleDebug(DEBUG, "Mail sent");

			} catch (MessagingException e1) {
				e1.printStackTrace();
			}

		} else
			LangUtil.consoleDebug(true,
					"Not sending any mail due to invalid result from database");

	}

	/**
	 * Sends a mail to customers which accounts have been created or modified
	 * 
	 * @param customer
	 */
	public void sendAccountEmail(Customer customer, Account account)
			throws SQLException {

		StringBuffer sb = new StringBuffer();
		boolean rowOk = false;

		String stmt = "SELECT c.Gender, r.Text4, r.Text5"
				+ " FROM Customer c, Reseller r" + " WHERE c.ID = "
				+ customer.getDatabaseId() + " AND c.ResellerID = r.ID";

		try {

			//jdbcDatabase.openConnection();

			LangUtil.consoleDebug(DEBUG2, this, "Executing statement: " + stmt);
			ResultSet rs = jdbcDatabase.executeQuery(stmt);
			rowOk = rs.next();

			LangUtil.consoleDebug(DEBUG2, this, "sendAccountMail(): rowOk="
					+ rowOk);

			// Got row from database
			if (rowOk) {

				LangUtil.consoleDebug(DEBUG2, this,
						"Got result from database. Generating mail...");

				sb.append(genderToString(rs.getInt("Gender")) + " "
						+ customer.getFirstName() + " "
						+ customer.getLastName() + ",\n\n"
						+ rs.getString("Text4") + "\n"
						+ "Folgende Benutzerdaten wurden Ihnen zugeteilt:\n\n"
						+ "Benutzername:\t\t" + realm.getRealmLoginPrefix()
						+ "/" + account.getUid() + realm.getRealmLoginSuffix()
						+ "\n" + "Passwort:\t\t" + account.getPassword()
						+ "\n\n" + rs.getString("Text5") + "\n");

				//jdbcDatabase.closeConnection();

			} else
				LangUtil.consoleDebug(true, "No result from database!");

		} catch (SQLException e) {
			e.printStackTrace();
		}

		LangUtil.consoleDebug(DEBUG2, this, "sendAccountEmail(): rowOk="
				+ rowOk);

		// Got a row from database
		if (rowOk) {

			setEmailHeaders(customer);

			try {

				s.setSubject("callando GmbH - Accountdaten");
				s.addBody(sb.toString(), s.TEXTPLAIN);

				s.sendMail();
				LangUtil.consoleDebug(DEBUG, "Mail sent");

			} catch (MessagingException e1) {
				e1.printStackTrace();
			}

		} else
			LangUtil.consoleDebug(true,
					"Not sending any mail due to invalid result from database");

	}

	public void setArgumentParser(ArgumentParser argParser) {

		System.out.println("argParser=" + argParser);

		this.argParser = argParser;

		if (argParser.hasArgument("-d"))
			DEBUG = true;
		if (argParser.hasArgument("-dd"))
			DEBUG2 = true;
		if (argParser.hasArgument("-s"))
			setSwitchS(true);
		if (argParser.hasArgument("-e"))
			setSwitchE(true);
		if (argParser.containsValue("-t", "r"))
			setSendRegMail(true);
		if (argParser.containsValue("-t", "a"))
			setSendAccMail(true);

	}

	public void setSwitchE(boolean switchE) {
		this.switchE = switchE;
	}

	public void setSwitchS(boolean switchS) {
		this.switchS = switchS;
	}

	public void setSendRegMail(boolean sendRegMail) {
		this.sendRegMail = sendRegMail;
	}

	public void setSendAccMail(boolean sendAccMail) {
		this.sendAccMail = sendAccMail;
	}

	/**
	 * 
	 *  
	 */
	public void logic() {

		try {

			//jdbcDatabase.openConnection();

			// Retrieve information about an account for a certain
			// customer
			/*
			 * AccountPool accountPool = new AccountPool(realm);
			 * accountPool.loadAccountsFromDatabase(); Customers customers = new
			 * Customers(jdbcDatabase, accountPool);
			 */
			AccountPool accountPool = realm.getAccountPool();
			Customers customers = realm.getCustomers();
			int customerId = argParser.getInt("-c");
			Customer customer = customers
					.getCustomerByIdFromDatabase(customerId);
			Account account = accountPool.getAccountByUid(customer
					.getAccountUid());

			if (customer == null) {

				LangUtil.consoleDebug(true, "Customer " + customerId
						+ " not found.");
				System.exit(2);

			}

			if (switchE)
				LangUtil
						.consoleDebug(true, "Won't send any mails to customer!");

			// Send registration mail
			if (sendRegMail) {

				LangUtil.consoleDebug(true, "Sending registration mail for "
						+ customer.toString());

				if (!argParser.hasArgument("-p"))
					sendRegistrationEmail(customer, account);

			}

			// Send mail with account data
			if (sendAccMail) {

				if (account != null) {

					LangUtil.consoleDebug(true, "Sending account mail for "
							+ customer.toString());

					if (!argParser.hasArgument("-p"))
						sendAccountEmail(customer, account);

				} else
					LangUtil
							.consoleDebug(true,
									"No account for customer found. Will not send mail");

			}

			//jdbcDatabase.closeConnection();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * 
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {

		Boot.printCopyright("1[DSL] - SEND EMAILS");

		LangUtil.consoleDebug(true, "START");

		ArgumentParser argParser = new ArgumentParser(args);
		argParser.add("-c", 0, "* Customer ID");
		argParser.add("-d", null, "Debug mode");
		argParser.add("-dd", null, "Detailed debug mode");
		argParser.add("-e", null, "Do not send mails to customer");
		argParser.add("-h", null, "Display help");
		argParser.add("-r", 0, "* Realm: 1=callando, 2=callandoflat");
		argParser.add("-s", null, "Simulate and don't send any mails");
		argParser.add("-t", "",
				"* Type of mail: a=account data, r=registration");
		argParser.add("-p", null, "Don't process any mail");
		argParser.parse();

		// Help?!
		if (argParser.hasArgument("-h") | !argParser.hasArgument("-r")
				| !argParser.hasArgument("-c") | !argParser.hasArgument("-t")) {
			argParser.usage();
			System.exit(2);
		}

		Realm realm = null;
		if (argParser.getInt("-r") == 1)
			realm = new Realms().getRealm("callandodsl");
		else if (argParser.getInt("-r") == 2)
			realm = new Realms().getRealm("callandoflat");

		if (realm != null && argParser != null) {

			Configuration.getInstance().argParser = argParser;
			Email e = new Email(realm);
			e.logic();

		} else
			System.out.println("Could not create realm or argument parser!");

		LangUtil.consoleDebug(true, "STOP");

	}
}
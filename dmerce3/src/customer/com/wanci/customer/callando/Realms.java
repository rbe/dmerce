/*
 * Created on 04.07.2004
 *
 */
package com.wanci.customer.callando;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import com.enterprisedt.net.ftp.FTPException;
import com.wanci.dmerce.cli.ArgumentParser;
import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $$Id: Realms.java,v 1.4 2004/08/10 15:26:49 rb Exp $$
 *  
 */
public class Realms {

	/**
	 * Debug flags
	 */
	private boolean DEBUG = Configuration.getInstance().DEBUG;

	private boolean DEBUG2 = Configuration.getInstance().DEBUG2;

	/**
	 * Vector holding all realms
	 */
	private HashMap realms = new HashMap();

	/**
	 * Constructor
	 * 
	 * @throws SQLException
	 */
	public Realms() {
	}

	/**
	 * Initialize realm 'callandodsl'
	 * 
	 * @throws SQLException
	 */
	private Realm initCallandoDsl() throws SQLException {
		LangUtil.consoleDebug(DEBUG2, this,
				".<init>: Constructing realm 'callandodsl'");

		Realm realm = new Realm(1, "callandodsl", "dslflat", "%callando");
		realm
				.setTransferFtpAccount("213.20.254.85", "callandodsl",
						"sokat8106");

		LangUtil.consoleDebug(DEBUG2, this,
				".<init>: Realm 'callandodsl' constructed");

		realms.put("callandodsl", realm);

		return realm;

	}

	/**
	 * Initialize realm 'callandoflat'
	 * 
	 * @throws SQLException
	 */
	private Realm initCallandoFlat() throws SQLException {
		LangUtil.consoleDebug(DEBUG2, this,
				".<init>: Constructing realm 'callandoflat'");

		Realm realm = new Realm(2, "callandoflat", "dslflat", "%callandoflat");
		realm.setTransferFtpAccount("213.20.254.85", "callandofldap",
				"33f9f367");

		LangUtil.consoleDebug(DEBUG2, this,
				".<init>: Realm 'callandoflat' constructed");

		realms.put("callandoflat", realm);

		return realm;

	}

	/**
	 * Return realm "realmName"
	 * 
	 * @param realmName
	 * @return
	 */
	public Realm getRealm(String realmName) throws SQLException {

		Realm realm = (Realm) realms.get(realmName);
		if (realm == null) {

			if (realmName.equals("callandodsl"))
				realm = initCallandoDsl();
			else if (realmName.equals("callandoflat"))
				realm = initCallandoFlat();

		}

		return realm;

	}

	/**
	 * @param args
	 * @throws DmerceException
	 * @throws IOException
	 * @throws FTPException
	 * @throws SQLException
	 */
	public static void main(String[] args) throws DmerceException, IOException,
			FTPException, SQLException {

		// Configuration singleton
		Configuration cfg = Configuration.getInstance();

		// Parse command line arguments
		ArgumentParser argParser = new ArgumentParser(args);
		argParser.add("-d", null, "Show debug messages");
		argParser.add("-dd", null, "Show more debug messages");
		argParser.add("-e", null, "PR: Don't send emails to customers.");
		argParser.add("-f", "", "RA: Use local file to read answer.");
		argParser.add("-h", null, "Display help.");
		argParser.add("-i", "", "RA: Use remote file to read answer.");
		argParser.add("-m", null, "PR: Don't send mails.");
		argParser.add("-n", null, "PR: Don't transmit data to mediaWays.");
		argParser
				.add("-o", "",
						"PR: Process customers with given status ids (comma separated) only.");
		argParser
				.add("-p", null,
						"PR: Do not change passwords when assigning accounts to customers.");
		argParser.add("-q", null, "Be quiet.");
		argParser.add("-r", "", "* Set realm (1, 2)");
		argParser.add("-s", null,
				"Simulate. Don't modify database or transmit data.");
		argParser
				.add("-z", "", "* Mode: p = process requests, r = read answer");
		argParser.parse();

		// Be quiet
		if (argParser.hasArgument("-q")) {
			cfg.DEBUG = false;
			cfg.DEBUG2 = false;
		} else
			Boot.printCopyright("1[DSL]");

		// Simulate?
		if (argParser.hasArgument("-s"))
			LangUtil.consoleDebug(cfg.DEBUG, "Simulating!");

		// Set debug flags in configuration
		if (argParser.hasArgument("-d"))
			cfg.DEBUG = true;
		if (argParser.hasArgument("-dd"))
			cfg.DEBUG2 = true;

		// Add argument parser instance to configuration
		cfg.argParser = argParser;

		// Display help
		if (argParser.hasArgument("-h") | !argParser.hasArgument("-r")
				| !argParser.hasArgument("-z")) {
			argParser.usage();
			System.exit(1);
		}

		LangUtil.consoleDebug(cfg.DEBUG, "START");

		LangUtil.consoleDebug(cfg.DEBUG2, "Creating instance of 'Realms'");
		Realms realms = new Realms();

		// Process requests of realms (and transmit data to mediaWays)
		if (argParser.getString("-z").equals("p")) {

			LangUtil.consoleDebug(cfg.DEBUG, "Processing requests");

			if (argParser.getInt("-r") == 1)
				realms.getRealm("callandodsl").processRequests();
			if (argParser.getInt("-r") == 2)
				realms.getRealm("callandoflat").processRequests();
		}

		// Read answeres of realms (from mediaWays)
		if (argParser.getString("-z").equals("r")) {

			LangUtil.consoleDebug(cfg.DEBUG, "Reading answeres");

			if (argParser.getInt("-r") == 1)
				realms.getRealm("callandodsl").readAnswer();
			if (argParser.getInt("-r") == 2)
				realms.getRealm("callandoflat").readAnswer();
		}

		cfg.jdbcDatabase.closeConnection();

		LangUtil.consoleDebug(cfg.DEBUG, "STOP");

	}

}
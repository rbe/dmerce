/*
 * JdbcMySQL.java
 *
 * Created on January 6, 2003, 8:53 PM
 */

package com.wanci.dmerce.jdbcal;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.java.LangUtil;

/**
 * dmerce/MySQL database handler
 * 
 * @author rb
 */
public class MySQL extends DatabaseHandler {

	private static boolean DEBUG = false;

	public String driver = "com.mysql.jdbc.Driver";

	public String URL = "jdbc:mysql://localhost/test";

	public int port = 3306;

	public String user = "root";

	public String password = "";

	public MySQLDatabaseDefinition databaseDefinition = new MySQLDatabaseDefinition();

	/** Creates a new instance of JdbcMySQL */
	public MySQL() throws DmerceException {

		super.loadDriver(driver);
		super.setUrl(URL);
		super.setUsername(user);
		super.setPassword(password);

	}

	/** Creates a new instance of JdbcMySQL */
	public MySQL(String url, String username, String password)
			throws DmerceException {

		super.loadDriver(driver);
		super.setUrl(url);
		super.setUsername(username);
		super.setPassword(password);

	}

	/** Creates a new instance of JdbcMySQL */
	public MySQL(String hostname, String databaseName, String username,
			String password) throws DmerceException {

		super.loadDriver(driver);
		super.setHostname(hostname);
		super.setPort(port);
		super.setDatabaseName(databaseName);
		super.setUsername(username);
		super.setPassword(password);

		generateUrl();

	}

	/** Creates a new instance of JdbcMySQL */
	public MySQL(String hostname, int port, String databaseName,
			String username, String password) throws DmerceException {

		super.loadDriver(driver);
		super.setHostname(hostname);
		super.setPort(port);
		super.setDatabaseName(databaseName);
		super.setUsername(username);
		super.setPassword(password);

		generateUrl();

	}

	public void generateUrl() {

		setUrl("jdbc:mysql://" + getHostname() + ":" + getPort() + "/"
				+ getDatabaseName() + "?autoReconnect=true");

	}

	/**
	 * @see com.wanci.dmerce.dmf.JdbcDatabase#getDatabaseDefinition()
	 */
	public DatabaseDefinition getDatabaseDefinition() {
		return databaseDefinition;
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		MySQL j;

		try {

			j = new MySQL();
			j.test();

		} catch (DmerceException e) {
			// TODO Auto-generated catch block
			LangUtil.consoleDebug(DEBUG, e.getMessage());
		}

	}

}
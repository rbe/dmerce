/*
 * JdbcOracle.java
 *
 * Created on January 6, 2003, 8:53 PM
 */

package com.wanci.dmerce.jdbcal;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.java.LangUtil;

/** dmerce/ORACLE database handler
 *
 * @author  rb
 */
public class Oracle extends DatabaseHandler {

	private static boolean DEBUG = false;

	public String driver = "oracle.jdbc.OracleDriver";

	public String URL = "jdbc:oracle:thin:scott/tiger@localhost:1521:ora";

	public int port = 1521;

	public String user = "system";

	public String password = "manager";

	public OracleDatabaseDefinition databaseDefinition =
		new OracleDatabaseDefinition();

	/** Creates a new instance of JdbcOracle */
	public Oracle() throws DmerceException {

		super.loadDriver(driver);
		super.setUrl(URL);
		super.setUsername(user);
		super.setPassword(password);
	}

	/** Creates a new instance of JdbcOracle */
	public Oracle(String url) throws DmerceException {

		super.loadDriver(driver);
		super.setUrl(url);
	}

	/** Creates a new instance of JdbcOracle */
	public Oracle(String url, String username, String password)
		throws DmerceException {

		super.loadDriver(driver);
		super.setPort(port);
		super.setUrl(url);
		super.setUsername(username);
		super.setPassword(password);
	}

	/** Creates a new instance of JdbcOracle */
	public Oracle(
		String hostname,
		String databaseName,
		String username,
		String password)
		throws DmerceException {

		super.loadDriver(driver);
		super.setHostname(hostname);
		super.setPort(port);
		super.setDatabaseName(databaseName);
		super.setUsername(username);
		super.setPassword(password);
		generateUrl();
	}

	/** Creates a new instance of JdbcOracle */
	public Oracle(
		String hostname,
		int port,
		String databaseName,
		String username,
		String password)
		throws DmerceException {

		super.loadDriver(driver);
		super.setHostname(hostname);
		super.setPort(port);
		super.setDatabaseName(databaseName);
		super.setUsername(username);
		super.setPassword(password);
		generateUrl();
	}

	public void generateUrl() {
		//jdbc:oracle:thin:scott/tiger@localhost:1521:ora
		setUrl(
			"jdbc:oracle:thin"
				+ getUsername()
				+ "/"
				+ getPassword()
				+ "@"
				+ getHostname()
				+ ":"
				+ getPort()
				+ ":"
				+ getDatabaseName());
	}

	/**
	 * @see com.wanci.dmerce.dmf.JdbcDatabase#getDatabaseDefinition()
	 */
	public DatabaseDefinition getDatabaseDefinition() {
		return databaseDefinition;
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {

		/*
		Oracle j = new Oracle();
		j.test();
		
		Oracle j2 =
			new Oracle(
				"jdbc:oracle:thin:dmerce_sys/dmerce_sys@localhost:1521:wanci1",
				"dmerce_sys",
				"dmerce_sys");
		*/

		try {
			Oracle j2 =
				new Oracle(
					"jdbc:oracle:thin:pg/pg@10.48.35.3:1521:wanci2",
					"pg",
					"pg");
			j2.test();
		}
		catch (DmerceException e) {
			LangUtil.consoleDebug(DEBUG, e.getMessage());
		}

	}

}
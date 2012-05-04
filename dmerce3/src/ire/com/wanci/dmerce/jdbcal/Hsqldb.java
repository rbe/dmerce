/*
 * Hsqldb.java
 *
 * Created on January 27, 2004, 9:54 PM
 */

package com.wanci.dmerce.jdbcal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.wanci.dmerce.exceptions.DmerceException;

/** dmerce/Hsqldb (Hypersonic) database handler
 *
 * @author  mf
 */
public class Hsqldb extends DatabaseHandler {
	
	private static boolean DEBUG = true;

	public String driver = "org.hsqldb.jdbcDriver";

	public String URL = "jdbc:hsqldb:hsql://localhost:1702";

	public int port = 1702;

	public String user = "sa";

	public String password = "";

	public HsqldbDatabseDefinition databaseDefinition = new HsqldbDatabseDefinition();

	public Hsqldb() throws DmerceException {
		super.loadDriver(driver);
		super.setUrl(URL);
		super.setUsername(user);
		super.setPassword(password);
	}

	public Hsqldb(String url, String username, String password) throws DmerceException {
		super.loadDriver(driver);
		super.setUrl(url);
		super.setUsername(username);
		super.setPassword(password);
	}

	public Hsqldb(
		String hostname,
		String databaseName,
		String username,
		String password) throws DmerceException {
		super.loadDriver(driver);
		super.setHostname(hostname);
		super.setPort(port);
		super.setDatabaseName(databaseName);
		super.setUsername(username);
		super.setPassword(password);
		generateUrl();
	}

	public Hsqldb(
				String hostname,
				int port,
				String databaseName,
				String username,
				String password) throws DmerceException {
		
		super.loadDriver(driver);
		super.setHostname(hostname);
		super.setPort(port);
		super.setDatabaseName(databaseName);
		super.setUsername(username);
		super.setPassword(password);
		generateUrl();
	}

	public void generateUrl() {
		setUrl(
			"jdbc:hsqldb:hsql://"
				+ getHostname()
				+ ":"
				+ getPort());
	}

	/**
	 * @see com.wanci.dmerce.dmf.JdbcDatabase#getDatabaseDefinition()
	 */
	public DatabaseDefinition getDatabaseDefinition() {
		return databaseDefinition;
	}
	
	public static void main(String[] arg) {
		try {
			Hsqldb instance = new Hsqldb("jdbc:hsqldb:hsql://localhost:1702","sa","");
			PreparedStatement stmt = instance.getPreparedStatement("INSER INTO VKUNDE ");
			ResultSet result = stmt.executeQuery();
			// System.out.println("OK: "+result.getFetchSize());
			ResultSetMetaData meta = result.getMetaData();
			int colCount = meta.getColumnCount();
			for (int i = 1; i <= colCount; i++) {
				// System.out.println("Spalte: "+meta.getColumnName(i)+"("+ meta.getColumnClassName(i)+")");
			}
				
		} catch (DmerceException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
/*
 * JdbcDatabaseHandler.java
 * 
 * Created on 21. Dezember 2002, 21:51
 */

package com.wanci.dmerce.jdbcal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.Logfile;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.java.LangUtil;
import com.wanci.java.LinkedQueue;

/**
 * @author rb
 * @version $Id: DatabaseHandler.java,v 1.4 2004/07/26 11:41:43 rb Exp $
 * 
 * Provides an intelligent handler for SQL databases
 *  
 */
public abstract class DatabaseHandler implements Database {

	/**
	 * Debug-Flags
	 */
	private boolean DEBUG = false;

	private boolean DEBUG2 = false;

	/**
	 * Der Hostname des Datenbankservers
	 */
	private String hostname = "localhost";

	/**
	 * Der TCP/IP-Port für den Connect zur Datenbank
	 */
	private int port;

	/**
	 * Name der Datenbank (Instanz)
	 */
	private String databaseName = null;

	/**
	 * Benutzername f?r den Connect
	 */
	private String username = null;

	/**
	 * Passwort für den Benutzernamen
	 */
	private String password = null;

	/**
	 * URL für die Verbindung zur Datenbank
	 */
	private String url = null;

	/**
	 * Das Connection-Objekt
	 */
	private Connection conn;

	private DatabaseMetaData metaData;

	private String driverName = null;

	private String driverVersion = null;

	private int driverMajorVersion;

	private int driverMinorVersion;

	private LinkedQueue stmtQueue;

	private Logfile logfile;

	/**
	 * Creates an instance
	 *  
	 */
	public DatabaseHandler() {

		try {

			DEBUG = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
					"debug");
			DEBUG2 = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
					"core.debug");

		} catch (XmlPropertiesFormatException e) {
		}

		LangUtil.consoleDebug(DEBUG2, this, "Initializing statement queue");
		stmtQueue = new LinkedQueue();

		LangUtil.consoleDebug(DEBUG2, this, "Initialized");

	}

	/**
	 *  
	 */
	public void analyzeMetaData() throws SQLException {

		driverName = metaData.getDriverName();
		driverVersion = metaData.getDriverVersion();
		driverMajorVersion = metaData.getDriverMajorVersion();
		driverMinorVersion = metaData.getDriverMinorVersion();

	}

	/**
	 * Start a transaction / see endTransaction()
	 */
	public void beginTransaction() {
	}

	/**
	 * Checks if connection to database is open and usable If not, we open a new
	 * connection
	 * 
	 * @throws SQLException
	 */
	void checkConnection() throws SQLException {

		if (conn == null || conn.isClosed()) {

			LangUtil.consoleDebug(DEBUG2, this,
					"executeQuery(): Database connection not open!"
							+ " Opening connection");

			openConnection();

		}

	}

	/**
	 * Close connection to database
	 */
	public void closeConnection() throws SQLException {

		LangUtil.consoleDebug(DEBUG2, this, "Closing database connection");

		Iterator i = stmtQueue.iterator();
		while (i.hasNext()) {

			Statement s = (Statement) i.next();
			s.close();

		}

		conn.close();

		LangUtil.consoleDebug(DEBUG2, this, "Database connection closed");

	}

	/**
	 * Dump metadata of SQL connection to console
	 * 
	 * @throws SQLException
	 */
	public void dumpMetaData() throws SQLException {

		LangUtil.consoleDebug(DEBUG, "Database Metadata:");
		LangUtil.consoleDebug(DEBUG,
				"---------------------------------------------");
		LangUtil.consoleDebug(DEBUG, "Database name: "
				+ metaData.getDatabaseProductName());
		LangUtil.consoleDebug(DEBUG, "Database version: "
				+ metaData.getDatabaseProductVersion());
		LangUtil.consoleDebug(DEBUG, "Driver name: " + driverName);
		LangUtil.consoleDebug(DEBUG, "Driver version: " + driverVersion + "/"
				+ driverMajorVersion + "." + driverMinorVersion);
		LangUtil.consoleDebug(DEBUG, "Default transaction isolation: "
				+ metaData.getDefaultTransactionIsolation());
		LangUtil.consoleDebug(DEBUG, "Max. connections: "
				+ metaData.getMaxConnections());
		LangUtil.consoleDebug(DEBUG, "Max. user name length: "
				+ metaData.getMaxUserNameLength());
		LangUtil.consoleDebug(DEBUG, "Max. catalog name length: "
				+ metaData.getMaxCatalogNameLength());
		LangUtil.consoleDebug(DEBUG, "Max. schema name length: "
				+ metaData.getMaxSchemaNameLength());
		LangUtil.consoleDebug(DEBUG, "Max. table name length: "
				+ metaData.getMaxTableNameLength());
		LangUtil.consoleDebug(DEBUG, "SQL Keywords: "
				+ metaData.getSQLKeywords());
		LangUtil.consoleDebug(DEBUG,
				"---------------------------------------------");

		dumpResultSet(metaData.getSchemas());
		dumpResultSet(metaData.getCatalogs());
		dumpResultSet(metaData.getTables(null, null, null,
				new String[] { "TABLE" }));

	}

	/**
	 * Dump a result set to console
	 * 
	 * @throws SQLException
	 */
	public void dumpResultSet(ResultSet r) throws SQLException {

		ResultSetMetaData rmd = r.getMetaData();
		int columnCount = rmd.getColumnCount() + 1;
		int i = 1;

		LangUtil.consoleDebug(DEBUG, "=== dumpResultSet - BEGIN ===\n");

		while (r.next()) {

			for (int j = 1; j < columnCount; j++) {

				LangUtil.consoleDebug(DEBUG, "Row: " + i + " Column: " + j
						+ " -> " + rmd.getColumnName(j) + " = "
						+ r.getObject(j));
			}

			LangUtil.consoleDebug(DEBUG, "\n----------------------------\n");
			i++;

		}

		LangUtil.consoleDebug(DEBUG, "=== dumpResultSet - END   ===\n");

	}

	/**
	 *  
	 */
	public void dumpResultSetMetaData(ResultSetMetaData r) throws SQLException {

		for (int i = 1; i < r.getColumnCount(); i++) {

			LangUtil.consoleDebug(DEBUG, r.getColumnName(i) + ":" + " TYPE="
					+ r.getColumnType(i) + "/" + r.getColumnTypeName(i)
					+ " SIZE=" + r.getColumnDisplaySize(i) + " PRECISION="
					+ r.getPrecision(i) + " SCALE=" + r.getScale(i)
					+ " AUTOINCREMENT=" + r.isAutoIncrement(i)
					+ " CASESENSITIVE=" + r.isCaseSensitive(i) + " CLASS="
					+ r.getColumnClassName(i));

		}

	}

	/**
	 *  
	 */
	public void dumpSqlException(SQLException e) {

		int i = 1;
		e.getNextException();

		while (e != null) {

			logfile.putDump("\n-----------------" + "\nSQL Exception: " + i
					+ "\nMessage: " + e.getMessage() + "\nErrorCode: "
					+ e.getErrorCode() + "\nSQL-State: " + e.getSQLState()
					+ "\nURL: " + url + "\njdbc.drivers: "
					+ System.getProperty("jdbc.drivers") + "\nDriver info: "
					+ driverName + " " + driverVersion + "\nUsername: "
					+ username + "\n-----------------");

			i++;
			e = e.getNextException();

		}

	}

	/**
	 * End a transaction
	 */
	public void endTransaction() {
	}

	/**
	 *  
	 */
	public ResultSet executeQuery(String sql) throws SQLException,
			IllegalArgumentException {

		if (!sqlIsSelect(sql) && !sqlIsCreate(sql) && !sqlIsDrop(sql)
				&& !sqlIsExecute(sql))
			throw new IllegalArgumentException("Not a SQL SELECT/CREATE/DROP: "
					+ sql);

		checkConnection();

		LangUtil.consoleDebug(DEBUG2, this,
				"executeQuery(): Executing SELECT query: " + sql);

		Statement stmt = getStatement();
		stmt.execute(sql);
		ResultSet r = stmt.getResultSet();
		//putStatement(stmt);

		LangUtil.consoleDebug(DEBUG2, this, "Executed SELECT query");

		return r;

	}

	public int executeUpdate(String sql) throws SQLException,
			IllegalArgumentException {

		if (!sqlIsInsert(sql) && !sqlIsUpdate(sql) && !sqlIsDelete(sql)
				&& !sqlIsExecute(sql))
			throw new IllegalArgumentException("Not a SQL DML-Query: " + sql);

		checkConnection();

		LangUtil.consoleDebug(DEBUG2, this,
				"executeQuery(): Executing DML query: " + sql);

		Statement stmt = getStatement();
		int r = stmt.executeUpdate(sql);
		putStatement(stmt);

		LangUtil.consoleDebug(DEBUG2, this, "Executed DML query");

		return r;

	}

	public abstract void generateUrl();

	public CallableStatement getCallableStatement(String stmt)
			throws SQLException {

		checkConnection();
		return conn.prepareCall(stmt);

	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getHostname() {
		return hostname;
	}

	public String getPassword() {
		return password;
	}

	public int getPort() {
		return port;
	}

	public ResultSet getTables() throws SQLException {
		return metaData.getTables(null, null, null, new String[] { "TABLE" });
	}

	public String getUsername() {
		return username;
	}

	public PreparedStatement getPreparedStatement(String sql)
			throws SQLException {

		checkConnection();
		return conn.prepareStatement(sql);

	}

	/**
	 *  
	 */
	public Statement getStatement() throws SQLException {

		Statement stmt;

		checkConnection();

		if (stmtQueue.size() <= 0) {

			LangUtil.consoleDebug(DEBUG2, this, "Queue size: "
					+ stmtQueue.size() + ", creating new statement");

			stmt = conn.createStatement();

		} else {

			LangUtil.consoleDebug(DEBUG2, this, "Queue size: "
					+ stmtQueue.size() + ", retrieving statement from queue");

			stmt = (Statement) stmtQueue.retrieve();

		}

		return stmt;

	}

	/**
	 *  
	 */
	public Statement getStatement(int scroll_type, int concurrency_type)
			throws SQLException {

		Statement stmt;

		checkConnection();

		if (stmtQueue.size() <= 0) {

			LangUtil.consoleDebug(DEBUG2, this, "Queue size: "
					+ stmtQueue.size() + ", creating new statement");

			stmt = conn.createStatement(scroll_type, concurrency_type);

		} else {

			LangUtil.consoleDebug(DEBUG2, this, "Queue size: "
					+ stmtQueue.size() + ", retrieving statement from queue");

			stmt = (Statement) stmtQueue.retrieve();

		}

		return stmt;

	}

	public void loadDriver(String driver) throws DmerceException {

		String newJdbcDrivers;

		LangUtil.consoleDebug(DEBUG2, this, "Loading JDBC driver: '" + driver
				+ "'");

		if (driver != null) {

			Properties p = System.getProperties();
			String systemJdbcDrivers = p.getProperty("jdbc.drivers");

			if (systemJdbcDrivers != null
					&& systemJdbcDrivers.indexOf(driver) == -1)
				newJdbcDrivers = systemJdbcDrivers + ":" + driver;
			else
				newJdbcDrivers = driver;

			System.setProperty("jdbc.drivers", newJdbcDrivers);

			LangUtil.consoleDebug(DEBUG2, this, "Property jdbc.drivers: '"
					+ System.getProperty("jdbc.drivers") + "'");

			// Treiber explizit laden
			try {
				Class.forName(driver).newInstance();
			} catch (ClassNotFoundException e) {

				throw new DmerceException("Error loading driver: '" + driver
						+ "' cannot find class! Please check .jars"
						+ " and classpath: " + e.getMessage());

			} catch (InstantiationException e) {

				throw new DmerceException("Error loading driver: '" + driver
						+ "' cannot create an instance: " + e.getMessage());

			} catch (IllegalAccessException e) {

				throw new DmerceException("Error loading driver: '" + driver
						+ "': " + e.getMessage());

			}

		}

	}

	/**
	 *  
	 */
	public void openConnection() throws SQLException {

		if (url == null && hostname != null && port > 0)
			generateUrl();

		if (url != null && username != null && password != null) {

			if (conn==null || !conn.isClosed()) {

				LangUtil.consoleDebug(DEBUG2, this,
						"Opening database connection" + " to: " + url + ", "
								+ username + "/" + password);

				//  + "?useUnicode=true&characterEncoding=latin1"
				conn = DriverManager.getConnection(url, username, password);

				LangUtil.consoleDebug(DEBUG2, this,
						"Connection successfully opened");

				LangUtil.consoleDebug(DEBUG2, this, "Reading metadata");

				metaData = conn.getMetaData();
				analyzeMetaData();

				LangUtil.consoleDebug(DEBUG2, this, "Read metadata");

			} else
				LangUtil.consoleDebug(DEBUG2, this, "Connection already open!");

		} else {

			LangUtil.consoleDebug(DEBUG2, this, "Cannot open connection: "
					+ " url, hostname, username and/or password is missing");

		}

	}

	/**
	 * Put a statement back into the pool
	 */
	public void putStatement(Statement stmt) {

		stmtQueue.add(stmt);

		LangUtil.consoleDebug(DEBUG2, this, "Queue size: " + stmtQueue.size()
				+ ", added statement to queue");

	}

	public void setDatabaseName(String databaseName) {
		if (databaseName != null)
			this.databaseName = databaseName;
		else
			this.databaseName = "";
	}

	public void setHostname(String hostname) {
		if (hostname != null)
			this.hostname = hostname;
		else
			this.hostname = "localhost";
	}

	public void setPort(int port) {
		if (port > 0)
			this.port = port;
		else
			this.port = 0;
	}

	public void setUrl(String url) {
		if (url != null)
			this.url = url;
		else
			this.url = "";
	}

	public void setUsername(String username) {
		if (username != null)
			this.username = username;
		else
			this.username = "";
	}

	public void setPassword(String password) {
		if (password != null)
			this.password = password;
		else
			this.password = "";
	}

	private boolean sqlIsSelect(String sql) {
		if (sql.substring(0, 6).equalsIgnoreCase("SELECT"))
			return true;
		else
			return false;
	}

	private boolean sqlIsInsert(String sql) {
		if (sql.substring(0, 6).equalsIgnoreCase("INSERT"))
			return true;
		else
			return false;
	}

	public boolean sqlIsUpdate(String sql) {
		if (sql.substring(0, 6).equalsIgnoreCase("UPDATE"))
			return true;
		else
			return false;
	}

	public boolean sqlIsDelete(String sql) {
		if (sql.substring(0, 6).equalsIgnoreCase("DELETE"))
			return true;
		else
			return false;
	}

	public boolean sqlIsCreate(String sql) {
		if (sql.substring(0, 6).equalsIgnoreCase("CREATE"))
			return true;
		else
			return false;
	}

	public boolean sqlIsDrop(String sql) {
		if (sql.substring(0, 4).equalsIgnoreCase("DROP"))
			return true;
		else
			return false;
	}

	public boolean sqlIsExecute(String sql) {
		if (sql.substring(0, 7).equalsIgnoreCase("EXECUTE"))
			return true;
		else
			return false;
	}

	public void test() {

		if (DEBUG) {
			System.out.println("test-methode");
		}
		logfile.setSuppressDebug(false);

		try {

			LangUtil.consoleDebug(DEBUG, "opening connection");

			openConnection();

			try {
				LangUtil.consoleDebug(DEBUG, "drop table dmerceJdbcTest");
				executeQuery("DROP TABLE dmerceJdbcTest");
			} catch (SQLException e) {
			}

			LangUtil.consoleDebug(DEBUG, "create table dmerceJdbcTest");
			executeQuery("CREATE TABLE dmerceJdbcTest (x INT)");

			LangUtil.consoleDebug(DEBUG, "insert into table dmerceJdbcTest");
			executeUpdate("INSERT INTO dmerceJdbcTest VALUES (1)");

			ResultSet r = executeQuery("SELECT * FROM dmerceJdbcTest");
			dumpResultSet(r);

			LangUtil.consoleDebug(DEBUG, "dump metadata");
			dumpMetaData();

			try {
				LangUtil.consoleDebug(DEBUG, "drop table dmerceJdbcTest");
				executeQuery("DROP TABLE dmerceJdbcTest");
			} catch (SQLException e) {
			}

			LangUtil.consoleDebug(DEBUG, "closing connection");
			closeConnection();

		} catch (SQLException e) {
			dumpSqlException(e);
		}

	}

	/**
	 * Liefert eine Instanz einer JDBC Klasse zurück, die die Verbindung zur
	 * einer Datenbank darstellt. Je nach Werten in der dmerce.properties
	 * 
	 * @return Objekt vom Typ JdbcDatabase (Interface aller Jdbc*-Klassen)
	 */
	public static Database getDatabaseConnection(String jdbcPrefix) {

		Database jd = null;
		
		try {

			XmlPropertiesReader xmlPropertiesReader = XmlPropertiesReader
					.getInstance();

			String prefix = jdbcPrefix + ".jdbc";
			String jdbcUrl = xmlPropertiesReader.getProperty(prefix + ".url");
			String jdbcUsername = xmlPropertiesReader.getProperty(prefix
					+ ".username");
			String jdbcPassword = xmlPropertiesReader.getProperty(prefix
					+ ".password");

			if (jdbcUrl.indexOf("mysql") > 0)
				jd = new MySQL(jdbcUrl, jdbcUsername, jdbcPassword);
			else if (jdbcUrl.indexOf("postgresql") > 0)
				jd = new PostgreSQL(jdbcUrl, jdbcUsername, jdbcPassword);
			else if (jdbcUrl.indexOf("oracle") > 0)
				jd = new Oracle(jdbcUrl, jdbcUsername, jdbcPassword);
			else if (jdbcUrl.indexOf("pointbase") > 0)
				jd = new Pointbase(jdbcUrl, jdbcUsername, jdbcPassword);
			else if (jdbcUrl.indexOf("hsqldb") > 0)
				jd = new Hsqldb(jdbcUrl, jdbcUsername, jdbcPassword);

		} catch (XmlPropertiesFormatException e) {
			//e.printStackTrace();
		} catch (DmerceException e1) {
			//e1.printStackTrace();
		}

		return jd;

	}

	/**
	 * Alternative Methode zum Holen einer Datenbankverbindung, bei der man
	 * explizit Username und Passwort übergibt. Diese Methode arbeitet damit
	 * vollständig ohne Zugriff auf die properties.xml.
	 * 
	 * Hinweis: Insbesondere wird diese Methode in Verbindung mit dem
	 * Login-Modul für JBoss verwendet.
	 * 
	 * @author Masanori Fujita
	 * 
	 * @param jdbcUrl
	 *            JDBC-Url-String für die Datenbankverbindung
	 * @param jdbcUsername
	 *            Benutzerkennung für den Zugriff auf die Datenbank
	 * @param jdbcPassword
	 *            Passwort für den Zugriff auf die Datenbank
	 * @return Instanz einer Database-Klasse für die weitere Kommunikation mit
	 *         der Datenbank
	 * @throws DmerceException
	 *             Wird geworfen, falls ein Fehler beim Aufbau der
	 *             Datenbankverbindung auftritt.
	 */
	public static Database getDatabaseConnection(String jdbcUrl,
			String jdbcUsername, String jdbcPassword) throws DmerceException {

		Database jd = null;

		if (jdbcUrl.indexOf("mysql") > 0)
			jd = new MySQL(jdbcUrl, jdbcUsername, jdbcPassword);
		else if (jdbcUrl.indexOf("postgresql") > 0)
			jd = new PostgreSQL(jdbcUrl, jdbcUsername, jdbcPassword);
		else if (jdbcUrl.indexOf("oracle") > 0)
			jd = new Oracle(jdbcUrl, jdbcUsername, jdbcPassword);
		else if (jdbcUrl.indexOf("pointbase") > 0)
			jd = new Pointbase(jdbcUrl, jdbcUsername, jdbcPassword);
		else if (jdbcUrl.indexOf("hsqldb") > 0)
			jd = new Hsqldb(jdbcUrl, jdbcUsername, jdbcPassword);

		return jd;

	}

	/*
	 * public String toString() {
	 * 
	 * return "url: " + url + ", username: " + username + ", password: " +
	 * password; }
	 */
}
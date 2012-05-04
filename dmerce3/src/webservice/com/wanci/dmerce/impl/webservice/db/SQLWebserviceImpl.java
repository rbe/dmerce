/*
 * Created on Aug 20, 2003
 */
package com.wanci.dmerce.impl.webservice.db;

import java.io.StringWriter;
import java.rmi.RemoteException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.dmerce.jdbcal.Hsqldb;
import com.wanci.dmerce.jdbcal.HsqldbHelper;
import com.wanci.dmerce.jdbcal.MySQL;
import com.wanci.dmerce.jdbcal.Oracle;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.dmerce.webservice.db.JAXBTypeMap;
import com.wanci.dmerce.webservice.db.SQLWebservice;
import com.wanci.dmerce.webservice.db.xmlbridge.DATA;
import com.wanci.dmerce.webservice.db.xmlbridge.META;
import com.wanci.dmerce.webservice.db.xmlbridge.ObjectFactory;
import com.wanci.dmerce.webservice.db.xmlbridge.ROW;
import com.wanci.dmerce.webservice.db.xmlbridge.Rdbsdata;
import com.wanci.dmerce.webservice.db.xmlbridge.TYPEINFO;
import com.wanci.dmerce.webservice.db.xmlbridge.TYPEINFO.ColumnType;
import com.wanci.java.LangUtil;

/**
 * @author Masanori Fujita
 * @author Ralf Bensmann
 * @version $Id: SQLWebserviceImpl.java,v 1.3 2004/06/11 14:13:14 mf Exp $
 */
public class SQLWebserviceImpl implements SQLWebservice {

	private boolean DEBUG = false;

	private boolean DEBUG2 = false;

	private JAXBContext jc;

	private ObjectFactory objFactory;

	private Database database;

	private String errorMessage;

	private JAXBTypeMap typeMap;

	public SQLWebserviceImpl() throws DmerceException, SQLException {

		try {

			DEBUG = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
					"debug");
			DEBUG2 = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
					"core.debug");

		} catch (XmlPropertiesFormatException e) {
		}

		LangUtil.consoleDebug(DEBUG2, this, "Initialized XmlPropertiesReader");

		LangUtil.consoleDebug(DEBUG2, this, "Initializing JAXB");

		try {

			// JAXB initialisieren
			this.jc = JAXBContext
					.newInstance("com.wanci.dmerce.webservice.db.xmlbridge");
			this.objFactory = new ObjectFactory();
			typeMap = new JAXBTypeMap();

		} catch (JAXBException e) {

			String msg = "Caught JAXBException while initializing JAXB: "
					+ e.getMessage();

			LangUtil.consoleDebug(DEBUG2, this, msg);
			throw new DmerceException(msg);

		}

		LangUtil.consoleDebug(DEBUG2, this, "JAXB initialized");

		LangUtil.consoleDebug(DEBUG2, this, "Initializing database connection");

		database = DatabaseHandler.getDatabaseConnection("defaultDS");
		if (database == null) {

			LangUtil.consoleDebug(DEBUG2, this,
					"database == null! Cannot find property defaultDS?!");

			throw new DmerceException("Cannot get database connection "
					+ "'defaultDS'. Please check etc/applications.xml");

		}

		LangUtil.consoleDebug(DEBUG2, this,
				"Database connection initialized. URL: " + database.toString());

		database.openConnection();

	}

	/**
	 * @see com.wanci.dmerce.webservice.SQLQueryService#executeQuery(java.lang.String,
	 *      java.util.Map)
	 */
	public String executeQuery(String sqlStatement) throws RemoteException {

		LangUtil.consoleDebug(DEBUG2, this, "executeQuery(): Executing SQL: '"
				+ sqlStatement + "'");

		StringWriter stringWriter = new StringWriter();

		try {

			LangUtil.consoleDebug(DEBUG2, this,
					"executeQuery(): Executing statement: '" + sqlStatement
							+ "'");

			// SQL-Abfrage durchführen
			Statement stmt = database.getStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet result = stmt.executeQuery(sqlStatement);

			// In JAXB-Struktur bringen
			Rdbsdata rdbsdata = getJAXBStructure(sqlStatement, result);

			// Begin FIX-20040611-A
			// ResultSet schließen, damit der Cursor nicht offen bleibt.
			result.close();
			// End FIX-20040611-A

			// SQL-Statement in den Cache zurueck
			database.putStatement(stmt);

			LangUtil.consoleDebug(DEBUG2, this,
					"executeQuery(): Converted result into JAXB structure");

			// rdbs-Objekt marshallen
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(rdbsdata, stringWriter);

			LangUtil.consoleDebug(DEBUG2, this,
					"executeQuery(): Marshalled rdbs object");

		} catch (SQLException e) {

			e.printStackTrace();

			try {

				errorMessage = e.getMessage();
				Rdbsdata errordata = getJAXBStructure(sqlStatement, null);
				// rdbs-Objekt marshallen
				Marshaller m = jc.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				m.marshal(errordata, stringWriter);

				return stringWriter.toString();

			} catch (Exception nestede) {
				throw new RemoteException(nestede.getMessage());
			}
			// throw new RemoteException(e.getMessage());

		} catch (JAXBException e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}

		return stringWriter.toString();

	}

	/**
	 * @see com.wanci.dmerce.webservice.SQLQueryService#executeQuery(java.lang.String,
	 *      java.util.Map)
	 */
	public String executePreparedQuery(String sqlStatement, Map parameters)
			throws RemoteException {

		throw new RemoteException(
				"Webservice for executing prepared queries is not"
						+ " implemented at this time");

	}

	/**
	 * Hilfsmethode, die ein Resultset in die JAXB-Datenobjekte überträgt.
	 * 
	 * @param sqlStatement
	 *            SQL-Statement der Abfrage
	 * @param result
	 *            ResultSet der SQL-Abfrage, null im Fehlerfall
	 */
	private Rdbsdata getJAXBStructure(String sqlStatement, ResultSet result)
			throws JAXBException, SQLException {

		LangUtil.consoleDebug(DEBUG2, this, "Creating RBSData");

		// typeinfo-Objekt erzeugen
		TYPEINFO typeinfo = objFactory.createTYPEINFO();

		// Spalten des Resultsets durchlaufen
		if (result != null) {

			ResultSetMetaData sqlmeta = result.getMetaData();
			ColumnType coltype;
			Class columnClass;

			for (int i = 1; i <= sqlmeta.getColumnCount(); i++) {

				String columnName = sqlmeta.getColumnName(i);
				String columnClassName = sqlmeta.getColumnClassName(i);

				coltype = objFactory.createTYPEINFOColumnType();
				//Direktive: dmerce ist nicht CASE-insensitive
				//
				//alt: Spaltennamen grundsätzlich klein schreiben
				coltype.setName(columnName.toLowerCase());
				//neue Direktive: Spaltennamen so schreiben wie in DB
				//coltype.setName(sqlmeta.getColumnName(i));
				// TODO: Auf Richtigkeit Prüfen!
				// Eigenes Type-Mapping verwenden

				try {

					if (database instanceof Hsqldb) {

						HsqldbHelper helper = new HsqldbHelper(sqlmeta);
						columnClass = Class.forName(helper
								.getColumnClassName(i));

					} else
						columnClass = Class.forName(columnClassName);

					coltype.setType(typeMap.getMappedType(columnClass,
							sqlmeta.getScale(i)).getName());
					typeinfo.getColumn().add(coltype);

					LangUtil.consoleDebug(DEBUG2, this, "Column '"
							+ sqlmeta.getTableName(i) + "." + columnName
							+ "' has type '" + columnClassName + " -> "
							+ coltype.getType() + "'");

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

			}

		}

		// meta-Objekt erzeugen
		META meta = objFactory.createMETA();
		meta.setSqlStatement(sqlStatement);
		meta.setTypeInfo(typeinfo);

		// data-Objekt erzeugen
		DATA data = objFactory.createDATA();
		if (result != null) {

			ROW row;
			while (result.next()) {

				// row-Objekt erzeugen
				row = objFactory.createROW();

				for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
					// TODO: Bitte auf Richtigkeit prüfen!
					// Eigenes Type-Mapping verwenden
					// Start Debugging
					/*
					 * String debuggingAusgabe =
					 * "SQLWebserviceImpl.getJAXBStructure(): Feld
					 * "+result.getMetaData().getColumnName(i)+",
					 * Wert="+result.getString(i); if (result.getObject(i) !=
					 * null) { debuggingAusgabe += ",
					 * JDBC-Objekt-Typ="+result.getObject(i).getClass().getName(); }
					 * else { debuggingAusgabe += ", JDBC-Object ist null"; }
					 * LangUtil.consoleDebug(DEBUG, debuggingAusgabe);
					 */
					// Ende Debugging
					if (result.getObject(i) != null) {
						// LangUtil.consoleDebug(DEBUG,
						// "SQLWebserviceImpl.getJAXBStructure(): Füge
						// gemappten
						// String \""+typeMap.toString(result.getObject(i))+"\"
						// dem
						// JAXB-Objekt hinzu...");
						row.getField().add(
								typeMap.toString(result.getObject(i)));
						// LangUtil.consoleDebug(DEBUG,
						// "SQLWebserviceImpl.getJAXBStructure(): ...
						// hinzugefügt.");
					} else {
						// LangUtil.consoleDebug(DEBUG,
						// "SQLWebserviceImpl.getJAXBStructure(): Füge \"\"
						// JAXB-Objekt hinzu...");
						row.getField().add("");
						// LangUtil.consoleDebug(DEBUG,
						// "SQLWebserviceImpl.getJAXBStructure():
						// ...hinzugefügt.");
					}

				}

				data.getRow().add(row);

			}

		}

		// rdbsdata-Objekt erzeugen
		Rdbsdata rdbsdata = objFactory.createRdbsdata();
		rdbsdata.setMeta(meta);
		rdbsdata.setData(data);
		if (result == null)
			rdbsdata.setError(errorMessage);
		else
			rdbsdata.setError("");

		LangUtil.consoleDebug(DEBUG2, this, "RBSData created");

		return rdbsdata;

	}

	public int submitData(String workflowid, Map fields) throws RemoteException {

		throw new RemoteException("Webservice for submitting data is not"
				+ " implemented at this time");

	}

	public int updateData(String tableName, String primkey, Map fields)
			throws RemoteException, DmerceException {

		LangUtil.consoleDebug(DEBUG2, this, "updateData()");

		try {

			if (!fields.containsKey(primkey)) {
				String error = "Primary key is missing! Cannot continue!";
				LangUtil.consoleDebug(DEBUG, this, error);
				throw new RemoteException(error);
			}

			Iterator it = fields.keySet().iterator();
			String keyvalues = "";
			String key;
			while (it.hasNext()) {
				key = (String) it.next();
				if (key != null && !key.equals(primkey)) { // Spalte ist nicht
					// Primary-Key
					if (!keyvalues.equals(""))
						// Komma nur anhängen, wenn die Liste noch nicht leer
						// ist
						keyvalues += ",";
					keyvalues += key + "=?";
				}
			}

			LangUtil.consoleDebug(DEBUG2, this,
					"updateData() Creating update statement");

			String sql = "UPDATE " + tableName + " SET " + keyvalues
					+ " WHERE " + primkey + "=?";
			LangUtil.consoleDebug(DEBUG, sql);
			PreparedStatement statement = database.getPreparedStatement(sql);

			Iterator it2 = fields.keySet().iterator();
			LangUtil.consoleDebug(DEBUG2, this,
					"updateData() Number of fields: " + fields.size());
			int colNum = 1;
			Object mappedObject;
			while (it2.hasNext()) {
				key = (String) it2.next();
				// Primary-Key-Feld ignorieren, da Teil des WHERE-Clauses
				if (key != null && !key.equals(primkey)) {

					// Debug
					LangUtil.consoleDebug(DEBUG2, this, "Mapping object '"
							+ fields.get(key) + "'");

					mappedObject = typeMap.toJDBCObject(fields.get(key));

					statement.setObject(colNum++, mappedObject);

				}
			}
			int primaryKeyValue = ((Integer) fields.get(primkey)).intValue();
			statement.setInt(colNum++, primaryKeyValue); // Primary-Key angeben

			LangUtil.consoleDebug(DEBUG2, this,
					"updateData() .executeUpdate() on PreparedStatement: "
							+ statement.toString());

			statement.executeUpdate();

			return primaryKeyValue;

		} catch (SQLException e) {
			throw new RemoteException(e.toString());
		}

	}

	/**
	 * 
	 * @param sequenceName
	 * @return @throws
	 *         SQLException
	 */
	private int getNewSequenceNumber(String sequenceName) throws SQLException {

		String stmt = "SELECT " + sequenceName + ".nextval FROM dual";

		ResultSet result = database.executeQuery(stmt);
		result.next();
		int pkey = result.getInt(1);

		LangUtil.consoleDebug(DEBUG2, this,
				"getNewSequenceNumber() Oracle: getting primary key: " + stmt
						+ " = " + pkey);

		return pkey;

	}

	public int deleteData(String tableName, String key, int keyValue)
			throws RemoteException {

		LangUtil.consoleDebug(DEBUG2, this, "deleteData()");

		try {

			String sql = "DELETE FROM " + tableName + " WHERE " + key + "="
					+ keyValue;
			LangUtil.consoleDebug(DEBUG, "Deleting data: " + sql);

			return database.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RemoteException(e.toString());
		}

	}

	public int insertData(String tableName, String keyName, Map fields)
			throws RemoteException, DmerceException {

		LangUtil.consoleDebug(DEBUG2, this, "insertData(" + tableName + ", "
				+ keyName + ", map)");

		if (DEBUG) {

			Iterator testit = fields.keySet().iterator();
			while (testit.hasNext()) {

				String element = (String) testit.next();
				LangUtil.consoleDebug(DEBUG2, this, "Found key: " + element
						+ " with value: " + fields.get(element));

			}

		}

		Iterator it = fields.keySet().iterator();
		String key;
		String spalten = "";
		String fragezeichen = "";

		while (it.hasNext()) {

			key = (String) it.next();
			LangUtil.consoleDebug(DEBUG2, this, "Found key: " + key);

			// Pruefe key auf null, passiert wenn der
			// Primaerschluessel bei action type="maintain" in der
			// forms.xml
			// angegeben wird
			if (key != null && !key.equals(keyName)) {
				// falls Primary-Key-Feld enthalten, ignorieren
				spalten += key + ",";
				fragezeichen += "?,";
			}

		}
		spalten += keyName;
		fragezeichen += "?"; // für Primary-Key reservieren

		String sql = "INSERT INTO " + tableName + " (" + spalten + ") VALUES ("
				+ fragezeichen + ") ";
		LangUtil.consoleDebug(DEBUG, "Insert data: " + sql);

		int newPrimKey = -1;
		try {

			PreparedStatement statement = database.getPreparedStatement(sql);

			Iterator it2 = fields.keySet().iterator();
			LangUtil.consoleDebug(DEBUG2, this, "Number of fields: "
					+ fields.size());
			int colNum = 1;
			Object mappedObject;
			while (it2.hasNext()) {
				key = (String) it2.next();
				if (key != null && !key.equals(keyName)) {
					// falls key null oder Primary-Key-Feld enthalten,
					// ignorieren
					mappedObject = typeMap.toJDBCObject(fields.get(key));
					statement.setObject(colNum++, mappedObject);
				}
			}

			if (database instanceof Oracle) {
				// Nächste Sequence-Number von Oracle holen
				newPrimKey = getNewSequenceNumber(tableName + "_seq");
				// Bei Oracle explizit den Primary Key setzen
				statement.setInt(colNum++, newPrimKey);
			} else if (database instanceof MySQL | database instanceof Hsqldb) {
				// MySQL und Hypersonic bruachen keinen Wert für
				// Auto-Inc-Felder
				statement.setObject(colNum++, null);
			}

			LangUtil.consoleDebug(DEBUG2, this,
					"executeUpdate() auf PreparedStatement");
			statement.executeUpdate();

			// Bei MySQL und Hypersonic nun den automatisch erzeugten
			// Primary-Key
			// holen
			if (database instanceof MySQL | database instanceof Hsqldb) {
				ResultSet generated = statement.getGeneratedKeys();
				generated.next();
				newPrimKey = generated.getInt(1);
				generated.close();
			}

			statement.close();

		} catch (SQLException e) {

			e.printStackTrace();

			// Fehlermeldung erzeugen
			String fehler = "Message: " + e.getMessage() + "\nSQL-State: "
					+ e.getSQLState() + "\nerror code: " + e.getErrorCode();
			throw new RemoteException(fehler);

		}

		LangUtil.consoleDebug(DEBUG2, this, "Inserted data");

		return newPrimKey;

	}

	/**
	 * Ruft eine Stored Procedure auf.
	 */
	public void callProc(String procName, List parameter)
			throws RemoteException {

		// Statement zusammensetzen
		String stmt = "{call " + procName + "(";
		for (int i = 0; i < parameter.size(); i++) {
			stmt += "?";
			if (i < parameter.size() - 1) {
				stmt += ",";
			}
		}
		stmt += ")}";

		LangUtil.consoleDebug(DEBUG, "Calling stored procedure '" + procName
				+ "'");

		CallableStatement cstmt = null;
		try {

			// Callable Statement holen
			cstmt = database.getCallableStatement(stmt);
			// Parameter füllen
			Iterator it = parameter.iterator();
			int counter = 1;

			while (it.hasNext()) {

				Object element = it.next();
				cstmt.setObject(counter++, typeMap.toJDBCObject(element));

			}

			// Statement ausführen
			cstmt.execute();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RemoteException(e.getClass().getName() + ": "
					+ e.getMessage());
		} catch (DmerceException e) {
			e.printStackTrace();
			throw new RemoteException(e.getClass().getName() + ": "
					+ e.getMessage());
		} finally {
			try {
				cstmt.close();
			} catch (SQLException e) {
			}
		}

		LangUtil.consoleDebug(DEBUG, "Called stored procedure: " + procName);

	}

}
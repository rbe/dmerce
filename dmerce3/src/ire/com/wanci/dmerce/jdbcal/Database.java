/*
 * JdbcDatabase.java
 *
 * Created on 21. Dezember 2002, 20:51
 */

package com.wanci.dmerce.jdbcal;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.wanci.dmerce.exceptions.DmerceException;

/**
 * @author rb
 */
public interface Database {

	/** Holt alle MetaDaten einer Datenbankverbindung und speichert
	 * sie in Variablen
	 *
	 * @exception SQLException
	 */
	void analyzeMetaData() throws SQLException;

	/** Schlie?t eine Verbindung zur Datenbank und alle damit verbundenen
	 * Statements
	 *
	 * @exception SQLException
	 */
	void closeConnection() throws SQLException;

	DatabaseDefinition getDatabaseDefinition();

    /** Liefert den Namen der Datenbank, die konnektiert wird
     *
     * @return Datenbankname (Instanz)
     */
    String getDatabaseName();

	/** Liefert den Hostnamen f?r die Verbindung zur Datenbank
	 *
	 * @return Hostnamen
	 */
	String getHostname();

	/** Liefert den Port f?r die Verbindung zur Datenbank
	 *
	 * @return TCP/IP-Port
	 */
	int getPort();
    
    /** Liefert alle Tabellen eines Schemas/einer Datenbank
     * 
     * @return ResultSet
     * @throws SQLException
     */
    ResultSet getTables() throws SQLException;

	/**
	 * @exception SQLException
	 * @return Gibt ein Statement zur?ck
	 */
	Statement getStatement() throws SQLException;

	/**
	 * @exception SQLException
	 * @param scroll_type ResultSet.TYPE_SCROLL_INSENSITIVE oder ResultSet.TYPE_FORWARD_ONLY
	 * @return Gibt ein Statement zurück
	 */
	Statement getStatement(int scroll_type, int concurrency_type) throws SQLException;

	/** Gibt ein Statement an den Cache zur?ck, dass zuvor per
	 * getStatement() geholt wurde
	 *
	 * @param stmt Eine Instanz von Statement
	 */
	void putStatement(Statement stmt);
    
    /**
     * 
     * @param stmt
     * @return
     */
    CallableStatement getCallableStatement(String stmt) throws SQLException;

	/**
	 * @exception SQLException
	 * @return Gibt ein PreparedStatement zur?ck
	 */
	PreparedStatement getPreparedStatement(String sql) throws SQLException;

	/** Setzt Hostnamen f?r den Connect zur Datenbank
	 *
	 * @param hostname Hostnamen der Datenbank
	 */
	void setHostname(String hostname);

	/** Setzt TCP/IP-Port f?r den Connect zur Datenbank
	 *
	 * @param port TCP/IP-Port
	 */
	void setPort(int port);

	/** Setzt den Namen der Datenbank, die konnektiert werden soll
	 *
	 * @param databaseName Name der Datenbank
	 */
	void setDatabaseName(String databaseName);

	/** Setzt den Benutzernamen f?r die Datenbankverbindung
	 *
	 * @param username Der Benutzername
	 */
	void setUsername(String username);

	/** Setzt das Passwort f?r den Benutzer
	 * @param password Das Passwort
	 */
	void setPassword(String password);

	/** Setzt die URL f?r die Verbindung (als Alternative f?r die Methoden
	 * set{Hostname|DatabaseName|Username|Password})
	 *
	 * @param url Die URL (das Format steht in der Dokumentation des JDBC-Treibers)
	 */
	void setUrl(String url);

	/** L?dt den JDBC-Treiber
	 *
	 * @param driver JDBC-Treibername
	 */
	void loadDriver(String driver) throws DmerceException;

	/** ?ffnet eine Verbindung zur Datenbank
	 *
	 * @exception SQLException
	 */
	void openConnection() throws SQLException;

	/** F?hrt einen SELECT-Query auf der Datenbank aus
	 *
	 * @exception SQLException
	 * @exception IllegalArgumentException Wird ausgel?st, wenn das ?bergebene Statement
	 * kein SELECT-Statement ist
	 * @return Ein ResultSet mit dem Ergebnis der Abfrage
	 */
	ResultSet executeQuery(String sql)
		throws SQLException, IllegalArgumentException;

	/** F?hrt einen INSERT-, UPDATE- oder DELETE-Query auf der Datenbank aus
	 *
	 * @exception SQLException
	 * @exception IllegalArgumentException Wird ausgel?st, wenn das ?bergebene Statement
	 * kein INSERT-, UPDATE- oder DELETE-Statement ist
	 * @return Anzahl der betroffenen Zeilen
	 */
	int executeUpdate(String sql)
		throws SQLException, IllegalArgumentException;

	/** Zeigt den Inhalt einer SQLException auf stdout an
	 *
	 * @param e SQLException
	 */
	void dumpSqlException(SQLException e) throws SQLException;

	/**
	 *
	 */
	void beginTransaction();

	/**
	 *
	 */
	void endTransaction();

	/** Zeigt alle Meta-Daten auf stdout an
	 */
	void dumpMetaData() throws SQLException;

	/**
	 * @param r ResultSetMetaData eines ResultSets (ResultSet.getMetaData())
	 * @exception SQLException
	 */
	void dumpResultSetMetaData(ResultSetMetaData r) throws SQLException;

	/** Gibt ein ResultSet r auf stdout aus
	 *
	 * @param r Ein ResultSet
	 * @exception SQLException
	 */
	void dumpResultSet(ResultSet r) throws SQLException;
	
}
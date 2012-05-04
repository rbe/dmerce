/*
 * Created on Aug 26, 2003
 */
package com.wanci.dmerce.webservice.db;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import com.wanci.dmerce.exceptions.DmerceException;

/**
 * @author Masanori Fujita
 * @version $Id: SQLWebservice.java,v 1.1 2004/03/29 13:39:37 rb Exp $
 */
public interface SQLWebservice extends Remote {

	/**
	 * Führt eine SQL-Abfrage über den dmerce-Kern durch. Die Ergebnismenge
	 * wird vom Kern in eine XML-Struktur gebracht und als String
	 * zurückgegeben. Dieser String muss auf der Client-Seite wieder in eine
	 * Objektstruktur überführt werden.
	 * 
	 * @param id
	 *            Name, unter dem das Prepared Statement abgelegt ist
	 * @param parameters
	 *            Parameter des Prepared Statements (ersetzen die Fragezeichen)
	 * @return Ergebnismenge der SQL-Abfrage als XML-Plaintext-Dokument
	 */
	public String executePreparedQuery(String id, Map parameters)
		throws RemoteException;

	/**
	 * Führt eine SQL-Abfrage über den dmerce-Kern durch. Die Ergebnismenge
	 * wird vom Kern in eine XML-Struktur gebracht und als String
	 * zurückgegeben. Dieser String muss auf der Client-Seite wieder in eine
	 * Objektstruktur überführt werden.
	 * 
	 * @param sqlStatement
	 *            SQL-Statement
	 * @return Ergebnismenge der SQL-Abfrage als XML-Plaintext-Dokument
	 */
	public String executeQuery(String sqlStatement) throws RemoteException;

	/**
	 * Führt ein einfaches UPDATE aus und trägt die Daten in die Datenbank ein.
	 * 
	 * @return Anzahl der betroffenen Datensätze
	 * @param tableName
	 *            Name der Tabelle, in die geschrieben werden soll
	 * @param keyName
	 *            Name des Primary-Key-Feldes
	 * @param fields
	 *            Key-Value-Paare für die Felder, die eingetragen werden
	 *            sollen.
	 * @throws RemoteException
	 */
	public int updateData(String tableName, String keyName, Map fields)
		throws RemoteException, DmerceException;

	/**
	 * Führt ein einfaches INSERT aus und trägt die Daten in die Datenbank ein.
	 * 
	 * @return Primary Key des neuen Datensatzes
	 * @param tableName
	 *            Name der Tabelle, in die geschrieben werden soll
	 * @param keyName
	 *            Name des Primary-Key-Feldes
	 * @param fields
	 *            Key-Value-Paare für die Felder, die eingetragen werden
	 *            sollen.
	 * @throws RemoteException
	 */
	public int insertData(String tableName, String keyName, Map fields)
		throws RemoteException, DmerceException;

	/**
	 * Trägt Daten eines Formulars in die Datenbank ein.
	 * 
	 * @param formid
	 *            ID des Formulars, das eingetragen werden soll.
	 * @param fields
	 *            Key-Value-Paare für die Formular-Felder, die eingetragen
	 *            werden sollen
	 */
	public int submitData(String formid, Map fields) throws RemoteException;

	/**
	 * Ruft eine Stored Procedure in der Datenbank auf.
	 * 
	 * @param procName
	 *            Name der auzuführenden Stored Proc
	 * @param parameter
	 *            Liste der Parameterwerte
	 */
	public void callProc(String procName, List parameter)
		throws RemoteException;

	/**
	 * Löscht einen Datensatz aus der Datenbank Wenn der Datensatz mit der
	 * angegebenen keyValue nicht existiert, wird eine 0 zurückgegeben. Wenn
	 * key oder tableName ungültig sind, wird eine RemoteException geworfen mit
	 * einer SQL-Fehlermeldung.
	 * 
	 * @return Anzahl der betroffenen Zeilen
	 */
	public int deleteData(String tableName, String key, int keyValue)
		throws RemoteException;

}

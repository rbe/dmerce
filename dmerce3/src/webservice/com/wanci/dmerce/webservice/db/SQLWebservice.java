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
	 * F�hrt eine SQL-Abfrage �ber den dmerce-Kern durch. Die Ergebnismenge
	 * wird vom Kern in eine XML-Struktur gebracht und als String
	 * zur�ckgegeben. Dieser String muss auf der Client-Seite wieder in eine
	 * Objektstruktur �berf�hrt werden.
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
	 * F�hrt eine SQL-Abfrage �ber den dmerce-Kern durch. Die Ergebnismenge
	 * wird vom Kern in eine XML-Struktur gebracht und als String
	 * zur�ckgegeben. Dieser String muss auf der Client-Seite wieder in eine
	 * Objektstruktur �berf�hrt werden.
	 * 
	 * @param sqlStatement
	 *            SQL-Statement
	 * @return Ergebnismenge der SQL-Abfrage als XML-Plaintext-Dokument
	 */
	public String executeQuery(String sqlStatement) throws RemoteException;

	/**
	 * F�hrt ein einfaches UPDATE aus und tr�gt die Daten in die Datenbank ein.
	 * 
	 * @return Anzahl der betroffenen Datens�tze
	 * @param tableName
	 *            Name der Tabelle, in die geschrieben werden soll
	 * @param keyName
	 *            Name des Primary-Key-Feldes
	 * @param fields
	 *            Key-Value-Paare f�r die Felder, die eingetragen werden
	 *            sollen.
	 * @throws RemoteException
	 */
	public int updateData(String tableName, String keyName, Map fields)
		throws RemoteException, DmerceException;

	/**
	 * F�hrt ein einfaches INSERT aus und tr�gt die Daten in die Datenbank ein.
	 * 
	 * @return Primary Key des neuen Datensatzes
	 * @param tableName
	 *            Name der Tabelle, in die geschrieben werden soll
	 * @param keyName
	 *            Name des Primary-Key-Feldes
	 * @param fields
	 *            Key-Value-Paare f�r die Felder, die eingetragen werden
	 *            sollen.
	 * @throws RemoteException
	 */
	public int insertData(String tableName, String keyName, Map fields)
		throws RemoteException, DmerceException;

	/**
	 * Tr�gt Daten eines Formulars in die Datenbank ein.
	 * 
	 * @param formid
	 *            ID des Formulars, das eingetragen werden soll.
	 * @param fields
	 *            Key-Value-Paare f�r die Formular-Felder, die eingetragen
	 *            werden sollen
	 */
	public int submitData(String formid, Map fields) throws RemoteException;

	/**
	 * Ruft eine Stored Procedure in der Datenbank auf.
	 * 
	 * @param procName
	 *            Name der auzuf�hrenden Stored Proc
	 * @param parameter
	 *            Liste der Parameterwerte
	 */
	public void callProc(String procName, List parameter)
		throws RemoteException;

	/**
	 * L�scht einen Datensatz aus der Datenbank Wenn der Datensatz mit der
	 * angegebenen keyValue nicht existiert, wird eine 0 zur�ckgegeben. Wenn
	 * key oder tableName ung�ltig sind, wird eine RemoteException geworfen mit
	 * einer SQL-Fehlermeldung.
	 * 
	 * @return Anzahl der betroffenen Zeilen
	 */
	public int deleteData(String tableName, String key, int keyValue)
		throws RemoteException;

}

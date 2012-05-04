/*
 * Created on Aug 20, 2003
 */
package com.wanci.dmerce.webservice;

import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.dmerce.webservice.db.JAXBTypeMap;
import com.wanci.dmerce.webservice.db.QColumnException;
import com.wanci.dmerce.webservice.db.QColumnInfo;
import com.wanci.dmerce.webservice.db.QColumnTypeException;
import com.wanci.dmerce.webservice.db.QResult;
import com.wanci.dmerce.webservice.db.QRow;
import com.wanci.dmerce.webservice.db.QTypeInfo;
import com.wanci.dmerce.webservice.db.QTypeMissmatchException;
import com.wanci.dmerce.webservice.db.SQLWebservice;
import com.wanci.dmerce.webservice.db.xmlbridge.DATA;
import com.wanci.dmerce.webservice.db.xmlbridge.META;
import com.wanci.dmerce.webservice.db.xmlbridge.ROW;
import com.wanci.dmerce.webservice.db.xmlbridge.RdbsdataType;
import com.wanci.dmerce.webservice.db.xmlbridge.TYPEINFO;
import com.wanci.java.LangUtil;

/**
 * Wrapper-Klasse, die einen transparenten Aufruf des SQLQueryWebservice
 * durchführt.
 * 
 * @author Masanori Fujita
 * @version $Id: SQLService.java,v 1.2 2004/04/15 12:02:11 rb Exp $
 */
public class SQLService {

	private boolean DEBUG = false;
	private boolean DEBUG2 = false;

	private SQLWebservice service;

	private JAXBTypeMap typeMap;

	private String sqlErrorMessage;

	/**
	 * @throws DmerceException
	 */
	public SQLService() throws DmerceException {

		LangUtil.consoleDebug(DEBUG2, this, "Initializing");

		try {
		    
			DEBUG =
				XmlPropertiesReader.getInstance().getPropertyAsBoolean("debug");
			DEBUG2 =
				XmlPropertiesReader.getInstance().getPropertyAsBoolean(
					"core.debug");
			
		}
		catch (XmlPropertiesFormatException e) {
		}

		LangUtil.consoleDebug(DEBUG2, this, "Initializing webservice");

		// WebServiceLocator holen
		WebServiceLocator wsl = WebServiceLocator.getInstance();
		// WebServiceLocator nach SQLQueryWebservice suchen lassen
		service = wsl.getSQLWebservice();

		LangUtil.consoleDebug(DEBUG2, this, "Initializing JAXB type map");
		typeMap = new JAXBTypeMap();

	}

	/**
	 * Führt eine SQL-Abfrage über den dmerce-Kern durch. Die Ergebnismenge
	 * wird vom Kern in eine XML-Struktur gebracht und als String
	 * zurückgegeben. Dieser String wird transparent in eine Objektstruktur
	 * überführt. Für die Serialisierung und Deserialisierung wird JAXB
	 * verwendet.
	 * 
	 * @param sqlStatement
	 *            SQL-Statement
	 * @return Ergebnismenge der SQL-Abfrage als
	 *         {@link com.wanci.dmerce.webservice.db.QResult QResult}-Objekt
	 */
	public QResult executeQuery(String sqlStatement)
		throws
			QColumnException,
			QColumnTypeException,
			QTypeMissmatchException,
			DmerceException {

		String resultstring = null;
		QResult qResult = null;
		// Rückgabestring mit JAXB parsen
		try {
			// Webservice-Aufruf durchführen
			resultstring = service.executeQuery(sqlStatement);
			// JAXBContext erzeugen für das entsprechende Package
			JAXBContext jc =
				JAXBContext.newInstance(
					"com.wanci.dmerce.webservice.db.xmlbridge");
			// Unmarshaller erzeugen
			Unmarshaller u = jc.createUnmarshaller();
			// Unmarshalling
			StringReader stringReader = new StringReader(resultstring);
			RdbsdataType rdbs =
				(RdbsdataType) u.unmarshal(new StreamSource(stringReader));

			// META-Daten holen und in QTypeInfo übertragen.
			QTypeInfo newTypeInfo = new QTypeInfo();

			META meta = rdbs.getMeta();
			TYPEINFO typeinfo = meta.getTypeInfo();
			List colList = typeinfo.getColumn();
			Iterator colinfoIt = colList.iterator();
			while (colinfoIt.hasNext()) {
				TYPEINFO.ColumnType coltype =
					(TYPEINFO.ColumnType) colinfoIt.next();
				QColumnInfo newColumnInfo =
					new QColumnInfo(
						coltype.getName(),
						Class.forName(coltype.getType()));
				newTypeInfo.addColumnInfo(newColumnInfo);
			}

			// QResult anhand der META-Info erzeugen
			qResult = new QResult(meta.getSqlStatement(), newTypeInfo);

			// Eventuelle Fehler abfangen
			if (rdbs.getError().equals("")) {

				// DATA-Daten holen
				DATA data = rdbs.getData();

				Iterator it = data.getRow().iterator();
				ROW aRow;
				int colNum = 0;
				while (it.hasNext()) {

					aRow = (ROW) it.next();
					QRow newRow = new QRow(newTypeInfo);
					Iterator rowit = aRow.getField().iterator();
					QColumnInfo aColumnInfo;
					String fieldValue;
					Object outputObject;

					while (rowit.hasNext()) {

						aColumnInfo = newTypeInfo.getColumnInfo(colNum);
						fieldValue = (String) rowit.next();

						LangUtil.consoleDebug(
							DEBUG2,
							this,
							"Column '"
								+ newTypeInfo.getColumnName(colNum)
								+ "' has value '"
								+ fieldValue
								+ "'");

						outputObject =
							typeMap.toQResultObject(
								fieldValue,
								newTypeInfo.getColumnType(
									aColumnInfo.getName()));
						newRow.setValue(aColumnInfo.getName(), outputObject);
						colNum++;

					}

					colNum = 0;
					qResult.addRow(newRow);

				}

			}
			else
				qResult.setError(rdbs.getError());

		}
		catch (JAXBException je) {

			String msg =
				"Error while processing JAXB result:"
					+ " The webservice returned a void result to the client."
					+ " <br>Maybe you have incompatible software components"
					+ " (versions)? Consider updating dmerce(R) software!";

			LangUtil.consoleDebug(
				DEBUG2,
				this,
				msg + " Exception: " + je.getCause() + ": " + je.getMessage());

			throw new DmerceException(msg);

		}
		catch (ClassNotFoundException e) {

			String msg =
				"Error while converting a JAXB object into a QResult object: "
					+ e.getMessage();

			LangUtil.consoleDebug(DEBUG2, this, msg);

			throw new DmerceException(msg);

		}
		catch (RemoteException e) {
			throw new DmerceException(e.getMessage());
		}

		return qResult;

	}

	/**
	 * Return sql error message that exists in case of an error during an
	 * update/insert
	 * 
	 * @return String with SQL error message
	 */
	public String getSqlErrorMessage() {
		return sqlErrorMessage;
	}

	/**
	 * Führt ein einfaches UPDATE auf der Datenbank aus, bei der ein bestimmter
	 * Datensatz geändert werden soll.
	 * 
	 * @return Anzahl der betroffenen Datensätze, -1 bei Fehlern
	 * @param tableName
	 *            Name der Tabelle, in die geschrieben werden soll
	 * @param keyName
	 *            Name des Primary-Key-Feldes
	 * @param fields
	 *            Key-Value-Paare für die Felder, die eingetragen werden
	 *            sollen.
	 */
	public int updateData(String tableName, String primkeyName, Map values) {

		int updatedRows = -1;

		try {
			updatedRows = service.updateData(tableName, primkeyName, values);
		}
		catch (RemoteException e) {

			if (DEBUG2)
				e.printStackTrace();

			sqlErrorMessage = e.getMessage();

		}
		catch (DmerceException e) {

			if (DEBUG2)
				e.printStackTrace();

			sqlErrorMessage = e.getMessage();

		}

		return updatedRows;

	}

	/**
	 * Führt ein einfaches INSERT auf der Datenbank aus, bei der ein bestimmter
	 * Datensatz geändert werden soll.
	 * 
	 * @return Neu generierte Primary Key
	 * @param tableName
	 *            Name der Tabelle, in die geschrieben werden soll
	 * @param keyName
	 *            Name des Primary-Key-Feldes
	 * @param fields
	 *            Key-Value-Paare für die Felder, die eingetragen werden
	 *            sollen.
	 */
	public int insertData(String tableName, String primkeyName, Map values) {

		int newPrimaryKey = -1;

		try {
			newPrimaryKey = service.insertData(tableName, primkeyName, values);
		}
		catch (RemoteException e) {

			if (DEBUG2)
				e.printStackTrace();

			sqlErrorMessage = e.getMessage();

		}
		catch (DmerceException e) {

			if (DEBUG2)
				e.printStackTrace();

			sqlErrorMessage = e.getMessage();

		}

		return newPrimaryKey;

	}

	/**
	 * Löscht einen Datensatz aus der Datenbank.
	 * 
	 * @param tableName
	 *            Name der Tabelle, in die geschrieben werden soll
	 * @param primkeyName
	 *            Name des Primary-Key-Feldes
	 * @param primkeyValue
	 *            Primary-Key-Wert des zu löschenden Datensatzes
	 * @return Anzahl der betroffenen Datensätze, 0 falls Datensatz nicht
	 *         gefunden, -1 falls Löschvorgang aus anderen Gründen
	 *         fehlgeschlagen ist
	 */
	public int deleteData(
		String tableName,
		String primkeyName,
		int primkeyValue) {
		try {
			return service.deleteData(tableName, primkeyName, primkeyValue);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Ruft eine Stored Procedure auf, die nur IN-Parameter erwartet.
	 * 
	 * @param procName
	 *            Name der Stored Procedure
	 * @param parameter
	 *            Liste von Parameterobjekten, die an die StoredProc übergeben
	 *            werden sollen.
	 */
	public void callProc(String procName, List parameter)
		throws DmerceException {
		try {
			service.callProc(procName, parameter);
		}
		catch (RemoteException e) {
			DmerceException de = new DmerceException(e.getMessage());
			de.setStackTrace(e.getStackTrace());
			throw de;
		}
	}

}

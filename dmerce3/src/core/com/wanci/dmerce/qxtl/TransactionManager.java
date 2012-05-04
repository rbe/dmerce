/*
 * Created on Jul 9, 2003
 */
package com.wanci.dmerce.qxtl;

import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.dmerce.kernel.XmlPropertiesReader;

/**
 * @author pg
 * @author mm
 * @version $Id: TransactionManager.java,v 1.4 2003/09/30 14:27:37 pg Exp $
 * 
 */
public class TransactionManager implements TransactionManagerInterface {

	/**
	 * xml document 
	 */
	protected Document docArgs;
	/**
	 * parser
	 */
	protected SAXBuilder parser;

	/**
	 * availability of the database
	 */
	private boolean databaseAvailable = false;

	/**
	 * singleton pattern
	 */
	private QueueTxm queue;

	/**
	 * gewöhnliche Fehlersuch-Variable
	 */
	private boolean DEBUG = false;

	/**
	 * singleton design pattern variable
	 */
	private static TransactionManager object = null;

	/**
	 * properties Handler
	 */
	private XmlPropertiesReader dmerceProperties;

	/**
	 * database handler
	 */
	private Database jdbcDatabase;

	/**
	 * temp queue
	 * Vector of Elements
	 * @return
	 */
	private Vector tempQueue = new Vector();

	/**
	 * singleton pattern 
	 * @return object
	 */
	public static synchronized TransactionManager getInstance()
		throws XmlPropertiesFormatException {

		if (object == null) {
			object = new TransactionManager();
		}

		return object;

	}

	/**
	 * opens a database connection and sets availableDatabase to true if the
	 * database is available, else availableDatabase is false
	 */
	public TransactionManager() throws XmlPropertiesFormatException {
		//debugging
		queue = QueueTxm.getInstance();
		dmerceProperties = XmlPropertiesReader.getInstance();
		if (dmerceProperties.propertyExists("transactionmanager.debug")) {
			DEBUG =
				dmerceProperties.getPropertyAsBoolean(
					"transactionmanager.debug");
		}

		//database connections
		try {
			jdbcDatabase = DatabaseHandler.getDatabaseConnection("test1");
			jdbcDatabase.openConnection();
			databaseAvailable = true;
		}
		catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}
		databaseAvailable = false;

	}

	/**
	 * saves the transactions that couldn't be executed in a queue
	 * @return
	 */
	public int saveQueue() {
		return queue.saveQueue(tempQueue);
	}

	/**
	 * @param transaction
	 * @return
	 */
	public int checkTransactionResult(Element transaction) {
		Element error = transaction.getChild("error");
		if (error != null) {
			int i = Integer.parseInt(error.getAttributeValue("value"));
			return i;
		}

		return 0;
	}

	/**
	 * build a xml document from the given string
	 * @param string
	 * @return
	 */
	private Element readTransaction(String string) {
		parser = new SAXBuilder();
		try {
			docArgs = parser.build(new StringReader(string));
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return (Element) docArgs.getRootElement().clone();
	}

	/**
	 * gets a string in xml style and searches for the Elements and
	 * Attributes. 
	 */
	public Element executeTransactionAsString(String string) {
		//string: <transaction>...<step type="..."> ... </transaction>
		Element el = readTransaction(string);
		return executeTransaction(el);
	}

	public int executeDefinedStep(
		Element step,
		String command,
		String parameter)
		throws XmlPropertiesFormatException {

		int transactionId = 0;
		int rowcount = 0;

		try {
			String sql =
				"SELECT transId, stepId, rollbackId, position, type, command"
					+ " FROM transactionStepsView where transactionName='"
					+ command
					+ "' order by position";
			if (databaseAvailable) {
				ResultSet rs = jdbcDatabase.executeQuery(sql);

				while (rs.next()) {
					rowcount++;
					//execute command with type, command and parameters
					executeCommand(
						step,
						rs.getString(5),
						rs.getString(6),
						parameter);
					transactionId = rs.getInt(1);
					//transaction found, now get the neccessary steps
				}
			}

			if (rowcount == 0)
				return -1;
			else
				return transactionId;

		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 
	 * @param command
	 * @param parameter
	 * @return
	 */
	String parseParametersIntoCommand(String command, String parameter) {
		if (parameter != "" && parameter != null) {
			String[] parameters = parameter.split("\\|");
			for (int i = 0; i < parameters.length; i++) {
				try {
					String[] splits = command.split("%", 3);
					command = splits[0] + "'" + parameters[i] + "'" + splits[2];
				}
				catch (Exception e) {
					System.err.println(
						"EXECUTE CMD" + command + ": too many parameters");
					return "";
				}
			}
			if (command.indexOf("%") > -1) {
				System.err.println(
					"EXECUTE CMD " + command + ": too few parameters");
				return "";
			}
			//everything ok	
		}
		return command;

	}

	/**
	 * execute a command
	 * get back result:
	 * -1 if an unspecified error occured
	 * -101 if parameter count doesn't match (to few arguments)
	 * -102 if parameter count doesn't match (to many arguments)
	 * 
	 * @param type
	 * @param command
	 * @param parameter
	 * @return int result
	 */
	public int executeCommand(
		Element step,
		String type,
		String command,
		String parameter)
		throws XmlPropertiesFormatException {

		if (DEBUG)
			System.out.println(
				"EXECUTING CMD: t:"
					+ type
					+ " c:"
					+ command
					+ " p:"
					+ parameter);

		if (type.equals("sql")) {

			//command, parameter holen
			command = step.getChild("cmd").getAttributeValue("value");
			parameter = step.getChild("parameter").getAttributeValue("value");
			command = parseParametersIntoCommand(command, parameter);
			SqlTxm handler = new SqlTxm();
			return handler.execute(step, command);

		}
		else if (type.equals("storedproc")) {

			SqlTxm handler = new SqlTxm();
			return handler.executeStoredProc(step);

		}
		else if (type.equals("defined")) {

			return executeDefinedStep(step, command, parameter);

		}
		else {

			System.out.println("noch nicht implementiert");

		}

		return -1;

	}

	// int checkTransactionResult(executeTransaction(transaction))

	/**
	 * gets a string in xml style and searches for the Elements and
	 * Attributes. 
	 * 
	 * Soweit fertig, wenn die DTD so bleibt.
	 * 
	 * Methode liefert transaction als Element zurück mit Informationen über Ausführungsstatus
	 * <error value="ok"> transaktion erfolgreich
	 */
	public Element executeTransaction(Element transaction) {

		// soll das Element transaction gegen die DTD validieren
		// das Element muss wahrscheinlich in einer temporaeren Datei
		// gespeichert werden um validiert werden zu koennen.
		/*Document doc = new Document();
		DocType documentType = new DocType("transaction", "etc/transaction.dtd");
		doc.setDocType(documentType);
		Element transactions = new Element("transactions");
		doc.setRootElement(transactions);
		Element sq = (Element) doc.getRootElement();
		
		sq.addContent((Element) transaction.clone());
		
		XmlJdomViewer view = new XmlJdomViewer();
		// result gibt das document mit allen elementen als String wider
		String result = view.docToString(doc);
		
		XmlValidator validate = new XmlValidator();
		validate.elementCheckStr(result);*/

		String type = "";
		String command = "";
		String parameters = "";
		int stepResult = 0;

		if (DEBUG)
			System.out.println("EXECUTING TX: ");

		try {
			//kopiere transaction-block nach taRoot
			Element taRoot = transaction;
			//holen aller Unterelemente
			List tests = taRoot.getChildren();
			//Schleife, um alle gültigen Schritte zu bekommen
			//Schritte werden in steps gespeichert
			// prueft ob type gültig ist
			// weitere koennen hinzugefuegt werden
			// zur Zeit noch Positiv Liste 
			Vector steps = new Vector();
			for (int i = 0; i < tests.size(); i++) {
				Element el = (Element) tests.get(i);
				String name = el.getName();
				if (name.equals("sql")
					|| name.equals("scan")
					|| name.equals("storedproc")) {
					steps.addElement((Element) el.clone());
				}
			} //for

			// hier werden alle Kommando-Parameter gesucht und in Strings geschrieben
			// parameter sind: type, cmd, parameter
			Iterator iterator = steps.iterator();
			while (iterator.hasNext()) {

				//einzelnen Schritt holen
				Element step = (Element) iterator.next();
				//Type festlegen
				type = step.getName().toString();

				command = step.getChild("cmd").getAttributeValue("value");
				parameters =
					step.getChild("parameter").getAttributeValue("value");

				//jetzt ausführen

				stepResult = executeCommand(step, type, command, parameters);
				if (stepResult < 0) {
					if (DEBUG)
						System.out.println("Transaction failed");

					//do a rollback

					//transaction modifizieren: attempt/error aktualisieren
					transaction =
						addExecutionInfo(transaction, "" + stepResult);

					//in queue schreiben
					queue.replace(transaction);

					return transaction;
				} //if

			} //while step

			//transaktion bis zum Ende durchgelaufen: also erfolgreich beendet
			transaction = addExecutionInfo(transaction, "0");

		}
		catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}
		return transaction;
	}

	/**
	 * @param transaction
	 * @return
	 */
	private Element addExecutionInfo(Element transaction, String errorcode) {
		//neues Datum holen
		String date = new Date().toString();

		//attempt
		Element attempt = transaction.getChild("attempt");
		if (attempt == null) {
			//attempt hinzufügen, erster versuch
			transaction.addContent(
				new Element("attempt").setAttribute("value", "1").setAttribute(
					"date",
					date));
		}
		else {
			//attempt gibts schon
			transaction.removeChild("attempt");
			int attNr =
				Integer.parseInt(attempt.getAttributeValue("value")) + 1;

			transaction.addContent(
				new Element("attempt").setAttribute(
					"value",
					"" + attNr).setAttribute(
					"date",
					date));
		}

		//error
		Element error = transaction.getChild("error");
		if (error == null) {
			//error hinzufügen, erster versuch
			transaction.addContent(
				new Element("error").setAttribute("value", errorcode));
		}
		else {
			//error gibts schon
			transaction.removeChild("error");

			transaction.addContent(
				new Element("error").setAttribute("value", errorcode));
		}

		return transaction;
	}

	/**
	 * Transaktionen werden von der Transaktionen Queue Datei gelesen und
	 * als List widergegeben.
	 * Danach werden die Transaktionen, die weiterhin nicht ausgeführt werden
	 * können, da DB Verbindung nicht hergestellt werden kann usw., in eine
	 * Warteschlange geschrieben, ansonsten aus der Queue gelöscht. 
	 */
	public void executeQueue() {
		/*
		 * wann die executeQueue Methode ausgefuehrt werden soll muss noch
		 * festgelegt werden.
		 */
		if (DEBUG)
			System.out.println("-------------------");
		System.out.println("queue execution");

		//lesen von transaction.xml
		List transactions = queue.getTransactionsFromQueue();
		//Transaktionen ausführen
		Iterator iterator = transactions.iterator();
		while (iterator.hasNext()) {

			Element transaction = (Element) iterator.next();
			transaction = executeTransaction(transaction);

			if (checkTransactionResult(transaction) < 0) {
				//fehlgeschlagene Transaktionen in Warteschlange schreiben
				System.out.println("TX failed: saving to queue");
				queue.replace(transaction);
			}
			else {
				System.out.println("TX succeeded: removing from queue");
				queue.remove(
					Integer.parseInt(transaction.getAttributeValue("id")));
			}
		}

		//löschen der tempQueue
		System.out.println("--------");

	}

	/**
	 * Thread
	 */
	private void run() {

		while (true) {

			executeQueue();
			try {
				Thread.sleep(300 * 1000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * Methode zum testen
	 * 
	 * @param args
	 * @throws XmlPropertiesFormatException
	 */
	public static void main(String[] args)
		throws XmlPropertiesFormatException {

		Boot.printCopyright("TRANSACTION MANAGER");
		TransactionManager tm = new TransactionManager();
		tm.run();

		/*String test =
		    "<transaction><sender name=\"nccscanner\" date=\"timestamp...\" />"
		        + "<dsn value=\"\" />"
		        + "<sql>"
		        + "<cmd value=\"INSERT INTO asdstatus (id, description) VALUES (status_seq.nextval, %description%)\" />"
		        + "<parameter value=\"hallo\" />"
		        + "</sql>"
		        + "</transaction>";
		tm.executeTransactionAsString(test);
		*/

	}

}
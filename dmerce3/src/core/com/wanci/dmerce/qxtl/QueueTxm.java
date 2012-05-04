/*
 * Created on 24.07.2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.wanci.dmerce.qxtl;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlConfigReaderInterface;
import com.wanci.dmerce.kernel.XmlConfigWriterInterface;
import com.wanci.dmerce.kernel.XmlPropertiesReader;

/**
 * @author pg
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class QueueTxm
	implements XmlConfigReaderInterface, XmlConfigWriterInterface {

	/**
	 * Bekommt die höchste Transaktions id zugewiesen.
	 */
	private int maxId = 0;

	/**
	 *  
	 */
	private boolean canAccessActiveQueue = true;
	
	/**
	 * Gibt die Anzahl der Versuche an, nach der eine Transaktion verworfen wird
	 */
	private int discardAfterAttempts = 3;

	/**
	 * parser
	 */
	protected SAXBuilder parser;

	/**
	 * xml document 
	 */
	protected Document docArgs;

	/**
	 * xml document 
	 */
	protected Document document;

	/**
	 * zum debuggen auf true setzen
	 */
	private boolean DEBUG = false;

	/**
	 * xml Resource
	 */
	protected String xmlResource;

	/**
	 * xml Resource
	 */
	protected String xmlResourceDiscard = "";

	/**
	 * properties Handler
	 */
	private XmlPropertiesReader dmerceProperties;

	/**
	 * singleton design pattern variable
	 */
	private static QueueTxm object = null;

	/**
	 * singleton pattern 
	 * @return object
	 */
	public static synchronized QueueTxm getInstance()
		throws XmlPropertiesFormatException {

		if (object == null) {
			object = new QueueTxm();
		}

		return object;

	}

	/**
	 * Liest eine der folgenden Xml Dateien und erzeugt ein JDOM
	 * Objekt bei Erfolg.
	 * 
	 * @throws XmlPropertiesFormatException
	 */
	public QueueTxm() throws XmlPropertiesFormatException {
		
		dmerceProperties = XmlPropertiesReader.getInstance();
		
		if (dmerceProperties.propertyExists("transactionmanager.debug")) {
			DEBUG =
				dmerceProperties.getPropertyAsBoolean(
					"transactionmanager.debug");
		}

		//der dateiname für die aktive Warteschlange ist konfigurierbar, wenn fehlt, 
		// dann standardmäßig etc/activetransactionqueue.xml
		if (dmerceProperties
			.propertyExists("transactionmanager.activequeuefilename")) {
			setXmlResource(
				dmerceProperties.getProperty(
					"transactionmanager.activequeuefilename"));
		}
		else {
			setXmlResource(
				System.getProperty("user.dir")
					+ System.getProperty("file.separator")
					+ "etc"
					+ System.getProperty("file.separator")
					+ "qxtlqueue.xml");
		}

		//discard file
		if (dmerceProperties
			.propertyExists("transactionmanager.discardqueuefilename")) {
			xmlResourceDiscard =
				dmerceProperties.getProperty(
					"transactionmanager.discardqueuefilename");
		}

		if (dmerceProperties
			.propertyExists("transactionmanager.discardafterattempts")) {
			discardAfterAttempts =
				dmerceProperties.getPropertyAsInt(
					"transactionmanager.discardafterattempts");
		}

		read();
	}

	/**
	 * set Xml Resource File
	 */
	public void setXmlResource(String string) {
		
		xmlResource = string;
		if (DEBUG)
			System.out.println("QueueTxm: active queue is " + xmlResource);
	}

	/**
	 * saves the transactions that couldn't be executed because
	 * db connection failed or something else
	 *
	 * Die Transaktionswarteschlange transaction.xml wird komplett
	 * neu geschrieben
	 *
	 * Die Priorität der einzelnen transactions müsste noch geklärt
	 * werden, ausser diese wird als String oder im Element mitgegeben.
	 */
	public int saveQueue(Vector transactionList) {
		
		if (DEBUG)
			System.out.println("Saving Queue");
		
		//neues Dokument anlegen
		document = new Document();
		DocType docType = new DocType("transactions", "transaction.dtd");
		document.setDocType(docType);
		Element transactions = new Element("transactions");
		document.setRootElement(transactions);

		//root element setzen
		Element sq = document.getRootElement();

		//transaktionsliste durchgehen
		Iterator iterator = transactionList.iterator();
		
		while (iterator.hasNext()) {
			Element transaction = (Element) iterator.next();
			sq.addContent((Element) transaction.clone());
		}
		write();

		if (DEBUG)
			System.out.println("Transaction Queue erfolgreich geschrieben!");

		return 0;
	}

	/**
	 * vielleicht später als Thread methode verwenden
	 */
	public void run() {

	}

	/**
	 *
	 */
	public synchronized void p() {
		
		while (!canAccessActiveQueue) {
			
			try {
				Thread.sleep(500);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		canAccessActiveQueue = false;
	}

	/**
	 *
	 */
	public synchronized void v() {
		
		canAccessActiveQueue = true;
	}

	/**
	 * read method of XmlConfigReader class
	 */
	public boolean read() {

		if (DEBUG)
			System.out.println("QueueTxm: reading Queue");

		if (xmlResource == "" || xmlResource == null) {
			return false;
		}

		p();

		parser = new SAXBuilder();
		try {
			document = parser.build(xmlResource);
		}
		catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
			v();
			return false;
		}

		//get MaxId
		Iterator iterator =
			document.getRootElement().getChildren("transaction").iterator();
		
		while (iterator.hasNext()) {
			Element el = (Element) iterator.next();
			if (Integer.parseInt(el.getAttributeValue("id")) > maxId)
				maxId = Integer.parseInt(el.getAttributeValue("id"));
		}

		if (DEBUG)
			System.out.println("QueueTxm: maxId is " + maxId);
		
		if (DEBUG)
			System.out.println(
				"QueueTxm: "
					+ document.getRootElement().getChildren("transaction").size()
					+ " tx in queue");
		v();

		return true;
	}

	/**
	 * write method of XmlConfigReader class
	 */
	public boolean write() {
		
		if (DEBUG)
			System.out.println("QueueTxm: writing queue");
		
		if (xmlResource == "" || xmlResource == null)
			return false;

		p();

		// serialize it into a file
		FileOutputStream out = null;

		try {
			// write to file transaction.xml
			out = new FileOutputStream(xmlResource);
		}
		catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}

		//set up xml outputter object
		XMLOutputter serializer = new XMLOutputter();
		
		//set formatting options: get rid of all nasty whitespace,
		//indent with 2 spaces and insert newline-chars
		serializer.setTrimAllWhite(true);
		serializer.setIndent("  ");
		serializer.setNewlines(true);
		
		//output
		try {
			serializer.output(document, out);
			out.flush();
			out.close();
		}
		catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}

		v();

		return true;
	}
	
	/**
	 * Prüfung, ob die Transaktion eine Id hat. Falls die Transaktion keine
	 * Id hat, wird die höchste Id genommen und um 1 erhöht.
	 * Falls die Transaktion eine Id hat, wird die Transaktion gelöscht.
	 * 
	 * Wenn die Transaktions Id kleiner der discardAfterAttempts Zahl ist,
	 * dann wird die Transaktion zum document hinzugefügt. Anderenfalls wird
	 * die Transaktion entfernt.
	 * 
	 * Das document wird neu geschrieben
	 * 
	 * @param transaction Element mit der Transaktion
	 */
	public void replace(Element transaction) {

		//check if transaction has an id
		if (transaction.getAttribute("id") == null) {

			transaction.setAttribute("id", "" + getNextId());
			
			if (DEBUG)
				System.out.println(
					"new transaction id is "
						+ transaction.getAttributeValue("id"));
		}
		else {
			remove(Integer.parseInt(transaction.getAttributeValue("id")));
		}

		Element root = (Element) document.getRootElement().clone();

		//hier muss nun erst noch geprüft werden ob der attempt der x-te ist...
		if (checkAttempts(transaction) <= discardAfterAttempts) {
			
			//nur dann adden
			if (DEBUG)
				System.out.println(
					"adding tx " + transaction.getAttributeValue("id"));
			root.addContent((Element) transaction.clone());

		}
		else {
			if (DEBUG)
				System.out.println("discarding transaction");
			discardTransaction(transaction);
		}

		document = new Document(root);

		write();
	}

	/**
	 * Entfernt eine Transaktion nachdem sie vorher schon geprüft
	 * worden ist.
	 * 
	 * @param transaction
	 */
	private void discardTransaction(Element transaction) {
		
		//read discardqueue
		if (xmlResourceDiscard.equals(""))
			return;

		SAXBuilder parser = new SAXBuilder();
		
		try {
			Document document = parser.build(xmlResource);
			Element root = document.getRootElement();
			root.addContent((Element) transaction.clone());
		}
		catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}
		
		FileOutputStream out = null;

		try {
			// write to file transaction.xml
			out = new FileOutputStream(xmlResource);
		}
		catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}

		//set up xml outputter object
		XMLOutputter serializer = new XMLOutputter();
		
		//set formatting options: get rid of all nasty whitespace,
		//indent with 2 spaces and insert newline-chars
		serializer.setTrimAllWhite(true);
		serializer.setIndent("  ");
		serializer.setNewlines(true);
		
		//output
		try {
			serializer.output(document, out);
			out.flush();
			out.close();
		}
		catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}

	}

	/**
	 * Prüft anhand der Attribute in der Transaktion wie viele Versuche
	 * eingetragen sind und gibt einen Int Wert zurück.
	 * 
	 * @param transaction
	 * @return
	 */
	private int checkAttempts(Element transaction) {
		
		if (transaction.getChild("attempt") == null)
			return 0;

		return Integer.parseInt(
			transaction.getChild("attempt").getAttributeValue("value"));
	}

	/**
	 * Entfernt eine Transaktion aus dem document anhand seiner Id.
	 * 
	 * @param i
	 */
	public void remove(int i) {
		
		Element el = null;
		Element root = document.getRootElement();
		
		Iterator iterator = root.getChildren().iterator();
		
		while (iterator.hasNext()) {
			
			el = (Element) iterator.next();
			
			if (Integer.parseInt(el.getAttributeValue("id")) == i) {
				break;
			}
		}
		root.removeContent(el);
	}

	/**
	 * Erhöht die höchste Id um 1.
	 * 
	 * @return
	 */
	private int getNextId() {
		
		maxId++;
		
		return maxId;
	}

	/**
	 * Entfernt die angegebene Transaktion anhand des Elements.
	 * 
	 * @param transaction
	 */
	public void del(Element transaction) {
		
		Element root = document.getRootElement();
		root.removeContent(transaction);
		
		write();
	}

	/**
	 * Liest alle Elemente der Queue ein und erzeugt ein JDOM Objekt.
	 * Es werden alle "transaction" Kindelemente des Objektes
	 * zurückgegeben. 
	 * 
	 * @return
	 */
	public List getTransactionsFromQueue() {
		
		//zuerst alle Elemente in der Queue lesen
		read();

		//liste mit Transaktionen
		return ((Element) document.getRootElement().clone()).getChildren(
			"transaction");
	}

	/**
	 * Methode zum testen.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
	}

}

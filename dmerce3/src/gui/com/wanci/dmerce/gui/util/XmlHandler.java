package com.wanci.dmerce.gui.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.wanci.dmerce.gui.ApplHelper;

/**
 * Extrahiert XML-Log Einträge von einem InputStream (Datei oder Socket).
 * Als Parser wird der DOM-Parser verwendet.
 * <p>
 * Ausserdem befindet sich hier auch die Logik für die Aufbereitung 
 * der ExportToSql-Funktion der XML-Einträge (als File).
 * <p>
 * @author	Ron Kastner
 */
public class XmlHandler extends DefaultHandler implements MonitorConstants {

	/**
	 * Erzeuge ein End-Tag für das übergebene Tag.<br>
	 * ServiceMethode für das Aufbauen eines XML-Dokumentes
	 * @param	tag	java.lang.String
	 * @return	java.lang.String
	 */
	public static String toEndTag(String tag) {
		StringBuffer b = new StringBuffer();
		return b.append("</").append(tag).append(">").toString();
	}

	/**
	 * Erzeuge ein Anfangs-Tag für das übergebene Tag.<br>
	 * ServiceMethode für das Aufbauen eines XML-Dokumentes
	 * @param	tag	java.lang.String
	 * @return	java.lang.String
	 */
	public static String toStartTag(String tag) {
		StringBuffer b = new StringBuffer();
		return b.append("<").append(tag).append(">").toString();
	}

	private static String[] xmlheader =
		new String[] {
			"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>",
			"<!DOCTYPE LOG [",
			"<!-- DTD for LogMonitor -->",
			"<!ELEMENT LOG      (ENTRY+)>",
			"<!ELEMENT ENTRY    (DATE, CLIENT, PROXY, TEMPLATE, MODULE, MESSAGE)>",
			"<!ELEMENT DATE     (#PCDATA)>",
			"<!ELEMENT CLIENT   (#PCDATA)>",
			"<!ELEMENT PROXY    (#PCDATA)>",
			"<!ELEMENT TEMPLATE (#PCDATA)>",
			"<!ELEMENT MODULE   (#PCDATA)>",
			"<!ELEMENT MESSAGE  (#PCDATA)>",
			"<!ELEMENT MSGTYPE  (#PCDATA)>",
			"]>",
			" " };

	/** Liste aller Log-Einträge */
	protected static ArrayList list;

	/**
	 * Auslesen des XMLs aus einer Datei
	 * @param	src	InputSource
	 * @param	val	boolean
	 */
	public ArrayList parseFile(String filename, boolean val)
		throws SAXException, IOException {

		Document doc = null;

		try {
			DocumentBuilder b =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = b.parse(new File(filename));
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		if (val)
			System.out.println(filename);

		return parseDocument(doc);
	}

	protected ArrayList parseDocument(Document doc) {

		ArrayList entryList = new ArrayList();
		NodeList list = doc.getElementsByTagName(MonitorConstants.TAG_ENTRY);

		// alle Einträge ENTRIES abarbeiten
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			NodeList l = node.getChildNodes();
			HashMap m = new HashMap();
			for (int j = 0; j < l.getLength(); j++) {
				Node n = l.item(j);

				if (n != null && n.getNodeType() == Node.ELEMENT_NODE) {
					String name = n.getNodeName();
					String value = n.getFirstChild().getNodeValue();
					m.put(name, value);
					FilterDictionary.put(name, value);
				}
			}

			entryList.add(m);

		}

		return entryList;
	}

	/**
	 * Auslesen des XMLs über einen Socket
	 * @param	src	InputSource
	 * @param	val	boolean
	 */
	public ArrayList parseSocket(InputSource src, boolean val)
		throws SAXException, IOException {

		Document doc = null;

		try {
			DocumentBuilder b =
				DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = b.parse(src);
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		if (val)
			System.out.println("socket parsed");

		return parseDocument(doc);

	}

	/**
	 * Convert from a filename to a file URL.
	 */
	public static String convertToFileURL(String filename) {
		// On JDK 1.2 and later, simplify this to:
		String path = "";
		try {
			path = new File(filename).toURL().toString();
		}
		catch (Exception e) {
			if (ApplHelper.DEBUG)
				e.printStackTrace();
		}
		return path; //"file:"+path;
	}

	public static ArrayList getXMLDocument(JTable table) {

		ArrayList l = new ArrayList();

		for (int i = 0; i < xmlheader.length; i++) {
			l.add(xmlheader[i]);
		}

		l.add(toStartTag("LOG"));

		for (int i = 0; i < table.getRowCount(); i++) {

			l.add(toStartTag(TAG_ENTRY));

			l.add(
				"\t"
					+ toStartTag(TAG_DATE)
					+ table.getValueAt(i, COL_DATE)
					+ toEndTag(TAG_DATE));
			l.add(
				"\t"
					+ toStartTag(TAG_CLIENT)
					+ table.getValueAt(i, COL_CLIENT)
					+ toEndTag(TAG_CLIENT));
			l.add(
				"\t"
					+ toStartTag(TAG_PROXY)
					+ table.getValueAt(i, COL_PROXY)
					+ toEndTag(TAG_PROXY));
			l.add(
				"\t"
					+ toStartTag(TAG_TEMPLATE)
					+ table.getValueAt(i, COL_TEMPLATE)
					+ toEndTag(TAG_TEMPLATE));
			l.add(
				"\t"
					+ toStartTag(TAG_MODULE)
					+ table.getValueAt(i, COL_MODULE)
					+ toEndTag(TAG_MODULE));
			l.add(
				"\t"
					+ toStartTag(TAG_MESSAGE)
					+ table.getValueAt(i, COL_MESSAGE)
					+ toEndTag(TAG_MESSAGE));
			l.add(
				"\t"
					+ toStartTag(TAG_MSGTYPE)
					+ table.getValueAt(i, COL_MSGTYPE)
					+ toEndTag(TAG_MSGTYPE));

			l.add(toEndTag(TAG_ENTRY));
		}

		l.add(toEndTag("LOG"));

		return l;
	}

	/**
	 * Default Error handler to report errors and warnings
	 */
	class XMLDefaultErrorHandler implements ErrorHandler {

		/** Error handler output goes here */
		private PrintStream out;

		XMLDefaultErrorHandler(PrintStream out) {
			this.out = out;
		}

		/**
		 * Returns a string describing parse exception details
		 */
		private String getParseExceptionInfo(SAXParseException spe) {
			String systemId = spe.getSystemId();
			if (systemId == null) {
				systemId = "null";
			}
			String info =
				"URI="
					+ systemId
					+ " Line="
					+ spe.getLineNumber()
					+ ": "
					+ spe.getMessage();
			return info;
		}

		// The following methods are standard SAX ErrorHandler methods.
		// See SAX documentation for more info.

		public void warning(SAXParseException spe) throws SAXException {
			out.println("Warning: " + getParseExceptionInfo(spe));
		}

		public void error(SAXParseException spe) throws SAXException {
			String message = "Error: " + getParseExceptionInfo(spe);
			throw new SAXException(message);
		}

		public void fatalError(SAXParseException spe) throws SAXException {
			String message = "Fatal Error: " + getParseExceptionInfo(spe);
			throw new SAXException(message);
		}
	}
}

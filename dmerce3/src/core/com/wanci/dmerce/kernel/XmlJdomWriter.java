/*
 * Created on Jun 11, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.kernel;

import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;

import com.wanci.java.LangUtil;

/**
 * @author mm
 * @version $Id: XmlJdomWriter.java,v 1.3 2003/08/28 08:54:34 mm Exp $
 * 
 */
public class XmlJdomWriter {

	/**
	 * Konstruktor
	 */
	public XmlJdomWriter() {
	}

	/**
	 * Zeigt die Benutzung der Klasse an, falls keine oder falsche Parameter
	 * angegeben werden
	 */
	public static void usage() {
		System.err.println("usage: XmlJDOMWriter MyXmlFile.xml");
		System.exit(2);
	}

	/**
	 * Erstellt eine neue Xml Datei.
	 * 
	 * @param fileName Dateiname
	 * @throws IOException
	 */
	public void writer(String fileName) throws IOException {

		//Wurzelelement
		Element root = new Element("testfile");

		Element el = new Element("Serverinfo");

		el.addContent(
			(new Element("Server").setAttribute("name", "bigspacer")));
		
		el.addContent(
			(new Element("interface")).setAttribute(
				"name",
				"eri0").setAttribute(
				"ip-address",
				"10.48.30.200"));
		
		el.addContent(
			(new Element("inet-service")).setAttribute(
				"type",
				"http").setAttribute(
				"ip",
				""));
		
		el.addContent(
			(new Element("tcp")).setAttribute("name", "ports").setAttribute(
				"value",
				"80"));
		
		el.addContent(
			(new Element("tcp")).setAttribute("name", "send").setAttribute(
				"value",
				"GET /\r\n\r\n"));

		root.addContent(el);

		// legt eine neue Datei an...

		DocType type = new DocType("testfile");

		Document doc = new Document(root, type);

		// serialize it into a file
		FileOutputStream out = new FileOutputStream(fileName);
		XMLOutputter serializer = new XMLOutputter();
		serializer.output(doc, out);
		out.flush();
		out.close();
	}

	public static void main(String[] args) {

		Boot.printCopyright("XML JDOM WRITER");

		/**
		 * @args Dateiname fuer neu zu schreibende xml-Datei
		 */
		try {
			XmlJdomWriter xmlJdomWriter = new XmlJdomWriter();
			xmlJdomWriter.writer(args[0]);
			XmlJdomViewer.viewer(args[0]);
		}
		catch (IOException e) {
			e.printStackTrace();
			LangUtil.consoleDebug(
				true,
				"Document cannot be accesed: " + e.getMessage());
		}
		catch (JDOMException e) {
			LangUtil.consoleDebug(
				true,
				"Document is not a valid xml document: " + e.getMessage());
		}
	}
}

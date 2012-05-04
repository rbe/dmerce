/*
 * Created on 20.06.2003
 */
package com.wanci.dmerce.kernel;

import java.util.Iterator;
import java.util.Vector;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;


/**
 * @author mm
 * @author pg
 * @version $Id: PortScannerXmlConfigWriter.java,v 1.2 2003/08/27 15:59:13 mm Exp $
 *
 * writer for port scanner xml config 
 * 
 * @see com.wanci.dmerce.dmf.PortScannerXmlConfigWriter
 */

public class PortScannerXmlConfigWriter extends XmlConfigWriter {

	/**
	 * portscanner element for this class
	 */
	Element portScannerRoot = new Element("portscanner");

	/**
	 * Constructor
	 * set the xml resource in a default folder
	 */
	public PortScannerXmlConfigWriter() {
		
		setXmlResource(
			System.getProperty("user.dir")
				+ System.getProperty("file.separator")
				+ "etc"
				+ System.getProperty("file.separator")
				+ "ncc.xml");

		PortScannerXmlConfigReader reader = new PortScannerXmlConfigReader();
		reader.read();
		
		document = reader.getDocument();
		root = document.getRootElement();

	}

	/**
	 * setter for networks: build the elementlist
	 * 
	 * @param networks (Vector of NetworkScanner
	 * @return root  (Root element with child elements for qualified xml resource
	 */
	public void setNetworksToScan(Vector networks) {

		Iterator iterator = networks.iterator();

		//build network list
		while (iterator.hasNext()) {

			Vector network = (Vector) iterator.next();

			Element nw = new Element("network");
			nw.setAttribute("name", "");
			nw.setAttribute("range", "" + network.elementAt(0));

			for (int i = 1; i < network.size(); i++) {

				nw.addContent(
					(new Element("port")
						.setAttribute("number", "" + network.elementAt(i))
						.setAttribute("protocol", "tcp")));
			}

			portScannerRoot.addContent(nw);
		}
	}

	/**
	 * setter for hosts: build the elementlist
	 * @param hosts
	 */
	public void setHostsToScan(Vector hosts) {

		Iterator iterator = hosts.iterator();

		//build host list
		while (iterator.hasNext()) {

			Vector host = (Vector) iterator.next();

			Element hs = new Element("host");
			hs.setAttribute("name", "");
			hs.setAttribute("ipaddress", "" + host.elementAt(0));

			for (int i = 1; i < host.size(); i++) {

				hs.addContent(
					new Element("port").setAttribute(
						"number",
						"" + host.elementAt(i)).setAttribute(
						"protocol",
						"tcp"));
			}
			portScannerRoot.addContent(hs);
		}
	}

	/**
	 * method for string (xml) output
	 * @return
	 */
	public String toXml() {

		String xml = "";

		root = document.getRootElement();
		root.removeChild("portscanner");
		root.addContent(portScannerRoot);

		// serialize it into a file
		XMLOutputter serializer = new XMLOutputter();
		serializer.setTrimAllWhite(true);
		serializer.setIndent("  ");
		serializer.setNewlines(true);
		serializer.outputString(document);

		xml = serializer.outputString(document);

		System.out.println(xml);
		return xml;
	}

	/**
	 * write xml resource
	 * @see com.wanci.dmerce.dmf.XmlConfigWriter#write()
	 */
	public boolean write() {

		root.removeChild("portscanner");
		root.addContent(portScannerRoot);
		return super.write();

	}

	/**
	 * testing function
	 * @see com.wanci.dmerce.dmf.XmlConfigWriter#main(java.lang.String[])
	 */
	public static void main(String[] args) {

		Boot.printCopyright("XML CONFIG PORT SCANNER WRITER");

		PortScannerXmlConfigReader xml = new PortScannerXmlConfigReader();
		xml.read();
		Vector v = xml.getNetworksToScan();
		Vector v2 = xml.getHostsToScan();
		PortScannerXmlConfigWriter xmlWrite = new PortScannerXmlConfigWriter();
		xmlWrite.setNetworksToScan(v);
		xmlWrite.setHostsToScan(v2);
		//xmlWrite.toXml();
		xmlWrite.write();
	}
}

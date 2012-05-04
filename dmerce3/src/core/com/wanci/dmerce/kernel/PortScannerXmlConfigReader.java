/*
 * Created on Jun 18, 2003
 */
package com.wanci.dmerce.kernel;

//import java.beans.XMLEncoder;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;
import org.jdom.JDOMException;


/**
 * @author mm
 * @author pg
 * @version $Id: PortScannerXmlConfigReader.java,v 1.3 2003/10/01 09:51:47 mf Exp $
 * 
 * reader for scanner resource
 * 
 * in the file or db there are different kinds of scan objects, networks and hosts
 * this class provides a translation from xml to Vectors
 * 
 * the Vector for Hosts:
 * host1 (Hashmap) (inetaddress host)
 * host2 ...
 * ...
 * 
 * the Vector for Networks:
 * network1 (Hashmap) (string range)
 * network2 ...
 * ...
 * 
 * contains the business logic to ask for networks to scan, for hosts and ports
 *  
 */
public class PortScannerXmlConfigReader extends XmlConfigReader {

	/**
	 * root element of ncc portscanner
	 *
	 */
	public Element rootElement;

	/**
	 * Setzen des Dateipfades, wo die XML Datei zu finden ist.
	 */
	public PortScannerXmlConfigReader() {

		String xmlFileLocation =
			System.getProperty("user.dir")
				+ System.getProperty("file.separator")
				+ "etc"
				+ System.getProperty("file.separator")
				+ "ncc.xml";

		setXmlResource(xmlFileLocation);
	}

	/**
	 * Liest ein XML Dokument ein und macht daraus ein JDOM Objekt.
	 * Erstellt eine Element Liste als Vector einer bestimmten Elementgruppe
	 */
	public boolean read() {
		
		Vector elList = new Vector();
		
		if (super.read()) {
			elList = getElementList(document.getRootElement(), "portscanner");
		
			if (elList.size() == 1) {
				rootElement = (Element) elList.get(0);
		
			} else {
				return false;
			}

		} else
			return false;

		return true;
	}

	/**
	 * 
	 * @param string
	 */
	public PortScannerXmlConfigReader(String string) {
		setXmlResource(string);
	}

	/**
	 * Liefert eine Liste mit jeweils 2 Objekten als Eintrag: InetAddress und int-Array
	 * String = network, das gescannt werden soll
	 * int[] = Liste mit Ports
	 *
	 */
	public Vector getNetworksToScan() {

		Vector vNetworkList = new Vector();
		Vector networkList = getElementList(rootElement, "network");

		Iterator iterator = networkList.iterator();
		while (iterator.hasNext()) {
			Element network = (Element) iterator.next();
			Vector vNetwork = new Vector();
			vNetwork.addElement(
				new String(getAttributeValue(network, "range")));

			List content = network.getContent();
			Iterator contentiterator = content.iterator();

			while (contentiterator.hasNext()) {
				Object o = contentiterator.next();

				if (o instanceof Element) {
					Element port = (Element) o;
					vNetwork.addElement(
						new Integer(
							Integer.parseInt(
								getAttributeValue(port, "number"))));
				}
			} //while ports

			vNetworkList.addElement(new Vector(vNetwork));
		} //while networklist

		return vNetworkList;

	}

	/**
	 * Liefert eine Liste mit jeweils 2 Objekten als Eintrag: InetAddress und int-Array
	 * InetAddress = Host, der gescannt werden soll
	 * int[] = Liste mit Ports
	 *
	 */
	public Vector getHostsToScan() {
		Vector vHostList = new Vector();

		Vector hostList = getElementList(rootElement, "host");

		Iterator iterator = hostList.iterator();
		while (iterator.hasNext()) {
			Element host = (Element) iterator.next();
			Vector vHost = new Vector();
			vHost.addElement(new String(getAttributeValue(host, "ipaddress")));

			List content = host.getContent();
			Iterator contentiterator = content.iterator();

			while (contentiterator.hasNext()) {
				Object o = contentiterator.next();

				if (o instanceof Element) {
					Element port = (Element) o;
					vHost.addElement(
						new Integer(
							Integer.parseInt(
								getAttributeValue(port, "number"))));
				}
			} //while ports

			vHostList.addElement(new Vector(vHost));
		} //while hostlist
		
		return vHostList;
	}

	/**
	 * method for testing the scanner reader  
	 * @param args
	 */
	public static void main(String[] args) throws IOException, JDOMException {
		
		Boot.printCopyright("XML CONFIG PORT SCANNER READER");

		PortScannerXmlConfigReader xml = new PortScannerXmlConfigReader();

		//xml.process(xml.getRootElement());

		//Vector hosts = xml.getHostsToScan();
		//Vector networks = xml.getNetworksToScan();
		//System.out.println(networks.toString());
		xml.read();
		xml.process(xml.getRootElement());
		xml.getNetworksToScan();
		System.out.println(xml.getNetworksToScan());	
	}
}
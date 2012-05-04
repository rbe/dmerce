package com.wanci.dmerce.test;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/*
 * Created on Apr 9, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/**
 * @author mm
 *
 * Anhand einer Konfigurationsdatei:
 *
 * <server name="gandalf">
 *    <ip addr="10.48.35.3"/>
 * 	  <ip gateway="10.48.35.1"/>
 * </server>
 * 
 * Soll ein Objekt instantiiert und konfiguriert werden:
 * 
 * Server s = new Server();
 * s.setIp("10.48.35.3");
 * s.setGateway("10.48.35.1");
 * 
 */

public class XmlParser {

	static Document document = null;
	
	NamedNodeMap attrs;

	XmlParserIps s;

	public static void main(String argv[]) {
		XmlParser Parser = new XmlParser();
		if ((argv.length > 1) && (argv.length < 2)) {
			System.err.println("Usage: java XmlParserTest filename [p]");
			System.exit(1);
		}
		Parser.parse(argv[0]);
	}

	/**
	 * 
	 * @param node	beinhaltet das uebergebene xml-Dokument
	 */
	
	public void setObjectParameter(Node node) {

		int type = node.getNodeType();
		switch (type) {

			// print the document element 
			case Node.DOCUMENT_NODE :
				{
					setObjectParameter(((Document) node).getDocumentElement());
					break;
				}

				// print element with attributes 
			case Node.ELEMENT_NODE :
				{
					// setting objects from xml document
					attrs = node.getAttributes();
					
					//System.out.println("===================");
					
					s = new XmlParserIps();
					
					for (int i = 0; i < attrs.getLength(); i++) {
						Node attr = attrs.item(i);

						/* 
						 * Mit Hilfe von getNodeName() und getLocalName() werden die Attribute
						 * ueberprueft und mit den richtigen Werten gesetzt.
						 * So wird verhindert, dass mehrere Knoten, die den gleichen Attribut-
						 * namen besitzen, einen falschen Wert zugewiesen bekommen.
						 */

						// server
						if (node.getLocalName().equals("server")) {
							ServerName(attr);
						}

						// interface
						if (node.getLocalName().equals("interface")) {
							ServerInterface(attr);
						}

						// inet-service
						if (node.getLocalName().equals("inet-service")) {
							ServerInetService(attr);
						}

						// tcp
						if (node.getLocalName().equals("tcp")) {
							ServerTcp(attr);
						}

						// service-group
						if (node.getLocalName().equals("service-group")) {
							ServiceGroup(attr);
						}

						// service-group-member
						if (node
							.getLocalName()
							.equals("service-group-member")) {
							ServiceGroupMember(attr);
						}

						// Service-group
						if (node.getLocalName().equals("Service-group")) {
							MonitorServiceGroup(attr);
						}

						// Server
						if (node.getLocalName().equals("Server")) {
							MonitorServer(attr);
						}

						// Redirector
						if (node.getLocalName().equals("redirector")) {
							Redirector(attr);
						}

						// ip-from
						if (node.getLocalName().equals("ip-from")) {
							RedirectorIpFrom(attr);
						}

						// ip-to
						if (node.getLocalName().equals("ip-to")) {
							RedirectorIpTo(attr);
						}
					}

					/*
					 * print objects;
					 * was ausgedruckt werden soll, kann manuell definiert werden;
					 * hier jetzt nur zum testen! 
					 */
					if ((s.getServerTcpName() != null)
						|| (s.getRedirectorIpFromAddr() != null)
						|| (s.getServerTcpValue() != null)
						|| (s.getRedirectorIpFromPort() != null)) {
						//System.out.println(s.getServerName());
						//System.out.println(s.getServerInterfaceName());
						//System.out.println(s.getServerInterfaceIpAddr());
						System.out.println(s.getServerTcpName());
						System.out.println(s.getServerTcpValue());
						System.out.println(s.getRedirectorIpFromAddr());
						System.out.println(s.getRedirectorIpFromPort());
						//System.out.println(node.getLocalName());
					}
				}
				// recursive delegation of all child nodes
				NodeList children = node.getChildNodes();
				if (children != null) {
					int len = children.getLength();
					for (int i = 0; i < len; i++)
						setObjectParameter(children.item(i));
				}
				break;
		}
	}

	/**
	 * 
	 * @param node	hier wird die xml-Datei uebergeben.
	 */

	public void printDomTree(Node node) {

		int type = node.getNodeType();
		switch (type) {
			// print the document element 
			case Node.DOCUMENT_NODE :
				{
					System.out.print("");
					printDomTree(((Document) node).getDocumentElement());
					break;
				}

				// print element with attributes 
			case Node.ELEMENT_NODE :
				{
					System.out.print("<");
					System.out.print(node.getNodeName());
					NamedNodeMap attrs = node.getAttributes();
					for (int i = 0; i < attrs.getLength(); i++) {
						Node attr = attrs.item(i);
						System.out.print(
							" "
								+ attr.getNodeName()
								+ "=\""
								+ attr.getNodeValue()
								+ "\"");
					}
					// close tag of attributes
					boolean test = node.hasChildNodes();
					if (test != true)
						System.out.print("/>");
					else
						System.out.print(">");
					// recursive delegation of all child nodes
					NodeList children = node.getChildNodes();
					if (children != null) {
						int len = children.getLength();
						for (int i = 0; i < len; i++)
							printDomTree(children.item(i));
					}
					// close Element block
					if ((!(node.getNodeName().equals("ROOT")))
						&& (test == true))
						System.out.print("</" + node.getNodeName() + ">");
					break;
				}

				// print text 
			case Node.TEXT_NODE :
				{
					System.out.print(node.getNodeValue());
					break;
				}
		}
	}

	/**
	 * 
	 * @param filename	ist die xml-Datei die uebergeben wird.
	 */

	private void parse(String filename) {

		// instantiate a new DocumentBuilderFactory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(new File(filename));
			if (document != null) {
				/*
				 * hier werden jetzt die Objekte gesetzt;
				 * wenn man die Baumstruktur anzeigen moechte,
				 * sollte man anstatt setObjectParameter printDomTree
				 * aufrufen!
				 */
				setObjectParameter(document);
				//printDomTree(document);
			}

		}
		catch (SAXParseException spe) {
			// Error generated by the parser					
			System.out.println(
				"\n** Parsing error"
					+ ", line "
					+ spe.getLineNumber()
					+ ", uri "
					+ spe.getSystemId());
			System.out.println(" " + spe.getMessage());

			// Use the contained exception, if any
			Exception x = spe;
			if (spe.getException() != null)
				x = spe.getException();
			x.printStackTrace();

		}
		catch (SAXException sxe) {
			// Error generated during parsing
			Exception x = sxe;
			if (sxe.getException() != null)
				x = sxe.getException();
			x.printStackTrace();

		}
		catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			pce.printStackTrace();

		}
		catch (IOException ioe) {
			// I/O error
			ioe.printStackTrace();

		}
	}
	
	/**
	 * 
	 * @param attr	wird von setObjectParameter uebergeben
	 * 
	 */

	public void ServerName(Node attr) {

		// setting ServerName
		if (attr.getNodeName().equals("name"))
			s.setServerName(attr.getNodeValue());

	}

	public void ServerInterface(Node attr) {

		// setting ServerInterfaceName
		if (attr.getNodeName().equals("name"))
			s.setServerInterfaceName(attr.getNodeValue());

		// setting ServerInterfaceIpAddr
		if (attr.getNodeName().equals("ip-address"))
			s.setServerInterfaceIpAddr(attr.getNodeValue());

	}

	public void ServerInetService(Node attr) {

		// setting ServerInetServiceType
		if (attr.getNodeName().equals("type"))
			s.setServerInetServiceType(attr.getNodeValue());

		// setting ServerInetServiceIp
		if (attr.getNodeName().equals("ip"))
			s.setServerInetServiceIp(attr.getNodeValue());

	}
	
	public void ServerTcp(Node attr) {

		// setting ServerTcpName
		if (attr.getNodeName().equals("name"))
			s.setServerTcpName(attr.getNodeValue());

		// setting ServerTcpValue
		if (attr.getNodeName().equals("value"))
			s.setServerTcpValue(attr.getNodeValue());

	}
	
	public void ServiceGroup(Node attr) {

		// setting ServiceGroupName
		if (attr.getNodeName().equals("name"))
			s.setServiceGroupName(attr.getNodeValue());

	}

	public void ServiceGroupMember(Node attr) {

		// setting ServiceGroupMemberName
		if (attr.getNodeName().equals("name"))
			s.setServiceGroupMemberName(attr.getNodeValue());

	}

	public void MonitorServiceGroup(Node attr) {

		// setting MonitorServiceGroupName
		if (attr.getNodeName().equals("name"))
			s.setMonitorServiceGroupName(attr.getNodeValue());

	}

	public void MonitorServer(Node attr) {

		// setting MonitorServerName
		if (attr.getNodeName().equals("name"))
			s.setMonitorServerName(attr.getNodeValue());

	}

	public void Redirector(Node attr) {

		// setting RedirectorName
		if (attr.getNodeName().equals("name"))
			s.setRedirectorName(attr.getNodeName());

	}

	public void RedirectorIpFrom(Node attr) {

		// setting RedirectorIpFromAddr
		if (attr.getNodeName().equals("address"))
			s.setRedirectorIpFromAddr(attr.getNodeValue());

		// setting RedirectorIpFromPort
		if (attr.getNodeName().equals("port"))
			s.setRedirectorIpFromPort(attr.getNodeValue());

	}

	public void RedirectorIpTo(Node attr) {

		// setting RedirectorIpToAddr
		if (attr.getNodeName().equals("address"))
			s.setRedirectorIpToAddr(attr.getNodeValue());

		// setting RedirectorIpToPort
		if (attr.getNodeName().equals("port"))
			s.setRedirectorIpToPort(attr.getNodeValue());

	}

}
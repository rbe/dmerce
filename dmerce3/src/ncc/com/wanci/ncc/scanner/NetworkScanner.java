/*
 * Created on Jun 3, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.ncc.scanner;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.ncc.ipmgt.Range;

/**
 * @author rb
 *
 * Scannt alle IP-Adressen in einem angegebenen Netzwerk
 * - Sollte gethreaded werden, damit auch grosse Netzwerke
 *   schnell erfasst werden (vielleicht ein Thread pro 10 IPs)
 * 
 */
public class NetworkScanner
	extends Observable
	implements NetworkScannerInterface, Observer {

	/**
	 * dmerce properties
	 */
	private XmlPropertiesReader dmerceProperties;

	private boolean DEBUG = true;

	//Vector with hosts not running (Vector of HostScannerObjects)
	private Vector hostsNotRunning = new Vector();

	//Vector with hosts running (Vector of HostScannerObjects)
	private Vector hostsRunning = new Vector();

	/**
	 * check priority of scans is determined by thread-priority
	 */
	private int SCANPRIORITY = Thread.NORM_PRIORITY;

	/**
	 * how many threads should be run by this process
	 */
	private int MAXTHREADS = 2;

	/**
	 * look how many threads are running by this process
	 */
	private int numberOfThreadsRunning = 0;

	/**
	 * Vector for ports to scan
	 */
	private Vector ports = new Vector();

	/**
	 * variable which holds the subnet in string representation for iprange-object
	 */
	private String network;

	/**
	 * object which holds several information about the range to scan
	 * it provides an iterator which gives back a string with an host's IP Address
	 */
	private Range ipRange;

	/**
	 * number of results, we are waiting for. 
	 * status variable for the thread monitoring
	 */
	int resultsPending = 0;

	/**
	 * get the number of hosts the scanner should scan 
	 * @return
	 */
	protected int getNumberOfHosts() {
		return ipRange.getNumberOfHosts();
	}

	/**
	 * constructor reads default values, if present
	 *
	 */
	public NetworkScanner() throws XmlPropertiesFormatException {
		int i;
		//initialize all variables
		dmerceProperties = XmlPropertiesReader.getInstance();
		i = dmerceProperties.getPropertyAsInt("networkscanner.maxthreads");
		if (i > 0)
			MAXTHREADS = i;
		else {
			i = dmerceProperties.getPropertyAsInt("scanner.maxthreads");
			if (i > 0)
				MAXTHREADS = i;
		}

		i = dmerceProperties.getPropertyAsInt("networkscanner.priority");
		if (i > 0)
			SCANPRIORITY = i;
		else {
			i = dmerceProperties.getPropertyAsInt("scanner.priority");
			if (i > 0)
				SCANPRIORITY = i;
		}

		if (dmerceProperties.propertyExists("networkscanner.debug")) {
			DEBUG =
				dmerceProperties.getPropertyAsBoolean("networkscanner.debug");
		}
		else {
			DEBUG = dmerceProperties.getPropertyAsBoolean("scanner.debug");
		}

	}

	/**
	 * getter for max threads
	 * @return
	 */
	public int getMaxThreads() {
		return MAXTHREADS;
	}

	/**
	 * setter for max threads
	 * @param i
	 */
	public void setMaxThreads(int i) {
		MAXTHREADS = i;
	}

	/**
	 * check if the port is closed
	 */
	public boolean isClosed(String hostname, int portnr) {
		return (getPortState(hostname, portnr) == "closed");
	}

	/**
	 * check if the port is open
	 */
	public boolean isOpen(String hostname, int portnr) {
		return (getPortState(hostname, portnr) == "open");
	}

	/** 
	 * scan the network with internal parameters set through constructor
	 * @see com.wanci.ncc.scanner.NetworkScannerInterface#discover()
	 */
	public void discover() throws XmlPropertiesFormatException {

		Range i = new Range(network);
		String host;

		if (DEBUG)
			System.out.println("NETWORKSCANNER: scanning Network " + network);

		//go through hosts in network
		while (i.hasNext()) {
			//store hostname in string
			host = (String) i.next();
			//init hostscanner
			HostScanner h = new HostScanner();

			h.setHost(host);
			h.addObserver(this);
			synchronized (getClass()) {
				resultsPending++;
			}
			for (int j = 0; j < ports.size(); j++) {
				h.addPort(((Integer) ports.get(j)).intValue());
			}

			while (numberOfThreadsRunning >= MAXTHREADS) {
				try {
					Thread.sleep(500);
				}
				catch (InterruptedException e) {
					if (DEBUG)
						e.printStackTrace();
				}
			}

			synchronized (getClass()) {
				numberOfThreadsRunning++;
			}
			Thread scanThread = new Thread(h);
			scanThread.setPriority(SCANPRIORITY);
			scanThread.start();

		}

	}

	/**
	 * return all up and running hosts
	 */
	public Vector getHostsRunning() {
		return hostsRunning;
	}

	/**
	 * return all hosts not up and running
	 */
	public Vector getHostsNotRunning() {
		return hostsNotRunning;
	}

	/**
	 * all hosts who run ftp service
	 * check hosts for fixed port 21
	 * @see com.wanci.ncc.scanner.NetworkScannerInterface#getHostsRunningServiceFtp()
	 */
	public Vector getHostsRunningServiceFtp() {
		Vector result = new Vector();
		int port = 21;

		HostScanner hs;
		//go through list of running hosts and check them
		Iterator iterator = hostsRunning.iterator();
		while (iterator.hasNext()) {
			hs = (HostScanner) iterator.next();
			if (hs.getPortState(port).equals("open"))
				result.addElement(hs);
		}
		return result;
	}

	/** 
	 * all hosts who run an webserver
	 * @see com.wanci.ncc.scanner.NetworkScannerInterface#getHostsRunningServiceHttp()
	 */
	public Vector getHostsRunningServiceHttp() {
		Vector result = new Vector();
		int port = 80;

		HostScanner hs;
		//go through list of running hosts and check them
		Iterator iterator = hostsRunning.iterator();
		while (iterator.hasNext()) {
			hs = (HostScanner) iterator.next();
			if (hs.getPortState(port).equals("open")) {
				result.addElement(hs);
			}
		}
		return result;
	}

	/**
	 * all hosts who run ssh server
	 * @see com.wanci.ncc.scanner.NetworkScannerInterface#getHostsRunningServiceSsh()
	 */
	public Vector getHostsRunningServiceSsh() {
		Vector result = new Vector();
		int port = 22;

		HostScanner hs;
		//go through list of running hosts and check them
		Iterator iterator = hostsRunning.iterator();
		while (iterator.hasNext()) {
			hs = (HostScanner) iterator.next();
			if (hs.getPortState(port).equals("open"))
				result.addElement(hs);
		}
		return result;
	}

	/**
	 * all hosts where we can login
	 * @see com.wanci.ncc.scanner.NetworkScannerInterface#getHostsWhereLoginPerSshIsPossible()
	 */
	public Vector getHostsWhereLoginPerSshIsPossible() {
		//not implemented
		return null;
	}

	/**
	 * set the network range
	 * @param setter for the network definition, parameter for the Range-Object
	 */
	public void setNetwork(String in) {
		ipRange = new Range(in);
		network = in;
	}

	public String getNetwork() {
		return network;
	}

	/**
	 * update method for threads
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		HostScanner hs;

		if (o instanceof HostScanner) {
			synchronized (getClass()) {
				numberOfThreadsRunning--;
			}
			hs = (HostScanner) o;
			//check whether host is running or not
			if (hs.isHostRunning())
				hostsRunning.addElement(hs);
			else
				hostsNotRunning.addElement(hs);

			synchronized (getClass()) {
				resultsPending--;
			}
		}
	}

	/**
	 * boolean for checking if scan is ready
	 * @return status of check (true/false)
	 */
	public boolean scanReady() {
		if (resultsPending > 0)
			return false;
		else
			return true;
	}

	/**
	 * output method for displaying the PortscanStatus list on the console
	 * @param mode could be xml 
	 */
	public String displayPortScanResults(String mode) {

		HostScanner hs;
		String line = "";

		Iterator iterator = hostsRunning.iterator();
		while (iterator.hasNext()) {

			hs = (HostScanner) iterator.next();
			if (mode == "xml")
				line += hs.toXml();
			if (mode == "html")
				line += hs.toHtml();
		} //while hosts

		return line;
	}

	/**
	 * output method for xml string
	 * @return output string
	 */
	public String toXml() {

		String s =
			"<network rangefrom=\""
				+ ipRange.addressToString(ipRange.getStartHost())
				+ "\" rangeto=\""
				+ ipRange.addressToString(ipRange.getEndHost())
				+ "\">\n";
		s += displayPortScanResults("xml");
		s += "</network>\n";

		return s;

	}

	/**
		 * output method for xml string
		 * @return output string
		 */
	public String toHtml() {

		String s = "<html>\n";
		s += "<strong>"
			+ ipRange.addressToString(ipRange.getStartHost())
			+ " - "
			+ ipRange.addressToString(ipRange.getEndHost())
			+ "</strong>\n";
		s += displayPortScanResults("html");
		s += "</html>";

		return s;

	}

	/**
	 * add port to list of ports to scan
	 * @param p
	 */
	public void addPort(int p) {
		ports.addElement(new Integer(p));
	}

	/**
	 * getter for ports
	 * @return Vector of Integer
	 */
	public Vector getPorts() {
		return ports;
	}

	/**
	 * get port state of a given host and a port
	 * @param portnr
	 * @return cleartext State of port (unknown, closed, dropped, closed)
	 */
	public String getPortState(String hostname, int portnr) {

		String state = "unknown";
		HostScanner hs;

		//go through list of running hosts
		Iterator iterator = hostsRunning.iterator();
		while (iterator.hasNext()) {

			//look if this is the host we are looking for
			hs = (HostScanner) iterator.next();
			if (hs.getIp().equals(hostname)) {

				//we found it, get the port state and exit
				state = hs.getPortState(portnr);
				break;

			} //name equals

		} //iterator

		return state;

	}

	/**
	 * main method for testing purposes 
	 * @param args
	 */
	public static void main(String args[]) throws XmlPropertiesFormatException {

		Boot.printCopyright("NETWORKSCANNER");

		NetworkScanner n = new NetworkScanner();
		try {
			n.setNetwork("10.48.35.1-10.48.35.3");
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		n.addPort(80);
		n.addPort(22);
		n.discover();
		while (!n.scanReady()) {
			;
		}

		System.out.println(n.toXml());

		//System.out.println("---------EXAMPLE how to ask for port states -----");
		//System.out.println("port 80: " + n.getPortState("10.48.35.23", 80));
		//System.out.println("port 1000: " + n.getPortState("10.48.35.3", 1000));

	}

}
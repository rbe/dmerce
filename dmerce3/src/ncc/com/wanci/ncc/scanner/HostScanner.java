/*
 * Created on Jun 3, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.ncc.scanner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.dmerce.kernel.XmlPropertiesReader;

/**
 * @author rb
 * @author pg
 * @version $Id: HostScanner.java,v 1.1 2004/02/02 09:41:49 rb Exp $
 * 
 * Scan eines Hosts auf die "Well-Known-Services"
 * 
 * Spaeter soll eine Funktionalitaet implementiert werden,
 * die es ermoelgicht, Services, die nicht auf ihrem
 * Standard-Port laufen, zu erkennen. Zunaechst gehen wir
 * erstmal davon aus, dass ein Host seine Services auf den
 * Well-Known-Ports laufen laesst. 
 * 
 */
public class HostScanner
	extends Observable
	implements HostScannerInterface, Observer, Runnable {

	/**
	 * dmerce properties
	 */
	private XmlPropertiesReader dmerceProperties;

	/**
	 * debug variable
	 */
	private static boolean DEBUG = false;

	/**
	 * look how many threads are running by this process
	 */
	private int numberOfThreadsRunning = 0;

	/**
	 * how many threads should be run by this process
	 * = how many hosts should be scanned simultaneously
	 */
	private int MAXTHREADS = 1;

	/**
	 * check priority of scans is determined by thread-priority
	 * Thread.NORM_PRIORITY = 5, MAX = 10, MIN = 1
	 */
	private int SCANPRIORITY = Thread.NORM_PRIORITY;

	/**
	 * string representation of host
	 */
	private String hostString;

	/**
	 * count of answers to get
	 */
	private int resultsPending = 0;

	/**
	 * inetaddress object for the target host
	 */
	private InetAddress targetHost;

	/**
	 * result list for portscanresult
	 */
	private Vector listPorts = new Vector();

	/**
	 * list of ports to scan
	 */
	private Vector ports = new Vector();

	/**
	 * constructor reads default values, if present
	 *
	 */
	public HostScanner() throws XmlPropertiesFormatException {
		int i;
		//initialize all variables
		dmerceProperties = XmlPropertiesReader.getInstance();
		i = dmerceProperties.getPropertyAsInt("hostscanner.maxthreads");
		if (i > 0)
			MAXTHREADS = i;
		else {
			i = dmerceProperties.getPropertyAsInt("scanner.maxthreads");
			if (i > 0)
				MAXTHREADS = i;
		}

		i = dmerceProperties.getPropertyAsInt("hostscanner.priority");
		if (i > 0)
			SCANPRIORITY = i;
		else {
			i = dmerceProperties.getPropertyAsInt("scanner.priority");
			if (i > 0)
				SCANPRIORITY = i;
		}

		if (dmerceProperties.propertyExists("hostscanner.debug")) {
			DEBUG = dmerceProperties.getPropertyAsBoolean("hostscanner.debug");
			System.out.println(DEBUG);
		} else {
			DEBUG = dmerceProperties.getPropertyAsBoolean("scanner.debug");
		}

	}

	/**
	 * is the host running or not
	 * when applying a hostname to a inetaddress-object it will fire an exception,
	 * when applying an ip-addres, it will not!
	 * then you have to check all portscanresults for dropped... 
	 */
	private boolean hostIsRunning = false;

	/**
	 * boolean for checking if scan is ready
	 * @return result (true/false)
	 */
	public boolean scanReady() {

		if (resultsPending > 0)
			return false;
		else
			return true;
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
	 * get scan results 
	 * @return port list (vector of portscanner objects
	 */
	public Vector getResults() {
		return listPorts;
	}

	/**
	 * get state of port
	 * @param portnr
	 * @return string of port status
	 */
	public String getPortState(int portnr) {
		String state = "unknown";

		if (!scanReady())
			return "not ready";

		//go through pssList
		PortScanner ps;

		Iterator iterator = listPorts.iterator();
		while (iterator.hasNext()) {
			ps = (PortScanner) iterator.next();
			if (ps.getPort() == portnr) {
				state = ps.getStateTCP();
				break;
			}
		}
		return state;
	}

	/**
	 * Setzt target Host 
	 * 
	 * @param in
	 */
	public boolean setHost(String hostn) {
		hostString = hostn;
		try {
			targetHost = InetAddress.getByName(hostn);
		} catch (UnknownHostException e) {
			hostIsRunning = false;
			return false;
		}

		hostIsRunning = true;
		return true;
	}

	/**
	 * check if all ports have been dropped,
	 * this could be the case, when the host is not running
	 */
	public boolean allPortsDropped() {
		//assume all ports queries have been dropped
		boolean result = true;

		//portscanner object
		PortScanner ps;

		//go through resultlist
		Iterator iterator = listPorts.iterator();
		while (iterator.hasNext()) {
			ps = (PortScanner) iterator.next();
			//check for state, if it is not dropped function must return false
			if (!ps.getStateTCP().equals("dropped")) {
				result = false;
				break;
			}
		}
		return result;
	}

	/**
	 * Laeuft der gescannte Host?
	 * @return result (true/false)
	 */
	public boolean isHostRunning() {
		return hostIsRunning || !allPortsDropped();
	}

	/**
	 * @see com.wanci.ncc.scanner.HostScannerInterface#isHostRunningServiceFtp()
	 */
	public boolean isHostRunningServiceFtp() {
		String state = getPortState(21);
		return (state.equals("open"));
	}

	/**
	 * @see com.wanci.ncc.scanner.HostScannerInterface#isHostRunningServiceHttp()
	 */
	public boolean isHostRunningServiceHttp() {
		String state = getPortState(80);
		return (state.equals("open"));
	}

	/**
	 * @see com.wanci.ncc.scanner.HostScannerInterface#isHostRunningServiceSsh()
	 */
	public boolean isHostRunningServiceSsh() {
		String state = getPortState(22);
		return (state.equals("open"));
	}

	/**
	 * @see com.wanci.ncc.scanner.HostScannerInterface#isPortClosed(int)
	 */
	public boolean isPortClosed(int portNumber) {
		String state = getPortState(portNumber);
		return (state.equals("closed"));
	}

	/**
	 * @see com.wanci.ncc.scanner.HostScannerInterface#isPortOpen(int)
	 */
	public boolean isPortOpen(int portNumber) {
		String state = getPortState(portNumber);
		return (state.equals("open"));
	}

	/**
	 * Scannt einen Host (sprich alle moeglichen Ports auf einer IP)
	 * und speichert die gefundenen Services in einer Liste
	 * (einfach alle Portnummern auflisten)
	 *
	 * @see com.wanci.ncc.scanner.HostScannerInterface#discover()
	 */
	public void discover() throws XmlPropertiesFormatException {

		Iterator i = ports.iterator();

		if (DEBUG)
			System.out.println("HOSTSCANNER: scanning host " + hostString);

		while (i.hasNext()) {

			int port = ((Integer) i.next()).intValue();

			while (numberOfThreadsRunning >= MAXTHREADS) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					if (DEBUG)
						e.printStackTrace();
				}
			}

			numberOfThreadsRunning++;

			PortScanner p = new PortScanner();
			p.addObserver(this);
			if (p.setHost(hostString)) {
				synchronized (getClass()) {
					resultsPending++;
				}
				p.setPort(port);
				Thread scanThread = new Thread(p);
				scanThread.setPriority(SCANPRIORITY);
				scanThread.start();
			} //if

		} //while
	}

	/**
	 * update method for thread things
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {

		if (o instanceof PortScanner) {
			synchronized (getClass()) {
				numberOfThreadsRunning--;
			}
			listPorts.addElement(o);
			synchronized (getClass()) {
				resultsPending--;
			}
		}

	}

	/**
	 * output method for displaying the PortscanStatus list on the console
	 * @param mode could be console or xml
	 */
	public String displayPortScanResults(String mode) {

		PortScanner ps;
		String line = "";

		//go through port result list
		Iterator iterator = listPorts.iterator();
		while (iterator.hasNext()) {
			//cast vector element to portscanner and append result
			ps = (PortScanner) iterator.next();
			line += ps.displayResults(mode);
		}
		return line;
	}

	/**
	 * output method for xml string
	 * @return output string
	 */
	public String toXml() {

		String s = "  <host name=\"";
		if (!hostIsRunning) {
			s += hostString + "\"";
			s += " warning=\"host is not running\"";
			s += ">\n";

		} else {
			s += targetHost.getHostName()
				+ "\" address=\""
				+ targetHost.getHostAddress()
				+ "\"";
			s += ">\n";
			s += displayPortScanResults("xml");
		}

		s += "  </host>\n";
		return s;
	}

	/**
	 * output method for html string
	 * @return output string
	 */
	public String toHtml() {

		String s = "<html>\n";
		s += "<strong>";
		if (!hostIsRunning) {
			s += hostString;
			s += " (warning: <font color=\"red\">host is not running</font>";
			s += "</strong>\n";
		} else {
			s += targetHost.getHostName()
				+ "("
				+ targetHost.getHostAddress()
				+ ")";
			s += "</strong><br><br>Scanned ports:\n";
			s += displayPortScanResults("html");
		}
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
	 * method for thread thing
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		try {
			discover();
		} catch (XmlPropertiesFormatException e1) {
			if (DEBUG)
				e1.printStackTrace();
		}

		while (!scanReady()) {

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		setChanged();
		notifyObservers();

	}

	/**
	 * get the ipaddress of the host
	 * @return name of host
	 */
	public String getIp() {
		return targetHost.getHostAddress();
	}

	/**
	 * get service identification string from service 
	 */
	public String getServiceIdString(int portnr) {
		//check port open
		//check service
		String state = "unknown";

		if (!scanReady())
			return "not ready";

		//go through pssList
		PortScanner ps;

		Iterator iterator = listPorts.iterator();
		while (iterator.hasNext()) {
			ps = (PortScanner) iterator.next();
			if (ps.getPort() == portnr) {
				state = ps.getServiceIdString();
				break;
			}
		}
		return state;
	}

	/**
	 * @return
	 */
	public Vector getPorts() {
		return ports;
	}

	/**
	 * main method for testing purposes
	 * @param args
	 */
	public static void main(String args[])
		throws XmlPropertiesFormatException {

		Boot.printCopyright("HOSTSCANNER");

		HostScanner h = new HostScanner();
		if (h.setHost("gandalf.muenster1-3.de.1ci.net")) {
			//		if (h.setHost("123123")) {
			h.addPort(80);
			h.addPort(1000);
			h.discover();
			while (!h.scanReady()) {
				//wait for results
				;
			}
		}

		System.out.println(h.toXml());
		System.out.println("---------EXAMPLE how to ask for port states -----");
		System.out.println("port 1234: " + h.getPortState(1234));
		System.out.println("port 80: " + h.getPortState(80));
		System.out.println("port 1000: " + h.getPortState(1000));
		if (!h.isHostRunning())
			System.out.println("Host is not running");
	}

}
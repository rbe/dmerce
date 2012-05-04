/*
 * Created on Jun 3, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.ncc.scanner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.dmerce.kernel.XmlPropertiesReader;

/**
 * @author rb
 * @author pg
 * 
 * Klasse fuer einen Scanner, der einen bestimmten
 * Port analysiert
 * 
 */
public class PortScanner
	extends Observable
	implements PortScannerInterface, Observer, Runnable {

	/**
	 * Priority settings
	 */
	private int SCANPRIORITY = Thread.NORM_PRIORITY;

	/**
	 * dmerce properties
	 */
	private XmlPropertiesReader dmerceProperties;

	/**
	 * debug variable
	 */
	private boolean DEBUG = false;

	/**
	 * the host
	 */
	private InetAddress host;

	/**
	 * service id string for identifying the running service
	 */
	private String serviceIdString = "";

	/**
	 * hoststring
	 */
	private String hostString;

	/**
	 * if tcp port should be scanned
	 */
	private boolean scanTCP = true;

	/**
	 * holds the result of udp scan
	 */
	private String stateTCP = "";

	/**
	 * time to answer in that scan
	 */
	private int answerTime = 0;

	/**
	 * port number to scan
	 */
	private int port = 0;

	/**
	 * count of answers to get
	 * tcp/udp scan is parallelized
	 */
	private int resultsPending = 0;

	/**
	 * constructor reads default values, if present
	 *
	 */
	public PortScanner() throws XmlPropertiesFormatException {
		int i;
		dmerceProperties = XmlPropertiesReader.getInstance();
		i = dmerceProperties.getPropertyAsInt("portscanner.priority");
		if (i > 0)
			SCANPRIORITY = i;
		else {
			i = dmerceProperties.getPropertyAsInt("scanner.priority");
			if (i > 0)
				SCANPRIORITY = i;
		}

		if (dmerceProperties.propertyExists("portscanner.debug")) {
			DEBUG = dmerceProperties.getPropertyAsBoolean("portscanner.debug");
		}
		else {
			DEBUG = dmerceProperties.getPropertyAsBoolean("scanner.debug");
		}
	}

	/**
	 * the time the remote host needed for answer
	 * @return time in msec
	 */
	public int getAnswerTime() {
		return answerTime;
	}

	/**
	 * @return
	 */
	public InetAddress getHost() {
		return host;
	}

	public String getHostIp() {
		return host.getHostAddress();
	}

	/**
	 * @return
	 */
	public String getHostString() {
		return hostString;
	}

	/**
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param string
	 */
	public void setHostString(String string) {
		hostString = string;
	}

	/**
	 * @param string
	 */
	public void setStateTCP(String string) {
		stateTCP = string;
	}

	public String toXML() {
		String line = "";
		line += "    "
			+ "<port protocol=\"tcp\""
			+ " state=\""
			+ getStateTCP()
			+ "\"";
		if (getStateTCP() == "open") {
			line += " timetoanswer=\"" + getAnswerTime() + "\"";
			line += " serviceid=\"" + serviceIdString + "\"";
		}
		line += ">" + getPort() + "</port>\n";
		return line;

	}

	/**
	 * display the result of the portscan in console mode or in xml
	 * @param mode (xml/console)
	 * @return output string
	 */
	public String displayResults(String mode) {
		String line = "";
		if (mode == "console") {
			line =
				getHostString()
					+ "\t"
					+ getPort()
					+ "/tcp"
					+ "\t"
					+ getStateTCP()
					+ "\t"
					+ getAnswerTime();
			System.out.println(line);
		}

		if (mode == "xml") {
			line += "    "
				+ "<port protocol=\"tcp\""
				+ " state=\""
				+ getStateTCP()
				+ "\"";
			if (getStateTCP() == "open") {
				line += " timetoanswer=\"" + getAnswerTime() + "\"";
				line += " serviceid=\"" + serviceIdString + "\"";
			}
			line += ">" + getPort() + "</port>\n";
		}

		if (mode == "html") {
			line += "<br>Port: "
				+ getPort()
				+ "<br>"
				+ "Protocol: tcp<br>"
				+ "State: "
				+ getStateTCP()
				+ "<br>";
			if (getStateTCP() == "open") {
				line += " timetoanswer: " + getAnswerTime() + "<br>";
				line += " serviceid: " + serviceIdString + "<br>";
			}
		}

		return line;
	}

	/**
	 * set the answer time in msec
	 * @param time in msec
	 */
	public void setAnswerTime(int i) {
		answerTime = i;
	}

	/**
	 * check if the port is closed
	 * 
	 * @see com.wanci.ncc.scanner.PortScannerInterface#isClosed()
	 */
	public boolean isClosed() {
		if (!isScanReady())
			discover();
		return (stateTCP == "closed");
	}

	/**
	 * check if the port is open
	 * @see com.wanci.ncc.scanner.PortScannerInterface#isOpen()
	 */
	public boolean isOpen() {
		if (!isScanReady())
			discover();
		return (stateTCP == "open");
	}

	/**
	 * scan method to see if the port is scannable
	 * 
	 * now only on TCP base
	 */
	public void discover() {
		//the tcp port scan is repeated with different timeouts on error
		//the timeout changes from 1 to 3 seconds if the socket connection times out
		//if the connection is successful break from for look and return open
		if (DEBUG)
			System.out.println("PORTSCANNER: Scanning port " + port);
		if (scanTCP)
			scanTcpPort();
	}

	/**
	 * starts thread for tcp port scan with no timeout
	 */
	private void scanTcpPort() {
		//we are waiting for one more result
		synchronized (getClass()) {
			resultsPending++;
		}

		TcpPortScan tcp = new TcpPortScan(host, port);
		tcp.addObserver(this);
		Thread scanThread = new Thread(tcp);
		scanThread.setPriority(SCANPRIORITY);
		scanThread.start();
	}

	/**
	 * starts thread for tcp port scan with given timeout
	 * @param timeout
	 */
	private void scanTcpPort(InetAddress ahost, int aport, int atimeout) {
		//we are waiting for one more result
		synchronized (getClass()) {
			resultsPending++;
		}

		TcpPortScan tcp = new TcpPortScan(ahost, aport, atimeout);
		tcp.addObserver(this);
		Thread scanThread = new Thread(tcp);
		scanThread.setPriority(Thread.MAX_PRIORITY);
		scanThread.start();
	}

	/**
	 * setter for the host
	 * @see com.wanci.ncc.scanner.PortScannerInterface#setHost(java.net.InetAddress)
	 */
	public boolean setHost(String hostn) {
		hostString = hostn;
		try {
			host = InetAddress.getByName(hostn);
		}
		catch (UnknownHostException e) {
			return false;
		}
		return true;
	}

	/**
	 * setter for Port to scan
	 * @param port number
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * get the state of the tcpstate
	 * @return state (open/closed)
	 */
	public String getStateTCP() {
		return stateTCP;
	}

	/**
	 * thread: update method for thread functionality
	 * @param object
	 * @param arguments
	 */
	public void update(Observable o, Object arg) {

		if (o instanceof TcpPortScan) {

			TcpPortScan current = (TcpPortScan) o;
			PortScanStatus pssTemp = (PortScanStatus) arg;
			if (pssTemp.getState() == "open") {
				setAnswerTime(pssTemp.getAnswerTime());
				setStateTCP(pssTemp.getState());
				serviceIdString = getOutputFromServiceFinder();
				synchronized (getClass()) {
					resultsPending--;
				}
			}
			else {
				//now check timeout on trials
				if (pssTemp.getTrialNumber() == 3) {
					//scan ended
					setAnswerTime(pssTemp.getAnswerTime());
					setStateTCP(pssTemp.getState());
					synchronized (getClass()) {
						resultsPending--;
					}
				}
				else {
					//timeout is not 3 -> perform further scans
					try {
						scanTcpPort(
							current.getHost(),
							current.getPort(),
							(pssTemp.getTrialNumber() + 1));
						synchronized (getClass()) {
							resultsPending--;
						}
					}
					catch (Exception e) {
						if (DEBUG)
							e.printStackTrace();
					} //try catch
				} //else timout
			} //else port not open
		}
		else if (o instanceof UdpPortScan) {
			System.out.println("sorry, not implemented");
		} //objecttype
	} //method

	/**
	 * boolean for checking if scan is ready
	 * @return check if scan is ready (true/false)
	 */
	public boolean isScanReady() {
		if (resultsPending > 0)
			return false;
		else
			return true;
	}

	/**
	 * output method for displaying the PortscanStatus list on the console
	 * @param mode could be console or xml
	 */
	public String displayPortScanResults(String mode) {
		String line = "";
		line += displayResults(mode);

		return line;
	}

	/**
	 * output method for xml string
	 * @return output string
	 */
	public String portScanResultsToXML() {
		String s = "<xml>\n";
		s += displayPortScanResults("xml");
		s += "</xml>";
		return s;
	}

	/**
	 * necessary run method to follow thread specification
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		discover();

		while (!isScanReady()) {
			try {
				Thread.sleep(500);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		setChanged();
		notifyObservers();
	}

	/**
	 * get the output string from the service finder module
	 */
	public String getOutputFromServiceFinder() {
		ServiceFinder sf = new ServiceFinder();
		sf.setHost(hostString);
		sf.setPort(port);
		sf.discover();
		return sf.getIdentificationString();
	}

	/**
	 * main method for testing purposes
	 * @param args
	 */
	public static void main(String args[]) throws XmlPropertiesFormatException {

		Boot.printCopyright("PORTSCANNER");

		PortScanner p = new PortScanner();
		if (!p.setHost("gandalf.muenster1-3.de.1ci.net"))
			System.out.println("host not reachable");
		else {
			p.setPort(21);
			p.discover();
			while (!p.isScanReady()) {
				;
			}
			System.out.println(p.portScanResultsToXML());
		}
	}

	/**
	 * @return
	 */
	public String getServiceIdString() {
		return serviceIdString;
	}

}
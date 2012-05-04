/*
 * Created on 20.06.2003
 */
package com.wanci.ncc.scanner;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;

import com.wanci.dmerce.comm.Ssh;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.dmerce.kernel.PortScannerXmlConfigReader;
import com.wanci.dmerce.kernel.PortScannerXmlConfigWriter;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.dmerce.qxtl.TransactionBuilder;
import com.wanci.dmerce.qxtl.TransactionManager;

/**
 * Full automated Network/Host Scanner Module for NCC
 * 
 * this module is the remote control for all other 
 * classes in this package, is is driven by an xml resource 
 * which can hold several nets and hosts and ports
 *
 * USAGE:
 * the main method of this class implements an example how to use this class: 
 * it reads etc/ncc.xml and builds a list of 
 * networks and hosts to scan
 * then it will start the scan and redo this every 5 minutes  
 * 
 * @author pg
 * @version $Id: NccScanner.java,v 1.3 2004/06/30 13:53:19 rb Exp $ 
 */
public class NccScanner {

	private boolean WRITERESULT;

	/**
	 * variable for setting pause to scan, 
	 * if set to 0, program will exit after one scan
	 * pause between in seconds
	 * 300 = scan every 5 min 
	 */
	int PAUSEBETWEENSCANS = 0;

	/**
	 * dmerce properties
	 */
	private XmlPropertiesReader dmerceProperties;

	/**
	 * scan ready
	 */
	boolean scanReady = false;

	/**
	 * debug variable
	 */
	boolean DEBUG = true;

	/**
	 * scan results of networks
	 * Vector of NetworkScanner
	 */
	private Vector listNetworks = new Vector();

	/**
	 * scan hosts results
	 * Vector of HostScanner
	 */
	private Vector listHosts = new Vector();

	/**
	 * networks to scan
	 * first element of Vector is a string with range
	 * next elements are integers (portnumbers)
	 */
	private Vector networks = new Vector();

	/**
	 * hosts to scan
	 * first element of Vector is a string holding the host (name _or_ IP) 
	 * next elements are integers (portnumbers)
	 */
	private Vector hosts = new Vector();

	/**
	 * constructor reads default values, if present
	 *
	 */
	public NccScanner() throws XmlPropertiesFormatException {
		//initialize all variables
		dmerceProperties = XmlPropertiesReader.getInstance();
		if (dmerceProperties.propertyExists("nccscanner.debug")) {
			DEBUG = dmerceProperties.getPropertyAsBoolean("nccscanner.debug");
		}
		else {
			DEBUG = dmerceProperties.getPropertyAsBoolean("scanner.debug");
		}

		WRITERESULT =
			dmerceProperties.getPropertyAsBoolean("scanner.writeresult");

		PAUSEBETWEENSCANS =
			dmerceProperties.getPropertyAsInt("scanner.pausebetweenscans");

	}

	/**
	 * reads the initial values from an xml file 
	 * and builds the networks and hosts Vector (implemented in ReaderClass)
	 * @return success in reading the config file
	 */
	public boolean readXmlConfig() {
		boolean readResult;
		PortScannerXmlConfigReader xmlReader = new PortScannerXmlConfigReader();
		if ((readResult = xmlReader.read()) == true) {
			networks = xmlReader.getNetworksToScan();
			hosts = xmlReader.getHostsToScan();
		}
		return readResult;
	}

	/**
	 * writes the values for the config Xml-File 
	 * @return success in writing the config file
	 */
	public boolean writeXmlConfig() {
		boolean writeResult;
		PortScannerXmlConfigWriter xmlWriter = new PortScannerXmlConfigWriter();
		writeResult = xmlWriter.write();
		return writeResult;
	}

	/**
	 * set network with two Strings, the startIp, and the stopIp
	 * further set default ports for scanning the basic ports
	 * @param startIp
	 * @param stopIp
	 */
	private void addNetwork(String startIp, String stopIp) {
		Vector v = new Vector();
		v.addElement(new String(startIp + "-" + stopIp));
		v.addElement(new Integer(21));
		v.addElement(new Integer(22));
		v.addElement(new Integer(80));

		networks.addElement(v);
	}

	/**
	 * check if the given port of a given hostname is closed
	 * @return true/false
	 */
	public boolean isClosed(String hostname, int portnr) {
		return (getPortState(hostname, portnr) == "closed");
	}

	/**
	 * check if the given port of a given hostname is open
	 * @return true/false
	 */
	public boolean isOpen(String hostname, int portnr) {
		return (getPortState(hostname, portnr) == "open");
	}

	/**
	 * do a full scan of the hosts and store the results as Vector of HostScanner
	 */
	public void discoverHosts() throws XmlPropertiesFormatException {
		/**
		 * count variables for the loops
		 */
		int i, j;

		//do the host scanning
		for (i = 0; i < hosts.size(); i++) {
			//build temp vector for hosts, first item is string, next items are the port vectors
			Vector temp = (Vector) hosts.get(i);
			//get the range from temp
			String host = (String) temp.get(0);
			if (DEBUG)
				System.out.println("scanning host " + host + "...");

			//setup host scanner
			HostScanner h = new HostScanner();
			try {
				h.setHost(host);
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			//add the ports from the vector
			for (j = 1; j < temp.size(); j++) {
				Integer port = (Integer) temp.get(j);
				h.addPort(port.intValue());
			}

			//do the scanning
			h.discover();
			//h is threaded -> wait for scan ready
			while (!h.scanReady()) {
				;
			}
			//add complete hostscanner object to resultvector
			listHosts.addElement(h);
		}
	}

	/**
	 * do a full scan of the given networks and store the results in
	 * a vector of NetworkScanners
	 *
	 */
	public void discoverNetworks() throws XmlPropertiesFormatException {
		// count variables for loops
		int j;
		//iterator for networks
		Iterator iter = networks.iterator();

		while (iter.hasNext()) {
			//build temp vector for networks, first item is string, next items are the port vectors
			Vector temp = (Vector) iter.next();
			//get the range from temp
			String range = (String) temp.get(0);
			if (DEBUG)
				System.out.println("scanning network " + range + "...");
			//setup network scanner
			NetworkScanner n = new NetworkScanner();
			try {
				n.setNetwork(range);
			}
			catch (Exception e) {
				if (DEBUG)
					System.out.println(
						"ERROR: discoverNetworks: wrong format of range");
				break;
			}
			//add the ports from the vector
			for (j = 1; j < temp.size(); j++) {
				Integer port = (Integer) temp.get(j);
				n.addPort(port.intValue());
			}
			//do the scanning
			n.discover();
			//n is threaded, so wait for results
			while (!n.scanReady()) {
				;
			}
			//add complete NetworkScannerObject to the resultvector
			listNetworks.addElement(n);
		} //while nect network
	} //method

	/**
	 * do the scanning of networks and hosts
	 */
	public void discover() throws XmlPropertiesFormatException {
		discoverNetworks();
		discoverHosts();
		setScanReady(true);
	}

	/**
	 * get status of port
	 * this method is a small search engine for the ports
	 * it searches in the hostresultVector and 
	 * in the network resultvector for the desired port 
	 * 
	 * @param host
	 * @param port
	 * @return cleartext for state of port (unkown, not found, open, dropped, closed)
	 */
	public String getPortState(String host, int port) {

		//search in hosts
		HostScanner hs;
		String state = "not found";

		//go through list of hosts
		Iterator iterator = listHosts.iterator();
		while (iterator.hasNext()) {
			//look if this is the host we are looking for
			hs = (HostScanner) iterator.next();
			if (hs.getIp().equals(host)) {
				//we found it, get the port state and exit
				state = hs.getPortState(port);
				break;
			} //name equals
		} //iterator

		if (state.equals("not found")) {
			//host was not found in host list
			//go through network list
			state = "not found";

			//go through list of Networks
			NetworkScanner ns;

			iterator = listNetworks.iterator();
			while (iterator.hasNext()) {
				//look if this is the host we are looking for
				ns = (NetworkScanner) iterator.next();
				state = ns.getPortState(host, port);
				if (!state.equals("not found")) {
					break;
				} //state
			} //iterator
		}
		//now state has been set to the right value
		return state;
	}

	/**
	 * get the list of all Hosts with an open, given port
	 * @param port
	 * @return Vector of HostScanner
	 */
	public Vector getHostsWithOpenPort(int port) {
		Vector allRunning = getHostsRunning();

		Vector v = new Vector();
		//go through host List
		Iterator iterator = allRunning.iterator();
		while (iterator.hasNext()) {
			HostScanner hs = (HostScanner) iterator.next();
			if (hs.getPortState(port).equals("open")) {
				v.addElement(hs);
			}
		} //while
		return v;
	}

	/**
	 * check which hosts run service http
	 * @return vector of hostScannerObjects
	 */
	public Vector getHostsRunningServiceHttp() {
		return getHostsWithOpenPort(80);
	}

	/**
	 * check which hosts run service ftp
	 * @return vector of hostScannerObjects
	 */
	public Vector getHostsRunningServiceFtp() {
		return getHostsWithOpenPort(21);
	}

	/**
	 * check which hosts run service ssh
	 * @return vector of hostScannerObjects
	 */
	public Vector getHostsRunningServiceSsh() {
		return getHostsWithOpenPort(22);
	}

	/**
	 * check on which hosts a login with default logindata is possible
	 * @return
	 */
	public Vector getHostsWhereLoginPerSshIsPossible() {

		Vector allSshHosts = getHostsRunningServiceSsh();
		Vector v = new Vector();
		Ssh s;

		Iterator iterator = allSshHosts.iterator();
		while (iterator.hasNext()) {
			HostScanner hs = (HostScanner) iterator.next();
			try {
				//try to login to the host as default user
				s = new Ssh(hs.getIp(), "dmerce", "dmerce");
				s.init();
				s.connect();
				if (s.isConnected()) {
					if (DEBUG)
						System.out.println(
							"SSH-Scanner: connection successful on host: "
								+ hs.getIp());
					v.addElement(hs);
					s.disconnect();
				}
				else {
					if (DEBUG)
						System.out.println(
							"SSH-Scanner: can't login to: " + hs.getIp());
				}
			}
			catch (Exception e) {
				if (DEBUG)
					System.out.println(
						"SSH-Scanner: Exception occured (host: "
							+ hs.getIp()
							+ ")");
			} //try catch
		} //while hosts
		return v;
	}

	/**
	 * get all hosts in a list who seem to be up and running
	 * @return vector of hostscanner
	 */
	public Vector getHostsRunning() {
		Vector v = new Vector();
		Iterator iterator;

		//go through host List
		HostScanner hs;
		iterator = listHosts.iterator();
		while (iterator.hasNext()) {
			//look if this is the host we are looking for
			hs = (HostScanner) iterator.next();
			if (hs.isHostRunning()) {
				v.addElement(hs);
			} // if
		} //iterator

		//go through network list
		NetworkScanner ns;
		iterator = listNetworks.iterator();
		while (iterator.hasNext()) {
			ns = (NetworkScanner) iterator.next();
			Iterator iter = ns.getHostsRunning().iterator();
			while (iter.hasNext()) {
				v.addElement(iter.next());
			} //while
		} //iterator

		return v;
	}

	/**
	 * method for displaying the complete scanning result as an XML-String
	 * ohne umschließende XML-Tags 
	 * @return
	 */
	public String toXml() {
		String str = "";
		//go through hosts
		HostScanner hs;

		//go through list of hosts
		Iterator iterator = listHosts.iterator();
		while (iterator.hasNext()) {
			//look if this is the host we are looking for
			hs = (HostScanner) iterator.next();
			str += hs.toXml();
		} //iterator

		//go through networks
		NetworkScanner ns;

		iterator = listNetworks.iterator();
		while (iterator.hasNext()) {
			//look if this is the host we are looking for
			ns = (NetworkScanner) iterator.next();
			str += ns.toXml();
		} //iterator
		return str;
	}

	/**
	 * @return
	 */
	public Vector getHosts() {
		return hosts;
	}

	/**
	 * @return
	 */
	public Vector getNetworks() {
		return networks;
	}

	/**
	 * @return
	 */
	public boolean isScanReady() {
		return scanReady;
	}

	/**
	 * @param b
	 */
	private void setScanReady(boolean b) {
		scanReady = b;
	}

	/**
	 * get a host by its name
	 * @param name
	 * @return HostScanner object
	 */
	public HostScanner getHostByName(String name) {
		HostScanner hs = null;
		Iterator iterator = listHosts.iterator();
		while (iterator.hasNext()) {
			hs = (HostScanner) iterator.next();
			if (hs.getIp().equals(name))
				break;
		}
		return hs;
	}

	/**
	 * getter for network by the name
	 * @param name
	 * @return NetworkScanner object
	 */
	public NetworkScanner getNetworkByName(String name) {
		NetworkScanner ns = null;
		Iterator iterator = listNetworks.iterator();
		while (iterator.hasNext()) {
			ns = (NetworkScanner) iterator.next();
			if (ns.getNetwork().equals(name))
				break;
		}
		return ns;
	}

	/**
	 * sleep for given time
	 * @return
	 */
	public boolean sleep() {
		if (PAUSEBETWEENSCANS > 0) {
			if (DEBUG)
				System.out.println(
					"pausing for " + PAUSEBETWEENSCANS + " seconds...");
			try {
				Thread.sleep(PAUSEBETWEENSCANS * 1000);
			}
			catch (InterruptedException e) {
				if (DEBUG)
					e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	/**
	 * set the pause between scans
	 * @param pause
	 */
	public void setPause(int pause) {
		PAUSEBETWEENSCANS = pause;
	}

	public void go() throws XmlPropertiesFormatException {
		//variables for manual input of network data
		boolean automaticMode = true;

		String startIp = "";
		String stopIp = "";

		if (automaticMode) {
			automaticMode = readXmlConfig();
			if (!automaticMode)
				if (DEBUG)
					System.out.println("File ncc.xml not found.");
		}
		if (!automaticMode) {
			System.out.println("Please configure a network you want to scan.");

			BufferedReader reader =
				new BufferedReader(new InputStreamReader(System.in));

			try {
				System.out.print("Start-IP: ");
				startIp = reader.readLine();
				System.out.print("Stop-IP: ");
				stopIp = reader.readLine();
				addNetwork(startIp, stopIp);
			}
			catch (IOException e) {
				System.out.println("Error during configuration. Exiting.");
				System.exit(1);
			}
			//write config file
			if (!writeXmlConfig()) {
				if (DEBUG)
					System.out.println(
						"could not write config file. Aborting scan");
				System.exit(102);
			}
			automaticMode = true;
		}

		//endless loop with scanning again every 5 minutes
		while (true) {
			if (DEBUG)
				System.out.println("Scan in progress...");

			//read xmlconfig again and again
			if (!readXmlConfig()) {
				if (DEBUG)
					System.out.println(
						"could not read config file. Aborting scan");
				System.exit(103);
			}

			discover();
			if (DEBUG)
				System.out.println(toXml());

			//check writeResult-Flag from Config-File
			if (WRITERESULT)
				writeResult();

			if (DEBUG)
				System.out.println("...scan ready");

			//make the scanner sleep for set seconds
			if (!sleep()) {
				if (DEBUG)
					System.out.println("no repeat time set, exiting normal");
				System.exit(0);
			}
		} //endless loop		
	}

	/**
	 * write the result to database 
	 */
	private void writeResult() throws XmlPropertiesFormatException {

		HostScanner hs;
		PortScanner ps;
		NetworkScanner ns;

		//go through list of hosts
		Iterator iterator = listHosts.iterator();
		while (iterator.hasNext()) {
			//look if this is the host we are looking for
			hs = (HostScanner) iterator.next();
			Iterator iterResult = hs.getResults().iterator();
			while (iterResult.hasNext()) {
				ps = (PortScanner) iterResult.next();
				transaction(hs.getIp(), "" + ps.getPort(), ps.getStateTCP());
			}
		} //iterator

		//go through networks
		iterator = listNetworks.iterator();
		while (iterator.hasNext()) {
			ns = (NetworkScanner) iterator.next();
			Iterator iter = ns.getHostsRunning().iterator();
			while (iter.hasNext()) {
				//look if this is the host we are looking for
				hs = (HostScanner) iter.next();
				Iterator iterResult = hs.getResults().iterator();
				while (iterResult.hasNext()) {
					ps = (PortScanner) iterResult.next();
					transaction(
						hs.getIp(),
						"" + ps.getPort(),
						ps.getStateTCP());
				} //host iterator
			} // network iterator
		} //networklist iterator
	}

	/**
	 * transaktion erstellen und ausführen
	 * @param params
	 */
	private void transaction(String ip, String port, String stats)
		throws XmlPropertiesFormatException {

		TransactionManager tm = TransactionManager.getInstance();
		TransactionBuilder tb = new TransactionBuilder("nccscanner");

		int result;

		/*
		<storedproc>
		<command value="sp_nccSavePortScanResult">
		<parameter name="ipaddress" type="string" value="10.48.35.3">
		<parameter name="port" type="int" value="80">
		<parameter name="service" type="string" value="http">
		<parameter name="state" type="string" value="open">
		<returnvalue type="int" expectedvalue="1">
		</storedproc>
		 */
		//Vektoren für die Parameterlisten, analog zu der XML-Beschreibung oben
		Vector names = new Vector();
		Vector types = new Vector();
		Vector values = new Vector();

		//schritte
		/*
		 * SELECT serverid,serverinterfaceipid from serverview where ipaddress=ipaddress
		 * SELECT portid from ports where portnumber=port
		 * SELECT serviceid from services where service=service
		 * INSERT into portscanresults...
		 */

		//SQLStoredProc bauen
		//parameter für Stored proc sind name des parameters,
		// typ des parameters und wert,
		//ein paar Parameter (String ip, String port, String stats) sind einzubauen
		names.addElement("ipaddress");
		types.addElement("string");
		values.addElement(ip);
		names.addElement("port");
		types.addElement("int");
		values.addElement(port);
		names.addElement("protocol");
		types.addElement("string");
		values.addElement("tcp");
		names.addElement("status");
		types.addElement("string");
		values.addElement(stats);
		names.addElement("answertime");
		types.addElement("int");
		values.addElement("0");
		names.addElement("service");
		types.addElement("int");
		values.addElement("null");
		//bei erfolgreicher Ausführung gibt die Stored Procedure 1 zurück, sonst einen anderen Wert
		//returnvalue ist aber nur für die interne Verarbeitung durch den TM wichtig,
		//für dieses Modul wird 0 zurückgegeben wenn alles in Ordnung ist 
		tb.addStepStoredProc(
			"sp_nccSavePortScanResult",
			names,
			types,
			values,
			"int",
			"1");

		//transaktion ausführen und ergebnis prüfen
		tb.setTransaction(tm.executeTransaction(tb.getTransaction()));
		result = tm.checkTransactionResult(tb.getTransaction());
		if (result < 0) {
			System.err.println("Transaction failed!");
		}
	}

	/**
	 * main method for implementing a sample usage for this class
	 * also usage example
	 * @param args
	 */
	public static void main(String[] args)
		throws XmlPropertiesFormatException {

		Boot.printCopyright("NCCSCANNER");

		NccScanner scanner = new NccScanner();
		scanner.go();

	}

} //class

/*
	OTHER EXAMPLE USAGE		 
		 
				System.out.println("---------PORT STATE-----------");
				System.out.println(
					"gandalf/80: " + scanner.getPortState("10.48.35.3", 80));
				System.out.println("--------- FTP HOSTS -----------");
				iterator = scanner.getHostsRunningServiceFtp().iterator();
				while (iterator.hasNext()) {
					HostScanner hs = (HostScanner) iterator.next();
					System.out.println(hs.getHostName());
					System.out.println(hs.getServiceIdString(21));
				} //while
		
*/
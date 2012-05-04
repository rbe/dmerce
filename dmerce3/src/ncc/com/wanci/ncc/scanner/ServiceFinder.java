/*
 * Created on 18.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.ncc.scanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wanci.dmerce.kernel.Boot;

/**
 * @author pg
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ServiceFinder {

	/**
	 * Id String of first line when connecting a socket (WelcomeString)
	 */
	private String identificationString = "";

	/**
	 * holds the service name (IANA) of the service running on that port
	 */
	String serviceType = "";

	/**
	 * holds the Servertype of the service, is a human readable identification string, e.g. "apache 1.3.1"
	 */
	String serviceServer = "";

	/**
	 * the Socket output 
	 */
	Vector v = new Vector();

	/**
	 * host String
	 * @return
	 */
	String host = "";

	/**
	 * portnumber
	 * @return
	 */
	int port = 0;

	/**
	 * @param serviceType
	 * @return
	 */
	private boolean getServerType() {
		/*
		SSH-2.0-Sun_SSH_1.0
		SSH-1.99-OpenSSH_3.4p1
		220 gandalf FTP server ready. 
		Bei web "get /" schicken
		*/
		
		boolean b = false;
		
		if (serviceType == "")
			return false;

		if (serviceType == "web") {
			if (parseVectorForString(v, "<ADDRESS>") > -1) {
				Pattern p = Pattern.compile("<ADDRESS>(*.)<ADDRESS");
				Matcher m = p.matcher("aaaaab");
				b = m.matches();
			}
		} else if (serviceType == "ssh") {
		} else if (serviceType == "ftp") {
		}

		return b;
	}

	/**
	 * parse Vector for string
	 * @param v
	 * @param string
	 * @return row number
	 */
	private int parseVectorForString(Vector v, String str) {
		String row;
		int i = 0;
		for (Enumeration e = v.elements(); e.hasMoreElements();) {
			row = (String) e.nextElement();
			if (row.indexOf(str) > -1)
				return i;
			i++;
		}
		return -1;
	}

	/**
	 * getter for service identification string
	 * @return human readable string of service, e.g. "Proftpd 1.3"
	 */
	public String getServiceServer() {
		return serviceServer;
	}

	/**
	 * IANA service type, e.g. "ssh"
	 * @return IANA service string
	 */
	public String getServiceType() {
		return serviceType;
	}
	public void setHost(String hostname) {
		host = hostname;
	}

	public void setPort(int portnr) {
		port = portnr;
	}

	public void isPortHttpService() {
		/*
		 * 
		 
		 out.println("GET /");
		
		int i;
		String str = "";
		
		try {
			s.setSoTimeout(300);
		} catch (SocketException e3) {
			e3.printStackTrace();
		}
		
		try {
			while ((str = in.readLine()) != null) {
		
				v.addElement(new String(str));
				System.out.println("response: " + str);
		
			}
		
		} catch (Exception e1) {
			;
			//e1.printStackTrace();
		}
		*/
	}

	public boolean discoverServiceId() {
		if (host == "" || port == 0)
			return false;

		Socket s = new Socket();
		PrintWriter out = null;
		BufferedReader in = null;

		try {
			s.connect(new InetSocketAddress(host, port), 1000);
			s.setSoTimeout(2000);
			out = new PrintWriter(s.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (Exception e) {
			return false;
		}

		try {
			identificationString = in.readLine();
		} catch (Exception e) {
			;
		}
		out.close();
		try {
			in.close();
			s.close();
		} catch (Exception e2) {
			;
		}

		if (identificationString == null)
		{
			identificationString = "";
			
		}
		return true;
	}

	public boolean discover() {
		if (host == "" || port == 0)
			return false;

		if (discoverServiceId() == false)
			return false;

		//id string is now set
		if (identificationString == null) {
			System.out.println("no information at initial connect");
		}

		if (identificationString != "") {
			//parse Responses for different answers
			if (parseVectorForString(v, "FTP") > -1)
				serviceType = "ftp";
			else if (parseVectorForString(v, "SSH") > -1)
				serviceType = "ssh";
			else if (parseVectorForString(v, "HTML") > -1) {
				serviceType = "web";
			}
		}

		if (serviceType != "")
			getServerType();

		return true;
	}
	/**
	 * @return
	 */
	public String getIdentificationString() {
		return identificationString;
	}

	public static void main(String[] args) {

		Boot.printCopyright("SERVICEFINDER");

		ServiceFinder sf = new ServiceFinder();
		sf.setHost("gandalf.muenster1-3.de.1ci.net");
		sf.setPort(21);
		if (sf.discover()) {
			System.out.println(sf.getServiceServer());
			System.out.println(sf.getServiceType());
		} else
			System.out.println("error");

		System.out.println("!----");
		sf.discoverServiceId();
		System.out.println(sf.getIdentificationString());

	}

}

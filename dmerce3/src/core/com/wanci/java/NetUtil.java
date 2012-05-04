/**
 * Created on Jan 20, 2003
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package com.wanci.java;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author rb
 * 
 * Hilfen fuer java.net.*
 */
public class NetUtil {

	/** Prueft einen uebergebenen String auf Name oder IP-Adresse und gibt
	 * ein InetAddress-Objekt zurueck
	 */
	public static InetAddress getInetAddress(String hostname) throws UnknownHostException {
		InetAddress host = InetAddress.getLocalHost();
		if (LangUtil.isCharAtLetter(hostname, 0))
			host = InetAddress.getByName(hostname);
		else if (LangUtil.isCharAtNumber(hostname, hostname.length()))
			host = InetAddress.getByAddress(hostname.getBytes());
		return host;
	}

	public static void main(String[] args) {
	}
}

/*
 * DmerceProperties.java
 *
 * Created on January 6, 2003, 2:05 PM
 */

package com.wanci.ncc;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.java.LangUtil;
import com.wanci.java.UtilUtil;

/** Liest, verwaltet und lässt die dmerce.properties abfragen.
 *
 * @author  rb
 */
public class NccProperties {

	public String[] loadedFiles;

	private XmlPropertiesReader xmlPropertiesReader = null;

	private Properties dmerceNccProps;

	private String nccMonitorInetServicePrefix = "ncc.monitor.inet";

	/** 
	 */
	public NccProperties() {
		try {
			xmlPropertiesReader = XmlPropertiesReader.getInstance();
		} catch (XmlPropertiesFormatException e) {
			e.printStackTrace();
		}
	}

	/** Liefert eine Hashmap der NCC Monitoring Farbwerte zu ID
	 */
	public HashMap getNccMonitorInetColors() {
		HashMap m = new HashMap();
		Properties p =
			UtilUtil.filterPropertiesForKeys(
				new String[] { "ncc.monitor.inet.color" },
				dmerceNccProps);
		Enumeration e = p.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String k = key.replaceAll("ncc.monitor.inet.color.", "");
			k = k.replaceAll(".id", "");
			m.put(k.toLowerCase(), new Integer(p.getProperty(key)));
		}
		return m;
	}

	/**
	 * Method getNccMonitorInetServers.
	 * @return Vector
	 */
	public Iterator getNccMonitorInetServers() {
		return LangUtil
			.stringTokensToVector(
				new StringTokenizer(
					dmerceNccProps.getProperty("ncc.monitor.inet.servers"),
					","))
			.iterator();
	}

	/**
	 * Method getNccMonitorInetServerId.
	 * @param host
	 * @return int
	 */
	public int getNccMonitorInetServerId(String host) {
		return new Integer(
			dmerceNccProps.getProperty("ncc.monitor.inet." + host + ".id"))
			.intValue();
	}

	/**
	 * Method getNccMonitorInetServiceId.
	 * @param host
	 * @return int
	 */
	public int getNccMonitorInetServiceId(String service) {
		return new Integer(
			dmerceNccProps.getProperty(
				"ncc.monitor.inet.service." + service + ".id"))
			.intValue();
	}

	/**
	 * Method getNccMonitorInetServerServices.
	 * @param host
	 * @return Vector
	 */
	public Iterator getNccMonitorInetServerServices(String host) {
		return LangUtil
			.stringTokensToVector(
				new StringTokenizer(
					dmerceNccProps.getProperty(
						"ncc.monitor.inet." + host + ".services"),
					","))
			.iterator();
	}

	public Iterator getNccMonitorInetServerServicePorts(
		String host,
		String service) {
		return LangUtil
			.stringTokensToVector(
				new StringTokenizer(
					getNccMonitorInetService(host, service, "ports"),
					","))
			.iterator();

	}

	/** Liefert einen Wert einer NCC-Monitoring-InetService-Property
	 *
	 * @param host Hostname
	 * @param service das gewünschte InetService
	 * @param suffix das Suffix (ports, send, expect)
	 * @return String; der Wert der gewünschten NCC-Property
	 */
	public String getNccMonitorInetService(
		String host,
		String service,
		String suffix) {
		String key =
			nccMonitorInetServicePrefix
				+ "."
				+ host
				+ "."
				+ service
				+ "."
				+ suffix;
		Properties ports =
			UtilUtil.filterPropertiesForKeys(
				new String[] { key },
				dmerceNccProps);
		return ports.getProperty(key);
	}


	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		NccProperties d = new NccProperties();
		System.out.print("Loading dmerce.properties from:");
		String[] lf = d.loadedFiles;
		if (lf.length > 0) {
			for (int i = 0; i < lf.length; i++)
				System.out.print(" " + lf[i]);
			System.out.println("\n\nDumping all properties");
			System.out.println(
				"-->"
					+ d.getNccMonitorInetService("localhost", "http", "ports"));
			d.getNccMonitorInetServers();
			System.out.println("\n\nDumping System.properties:");
			UtilUtil.dumpProperties(System.getProperties());
		} else {
			System.out.println("No dmerce.properties found!");
		}
	}

}
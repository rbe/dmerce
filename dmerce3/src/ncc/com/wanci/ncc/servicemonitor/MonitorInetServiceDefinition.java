/**
 * Created on Jan 20, 2003
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package com.wanci.ncc.servicemonitor;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.wanci.java.NetUtil;

/**
 * @author rb
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class MonitorInetServiceDefinition {

	private InetServiceDefinition inetServiceDefinition;

	private InetAddress host;

	private int serverId = -1;

	private int serviceId = -1;

	private boolean connectOnly = true;

	private int pollTime;
	
	private String tcpSendString;
	
	private String tcpExpectString;

	/** Maximale Antwortzeit in Millisekunden, die erlaubt ist,
	 * bevor das ServiceResource als rot deklariert wird; Standard ist 1000 ms
	 */
	private int maximumResponseTime;

	/**
	 * Method MonitorInetServiceDefinition.
	 * @param inetServiceDefinition
	 * @param host
	 */
	public MonitorInetServiceDefinition(
		InetServiceDefinition isd,
		InetAddress host) {
		this.inetServiceDefinition = isd;
		this.host = host;
	}

	/**
	 * Method MonitorInetServiceDefinition.
	 * @param inetServiceDefinition
	 * @param hostname
	 */
	public MonitorInetServiceDefinition(
		InetServiceDefinition isd,
		String hostname)
		throws UnknownHostException {
		this.inetServiceDefinition = isd;
		this.host = NetUtil.getInetAddress(hostname);
	}

	/** Zeige alle gesetzten Variablen an
	 */
	public void dump() {
		System.out.println(
			"Hostname: "
				+ host.getHostName()
				+ "\nserverId: "
				+ serverId
				+ "\nserviceId: "
				+ serviceId
				+ "\npollTime: "
				+ pollTime
				+ "\nmaximumResponseTime: "
				+ maximumResponseTime
				+ "\ntcpSendString: "
				+ tcpSendString
				+ "\ntcpExpectString: "
				+ tcpExpectString);
	}

	/**
	 * Returns the host.
	 * @return InetAddress
	 */
	public InetAddress getHost() {
		return host;
	}

	/** Liefert den Hostnamen zurueck
	 */
	public String getHostname() {
		return host.getHostName();
	}

	/**
	 * Returns the inetServiceDefinition.
	 * @return InetServiceDefinition
	 */
	public InetServiceDefinition getInetServiceDefinition() {
		return inetServiceDefinition;
	}

	/**
	 * Returns the maximumResponseTime.
	 * @return int
	 */
	public int getMaximumResponseTime() {
		return maximumResponseTime;
	}

	/**
	 * Returns the pollTime.
	 * @return int
	 */
	public int getPollTime() {
		return pollTime;
	}

	/**
	 * Returns the serverId.
	 * @return int
	 */
	public int getServerId() {
		return serverId;
	}

	/**
	 * Returns the serviceId.
	 * @return int
	 */
	public int getServiceId() {
		return serviceId;
	}

	/**
	 * Method getTcpExpectString.
	 * @return String
	 */
	public String getTcpExpectString() {
		return tcpExpectString;
	}

	/**
	 * Method getTcpSendString.
	 * @return String
	 */
	public String getTcpSendString() {
		return tcpSendString;
	}

	/** Wird der Dienst nur konnektiert und keine Pruefung der Funktionalitaet gemacht?
	 * 
	 * @return boolean
	 */
	public boolean isConnectOnly() {
		return connectOnly;
	}

	/** Setzt ein Flag, so dass der Dienst nur konnektiert wird
	 * und keine Kommunikation stattfindet
	 * 
	 * @param connectOnly The connectOnly to set
	 */
	public void setConnectOnly(boolean connectOnly) {
		this.connectOnly = connectOnly;
	}

	/** Setzt den Hostname/IP-Addresse des zu prüfenden Dienstes
	 *
	 * @param host Hostname/IP-Addresse
	 */
	public void setHost(InetAddress host) {
		this.host = host;
	}

	/**
	 * Sets the inetServiceDefinition.
	 * @param inetServiceDefinition The inetServiceDefinition to set
	 */
	public void setInetServiceDefinition(InetServiceDefinition inetServiceDefinition) {
		this.inetServiceDefinition = inetServiceDefinition;
	}

	/**
	 * Sets the maximumResponseTime.
	 * @param maximumResponseTime The maximumResponseTime to set
	 */
	public void setMaximumResponseTime(int maximumResponseTime) {
		this.maximumResponseTime = maximumResponseTime;
	}

	/** Setzt den Zeitintervall, in dem ein Dienst abgefragt wird
	 *
	 * @param pollTime Zeit in Millisekunden
	 */
	public void setPollTime(int pollTime) {
		this.pollTime = pollTime;
	}

	/**
	 * Sets the serverId.
	 * @param serverId The serverId to set
	 */
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	/**
	 * Sets the serviceId.
	 * @param serviceId The serviceId to set
	 */
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
	
	public void setTcpExpectString(String tcpExpectString) {
		this.tcpExpectString = tcpExpectString;
	}

	public void setTcpSendString(String tcpSendString) {
		this.tcpSendString = tcpSendString;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "nothing";
	}

}
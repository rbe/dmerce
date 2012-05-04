/*
 * Created on 12.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.ncc.scanner;

import java.net.InetAddress;
import java.util.Date;

/**
 * @author pg
 * @version $Id: PortScanStatus.java,v 1.1 2004/02/02 09:41:49 rb Exp $
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PortScanStatus {

	/**
	 * host on that the portscan has been performed
	 */
	private InetAddress host;

	/**
	 * port scanned
	 */
	private int port = 0;

	/**
	 * protocol used in portscan (tcp/udp) 
	 */
	private String protocol;

	/**
	 * timestamp of scan
	 */
	private Date timestamp = new Date();

	/**
	 * time to answer in that scan
	 */
	private int answerTime = 0;

	/**
	 * status of port
	 */
	private String state = "";

	/**
	 * trial number
	 */
	private int trialNumber = 1;

	/**
	 * the time the remote host needed for answer
	 * @return time in msec
	 */
	public int getAnswerTime() {
		return answerTime;
	}

	public PortScanStatus() {

	}

	public PortScanStatus(
		InetAddress host,
		int port,
		String protocol,
		int answerTime,
		String state,
		int trialNumber) {
		this.host = host;
		this.port = port;
		this.protocol = protocol;

		this.answerTime = answerTime;
		this.state = state;
		this.trialNumber = trialNumber;
	}

	/**
	 * getter for port number
	 * @return port number
	 */
	public int getPort() {
		return port;
	}

	/**
	 * getter for protocol
	 * @return protocol type (tcp/udp)
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * getter for timestamp, when scan has been performed
	 * @return time of scan
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * setter for port
	 * @param port number 1-65536
	 */
	public void setPort(int i) {
		if (i > 0 && i < 65537)
			port = i;
	}

	/**
	 * setter for type of protocol (tcp/udp)
	 * @param protocol type
	 */
	public void setProtocol(String string) {
		if (string == "tcp" || string == "udp")
			protocol = string;
	}

	/**
	 * timestamp
	 * @param date
	 */
	public void setTimestamp(Date date) {
		timestamp = date;
	}

	/**
	 * getter for host
	 * @return the inetaddress object of the host
	 */
	public InetAddress getHost() {
		return host;
	}

	/**
	 * getter for state of port (open/closed)
	 * @return state of port
	 */
	public String getState() {
		return state;
	}

	/**
	 * setter for host
	 * @param address object
	 */
	public void setHost(InetAddress address) {
		host = address;
	}

	/**
	 * setter for state of port
	 * @param string
	 */
	public void setState(String string) {
		state = string;
	}

	/**
	 * number of tries performed on the port 
	 * @return number of tries
	 */
	public int getTrialNumber() {
		return trialNumber;
	}

	/**
	 * setter for trialnumber
	 * @param number of tries
	 */
	public void setTrialNumber(int i) {
		trialNumber = i;
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
				getHost()
					+ "\t"
					+ getPort()
					+ "/"
					+ getProtocol()
					+ "\t"
					+ getState()
					+ "\t"
					+ getAnswerTime();
			System.out.println(line);
		}

		if (mode == "xml") {
			line += "    "
				+ "<port protocol=\""
				+ getProtocol()
				+ "\""
				+ " state=\""
				+ getState()
				+ "\"";
			if (getState() == "open") {
				line += " timetoanswer=\"" + getAnswerTime() + "\"";
			}
			line += ">" + getPort() + "</port>\n";
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

}

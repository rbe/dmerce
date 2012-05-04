/**
 * MonitorInetService.java
 *
 * Created on 1. Januar 2003, 23:09
 */

package com.wanci.ncc.servicemonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

import com.wanci.dmerce.kernel.Logfile;
import com.wanci.ncc.audit.AuditMonitorInetService;

/** Überwachen eines Internet-Server-Dientes
 * Abstrakte Klasse, die alle grundlegenden Methoden zur Überwachung
 * von TCP- und UDP-basierten Internet-Diensten zur Verfügung stellt
 *
 * @author  rb
 */
public abstract class MonitorInetService implements InetServiceMonitor {

	private Logfile logfile;

	private MonitorInetServiceDefinition misd;

	private InetServiceDefinition isd;

	private Socket[] tcpSockets;

	private InputStream[] tcpIns;

	private OutputStream[] tcpOuts;

	private String tcpSendString;

	private String tcpExpectedAnswer;

	private DatagramSocket[] udpSockets;

	/** Creates a new instance of MonitorInetService
	 *
	 * @param inetServiceName Name des Services
	 * @param tcpPorts TCP-Ports
	 * @param udpPorts UDP-Ports
	 */
	public MonitorInetService(MonitorInetServiceDefinition misd) {
		this.misd = misd;
		this.isd = misd.getInetServiceDefinition();
		logfile =
			new Logfile("MonitorInetService[" + isd.getInetServiceName() + "]");
		setDebug(false);
	}

	public abstract AuditMonitorInetService check();

	/**
	 * @see com.wanci.ncc.InetServiceMonitor#checkTcp()
	 */
	public boolean checkTcp() throws IOException {
		boolean ok = false;
		System.out.println(misd.isConnectOnly());
		if (!misd.isConnectOnly()) {
			int[] tcpPorts = isd.getTcpPorts();
			if (tcpSendString != null && tcpExpectedAnswer != null) {
				for (int i = 0; i < tcpPorts.length; i++) {
					logfile.putDebug(
						"Writing to TCP-Port "
							+ tcpPorts[i]
							+ " "
							+ tcpSendString.trim());
					tcpOuts[i].write(tcpSendString.getBytes());
					logfile.putDebug("Reading from TCP-Port " + tcpPorts[i]);
					BufferedReader b =
						new BufferedReader(new InputStreamReader(tcpIns[i]));
					String s;
					logfile.putDebug("Trying to find: " + tcpExpectedAnswer);
					while ((s = b.readLine()) != null) {
						if (s.indexOf(tcpExpectedAnswer) != -1) {
							logfile.putDebug(
								"Found "
									+ tcpExpectedAnswer
									+ " in line: "
									+ s);
							ok = true;
						}
					}
				}
			}
			logfile.write();
			return ok;
		}
		else
			return true;
	}

	/**
	 * @see com.wanci.ncc.InetServiceMonitor#closeTcpSockets()
	 */
	public void closeTcpSockets() throws IOException {
		for (int i = 0; i < tcpSockets.length; i++) {
			Socket socket = tcpSockets[i];
			if (socket != null) {
				socket.shutdownInput();
				socket.shutdownOutput();
				socket.close();
			}
		}
	}

	public void closeUdpSockets() throws SocketException {
		for (int i = 0; i < udpSockets.length; i++) {
			DatagramSocket socket = udpSockets[i];
			if (socket != null) {
				socket.disconnect();
			}
		}
	}

	public abstract void deinit();

	public void dump() {
		int[] tcpPorts = isd.getTcpPorts();
		int[] udpPorts = isd.getUdpPorts();
		logfile.putDump("Service: " + isd.getInetServiceName());
		for (int i = 0; i < tcpPorts.length; i++)
			logfile.putDump("TCP-Port: " + tcpPorts[i]);
		for (int i = 0; i < udpPorts.length; i++)
			logfile.putDump("UDP-Port: " + udpPorts[i]);
		logfile.putDump("Hostname: " + misd.getHostname());
		logfile.putDump("Poll Time: " + misd.getPollTime());
		logfile.putDump(
			"Maximum Response Time: " + misd.getMaximumResponseTime());
		logfile.write();
	}

	public abstract void init();

	public void initTcpSockets() {
		int[] tcpPorts = isd.getTcpPorts();
		tcpSockets = new Socket[tcpPorts.length];
		tcpIns = new InputStream[tcpPorts.length];
		tcpOuts = new OutputStream[tcpPorts.length];
	}

	public void initUdpSockets() {
		int[] udpPorts = isd.getUdpPorts();
		udpSockets = new DatagramSocket[udpPorts.length];
	}

	public void openTcpSockets() throws IOException, ConnectException {
		int[] tcpPorts = isd.getTcpPorts();
		for (int i = 0; i < tcpPorts.length; i++) {
			Socket socket = new Socket(misd.getHost(), tcpPorts[i]);
			socket.setSoTimeout(1000);
			tcpSockets[i] = socket;
			tcpIns[i] = socket.getInputStream();
			tcpOuts[i] = socket.getOutputStream();
		}
	}

	public void openUdpSockets() throws SocketException {
		int[] udpPorts = isd.getUdpPorts();
		for (int i = 0; i < udpPorts.length; i++) {
			DatagramSocket socket = new DatagramSocket();
			udpSockets[i] = socket;
		}
	}

	public void setDebug(boolean debug) {
		logfile.setSuppressDebug(debug);
	}

	public void setTcpExpectedAnswer(String answer) {
		if (answer != null)
			tcpExpectedAnswer = answer;
		else
			tcpExpectedAnswer = "<html>";
	}

	public void setTcpSendString(String send) {
		if (send != null)
			tcpSendString = send;
		else
			tcpSendString = "GET /\r\n\r\n";
	}

}
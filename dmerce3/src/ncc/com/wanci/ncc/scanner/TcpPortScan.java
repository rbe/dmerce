package com.wanci.ncc.scanner;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Observable;

/**
* thread to scan TCP port on host
*
* @author 1Ci
* @version $Id: TcpPortScan.java,v 1.1 2004/02/02 09:41:49 rb Exp $
*/

public class TcpPortScan extends Observable implements Runnable {

	private InetAddress host;

	private int port;

	/**
	 * timeout in seconds
	 */
	private int timeout = 1;

	/**
	 * time to answer (ms)
	 */
	private long answerTime = 0;

	/*
	*Constructs new thread to Scan host on TCP port
	*@param InetAddress the IP address of host to scan
	*@param int port number to scan
	*/
	public TcpPortScan(InetAddress host, int port) {
		this.host = host;
		this.port = port;
		this.timeout = 1;
	}

	public TcpPortScan(InetAddress host, int port, int timeout) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}

	/**
	* returns the IP address being scanned
	*/
	public InetAddress getHost() {
		return host;
	}

	/**
	* return the port being scanned
	*/
	public int getPort() {
		return port;
	}

	/**
	* Scans host/port using the TCP protocol
	*/
	public void run() {
		try {
			String portsStatus = this.scanTCP();
			setChanged();
			PortScanStatus pss =
				new PortScanStatus(
					host,
					port,
					"tcp",
					(int) answerTime,
					portsStatus,
					timeout);
			notifyObservers(pss);
		} catch (NoRouteToHostException e) {
			setChanged();
			notifyObservers("null"); //null to signify error contacting host
			return;
		}
	}

	/**
	*Scans single port using specified TCP host/port
	*@return String - either OPEN CLOSED DROPPED
	*/
	public String scanTCP() throws NoRouteToHostException {
		try {
			Socket s = new Socket();
			long start = System.currentTimeMillis();
			s.connect(new InetSocketAddress(host, port), timeout * 1000);
			answerTime = System.currentTimeMillis() - start;
			s.close();
		} catch (NoRouteToHostException e) {
			throw e; //throw to calling
		} catch (SocketTimeoutException e) {
			answerTime = timeout * 1000;
			return "dropped";
		} catch (IOException e) {
			answerTime = timeout * 1000;
			return "closed";
		}
		return "open";
	}

}

/*
 * MonitorInetServiceSmtp.java
 *
 * Created on 2. Januar 2003, 01:02
 */

package com.wanci.ncc.servicemonitor;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import com.wanci.dmerce.kernel.Logfile;
import com.wanci.ncc.audit.AuditMonitorInetService;

/** 
 * @author  rb
 * @version $Id: MonitorInetServiceHttp.java,v 1.2 2004/03/29 13:39:33 rb Exp $
 * 
 * Prüft einen Dienst und gibt Datensätze auf stdout aus
 * oder schreibt diese in eine Datenbank
 * 
 */
public class MonitorInetServiceHttp extends MonitorInetService {

	private Logfile logfileHttp;

	private MonitorInetServiceDefinition misd;

	private AuditMonitorInetService misa;

	//private int serverId;

	/** Creates a new instance of MonitorInetServiceHttp
	 */
	public MonitorInetServiceHttp(MonitorInetServiceDefinition misd)
		throws UnknownHostException {
		super(misd);
		logfileHttp = new Logfile("MonitorInetServiceHttp");
		logfileHttp.setSuppressDebug(false);
		this.misd = misd;
		//this.serverId = serverId;
		misa = new AuditMonitorInetService(misd);
	}

	public void deinit() {
		logfileHttp.putDebug("Closing TCP sockets");
		try {
			closeTcpSockets();
		}
		catch (IOException ioe) {
			logfileHttp.putDebug("I/O-Error: " + ioe.toString());
		}
		logfileHttp.write();
	}

	public AuditMonitorInetService check() {
		String message;
		String serviceRespondedIn = "Service response time unknown";
		init();
		try {
			logfileHttp.putDebug("Opening TCP sockets");
			long timeBegin = System.currentTimeMillis();
			openTcpSockets();
			logfileHttp.putDebug("Checking service");
			boolean ok = checkTcp();
			long timeEnd = System.currentTimeMillis();
			long timeDelta = timeEnd - timeBegin;
			serviceRespondedIn =
				"Service responded in "
					+ timeDelta
					+ " ms ("
					+ misa.getMaximumResponseTime()
					+ " ms max)";
			logfileHttp.putInfo(serviceRespondedIn);
			misa.setResponseTime(timeDelta);
			if (ok) {
				message = "Service AVAILABLE and CONTENT OK";
				logfileHttp.putInfo(message);
				misa.setServiceResponded(true);
				misa.setContentOk(true);
			}
			else {
				message = "Service AVAILABLE but CONTENT NOT OK";
				logfileHttp.putError(message);
				misa.setServiceResponded(true);
				misa.setContentOk(false);
			}
		}
		catch (ConnectException ce) {
			logfileHttp.putDebug("Connection refused");
			message = "Service NOT AVAILABLE";
			logfileHttp.putError(message);
			misa.setServiceResponded(false);
		}
		catch (IOException ioe) {
			message =
				"Service COULD NOT BE CHECKED: I/O-Error: " + ioe.toString();
			logfileHttp.putError(message);
			misa.setServiceResponded(false);
		}
		misa.setMessage(message + ", " + serviceRespondedIn);
		misa.chooseColor();
		logfileHttp.write();
		deinit();
		return misa;
	}

	public void init() {
		logfileHttp.putDebug("Init");
		try {
			setTcpSendString("GET / \r\n\r\n");
			setTcpExpectedAnswer("<html>");
			initTcpSockets();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		logfileHttp.write();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		Logfile logfile = new Logfile("MonitorServiceHTTP.main");
		logfile.putInfo("Test: Monitor Service HTTP");
		logfile.write();
		try {
			MonitorInetServiceDefinition m =
				new MonitorInetServiceDefinition(
					InetServices.http,
					"localhost");
			m.setPollTime(1000);
			m.setMaximumResponseTime(100);
			MonitorInetServiceHttp mishttp = new MonitorInetServiceHttp(m);
			mishttp.init();
			mishttp.setDebug(true);
			mishttp.dump();
			mishttp.check();
			mishttp.deinit();
		}
		catch (UnknownHostException uhe) {
			logfile.putDebug("Unknown host: " + uhe.toString());
			logfile.write();
		}
	}

}
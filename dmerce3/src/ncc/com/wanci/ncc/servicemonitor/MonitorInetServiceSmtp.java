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
 * @version $Id: MonitorInetServiceSmtp.java,v 1.2 2004/03/29 13:39:33 rb Exp $
 * 
 * Prüft einen Dienst und gibt Datensätze auf stdout aus
 * oder schreibt diese in eine Datenbank
 * 
 */
public class MonitorInetServiceSmtp extends MonitorInetService {

	private Logfile logfileSmtp;

	private MonitorInetServiceDefinition misd;

	private AuditMonitorInetService misa;

	//private int serverId;

	/** Creates a new instance of MonitorInetServicSmtp */
	public MonitorInetServiceSmtp(MonitorInetServiceDefinition misd)
		throws UnknownHostException {
		super(misd);
		logfileSmtp = new Logfile("MonitorInetServiceSmtp");
		logfileSmtp.setSuppressDebug(false);
		this.misd = misd;
		//this.serverId = serverId;
		misa = new AuditMonitorInetService(misd);
	}

	public void deinit() {
		logfileSmtp.putDebug("Closing TCP sockets");
		try {
			closeTcpSockets();
		}
		catch (IOException ioe) {
			logfileSmtp.putDebug("I/O-Error: " + ioe.toString());
		}
		logfileSmtp.write();
	}

	public AuditMonitorInetService check() {
		String message;
		String serviceRespondedIn = "Service response time unknown";
		init();
		try {
			logfileSmtp.putDebug("Opening TCP sockets");
			long timeBegin = System.currentTimeMillis();
			openTcpSockets();
			logfileSmtp.putDebug("Checking service");
			boolean ok = checkTcp();
			long timeEnd = System.currentTimeMillis();
			long timeDelta = timeEnd - timeBegin;
			serviceRespondedIn =
				"Service responded in "
					+ timeDelta
					+ " ms ("
					+ misa.getMaximumResponseTime()
					+ " ms max)";
			logfileSmtp.putInfo(serviceRespondedIn);
			misa.setResponseTime(timeDelta);
			if (ok) {
				message = "Service AVAILABLE and CONTENT OK";
				logfileSmtp.putInfo(message);
				misa.setServiceResponded(true);
				misa.setContentOk(true);
			}
			else {
				message = "Service AVAILABLE but CONTENT NOT OK";
				logfileSmtp.putError(message);
				misa.setServiceResponded(true);
				misa.setContentOk(false);
			}
		}
		catch (ConnectException ce) {
			logfileSmtp.putDebug("Connection refused");
			message = "Service NOT AVAILABLE";
			logfileSmtp.putError(message);
			misa.setServiceResponded(false);
		}
		catch (IOException ioe) {
			message =
				"Service COULD NOT BE CHECKED: I/O-Error: " + ioe.toString();
			logfileSmtp.putError(message);
			misa.setServiceResponded(false);
		}
		misa.setMessage(message + ", " + serviceRespondedIn);
		misa.chooseColor();
		logfileSmtp.write();
		deinit();
		return misa;
	}

	public void init() {
		logfileSmtp.putDebug("Init");
		try {
			setTcpSendString("EHLO");
			setTcpExpectedAnswer("pleased");
			initTcpSockets();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		logfileSmtp.write();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		Logfile logfile = new Logfile("MonitorServiceSmtp.main");
		logfile.putInfo("Test: Monitor Service SMTP");
		logfile.write();
		try {
			MonitorInetServiceDefinition m =
				new MonitorInetServiceDefinition(
					InetServices.smtp,
					"localhost");
			m.setPollTime(1000);
			m.setMaximumResponseTime(100);
			MonitorInetServiceSmtp missmtp = new MonitorInetServiceSmtp(m);
			missmtp.init();
			missmtp.setDebug(true);
			missmtp.dump();
			missmtp.check();
			missmtp.deinit();
		}
		catch (UnknownHostException uhe) {
			logfile.putDebug("Unknown host: " + uhe.toString());
			logfile.write();
		}
	}

}
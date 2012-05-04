/*
 * MonitorServices.java
 *
 * Created on January 3, 2003, 6:51 PM
 */

package com.wanci.ncc.servicemonitor;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.dmerce.kernel.Logfile;
import com.wanci.ncc.NccProperties;
import com.wanci.ncc.audit.AuditMonitorInetService;
import com.wanci.ncc.audit.AuditMonitorServer;

/**
 * Führt das Monitoring aller per dmerce-Konfigurationsdateien
 * konfigurierten Dienste durch
 *
 * @author  rb
 */
public class MonitorServices {

	private NccProperties ncc = null;
	
	private Logfile logfile;

	private Database jdbc;

	private Vector monitor = new Vector();

	/** Creates a new instance of MonitorService */
	public MonitorServices() throws SQLException {
		ncc = new NccProperties();
		logfile = new Logfile("MonitorServices");
		//dp = new DmerceProperties();
	}

	public void openJdbcConnection() throws SQLException {
		jdbc = DatabaseHandler.getDatabaseConnection("ncc");
		jdbc.openConnection();
	}

	/**
	 * Schliesst Datenbankverbindung
	 * 
	 * @throws SQLException
	 */
	public void closeJdbcConnection() throws SQLException {
		jdbc.closeConnection();
	}

	public AuditMonitorInetService checkInetServiceHttp(MonitorInetServiceDefinition misd) {

		AuditMonitorInetService audit = new AuditMonitorInetService(misd);
		MonitorInetServiceHttp mishttp;

		try {
			mishttp = new MonitorInetServiceHttp(misd);
			audit = mishttp.check();
		} catch (UnknownHostException e) {
		}

		return audit;

	}

	/**
	 * Method check.
	 */
	public void check() {
		/*
		InetServiceDefinition isd = misd.getInetServiceDefinition();
		String server = misd.getHostname();
		String service = isd.getInetServiceName();
		StringBuffer s =
			new StringBuffer("Checking " + server + "/" + service + " (TCP@");
		int[] tcpPorts = isd.getTcpPorts();
		for (int i = 0; i < tcpPorts.length; i++) {
			s.append(tcpPorts[i]);
			if (i < tcpPorts.length - 1)
				s.append(",");
		}
		s.append(")");
		logfile.putInfo(s.toString());
		logfile.write();
		*/

		Iterator monit = monitor.iterator();

		while (monit.hasNext()) {

			MonitorInetServiceDefinition misd =
				(MonitorInetServiceDefinition) monit.next();
			AuditMonitorInetService audit = checkInetServiceHttp(misd);

			AuditMonitorServer ams = new AuditMonitorServer(misd.getServerId());
			ams.add(audit);
			ams.chooseColor();

			try {
				jdbc.executeUpdate(audit.toSqlUpdate());
				jdbc.executeUpdate(ams.toSqlUpdate());
			} catch (SQLException se) {
				logfile.putError("Error with database: " + se.getMessage());
				logfile.write();
			}

		}

	}

	/** Generiert MonitorInetService-Objekte aus der NCC-Konfiguration
	 */
	public void generateMonitorObjects() {

		Iterator inetServers = ncc.getNccMonitorInetServers();

		while (inetServers.hasNext()) {

			String server = (String) inetServers.next();
			int serverId = ncc.getNccMonitorInetServerId(server);
			Iterator inetServices = ncc.getNccMonitorInetServerServices(server);

			while (inetServices.hasNext()) {

				String service = (String) inetServices.next();
				int serviceId = ncc.getNccMonitorInetServiceId(service);
				Iterator ports =
					ncc.getNccMonitorInetServerServicePorts(server, service);

				while (ports.hasNext()) {

					Integer port = new Integer((String) ports.next());
					int pollTime =
						new Integer(
							ncc.getNccMonitorInetService(
								server,
								service,
								"pollTime"))
							.intValue();
					int maximumResponseTimeout =
						new Integer(
							ncc.getNccMonitorInetService(
								server,
								service,
								"maximumResponseTimeout"))
							.intValue();
					String tcpSendString =
						ncc.getNccMonitorInetService(server, service, "send");
					String tcpExepctString =
						ncc.getNccMonitorInetService(server, service, "expect");

					try {

						InetServiceDefinition i =
							new InetServiceDefinition(
								service,
								"Monitor " + service,
								new int[] { port.intValue()},
								new int[] {
						});

						MonitorInetServiceDefinition m =
							new MonitorInetServiceDefinition(i, server);

						m.setServerId(serverId);
						m.setServiceId(serviceId);
						m.setPollTime(pollTime);
						m.setMaximumResponseTime(maximumResponseTimeout);
						m.setTcpSendString(tcpSendString);
						m.setTcpExpectString(tcpExepctString);

						monitor.add(m);

					} catch (UnknownHostException e) {
					}

				}

			}

		}

	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws SQLException {

		Boot.printCopyright();

		Logfile logfile = new Logfile("MonitorServices.main");
		MonitorServices ms;

		ms = new MonitorServices();
		logfile.putInfo("Generating Monitor-objects");
		logfile.write();

		ms.generateMonitorObjects();
		logfile.putInfo("Connecting to database");
		logfile.write();

		try {
			ms.openJdbcConnection();
		} catch (SQLException e) {
			ms.jdbc.dumpSqlException(e);
		}

		ms.check();

		try {
			ms.closeJdbcConnection();
		} catch (SQLException e) {
			ms.jdbc.dumpSqlException(e);
		}

		logfile.putInfo("Disconnected from database");
		logfile.write();

	}

}
/**
 * MonitorInetServiceAudit.java
 *
 * Created on January 6, 2003, 7:24 PM
 * 
 * @author rb
 */

package com.wanci.ncc.audit;

import com.wanci.ncc.servicemonitor.MonitorInetServiceDefinition;

/** Audit/Datensatz für Internet Services
 *
 * @author  rb
 * @version $Id: AuditMonitorInetService.java,v 1.1 2004/02/02 09:41:53 rb Exp $
 * 
 */
public class AuditMonitorInetService extends ServiceMonitor {

	/** Creates a new instance of AuditMonitorInetService
	 */
	public AuditMonitorInetService(MonitorInetServiceDefinition misd) {

		readColorCodes();
		statusCount = status.size();
		this.misd = misd;
		maximumResponseTime = misd.getMaximumResponseTime();
		contentOk = false;

	}

	/** Stuft das Antwortverhalten eines Internet-Dienstes in Bezug
	 * auf Zeit und Funktionsfähigkeit in einen Farbcode ein
	 * 
	 * @param responseTime Antwortzeit des Dienstes in ms
	 * @author rb
	 */
	public void chooseColor() {

		if (serviceResponded) {

			if (contentOk) {

				if (maximumResponseTime >= 0 && responseTime >= 0) {

					double d = maximumResponseTime / statusCount;
					double e = responseTime / d;

					/* System.out.println(
						"chooseColor(): statusCount="
							+ statusCount
							+ ", mrt="
							+ maximumResponseTime
							+ ", rt="
							+ responseTime
							+ " e="
							+ (int) e); */

					switch ((int) e) {
						case 0 :
						case 1 :
							setServiceGreen();
							break;
						case 2 :
						case 3 :
							setServiceYellow();
							break;
						case 4 :
							setServiceOrange();
							break;
						case 5 :
							setServiceRed();
							break;
					}

				}

			}
			else
				setServiceRed();

		}
		else
			setServiceBlack();

	}

	/**
	 * @see com.wanci.ncc.ServiceAudit#toSqlUpdate()
	 */
	public String toSqlUpdate() {

		Integer statusId = new Integer(0);

		if (isServiceBlack())
			statusId = (Integer) status.get("black");
		if (isServiceGreen())
			statusId = (Integer) status.get("green");
		if (isServiceOrange())
			statusId = (Integer) status.get("orange");
		if (isServiceRed())
			statusId = (Integer) status.get("red");
		if (isServiceYellow())
			statusId = (Integer) status.get("yellow");

		return "UPDATE SrvServerSvcs"
			+ " SET StatusID = "
			+ statusId
			+ ", StatusMessage = '"
			+ message
			+ "'"
			+ " WHERE ServerID = "
			+ misd.getServerId()
			+ " AND ServiceID = "
			+ misd.getServiceId();

	}

}
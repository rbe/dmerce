/**
 * Created on Jan 21, 2003
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package com.wanci.ncc.audit;

import java.util.HashMap;

import com.wanci.ncc.servicemonitor.InetServiceDefinition;
import com.wanci.ncc.servicemonitor.MonitorInetServiceDefinition;

/**
 * @author rb
 * @version $Id: ServiceMonitor.java,v 1.1 2004/02/02 09:41:53 rb Exp $
 * 
 * Abstrakte Klasse, die Grundlagen für Audit-Klassen im NCC
 * zur Verfügung stellt
 */
public abstract class ServiceMonitor implements InetServiceAudit {

	/**
	 */
	protected HashMap status = new HashMap();

	/**
	 */
	public static int statusCount;

	/**
	 */
	protected boolean statusGreen;

	/**
	 */
	protected boolean statusYellow;

	/**
	 */
	protected boolean statusOrange;

	/**
	 */
	protected boolean statusRed;

	/**
	 */
	protected boolean statusBlack;

	/**
	 */
	protected MonitorInetServiceDefinition misd;

	/**
	 */
	protected boolean serviceResponded = false;

	/** Zeit in Millisekunden, in der ein Dienst geantwortet hat
	 */
	protected long responseTime = -1;

	/** Zeit in Millisekunden, die maximal bis zur Reaktion des
	 * ServiceResource vergehen darf
	 */
	protected long maximumResponseTime;

	/** Flag; war der Inhalt den der Dienst zurückliefert OK?
	 */
	protected boolean contentOk;

	/** Eine Statusmeldung zum gemonitoreden Zustand
	 */
	protected String message = "No message. Sorry";

	/** Liest die Farb-ID-Zuordnungen aus der dmerce.properties
	 */
	public void readColorCodes() {
		//DmerceProperties dp = new DmerceProperties();
		//status = dp.getNccMonitorInetColors();
	}

	/** Zeigt alle Status-Flags an
	 */
	public void dumpStatus() {
		System.out.println(isServiceBlack());
		System.out.println(isServiceGreen());
		System.out.println(isServiceOrange());
		System.out.println(isServiceRed());
		System.out.println(isServiceYellow());
	}

	public InetServiceDefinition getInetServiceDefinition() {
		return misd.getInetServiceDefinition();
	}

	public MonitorInetServiceDefinition getMonitorInetServiceDefinition() {
		return misd;
	}

	public long getMaximumResponseTime() {
		return maximumResponseTime;
	}

	public String getMessage() {
		return message;
	}

	public long getResponseTime() {
		return responseTime;
	}

	public boolean getServiceResponded() {
		return serviceResponded;
	}

	public boolean isServiceBlack() {
		return statusBlack;
	}

	public boolean isContentOk() {
		return contentOk;
	}

	public boolean isServiceGreen() {
		return statusGreen;
	}

	public boolean isServiceOrange() {
		return statusOrange;
	}

	public boolean isServiceRed() {
		return statusRed;
	}

	public boolean isServiceYellow() {
		return statusYellow;
	}

	public void setContentOk(boolean contentOk) {
		this.contentOk = contentOk;
	}

	public void setMaximumResponseTime(long maximumResponseTime) {
		if (maximumResponseTime >= 0)
			this.maximumResponseTime = maximumResponseTime;
		else
			this.maximumResponseTime = 0;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setResponseTime(long responseTime) {
		if (responseTime >= 0)
			this.responseTime = responseTime;
		else
			this.responseTime = -1;
	}

	public void setServiceResponded(boolean serviceResponded) {
		this.serviceResponded = serviceResponded;
	}

	public void setServiceBlack() {
		statusBlack = true;
		statusGreen = false;
		statusOrange = false;
		statusRed = false;
		statusYellow = false;
	}

	public void setServiceGreen() {
		statusBlack = false;
		statusGreen = true;
		statusOrange = false;
		statusRed = false;
		statusYellow = false;
	}

	public void setServiceOrange() {
		statusBlack = false;
		statusGreen = false;
		statusOrange = true;
		statusRed = false;
		statusYellow = false;
	}

	public void setServiceRed() {
		statusBlack = false;
		statusGreen = false;
		statusOrange = false;
		statusRed = true;
		statusYellow = false;
	}

	public void setServiceYellow() {
		statusBlack = false;
		statusGreen = false;
		statusOrange = false;
		statusRed = false;
		statusYellow = true;
	}

}

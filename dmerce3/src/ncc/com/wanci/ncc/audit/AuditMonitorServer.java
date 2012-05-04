/**
 * Created on Jan 21, 2003
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package com.wanci.ncc.audit;

import java.util.Iterator;
import java.util.Vector;


/**
 * @author rb
 * @version $Id: AuditMonitorServer.java,v 1.1 2004/02/02 09:41:53 rb Exp $
 * 
 * <p>Setzt aus mehreren ServiceMonitor* Objekten einen Status für einen Server
 * zusammen</p>
 * 
 * <p>Alle Farbcodes der einzelnen ServiceMonitor*-Klassen werden erfasst und
 * danach ausgewertet:</p>
 * <br>
 * <p>Ist mindestens ein ServiceResource eines Server schwarz oder rot, so bekommt
 * der Server auch schwarz oder rot</p>
 * <br>
 * <p>Ansonsten wird der am meisten vorkommende Farbcode (gelb, orange oder
 * grün) als Farbcode für den Server gewählt</p>
 */
public class AuditMonitorServer extends ServiceMonitor {

	/** Hält alle auszuwertenden ServiceMonitor*-Instanzen
	 */
	private Vector v = new Vector();

	/** Zähler für Services mit der Farbe schwarz
	 */
	private int blackCount;

	/** Zähler für Services mit der Farbe rot
	 */
	private int redCount;

	/** Zähler für Services mit der Farbe orange
	 */
	private int orangeCount;

	/** Zähler für Services mit der Farbe gelb
	 */
	private int yellowCount;

	/** Zähler für Services mit der Farbe grün
	 */
	private int greenCount;

	/** Server-ID
	 */
	private int serverId;

	/** Status-ID
	 */
	private Integer statusId = new Integer(0);

	/** Creates a new instance
	 */
	public AuditMonitorServer(int serverId) {
		this.serverId = serverId;
		readColorCodes();
	}

	/** Fügt ein Objekt, dass das ServiceAudit-Interface
	 * implementiert, zum Vector v hinzu
	 */
	public void add(ServiceAudit o) {
		if (o != null)
			v.add(o);
	}

	public void analyse() {
		Iterator i = v.iterator();
		while (i.hasNext()) {
			ServiceAudit o = (ServiceAudit) i.next();
			if (o.isServiceGreen())
				greenCount++;
			else if (o.isServiceYellow())
				yellowCount++;
			else if (o.isServiceOrange())
				orangeCount++;
			else if (o.isServiceRed())
				redCount++;
			else if (o.isServiceBlack())
				blackCount++;
		}
	}

	public void chooseColor() {

		analyse();

		if (greenCount > 0) {
			statusId = (Integer) status.get("green");
			setMessage("Server AVAILABLE and OK");
		}

		if (yellowCount > greenCount) {
			statusId = (Integer) status.get("yellow");
			setMessage("Server AVAILABLE and has SMALL LOAD");
		}

		if (orangeCount > yellowCount) {
			statusId = (Integer) status.get("orange");
			setMessage("Server AVAILABLE and has MEDIUM LOAD");
		}

		if (redCount > 0) {
			statusId = (Integer) status.get("red");
			setMessage("Server AVAILABLE and has BIG LOAD or BIG PROBLEMS");
		}

		if (blackCount > 0) {
			statusId = (Integer) status.get("black");
			setMessage("Server UNAVAILABLE");
		}

	}

	/** OVERDIDDEN
	 * @see com.wanci.ncc.ServiceMonitor#dumpStatus()
	 */
	public void dumpStatus() {
		System.out.println("GREEN=" + greenCount);
		System.out.println("YELLOW=" + yellowCount);
		System.out.println("ORANGE=" + orangeCount);
		System.out.println("RED=" + redCount);
		System.out.println("BLACK=" + blackCount);
	}

	public String toSqlUpdate() {

		return "UPDATE SrvServer"
			+ " SET StatusID = "
			+ statusId
			+ ", StatusMessage = '"
			+ getMessage()
			+ "' WHERE ID = "
			+ serverId;

	}

}
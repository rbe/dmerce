/*
 * Created on 01.07.2003
 */
package com.wanci.ncc.audit;

import java.sql.ResultSet;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.dmerce.kernel.Boot;

/**
 * Audit Class for processing Scanner results
 * @author pg
 * @author mm
 * @version $Id: ScannerAudit.java,v 1.1 2004/02/02 09:41:53 rb Exp $
 */
public class ScannerAudit implements ScannerAuditInterface {

	/**
	 * Erreichbarkeit der Datenbank
	 */
	private boolean databaseAvailable = false;

	/**
	 * gewöhnliche Fehlersuch-Variable
	 */
	private boolean DEBUG = false;

	/**
	 * database handler
	 */
	private Database jdbcDatabase;

	/**
	 * 
	 */
	private int open = 0;
	
	/**
	 * 
	 */
	private int total = 0;

	/**
	 * Constructor
	 * stellt die Verbindung zur Datenbank her
	 */
	public ScannerAudit() throws XmlPropertiesFormatException {
		
		try {
			jdbcDatabase = DatabaseHandler.getDatabaseConnection("test1");
			jdbcDatabase.openConnection();
			databaseAvailable = true;
		}
		catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}
	}

	/**
	 * sql Abfrage eines angegebenen Zeitraums, dabei wird mit Hilfe der
	 * Parameter ip, port, begin, end der SELECT-Befehl gesetzt.
	 * Es wird ein Vector, in dem jeweils ein Vector status und ein Vector
	 * timest, enthalten sind, zurueckgegeben 
	 */
	public Vector getStatus(String ip, int port, String begin, String end) {

		Vector v = new Vector();
		Vector vstatus = new Vector();
		Vector vtimestamp = new Vector();

		if (databaseAvailable) {

			try {
				String sql =
					"SELECT status, timest FROM portscanview WHERE ipaddress='"
						+ ip
						+ "' AND port="
						+ port
						+ " AND (timest BETWEEN '"
						+ begin
						+ "' AND '"
						+ end
						+ "')";
				
				ResultSet rs = jdbcDatabase.executeQuery(sql);

				while (rs.next()) {
					
					vstatus.addElement(new String(rs.getString("status")));
					Date date = rs.getDate("timest");
					vtimestamp.addElement(date);
				}
				
				v.addElement(new Vector(vstatus));
				v.addElement(new Vector(vtimestamp));
				
			}
			catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();
			}
		}
		else {

			if (DEBUG)
				System.out.println(
					"Datenbank ist zur Zeit nicht erreichbar...");
		}
		// Vector mit status und timestamp im Vector v
		return v;
	}

	/**
	 * Berechnet die Erreichbarkeit des gescannten Hosts in einen Prozentwert.
	 * 
	 */
	public double getPercentAvailability(Vector vector) {

		double percentage = 0;
		
		Iterator iterator = ((Vector) vector.get(0)).iterator();
		System.out.println(((Vector) vector.get(0)).size());

		while (iterator.hasNext()) {

			if (((String) iterator.next()).equals("open")) {
				open++;
			}
			total++;
			
		}
		try {
			percentage = open / total * 100;
		}
		catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}

		return percentage;
	}
	
	/**
	 * 
	 * @return offene Ports
	 */
	public int getOpenPorts() {
		
		return open;
	}

	/**
	 * 
	 * @return alle Ports, die im Vector sind
	 */
	public int getAllPorts() {
		
		return total;
	}

	public static void main(String args[])
		throws XmlPropertiesFormatException {

		Boot.printCopyright("AUDITSCANNER");

		ScannerAudit as = new ScannerAudit();
		//as.getStatus("10.48.35.3", 80, "01. Dec 02", "01. Aug 03");
		double p = as.getPercentAvailability(as.getStatus("10.48.35.3", 80, "01. Dec 02", "01. Aug 03"));
		int o = as.getOpenPorts();
		int t = as.getAllPorts();
		System.out.println("Erreichbarkeit: " + p + "%");
		System.out.println("offene Ports  : " + o);
		System.out.println("alle Ports  : " + t);
	}
}

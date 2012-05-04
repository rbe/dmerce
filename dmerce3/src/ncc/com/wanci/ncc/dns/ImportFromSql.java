/*
 * Created on 22.04.2003
 *  
 */
package com.wanci.ncc.dns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import com.wanci.dmerce.exceptions.ResourceRecordInvalidException;
import com.wanci.dmerce.exceptions.ValidatorException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: ImportFromSql.java,v 1.2 2004/02/28 22:39:42 rb Exp $
 * 
 * Import nameserver and zone objects from SQL database into nameserver objects
 *  
 */
public class ImportFromSql {

	/**
	 * Debug flag
	 */
	private boolean DEBUG = false;

	/**
	 * JDBC database connection
	 */
	private Database d;

	/**
	 * Count processed nameservers
	 */
	int nameserverCount;

	/**
	 * Count processed zones
	 */
	int zonesCount;

	/**
	 * Vector that holds generated nameservers
	 */
	private Vector dns = new Vector();

	public ImportFromSql() {
		d = DatabaseHandler.getDatabaseConnection("ncc");
	}

	/**
	 * Initialize ImportFromSql (connect to database, ...)
	 * 
	 * @throws SQLException
	 */
	public void init() throws SQLException {
		d.openConnection();
	}

	/**
	 * Deinitialize ImportFromSql (disconnect from database, ...)
	 * 
	 * @throws SQLException
	 */
	public void deinit() throws SQLException {
		d.closeConnection();
	}

	/**
	 * Return iterator over generated nameservers
	 * 
	 * @return Iterator
	 */
	public Iterator getNameservers() {
		return dns.iterator();
	}

	/**
	 * @return
	 */
	public int getNameserverCount() {
		return nameserverCount;
	}

	/**
	 * @return
	 */
	public int getZonesCount() {
		return zonesCount;
	}

	/**
	 * @param zoneName
	 * @return @throws
	 *         ResourceRecordInvalidException
	 * @throws SQLException
	 */
	private Zone processZone(String zoneName)
		throws ResourceRecordInvalidException, SQLException {

		Zone z = new Zone(zoneName);

		// Nameservers
		ResultSet rs1 =
			d.executeQuery(
				"SELECT nameserver, ipaddress, zonecemail,"
					+ " soaserial, soasubserial, soarefresh, soaretry,"
					+ " soaexpire, soamaximum, position"
					+ " FROM v_dnszonesns"
					+ " WHERE zonename = '"
					+ zoneName
					+ "' ORDER BY position ASC");

		// Primary nameserver
		rs1.next();
		String pns = rs1.getString(1);
		z.addNameserverRecord(
			new NameserverRecord(pns, rs1.getString(2), rs1.getInt(10)));
		z.setSOA(
			new SOA(
				zoneName,
				pns,
				rs1.getString(3),
				rs1.getDate(4),
				rs1.getInt(5),
				rs1.getInt(6),
				rs1.getInt(7),
				rs1.getInt(8),
				rs1.getInt(9)));

		// Secondary nameservers
		while (rs1.next()) {
			z.addNameserverRecord(
				new NameserverRecord(
					rs1.getString(1),
					rs1.getString(2),
					rs1.getInt(10)));
		}

		// Resource records
		ResultSet rs =
			d.executeQuery(
				"SELECT recordname, recordtype, recordvalue, mxprio"
					+ " FROM v_dnszonesrecords"
					+ " WHERE zonename = '"
					+ zoneName
					+ "'"
					+ " ORDER BY recordname ASC");

		try {

			while (rs.next()) {

				String recordName = rs.getString(1);
				String recordType = rs.getString(2);
				String recordValue = rs.getString(3);
				int mxPrio = rs.getInt(4);

				switch (recordType.charAt(0)) {

					case 'A' :
						z.addResourceRecord(
							new NameToAddressRecord(recordName, recordValue));
						break;

					case 'C' :
						z.addResourceRecord(
							new CanonicalNameRecord(recordName, recordValue));
						break;

					case 'M' :
						z.addResourceRecord(
							new MailExchangerRecord(recordValue, mxPrio));
						break;

					default :
						LangUtil.consoleDebug(
							DEBUG,
							"Record type '" + recordType + "' unknown!");

				}

			}

		}
		catch (ResourceRecordInvalidException rrie) {
			LangUtil.consoleDebug(DEBUG, rrie.getMessage());
		}
		catch (ValidatorException ve) {
			LangUtil.consoleDebug(DEBUG, ve.getMessage());
		}

		return z;

	}

	/**
	 * @param n
	 * @throws ResourceRecordInvalidException
	 * @throws SQLException
	 */
	private void processZonesOfNameserver(Nameserver nameserver)
		throws ResourceRecordInvalidException, SQLException {

		// Get zones of a nameserver
		ResultSet rs =
			d.executeQuery(
				"SELECT zonename, position"
					+ " FROM v_dnszonesns"
					+ " WHERE nameserver = '"
					+ nameserver.getName()
					+ "'");

		while (rs.next()) {

			String zoneName = rs.getString(1);
			int position = rs.getInt(2);

			LangUtil.consoleDebug(
				DEBUG,
				"Processing zone (" + position + ") '" + zoneName + "'");

			Zone z = processZone(zoneName);

			if (position == 1) {

				nameserver.addPrimaryZone(z);
				zonesCount++;

				LangUtil.consoleDebug(
					DEBUG,
					"Adding primary zone "
						+ zoneName
						+ " to nameserver "
						+ nameserver.getName());
				LangUtil.consoleDebug(DEBUG, z.getBindString());

			}
			else {

				nameserver.addSecondaryZone(z);

				LangUtil.consoleDebug(
					DEBUG,
					"Adding secondary zone "
						+ zoneName
						+ " to nameserver "
						+ nameserver.getName());

			}

		}

	}

	/**
	 * @throws ResourceRecordInvalidException
	 * @throws UnknownHostException
	 * @throws SQLException
	 */
	public void processNameservers()
		throws ResourceRecordInvalidException, UnknownHostException, SQLException {

		// Get nameservers
		ResultSet rs = d.executeQuery("SELECT name, ipaddress FROM t_dnsns");
		while (rs.next()) {

			String ns = rs.getString(1);
			LangUtil.consoleDebug(DEBUG, "Processing nameserver '" + ns + "'");

			Nameserver n = new Nameserver(ns);
			n.addListenOn(InetAddress.getByName(rs.getString(2)));
			processZonesOfNameserver(n);

			// Add nameserver to vector
			dns.add(n);
			nameserverCount++;

		}

	}

	/**
	 * @param debug
	 */
	public void setDebug(boolean debug) {
		DEBUG = debug;
	}

}
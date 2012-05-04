/*
 * Created on Nov 11, 2003
 *  
 */
package com.wanci.ncc.dns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Iterator;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version ${Id}
 * 
 * Classes that hold information about nameservers, zones, records are exported
 * into a 1[NCC] database schema
 *  
 */
public class ExportToSql {

	/**
	 *  
	 */
	boolean DEBUG = true;

	/**
	 *  
	 */
	Database jdbcDatabase;

	/**
	 *  
	 */
	CallableStatement assignZoneToDns;
	CallableStatement assignZoneToMasterPerson;
	CallableStatement createZone;
	CallableStatement createNameserver;
	CallableStatement createARecord;
	CallableStatement createCnameRecord;
	CallableStatement createMxRecord;

	/**
	 * @param jdbcDatabase
	 */
	public ExportToSql(Database jdbcDatabase) {
		this.jdbcDatabase = jdbcDatabase;
	}

	/**
	 * @param zone
	 * @throws UnknownHostException
	 * @throws SQLException
	 */
	private void assignZoneToNameserver(Zone zone)
		throws UnknownHostException, SQLException {

		// Assign zone to dns servers
		assignZoneToDns.setString(1, zone.getName());
		Iterator i = zone.getNameserverRecordIterator();
		while (i.hasNext()) {

			NameserverRecord n = (NameserverRecord) i.next();

			// Create nameserver
			nameserverToDatabase(new Nameserver(n.getName()));

			LangUtil.consoleDebug(
				DEBUG,
				"Assigning zone "
					+ zone.getName()
					+ " to nameserver "
					+ n.getName()
					+ " at position "
					+ n.getPosition());

			assignZoneToDns.setInt(2, n.getPosition());
			assignZoneToDns.setString(3, n.getName());
			assignZoneToDns.execute();

		}

	}

	/**
	 * @param zone
	 */
	private void assignZoneToMasterPerson(Zone zone, String type)
		throws SQLException {

		assignZoneToMasterPerson.setString(1, zone.getName());
		assignZoneToMasterPerson.setString(2, type);
		assignZoneToMasterPerson.execute();

	}

	/**
	 * @throws SQLException
	 */
	void deinit() throws SQLException {

		createZone.close();
		createNameserver.close();
		assignZoneToDns.close();
		createARecord.close();
		createCnameRecord.close();
		createMxRecord.close();
		jdbcDatabase.closeConnection();

	}

	/**
	 * @throws SQLException
	 */
	void init() throws DmerceException {

		try {

			jdbcDatabase.openConnection();

			assignZoneToDns =
				jdbcDatabase.getCallableStatement(
					"{call sp_assignzonetodns(?, ?, ?)}");

			assignZoneToMasterPerson =
				jdbcDatabase.getCallableStatement(
					"{call sp_assignzonetoperson(?, 'of Desaster', ?)}");

			createZone =
				jdbcDatabase.getCallableStatement(
					"{call sp_createzone(?, ?, ?, ?, ?, ?, ?)}");

			createNameserver =
				jdbcDatabase.getCallableStatement(
					"{call sp_createnameserver(?, ?)}");

			createARecord =
				jdbcDatabase.getCallableStatement(
					"{call sp_createarecord(?, ?, ?)}");

			createCnameRecord =
				jdbcDatabase.getCallableStatement(
					"{call sp_createcnamerecord(?, ?, ?)}");

			createMxRecord =
				jdbcDatabase.getCallableStatement(
					"{call sp_createmxrecord(?, ?, ?)}");

		}
		catch (SQLException e) {
			throw new DmerceException(e.getCause() + ": " + e.getMessage());
		}

	}

	/**
	 * @param nameserver
	 * @throws UnknownHostException
	 * @throws SQLException
	 */
	void nameserverToDatabase(Nameserver nameserver)
		throws UnknownHostException, SQLException {

		LangUtil.consoleDebug(
			DEBUG,
			"Creating nameserver " + nameserver.getName());

		createNameserver.setString(1, nameserver.getName());
		createNameserver.setString(
			2,
			(InetAddress.getByName(nameserver.getName())).getHostAddress());
		createNameserver.execute();

	}

	/**
	 * Enable or disable debuging output
	 * 
	 * @param debug
	 */
	void setDebug(boolean debug) {
		DEBUG = debug;
	}

	/**
	 * @param zone
	 * @throws UnknownHostException
	 * @throws SQLException
	 */
	void zoneToDatabase(Zone zone) throws UnknownHostException, SQLException {

		Iterator i;
		SOA soa = zone.getSOA();

		// Create zone
		LangUtil.consoleDebug(DEBUG, "Creating zone " + zone.getName());
		createZone.setString(1, zone.getName());
		createZone.setDate(2, new Date(new java.util.Date().getTime()));
		createZone.setInt(3, soa.getRefresh());
		createZone.setInt(4, soa.getRetry());
		createZone.setInt(5, soa.getExpire());
		createZone.setInt(6, soa.getNegativeCachingTtl());
		createZone.setInt(7, soa.getDefaultTtl());
		createZone.execute();

		assignZoneToNameserver(zone);

		// Create records
		i = zone.getResourceRecordIterator();
		while (i.hasNext()) {

			Record rr = (Record) i.next();

			LangUtil.consoleDebug(
				DEBUG,
				"Creating record ("
					+ rr.getClass()
					+ ") in zone "
					+ zone.getName()
					+ ": name: "
					+ rr.getName()
					+ ", ident: "
					+ rr.getIdentifier()
					+ ", prio: "
					+ rr.getPriority()
					+ ", value: "
					+ rr.getValue());

			if (rr.getIdentifier().equalsIgnoreCase("MX")) {
				createMxRecord.setString(1, zone.getName());
				createMxRecord.setInt(2, rr.getPriority());
				createMxRecord.setString(3, rr.getValue());
				createMxRecord.execute();
			}
			else if(rr.getIdentifier().equalsIgnoreCase("A")) {
				createARecord.setString(1, zone.getName());
				createARecord.setString(2, rr.getName());
				createARecord.setString(3, rr.getValue());
				createARecord.execute();
			}
			else if(rr.getIdentifier().equalsIgnoreCase("CNAME")) {
				createCnameRecord.setString(1, zone.getName());
				createCnameRecord.setString(2, rr.getName());
				createCnameRecord.setString(3, rr.getValue());
				createCnameRecord.execute();
			}

			assignZoneToMasterPerson(zone, null);
			assignZoneToMasterPerson(zone, "adminc");
			assignZoneToMasterPerson(zone, "techc");
			assignZoneToMasterPerson(zone, "zonec");

		}

	}

}
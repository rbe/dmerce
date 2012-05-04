/*
 * Created on 26.04.2003
 *
 */
package com.wanci.ncc.dns;

import java.util.Iterator;
import java.util.Vector;

/**
 * @author rb
 * @version $Id: Zone.java,v 1.1 2004/02/02 09:41:47 rb Exp $
 *
 * Representiert eine DNS Zone:
 *
 * - SOA
 * - Resource Records
 *
 */
public class Zone {

	/**
	 *
	 */
	private SOA soa;

	/**
	 *
	 */
	private String name;

	/**
	 *
	 */
	private String adminCHandle;

	/**
	 *
	 */
	private String techCHandle;

	/**
	 *
	 */
	private String zoneCHandle;

	/**
	 * Resource Records
	 */
	private Vector nameserverRecords = new Vector();
	private int nameserverRecordsCounter = 0;
	private Vector resourceRecords = new Vector();

	/**
	 *
	 */
	private int nccZoneId;

	/**
	 *
	 * @param name
	 */
	public Zone(String name) {
		this.name = name;
	}

	public void addNameserverRecord(NameserverRecord nameserverRecord) {
		nameserverRecords.add(nameserverRecord);
		nameserverRecordsCounter++;
	}

	/**
	 *
	 * @param resourceRecord
	 */
	public void addResourceRecord(RecordImpl resourceRecord) {
		resourceRecords.add(resourceRecord);
	}

	/**
	 * Returns a string that represents a zone file for BIND nameservers
	 * SOA-Record, NS-Records, IN-Records
	 *
	 * @return String Zone file representation for BIND nameservers
	 */
	public String getBindString() {

		if (nameserverRecordsCounter < 2)
			return null;

		StringBuffer sb = new StringBuffer();
		Iterator i;

		sb.append(soa.getBindString() + "\n");

		i = nameserverRecords.iterator();
		while (i.hasNext()) {

			NameserverRecord nsr = (NameserverRecord) i.next();
			sb.append(nsr.getBindString() + "\n");

		}

		i = resourceRecords.iterator();
		while (i.hasNext()) {

			RecordImpl rr = (RecordImpl) i.next();
			sb.append(rr.getBindString() + "\n");

		}

		return sb.toString();

	}

	/**
	 * 
	 * @return
	 */
	public Iterator getResourceRecordIterator() {
		return resourceRecords.iterator();
	}

	/**
	 * 
	 * @return
	 */
	public Iterator getNameserverRecordIterator() {
		return nameserverRecords.iterator();
	}

	/**
	 *
	 * @param handle
	 */
	public void setAdminC(String handle) {
		adminCHandle = handle;
	}

	/**
	 * @return
	 */
	public String getName() {
		if (name.endsWith("."))
			return name.substring(0, name.length() - 1);
		else
			return name;
	}

	public SOA getSOA() {
		return soa;
	}

	/**
	 * @return
	 */
	public NameserverRecord getNameserverRecord(int i) {
		return (NameserverRecord) nameserverRecords.get(i);
	}

	/**
	 *
	 * @param soa
	 */
	public void setSOA(SOA soa) {
		this.soa = soa;
	}

	/**
	 *
	 * @param handle
	 */
	public void setTechC(String handle) {
		techCHandle = handle;
	}

	/**
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 *
	 * @param nccZoneId
	 */
	public void setNccZoneId(int nccZoneId) {
		this.nccZoneId = nccZoneId;
	}

	/**
	 *
	 * @param handle
	 */
	public void setZoneC(String handle) {
		zoneCHandle = handle;
	}

}
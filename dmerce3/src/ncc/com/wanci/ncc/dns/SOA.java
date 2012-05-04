/*
 * Created on 26.04.2003
 *
 */
package com.wanci.ncc.dns;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.wanci.java.LangUtil;

/**
 * @author rb
 *
 * Start of Authority
 *
 */
public class SOA {

	/**
	 *
	 */
	private String zoneName;

	/**
	 *
	 */
	private String primaryNameserver;

	/**
	 *
	 */
	private String zoneContact;

	/**
	 *
	 */
	private Date serial;

	/**
	 *
	 */
	private int subserial = 1;

	/**
	 * Default TTL for zone
	 */
	private int defaultTtl = 10800;

	/**
	 *
	 */
	private int refresh = 10800;

	/**
	 *
	 */
	private int retry = 3600;

	/**
	 *
	 */
	private int expire = 3600000;

	/**
	 *
	 */
	private int negativeCachingTtl = 4 * 24 * 3600;

	/**
	 * 
	 * @param zoneName
	 * @param primaryNameserver
	 * @param zoneContact
	 */
	public SOA(String zoneName, String primaryNameserver, String zoneContact) {
		this.zoneName = zoneName;
		this.primaryNameserver = primaryNameserver;
		this.zoneContact = zoneContact;
	}

	/**
	 * 
	 * @param zoneName
	 * @param primaryNameserver
	 * @param zoneContact
	 * @param serial
	 * @param subserial
	 * @param refresh
	 * @param retry
	 * @param expire
	 * @param maximum
	 */
	public SOA(
		String zoneName,
		String primaryNameserver,
		String zoneContact,
		Date serial,
		int subserial,
		int refresh,
		int retry,
		int expire,
		int negativeCachingTtl) {
			
		this.zoneName = zoneName;
		this.primaryNameserver = primaryNameserver;
		this.zoneContact = zoneContact;
		this.serial = serial;
		this.subserial = subserial;
		this.refresh = refresh;
		this.retry = retry;
		this.expire = expire;
		this.negativeCachingTtl = negativeCachingTtl;
		
	}

	/**
	 * Liefert einen SOA, wie er in einem Zonfile zu
	 * stehen hat
	 *
	 * $TTL 86400
	 * 1ci.com.         IN      SOA     irb.ns1.1xsp.com.       zone-c.bensmann.com. (
	 *               2003042401      ; serial
	 *               10800           ; refresh
	 *               3600            ; retry
	 *               3600000         ; expire
	 *               4d )            ; Negative caching TTL
	 *
	 * @return
	 */
	public String getBindString() {

		Calendar c = Calendar.getInstance(new Locale("de"));
		if (serial != null)
			c.setTime(serial);

		DecimalFormat d = new DecimalFormat("00");

		String ser =
			c.get(Calendar.YEAR)
				+ d.format(c.get(Calendar.MONTH) + 1)
				+ d.format(c.get(Calendar.DAY_OF_MONTH))
				+ d.format(subserial);

		return "$TTL "
			+ defaultTtl
			+ "\n"
			+ LangUtil.ensureStringWithDotAtEnd(zoneName)
			+ "\tIN\tSOA\t"
			+ LangUtil.ensureStringWithDotAtEnd(primaryNameserver)
			+ "\t"
			+ LangUtil.ensureStringWithDotAtEnd(zoneContact.replace('@', '.'))
			+ " ("
			+ "\n\t\t"
			+ ser
			+ "\t\t; serial\n\t\t"
			+ refresh
			+ "\t\t\t; refresh\n\t\t"
			+ retry
			+ "\t\t\t; retry\n\t\t"
			+ expire
			+ "\t\t\t; expire\n\t\t"
			+ negativeCachingTtl
			+ " )\t\t; Negative caching TTL";

	}

	/**
	 * @return
	 */
	public int getDefaultTtl() {
		return defaultTtl;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getExpire() {
		return expire;
	}

	/**
	 * @return
	 */
	public String getPrimaryNameserver() {
		return primaryNameserver;
	}

	/**
	 * @return
	 */
	public int getRefresh() {
		return refresh;
	}

	/**
	 * @return
	 */
	public Date getSerial() {
		return serial;
	}

	/**
	 * @return
	 */
	public int getSubserial() {
		return subserial;
	}

	/**
	 * @return
	 */
	public int getRetry() {
		return retry;
	}

	/**
	 * @return
	 */
	public int getNegativeCachingTtl() {
		return negativeCachingTtl;
	}

	/**
	 * @return
	 */
	public String getZoneContact() {
		return zoneContact;
	}

	public void setDefaultTtl(int defaultTtl) {
		if (defaultTtl > 0)
			this.defaultTtl = defaultTtl;
	}

	/**
	 *
	 * @param expire
	 */
	public void setExpire(int expire) {
		this.expire = expire;
	}

	/**
	 *
	 * @param name
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	/**
	 * @param string
	 */
	public void setPrimaryNameserver(String primaryNameserver) {
		this.primaryNameserver = primaryNameserver;
	}

	/**
	 * @param i
	 */
	public void setRefresh(int i) {
		refresh = i;
	}

	/**
	 *
	 */
	public void setSerial(Date serial) {
		this.serial = serial;
	}

	/**
	 * Subserial is a two-digit number that will be appended to serial
	 * number (= YYYYMMDD from actual date)
	 *
	 * @param i
	 */
	public void setSubserial(int i) {
		subserial = i;
	}

	/**
	 * @param i
	 */
	public void setRetry(int i) {
		retry = i;
	}

	/**
	 * @param i
	 */
	public void setNegativeCachingTtl(int i) {
		negativeCachingTtl = i;
	}

	/**
	 * @param string
	 */
	public void setZoneContact(String string) {
		zoneContact = string;
	}

}
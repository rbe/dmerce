/*
 * Created on Aug 7, 2003
 *
 */
package com.wanci.ncc.dns;

import com.wanci.dmerce.exceptions.ResourceRecordInvalidException;

/**
 * @author rb
 * @version $Id: MailExchangerRecord.java,v 1.1 2004/02/02 09:41:47 rb Exp $
 *
 *
 *
 */
public class MailExchangerRecord extends RecordImpl {

	public static int HIGH_PRIORITY = 10;

	public static int MEDIUM_PRIORITY = 20;

	public static int LOW_PRIORITY = 30;

	/**
	 *
	 */
	private int priority;

	/**
	 * @param name
	 * @param value
	 * @throws ResourceRecordInvalidException
	 */
	public MailExchangerRecord(String value)
		throws ResourceRecordInvalidException {

		super("", value);

	}

	/**
	 * 
	 * @param value
	 * @param priority
	 * @throws ResourceRecordInvalidException
	 */
	public MailExchangerRecord(String value, int mxPriority)
		throws ResourceRecordInvalidException {

		super("", value);
		this.priority = mxPriority;

	}

	/**
	 *
	 * @param name
	 * @param value
	 * @param ttl
	 * @throws ResourceRecordInvalidException
	 */
	public MailExchangerRecord(String value, int ttl, int mxPriority)
		throws ResourceRecordInvalidException {

		super("", value, ttl);
		this.priority = mxPriority;

	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.dns.Record#checkName(java.lang.String)
	 */
	public String checkName(String name) {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.dns.Record#checkValue(java.lang.String)
	 */
	public String checkValue(String value) {
		return value;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.dns.Record#getIdentifier()
	 */
	public String getIdentifier() {
		return "MX";
	}
	
	public int getPriority() {
		return priority;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.dns.Record#getBindString()
	 */
	public String getBindString() {

		StringBuffer sb = new StringBuffer();

		if (ttl > 0 && name != null && value != null) {
			sb.append(
				name + "\t" + ttl + "\tIN\tMX\t" + priority + " " + value);
			return sb.toString();
		}
		else
			return null;

	}

	/**
	 *
	 * @param priority
	 */
	public void setMxPriority(int mxPriority) {
		this.priority = mxPriority;
	}

}

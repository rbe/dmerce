/*
 * Created on 26.04.2003
 *
 */
package com.wanci.ncc.dns;

import com.wanci.dmerce.exceptions.ResourceRecordInvalidException;

/**
 * @author rb
 * @version $Id: RecordImpl.java,v 1.1 2004/02/02 09:41:47 rb Exp $
 *
 */
public abstract class RecordImpl implements Record {

	protected static int STANDARD_TTL = 10800;

	/**
	 * 
	 */
	protected String name;

	/**
	 * 
	 */
	protected String value;

	/**
	 * Time to Live
	 * Standard ist 10800 Sekunden = 3 Stunden
	 */
	protected int ttl = 10800;

	/**
	 * 
	 * @param name
	 * @param value
	 */
	public RecordImpl(String name, String value)
		throws ResourceRecordInvalidException {

		setName(name);
		setValue(value);

	}

	/**
	 * 
	 * @param name
	 * @param value
	 * @param ttl
	 */
	public RecordImpl(String name, String value, int ttl)
		throws ResourceRecordInvalidException {

		setName(name);
		setValue(value);
		setTtl(ttl);

	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.dns.Record#checkName()
	 */
	public abstract String checkName(String name);

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.dns.Record#checkValue()
	 */
	public abstract String checkValue(String value);

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.dns.Record#zoneFileRepresentation()
	 */
	public abstract String getBindString();

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.dns.Record#getName()
	 */
	public String getName() {
		if (name.endsWith("."))
			return name.substring(0, name.length() - 1);
		else
			return name;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.dns.Record#getTtl()
	 */
	public int getTtl() {
		return ttl;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.dns.Record#getValue()
	 */
	public String getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.dns.Record#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.dns.Record#setTtl(int)
	 */
	public void setTtl(int ttl) {
		if (ttl > 0)
			this.ttl = ttl;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.dns.Record#setValue(java.lang.String)
	 */
	public void setValue(String value) throws ResourceRecordInvalidException {
		this.value = checkValue(value);
	}

}
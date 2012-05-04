/*
 * Created on Aug 6, 2003
 *
 */
package com.wanci.ncc.dns;

import com.wanci.dmerce.exceptions.ResourceRecordInvalidException;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: NameserverRecord.java,v 1.1 2004/02/02 09:41:47 rb Exp $
 *
 *
 *
 */
public class NameserverRecord extends RecordImpl {
	
	int position;

	/**
	 * @param name
	 * @param value
	 * @throws ResourceRecordInvalidException
	 */
	public NameserverRecord(String name, String value, int position)
		throws ResourceRecordInvalidException {

		super(name, value);
		this.position = position;

	}

	/**
	 * @param name
	 * @param value
	 * @param nttl
	 */
	public NameserverRecord(String name, String value, int position, int nttl)
		throws ResourceRecordInvalidException {

		super(name, value, nttl);
		this.position = position;

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
		return "NS";
	}
	
	public int getPriority() {
		return -1;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.dns.Record#getBindString()
	 */
	public String getBindString() {

		StringBuffer sb = new StringBuffer();

		if (ttl > 0 && name != null) {

			sb.append(
				"\t"
					+ ttl
					+ "\tIN\tNS\t"
					+ LangUtil.ensureStringWithDotAtEnd(name));

			return sb.toString();

		}
		else
			return null;

	}
	
	/**
	 * 
	 * @return
	 */
	public int getPosition() {
		return position;
	}

}
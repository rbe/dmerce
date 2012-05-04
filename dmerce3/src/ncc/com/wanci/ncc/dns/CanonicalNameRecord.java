/*
 * Created on Aug 6, 2003
 *
 */
package com.wanci.ncc.dns;

import com.wanci.dmerce.exceptions.ResourceRecordInvalidException;

/**
 * @author rb
 * @version $Id: CanonicalNameRecord.java,v 1.1 2004/02/02 09:41:47 rb Exp $
 *
 *
 *
 */
public class CanonicalNameRecord extends RecordImpl {
    
    /**
     * @param name
     * @param value
     * @throws ResourceRecordInvalidException
     */
    public CanonicalNameRecord(String name, String value)
    throws ResourceRecordInvalidException {
        
        super(name, value);
        
    }
    
    /**
     *
     * @param name
     * @param value
     * @param ttl
     * @throws ResourceRecordInvalidException
     */
    public CanonicalNameRecord(String name, String value, int ttl)
    throws ResourceRecordInvalidException {
        
        super(name, value, ttl);
        
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
		return "CNAME";
	}

	public int getPriority() {
		return -1;
	}

    public String getBindString() {
        
        StringBuffer sb = new StringBuffer();
        
        if (ttl > 0 && name != null && value != null) {
            sb.append(name + "\t" + ttl + "\tIN\tCNAME\t" + value);
            return sb.toString();
        }
        else
            return null;
        
    }
    
}
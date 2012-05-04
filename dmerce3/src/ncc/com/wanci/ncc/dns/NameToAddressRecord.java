/*
 * Created on Aug 6, 2003
 *
 */
package com.wanci.ncc.dns;

import com.wanci.dmerce.exceptions.ResourceRecordInvalidException;
import com.wanci.dmerce.exceptions.ValidatorException;
import com.wanci.ncc.dns.validator.AddressToNameRecordValidator;

/**
 * @author rb
 * @version $Id: NameToAddressRecord.java,v 1.1 2004/02/02 09:41:47 rb Exp $
 *
 * Zone "muenster1-1.de.1ci.net":
 *
 * dhcp-17  IN  A   192.168.1.17
 *
 */
public class NameToAddressRecord extends RecordImpl {
    
    /**
     * @param name
     * @param value
     * @throws ResourceRecordInvalidException
     */
    public NameToAddressRecord(String name, String value)
    throws ResourceRecordInvalidException, ValidatorException {
        
        super(name, value);
        new AddressToNameRecordValidator(this).validate();
        
    }
    
    /**
     *
     * @param name
     * @param value
     * @param ttl
     * @throws ResourceRecordInvalidException
     */
    public NameToAddressRecord(String name, String value, int ttl)
    throws ResourceRecordInvalidException, ValidatorException {
        
        super(name, value, ttl);
        new AddressToNameRecordValidator(this).validate();
        
    }
    
    /* (non-Javadoc)
     * @see com.wanci.dmerce.ncc.dns.ResourceRecord#checkName(java.lang.String)
     */
    public String checkName(String name) {
        return name;
    }
    
    /* (non-Javadoc)
     * @see com.wanci.dmerce.ncc.dns.ResourceRecord#checkValue(java.lang.String)
     */
    public String checkValue(String value) {
        return value;
    }
    
	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.dns.Record#getIdentifier()
	 */
	public String getIdentifier() {
		return "A";
	}
	
	public int getPriority() {
		return -1;
	}

    public String getBindString() {

        StringBuffer sb = new StringBuffer();

        if (ttl > 0 && name != null && value != null) {
            sb.append(name + "\t" + ttl + "\tIN\tA\t" + value);
            return sb.toString();
        }
        else
            return null;

    }

}
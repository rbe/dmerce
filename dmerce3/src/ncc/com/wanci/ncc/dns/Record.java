/*
 * Created on Aug 6, 2003
 *
 */
package com.wanci.ncc.dns;

import com.wanci.dmerce.exceptions.ResourceRecordInvalidException;

/**
 * @author rb
 * @version $Id: Record.java,v 1.1 2004/02/02 09:41:47 rb Exp $
 *
 * Ein Resource Record einer Zone
 * SOA, NS, A, PTR, CNAME
 * 
 */
public interface Record {
    
	/**
	 * Gibt einen Record zurueck, wie er in einem BIND-Zonefile stehen muss
	 * @return
	 */
	String getBindString();

    /**
     * 
     * @return
     */
    String getIdentifier();
    
    /**
     * 
     * @return
     */
    String getName();
    
    /**
     * 
     * @return
     */
    int getPriority();
    
    /**
     * 
     * @return
     */
    int getTtl();
    
    /**
     * 
     * @return
     */
    String getValue();
    
    /**
     * Prueft den Namen eines Records auf Richtigkeit
     * @throws ResourceRecordInvalidException
     */
    String checkName(String name) throws ResourceRecordInvalidException;
    
    /**
     * Prueft den Wert eines Records auf Richtigkeit
     * @throws ResourceRecordInvalidException
     */
    String checkValue(String value) throws ResourceRecordInvalidException;
    
    /**
     * 
     * @param name
     */
    void setName(String name) throws ResourceRecordInvalidException;
    
    /**
     * 
     * @param ttl
     */
    void setTtl(int ttl);
    
    /**
     * 
     * @param value
     */
    void setValue(String value) throws ResourceRecordInvalidException;
    
}
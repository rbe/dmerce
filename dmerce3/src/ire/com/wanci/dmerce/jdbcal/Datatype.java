/*
 * Created on Jan 28, 2003
 *
 */
package com.wanci.dmerce.jdbcal;

/**
 * @author rb
 * @version $Id: Datatype.java,v 1.1 2004/03/29 13:39:31 rb Exp $
 *
 */
interface Datatype {
	
	/**
     * 
	 * @return
	 */
    int getLength();
	
	/**
     * 
	 * @return
	 */
    String getMySQLDefinition();
	
	/**
     * 
	 * @return
	 */
    String getOracleDefinition();
	
	/**
     * 
	 * @return
	 */
    int getPrecision();
	
	/**
     * 
	 * @return
	 */
    String getPointbaseDefinition();

	/**
     * 
	 * @return
	 */
    String getPostgreSQLDefinition();
	
	/**
     * 
	 * @param length
	 */
    void setLength(int length);
	
	/**
     * 
	 * @param precision
	 */
    void setPrecision(int precision);
	
}
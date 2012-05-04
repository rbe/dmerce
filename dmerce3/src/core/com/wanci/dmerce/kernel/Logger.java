/*
 * Logger.java
 *
 * Created on 4. Januar 2003, 12:20
 */

package com.wanci.dmerce.kernel;

/**
 *
 * @author  rb
 * @version $Id: Logger.java,v 1.1 2004/03/29 13:39:34 rb Exp $
 * 
 */
public interface Logger {
    
    /**
     * 
     *
     */
    void flush();
    
    /**
     * 
     * @return
     */
    LoggerMessage getMessage();
    
    /**
     * 
     * @param name
     * @return
     */
    int getTypeByName(String name);
    
    /**
     * 
     * @param number
     * @return
     */
    String getTypeByNumber(int number);
    
    /**
     * 
     * @param message
     */
    void putMessage(LoggerMessage message);
    
    /**
     * 
     * @param number
     * @param name
     */
    void registerType(int number, String name);
    
    /**
     * 
     * @param module
     */
    void setModule(String module);
    
    /**
     * 
     * @param beQuiet
     */
    void setBeQuiet(boolean beQuiet);
    
    /**
     * 
     *
     */
    void write();

}
/*
 * LoggerMessage.java
 *
 * Created on 4. Januar 2003, 12:38
 */

package com.wanci.dmerce.kernel;

/**
 *
 * @author  rb
 * @version $Id: LoggerMessage.java,v 1.1 2004/03/29 13:39:34 rb Exp $
 * 
 */
public interface LoggerMessage {
    
    /**
     * 
     * @return
     */
    int getType();
    
    /**
     * 
     * @return
     */
    String getText();
    
    /**
     * 
     * @param type
     */
    void setType(int type);
    
    /**
     * 
     * @param text
     */
    void setText(String text);
    
}
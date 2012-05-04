/*
 * LogfileMessage.java
 *
 * Created on 5. Januar 2003, 15:22
 */

package com.wanci.dmerce.kernel;

import java.util.Date;

/**
 *
 * @author  rb
 * @version $Id: LogfileMessage.java,v 1.1 2004/03/29 13:39:34 rb Exp $
 * 
 */
public class LogfileMessage implements LoggerMessage {

    /**
     * 
     */
    private Date date;
    
    /**
     * 
     */
    private int type;
    
    /**
     * 
     */
    private String text;
    
    /** Creates a new instance of LogfileMessages */
    public LogfileMessage() {
        date = new Date();
    }
    
    /**
     * 
     * @param text
     */
    public LogfileMessage(String text) {
        date = new Date();
        setText(text);
    }
    
    /**
     * 
     * @param type
     * @param text
     */
    public LogfileMessage(int type, String text) {
        date = new Date();
        setType(type);
        setText(text);
    }
    
    /**
     * 
     */
    public int getType() {
        return type;
    }
    
    /**
     * 
     */
    public String getText() {
        return text;
    }
    
    /**
     * 
     */
    public void setType(int type) {
        this.type = type;
    }
    
    /**
     * 
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * 
     */
    public String toString() {
        return date.toString() + " " + type + " " + text;
    }
    
}
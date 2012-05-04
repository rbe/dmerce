/*
 * Logfile.java
 *
 * Created on 21. Dezember 2002, 23:10
 */

package com.wanci.dmerce.kernel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 
 * @author  rb
 * @version $Id: Logfile.java,v 1.1 2004/03/29 13:39:34 rb Exp $
 * 
 */
public class Logfile {
    
    /**
     * 
     */
    private String module;
    
    /**
     * 
     */
    private HashMap messageTypes;
    
    /**
     * 
     */
    private LinkedList messages;
    
    /**
     * 
     */
    private boolean beQuiet = false;
    
    /**
     * 
     */
    private boolean suppressDebug = true;
    
    /**
     * 
     * @param module
     */
    public Logfile(String module) {
        
        messageTypes = new HashMap();
        messages = new LinkedList();
        
        setModule(module);
        
        registerType(1, "DEBUG");
        registerType(2, "INFO");
        registerType(3, "WARNING");
        registerType(4, "ERROR");
        registerType(5, "DUMP");
        
    }
    
    /**
     * 
     *
     */
    private void flush() {
        messages.clear();
    }
    
    /*
    public int getTypeByName(String name) {
        return 1;
    }
    */
    
    /**
     * 
     */
    public String getTypeByNumber(int number) {
        return (String)messageTypes.get(new Integer(number));
    }
    
    /**
     * 
     * @param message
     */
    private void putMessage(LogfileMessage message) {
        if (message != null)
            messages.add(message);
    }
    
    /**
     * 
     * @param message
     */
    public void putDebug(String message) {
        putMessage(new LogfileMessage(1, message));
    }
    
    /**
     * 
     * @param message
     */
    public void putInfo(String message) {
       putMessage(new LogfileMessage(2, message));
    }
    
    /**
     * 
     * @param message
     */
    public void putWarning(String message) {
        putMessage(new LogfileMessage(3, message));
    }
    
    /**
     * 
     * @param message
     */
    public void putError(String message) {
        putMessage(new LogfileMessage(4, message));
    }
    
    /**
     * 
     * @param message
     */
    public void putDump(String message) {
        putMessage(new LogfileMessage(5, message));
    }
    
    /**
     * 
     * @param number
     * @param name
     */
    public void registerType(int number, String name) {
        messageTypes.put(new Integer(number), name);
    }
    
    /**
     * 
     * @param beQuiet
     */
    public void setBeQuiet(boolean beQuiet) {
        this.beQuiet = beQuiet;
    }
    
    /**
     * 
     * @param module
     */
    public void setModule(String module) {

        if (module != null)
            this.module = module;
        else
            this.module = "noModule";

    }
    
    /**
     * 
     * @param suppressDebug
     */
    public void setSuppressDebug(boolean suppressDebug) {
        this.suppressDebug = suppressDebug;
    }
    
    /**
     * 
     *
     */
    public void write() {

        if (!beQuiet) {

            Iterator i = messages.iterator();
            while (i.hasNext()) {

                LogfileMessage m = (LogfileMessage)i.next();
                if (!(suppressDebug && m.getType() == 1))
                    System.out.println(module + " " + m.toString());

            }

        }

        flush();

    }
    
}
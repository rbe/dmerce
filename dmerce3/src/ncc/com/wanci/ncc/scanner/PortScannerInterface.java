/*
 * Created on Jun 3, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.ncc.scanner;


/**
 * @author rb
 * 
 * Interface fuer einen Scanner, der einen bestimmten
 * Port analysiert
 * 
 */
public interface PortScannerInterface {

    /**
     * check if port is closed
     * @return status (true/false)
     */
    boolean isClosed();
    
    /**
     * check if port is open
     * @return status (true/false)
     */
    boolean isOpen();
    
    void discover();
    
    /**
     * 
     * @param in
     */
    boolean setHost(String str);
    
    /**
     * 
     * @param port
     */
    void setPort(int port);

}
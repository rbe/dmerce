/*
 * Created on 1. Januar 2003, 17:47
 * 
 */

package com.wanci.ncc.servicemonitor;

/**
 * Dieses Interface beschreibt einen Dienst auf einem Server
 *
 * @author  rb
 * @version $Id: InetService.java,v 1.1 2004/02/02 09:41:46 rb Exp $
 * 
 */
public interface InetService {
    
    /**
     * Setzt den Namen eines Dienstes (wie z.B. "http")
     * @param inetServiceName Der Name
     */
    void setInetServiceName(String inetServiceName);
    
    /**
     * Setzt die Beschreibung eines Dienstes
     * @param description Die Beschreibung
     */
    void setDescription(String description);
    
    /**
     * Setzt die TCP-Ports des Dienstes
     * @param tcpPorts Der/Die Port/Ports (Nummer)
     */
    void setTcpPorts(int[] tcpPorts);
    
    /**
     * Setzt die UDP-Ports des Dienstes
     * @param udpPort Der/Die Port/Ports (Nummer)
     */
    void setUdpPorts(int[] udpPorts);
    
}
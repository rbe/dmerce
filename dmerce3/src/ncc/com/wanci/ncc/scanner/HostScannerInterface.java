/*
 * Created on Jun 3, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.ncc.scanner;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;

/**
 * @author rb
 * 
 * Scan eines Hosts auf die "Well-Known-Services"
 * 
 * Spaeter soll eine Funktionalitaet implementiert werden,
 * die es ermoelgicht, Services, die nicht auf ihrem
 * Standard-Port laufen, zu erkennen. Zunaechst gehen wir
 * erstmal davon aus, dass ein Host seine Services auf denn
 * Well-Known-Ports laufen laesst. 
 * 
 */
interface HostScannerInterface {

	/**
	 * Scannt einen Host (sprich alle moeglichen Ports auf einer IP)
	 * und speichert die gefundenen Services in einer Liste
	 * (einfach alle Portnummern auflisten)
	 */
    void discover() throws XmlPropertiesFormatException;
    
    /**
     * Ist der gescannte Host "up and running"?
     * @return boolean Ja/Nein
     */
    boolean isHostRunning();

    /**
     * Laeuft das ServiceResource FTP auf dem gescannten Host?
     * @return boolean Ja/Nein
     */
    boolean isHostRunningServiceFtp();

    /**
     * Laeuft das ServiceResource HTTP auf dem gescannten Host?
     * @return boolean Ja/Nein
     */
    boolean isHostRunningServiceHttp();

    /**
     * Laeuft das ServiceResource SSH auf dem gescannten Host?
     * @return boolean Ja/Nein
     */
    boolean isHostRunningServiceSsh();
    
    /**
     * Ist ein Port mit der Nummer "portNumber" auf dem Host
     * geschlossen?
     * 
     * @param portNumber
     * @return boolean Ja/Nein
     */
    boolean isPortClosed(int portNumber);
    
    /**
     * Ist ein Port mit der Nummer "portNumber" auf dem Host
     * offen?
     * 
     * @param portNumber
     * @return boolean Ja/Nein
     */
    boolean isPortOpen(int portNumber);

    /**
     * Setzt die IP-Adresse des zu scannenden Hosts 
     * 
     * @param in
     */
    boolean setHost(String host);
    
}
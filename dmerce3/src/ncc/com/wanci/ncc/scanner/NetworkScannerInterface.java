/*
 * Created on Jun 3, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.ncc.scanner;

import java.util.Vector;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;

/**
 * @author rb
 *
 * Scannt alle IP-Adressen in einem angegebenen Netzwerk
 * - Sollte gethreaded werden, damit auch grosse Netzwerke
 *   schnell erfasst werden (vielleicht ein Thread pro 10 IPs)
 * 
 */
public interface NetworkScannerInterface {
    
    /**
     * Fuehrt den notwendigen Scan des Netzwerks aus. Es wird
     * auf alle moeglichen IPs die Klasse "ScanHost" angewendet.
     * 
     * Es sollen alle gescannten Hosts mit den darauf gefundenen
     * Services erfasst und in einer Datenstruktur gehalten werden
     *
     */
    void discover() throws XmlPropertiesFormatException;
    
    /**
     * Liefert einen Vector mit Objekten zurueck,
     * der alle Hosts enthaelt, die "up and running" sind
     * (koennte/sollte ein "HostScanner"-Objekt sein)
     * 
     * @return Vector Objekte InetAddress
     */
    Vector getHostsRunning();
    
    /**
     * Liefert einen Vector mit Objekten zurueck,
     * der alle Hosts enthaelt, die das ServiceResource FTP bieten
     * (koennte/sollte ein "HostScanner"-Objekt sein)
     * 
     * @return Vector Objekte InetAddress
     */
    Vector getHostsRunningServiceFtp();
    
    /**
     * Liefert einen Vector mit Objekten zurueck,
     * der alle Hosts enthaelt, die das ServiceResource HTTP bieten
     * (koennte/sollte ein "HostScanner"-Objekt sein)
     * 
     * @return Vector Objekte InetAddress
     */
    Vector getHostsRunningServiceHttp();
    
    /**
     * Liefert einen Vector mit Objekten zurueck,
     * der alle Hosts enthaelt, die das ServiceResource Ssh bieten
     * (koennte/sollte ein "HostScanner"-Objekt sein)
     * 
     * @return Vector Objekte InetAddress
     */
    Vector getHostsRunningServiceSsh();
    
    /**
     * Liefert einen Vector mit Objekten zurueck,
     * der alle Hosts enthaelt, die das ServiceResource Ssh bieten und wo
     * man sich einloggen kann (als User dmerce/Passwort dmerce)
     * (koennte/sollte ein "InetAddress"-Objekt sein)
     * 
     * @return Vector Objekte InetAddress
     */
    Vector getHostsWhereLoginPerSshIsPossible();
    
    /**
     * Setzt die Adresse des zu scannenden Netzwerks
     * 
     * @param in
     */
    void setNetwork(String in);

}
/*
 * Created on Jun 3, 2003
 *
 * To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 *
 */
package com.wanci.dmerce.comm;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Vector;

/**
 * @author rb
 * @version $Id: SshInterface.java,v 1.1 2004/06/30 13:53:18 rb Exp $
 *
 * SSH-Kommunikation mit anderen Hosts
 *
 */
interface SshInterface {
    
    /**
     * Konnektiert den Host
     *
     * @param command
     */
    void connect() throws Exception;
    
    /**
     * Beendet die Verbindung mit dem Host
     * @param command
     */
    void disconnect();
    
    /**
     * Fuehrt ein Shell-Kommando aus und gibt das
     * Ergebniss als Vector zurueck, der zeilenweise den Output
     * enthaelt
     *
     * @param command
     * @return Vector Output des Shell-Kommandos
     */
    BufferedReader executeShellCommandAsStream(String command) throws IOException;
    Vector executeShellCommand(String command) throws IOException;
    
    /**
     * Liefert ein File-Objekt, dass die Datei vom Host enthaelt,
     * die per Dateiname angegeben wurde
     *
     * @param filename
     * @return reception of file succeeded? (true/false)
     */
    boolean receiveFile(String remoteFilename, String localFilename);
    
    /**
     * Sendet die Datei, die durch ein File-Objekt angegeben ist
     * auf den Host und speichert diese unter dem angegebenen
     * Dateinamen ab
     *
     * @param file
     * @return sending of file succeeded? (true/false)
     */
    boolean sendFile(File file, String hostFilename);
    
    /**
     * Setzt den Host, auf dem gearbeitet werden soll
     * @param in
     */
    void setInetAddress(InetAddress in);
    
}
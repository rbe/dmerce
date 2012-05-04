/*
 * Created on 1. Januar 2003, 19:01
 * 
 */

package com.wanci.ncc.servicemonitor;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;

import com.wanci.ncc.audit.AuditMonitorInetService;

/**
 * Dieses Interface beschreibt einen überwachten Dienst
 *
 * @author  rb
 * @version $Id: InetServiceMonitor.java,v 1.1 2004/02/02 09:41:46 rb Exp $
 * 
 */

public interface InetServiceMonitor {

	/**
     * Fragt den Dienst ab: wartet die per setResponseTimeout() gesetzte
	 * Zeit ab, ob der Dienst antwortet
	 * @return Gibt true oder false zurück: Dienst antwortet oder antwortet
	 * nicht
	 */
	boolean checkTcp() throws IOException;

	abstract AuditMonitorInetService check();

	/**
     * Schließt den/die TCP Socket(s)
	 * @exception IOException Fehler bei Kommunikation mit dem Socket
	 */
	void closeTcpSockets() throws IOException;

	/**
     * Schließt den/die UDP Socket(s)
	 */
	void closeUdpSockets() throws SocketException;

	/**
     * Initialisieren der Arrays tcpSockets, tcpIns, tcpOuts
	 */
	void initTcpSockets();

	/**
     * Initialisieren des Arrays udpSockets
	 */
	void initUdpSockets();

	/**
     * Öffnet den/die TCP Socket(s)
	 * @exception IOException Fehler bei Kommunikation mit dem Socket
	 * @exception ConnectException Fehler bei Verbindungsaufbau
	 */
	void openTcpSockets() throws IOException, ConnectException;

	/** Öffnet den/die UDP Socket(s)
	 */
	void openUdpSockets() throws SocketException;

	/**
     * Setzt den Request, der an den Server gesendet wird
	 * @params send Request
	 */
	void setTcpSendString(String send);

	/**
     * Setzt die Antwort, die vom Server erwartet wird. Hier wird
	 * nur ein Auszug/Teil der Antwort erwartet
	 * @params answer Erwartete Antwort
	 */
	void setTcpExpectedAnswer(String answer);

}
/*
 * Created on 01.07.2003
 */
package com.wanci.ncc.audit;

import java.util.Vector;


/**
 * Interface for Scanner-Audit Module 
 * responsible for processing results from HostScanner/NetworkScanner
 * 
 * provides toXml-Methods for displaying results as XML and
 * provides SQL-Statements for db-status
 * 
 * ???JDBC-Connection here???
 *   
 * @author pg
 * @author mm
 * @version $Id: ScannerAuditInterface.java,v 1.1 2004/02/02 09:41:53 rb Exp $
 */
public interface ScannerAuditInterface {

	double getPercentAvailability(Vector vector);
	
	Vector getStatus(String ip, int port, String begin, String end);
}

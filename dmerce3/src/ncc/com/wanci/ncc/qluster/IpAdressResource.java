/*
 * Created on 11.07.2003
 *
 */
package com.wanci.ncc.qluster;

import java.net.InetAddress;

/**
 * @author rb
 * @version $Id: IpAdressResource.java,v 1.1 2004/02/02 09:41:50 rb Exp $
 *
 * Cluster IP Address
 * 
 */
public interface IpAdressResource extends Resource {
	
	/**
	 * 
	 * @param in
	 */
	void setIp(InetAddress in);
	
	/**
	 * Legt die Cluster IP auf der Node per
	 * ARP Eintrag an
	 *
	 */
	void createArpEntry();
	
	/**
	 * 
	 *
	 */
	void deleteArpEntry();

}
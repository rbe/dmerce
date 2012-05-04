/*
 * Created on 20.07.2003
 *
 */
package com.wanci.ncc.apache;

import java.net.InetAddress;

/**
 * @author rb
 * @version $Id: VirtualHost.java,v 1.1 2004/02/02 09:41:46 rb Exp $
 *
 *
 *
 */
public interface VirtualHost {
	
	void setAccessLogDestination(String dest);
	
	void setDocumentRoot(String documentRoot);

	void setErrorLogDestination(String dest);
	
	void setIp(InetAddress in);

	void setName();
	
	void setPort(int port);
	
}
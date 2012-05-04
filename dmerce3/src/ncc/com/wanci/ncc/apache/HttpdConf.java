/*
 * Created on 20.07.2003
 *
 */
package com.wanci.ncc.apache;

import com.wanci.ncc.user.Account;

/**
 * @author rb
 * @version $Id: HttpdConf.java,v 1.1 2004/02/02 09:41:46 rb Exp $
 *
 *
 *
 */
public interface HttpdConf {
	
	Account getAccount();
	
	void getPort(int port);
	
	void setUsername(Account account);
	
	void setPort(int port);

}
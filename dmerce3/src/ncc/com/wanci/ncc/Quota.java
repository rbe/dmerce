/*
 * Created on 27.04.2003
 *
 */
package com.wanci.ncc;

import com.wanci.ncc.user.Account;

/**
 * @author rb
 * @version $Id: Quota.java,v 1.1 2004/02/02 09:41:54 rb Exp $
 *
 */
public interface Quota {
	
	void disable();
	
	void enable();
	
	void setFilesystem(String filesystem);
	
	void setHardLimit(int bytes);
    
    void setUser(Account account);

	void setSoftLimit(int bytes);
	
}
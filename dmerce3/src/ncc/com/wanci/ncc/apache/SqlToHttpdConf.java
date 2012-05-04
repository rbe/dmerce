/*
 * Created on 20.07.2003
 *
 */
package com.wanci.ncc.apache;

import com.wanci.ncc.user.Account;

/**
 * @author rb
 * @version $Id: SqlToHttpdConf.java,v 1.1 2004/02/02 09:41:46 rb Exp $
 *
 *
 *
 */
public class SqlToHttpdConf implements HttpdConf {

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.apache.HttpdConf#getAccount()
	 */
	public Account getAccount() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.apache.HttpdConf#getPort(int)
	 */
	public void getPort(int port) {
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.apache.HttpdConf#setUsername(com.wanci.dmerce.ncc.user.Account)
	 */
	public void setUsername(Account account) {
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.apache.HttpdConf#setPort(int)
	 */
	public void setPort(int port) {
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
	}
}

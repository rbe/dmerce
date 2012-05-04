/*
 * Created on 26.04.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.ncc.user;

import java.util.Vector;

import com.wanci.ncc.Quota;
import com.wanci.ncc.mail.Mailbox;

/**
 * @author rb
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class Unix implements Account {

	private String description;

	private String home;

	private String login;

	private Mailbox mailbox;

	private String name;

	private String password;

	private Vector quotas;

	private String shell;

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.user.Account#addQuota(com.wanci.dmerce.ncc.Quota)
	 */
	public void addQuota(Quota quota) {
		quotas.add(quota);
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.user.Account#create()
	 */
	public abstract void create();
	
	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.user.Account#createHomeDir()
	 */
	public void createHomeDir() {
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.user.Account#delete()
	 */
	public abstract void delete();

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.user.Account#deleteHomeDirectory()
	 */
	public void deleteHomeDirectory() {
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.user.Account#disable()
	 */
	public abstract void disable();

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.user.Account#enable()
	 */
	public abstract void enable();

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.user.Account#getMailbox()
	 */
	public Mailbox getMailbox() {
		return mailbox;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.user.Account#setDescription(java.lang.String)
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.user.Account#setHomeDirectory(java.lang.String)
	 */
	public void setHomeDirectory(String home) {
		this.home = home;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.user.Account#setLogin(java.lang.String)
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.user.Account#setMailbox(com.wanci.dmerce.ncc.mail.Mailbox)
	 */
	public void setMailbox(Mailbox mailbox) {
		this.mailbox = mailbox;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.user.Account#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.user.Account#setPassword(java.lang.String)
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.user.Account#setQuotas(java.util.Vector)
	 */
	public void setQuotas(Vector quotas) {
		this.quotas = quotas;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.user.Account#setShell(java.lang.String)
	 */
	public void setShell(String shell) {
		this.shell = shell;
	}

}
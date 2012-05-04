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
 * Beschreibt einen Account eines Benutzers
 * 
 * Betriebssystem: Login, Paswort
 * Accounts nutzbarer Dienste: Mail, Proxy, Datenbank, etc.
 * 
 */
public interface Account {
	
	void addQuota(Quota quota);
	
	void create();
	
	void createHomeDir();
	
	void delete();
	
	void deleteHomeDirectory();
	
	void disable();
	
	void enable();
	
	Mailbox getMailbox();
	
	void setDescription(String description);

	void setHomeDirectory(String home);
	
	void setLogin(String login);
	
	void setMailbox(Mailbox mailbox);
	
	void setName(String name);
	
	void setPassword(String password);
	
	void setQuotas(Vector quotas);
	
	void setShell(String shell);

}
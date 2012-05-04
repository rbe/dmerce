/*
 * Created on 27.04.2003
 *
 */
package com.wanci.ncc.mail;

import com.wanci.ncc.filesystem.FilesystemQuota;

/**
 * @author rb
 * @version $Id: Maildir.java,v 1.1 2004/02/02 09:41:47 rb Exp $
 *
 */
public class Maildir implements Mailbox {

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.mail.Mailbox#create()
	 */
	public void create() {
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.mail.Mailbox#delete()
	 */
	public void delete() {
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.mail.Mailbox#empty()
	 */
	public void empty() {
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.mail.Mailbox#setLocation(java.lang.String)
	 */
	public void setLocation(String directory) {
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.mail.Mailbox#setQuota(com.wanci.dmerce.ncc.FilesystemQuota)
	 */
	public void setQuota(FilesystemQuota homeDirectoryQuota) {
	}
	
}
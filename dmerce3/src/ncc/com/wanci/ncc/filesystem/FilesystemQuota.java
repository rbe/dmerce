/*
 * Created on 26.04.2003
 *
 */
package com.wanci.ncc.filesystem;

import com.wanci.ncc.Quota;

/**
 * @author rb
 * @version $Id: FilesystemQuota.java,v 1.1 2004/02/02 09:41:49 rb Exp $
 *
 */
public abstract class FilesystemQuota implements Quota {

	private String filesystem;

	private int hardlimit;

	private int softlimit;

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.Quota#disable()
	 */
	public void disable() {
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.Quota#enable()
	 */
	public void enable() {
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.Quota#setFilesystem(java.lang.String)
	 */
	public void setFilesystem(String filesystem) {
		this.filesystem = filesystem;		
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.Quota#setHardLimit(int)
	 */
	public void setHardLimit(int bytes) {
		hardlimit = bytes;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.Quota#setSoftLimit(int)
	 */
	public void setSoftLimit(int bytes) {
		softlimit = bytes;
	}
	
}
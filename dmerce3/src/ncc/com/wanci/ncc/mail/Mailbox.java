/*
 * Created on 26.04.2003
 */
package com.wanci.ncc.mail;

import com.wanci.ncc.filesystem.FilesystemQuota;

/**
 * @author rb
 *
 */
public interface Mailbox {
	
	void create();
	
	void delete();
	
	void empty();

	void setLocation(String directory);
	
	void setQuota(FilesystemQuota homeDirectoryQuota);
	
}
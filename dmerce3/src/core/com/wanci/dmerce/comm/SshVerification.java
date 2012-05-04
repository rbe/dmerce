/*
 * Created on 06.06.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.comm;
import com.sshtools.j2ssh.transport.HostKeyVerification;
import com.sshtools.j2ssh.transport.TransportProtocolException;
import com.sshtools.j2ssh.transport.publickey.SshPublicKey;

/**
 * @author pg
 * @version $Id: SshVerification.java,v 1.1 2004/06/30 13:53:18 rb Exp $
 */
public class SshVerification implements HostKeyVerification {

	/* (non-Javadoc)
	 * @see com.sshtools.j2ssh.transport.HostKeyVerification#verifyHost(java.lang.String, com.sshtools.j2ssh.transport.publickey.SshPublicKey)
	 */
	public boolean verifyHost(String arg0, SshPublicKey arg1)
		throws TransportProtocolException {

		return true;

	}

}
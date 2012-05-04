/*
 * SshSendFileThread.java
 *
 * Created on September 15, 2003, 3:26 PM
 */

package com.wanci.dmerce.comm;
import java.io.File;
import java.io.IOException;

import com.wanci.java.LangUtil;

/**
 *
 * @author  rb
 */
public class SshSendFileThread implements Runnable {
    
    private boolean DEBUG = true;
    
    private Ssh ssh;
    
    private String localFileName;
    
    private String remoteFileName;
    
    public SshSendFileThread(Ssh ssh, String localFileName,
    String remoteFileName) {
        
        this.ssh = ssh;
        this.localFileName = localFileName;
        this.remoteFileName = remoteFileName;
        
    }
    
    public boolean send() {
        
        boolean sent = false;
        
        if (remoteFileName == null)
            remoteFileName = localFileName;
        
        LangUtil.consoleDebug(DEBUG, "Sending file "
        + localFileName + " -> " + remoteFileName);
        
        File f = new File(localFileName);
        boolean canRead = f.canRead();
        
        if (canRead && ssh.isConnected()) {
            
            try {
                
                ssh.sendFile(localFileName, remoteFileName);
                
                LangUtil.consoleDebug(DEBUG, "Sent file "
                + localFileName + " -> " + remoteFileName
                + " sent successfully");
                
            }
            catch (IOException e) {
                LangUtil.consoleDebug(DEBUG, "Did not send file "
                + localFileName + " -> " + remoteFileName
                + "!");
            }
            
        }
        
        if (!canRead)
            LangUtil.consoleDebug(DEBUG, "Sorry can't read file " + localFileName);
        if (!ssh.isConnected())
            LangUtil.consoleDebug(DEBUG, "Sorry we are not connected to ssh server");
        
        return sent;
        
    }
    
    public void run() {
        send();
    }
    
}
/*
 * SshSellCommand.java
 *
 * Created on September 15, 2003, 1:11 PM
 */

package com.wanci.dmerce.comm;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import com.sshtools.j2ssh.session.SessionChannelClient;
import com.wanci.java.LangUtil;

/**
 * Execute a command and give access to the output
 *
 * @author rb
 * @version $Id: SshShellCommand.java,v 1.1 2004/06/30 13:53:18 rb Exp $
 */
public class SshShellCommand {
    
    /**
     * Debug flag
     */
    private boolean DEBUG = true;
    
    /**
     * 
     */
    private Ssh ssh;
    
    /**
     * Single command to execute
     */
    private String command;
    
    /**
     * Vector of multiple commands to execute
     */
    private Vector commands = new Vector();
    
    public SshShellCommand(Ssh ssh, String command) {
        this.ssh = ssh;
        this.command = command;
    }
    
    public SshShellCommand(Ssh ssh, Vector commands) {
        this.ssh = ssh;
        this.commands = commands;
    }
    
    /**
     * Execute one or more commands
     */
    public Vector execute() throws IOException {
        
        if (command != null)
            return executeCommand();
        else if (commands != null)
            return executeCommands();
        else
            return null;
        
    }
    
    /**
     * Fuehrt ein Shell-Kommando aus und gibt das
     * Ergebniss als Vector zurueck, der zeilenweise den Output
     * enthaelt
     *
     * @param command
     * @return Vector Output des Shell-Kommandos
     */
    public Vector executeCommand() throws IOException {
        
        int i;
        boolean newline = false;
        Vector v = new Vector();
        StringBuffer sb = new StringBuffer();
        
        LangUtil.consoleDebug(DEBUG, "Executing command " + command);
        
        SessionChannelClient sessionChannelClient = ssh.openSessionChannelClient();
        sessionChannelClient.executeCommand(command);
        
        return null;
        
        /*
        BufferedReader r = new BufferedReader(new InputStreamReader(
        sessionChannelClient.getInputStream()));
        
        // go through the stream and write to vector
        // problem is handling of the different newline-sequences
        // windows:\r\n   unix:\n    mac:\r
        while ((i = r.read()) != -1) {
            
            //a newline sign
            if (i == 10 || i == 13) {
                
                if (!newline)
                    // it is the first newline sign in the sequence
                    newline = true;
                else {
                    
                    // newline is true and i is the second newline sign
                    // now add the line and reset newline and clear stringbuffer
                    v.addElement(sb.toString());
                    
                    sb = new StringBuffer();
                    newline = false;
                    
                }
                
            }
            else {
                if (!newline)
                    // no newline character, we are somewhere in the middle of the line
                    // just append i to the line
                    sb.append((char) i);
                else {
                    // newline is true and i is the second newline sign
                    // now add the line and reset newline and clear stringbuffer
                    v.addElement(sb.toString());
                    
                    sb = new StringBuffer();
                    newline = false;
                }
                
            }
            
        }
        
        // the string buffer may be filled if the last sign is not a new line sign
        // then write the buffer
        if (sb.length() > 0)
            v.addElement(sb.toString());
        
        return v;
        */
    }
    
    /**
     * Execute commands from vector
     */
    public Vector executeCommands() throws IOException {
        
        Vector allCommandsOutput = new Vector();
        
        Iterator commandIter = commands.iterator();
        while (commandIter.hasNext()) {
            
            command = (String) commandIter.next();
            executeCommand();
            
            /*
            Iterator vIter = execute().iterator();
            while (vIter.hasNext()) {
                allCommandsOutput.add((String) vIter.next());
            }
            */
            
        }
        
        return allCommandsOutput;
        
    }
    
}
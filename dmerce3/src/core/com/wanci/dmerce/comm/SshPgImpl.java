/*
 * Created on Jun 3, 2003
 */
package com.wanci.dmerce.comm;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.configuration.SshConnectionProperties;
import com.sshtools.j2ssh.io.UnsignedInteger32;
import com.sshtools.j2ssh.session.SessionChannelClient;
import com.sshtools.j2ssh.sftp.FileAttributes;
import com.sshtools.j2ssh.sftp.SftpFile;
import com.sshtools.j2ssh.sftp.SftpFileOutputStream;
import com.sshtools.j2ssh.sftp.SftpSubsystemClient;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @author pg
 * @version $Id: SshPgImpl.java,v 1.1 2004/06/30 13:53:18 rb Exp $
 *
 * SSH Communication with other hosts
 *
 * you can send binaries files or receive text files or display output of remote commands
 * the class can only handle one action per time
 * e.g. receive a file and close or send a file and close
 * or receive the output of a command and close
 * so if you want to do more actions, you have to reconnect or
 * reinstantiate
 *
 * @version $Id: SshPgImpl.java,v 1.1 2004/06/30 13:53:18 rb Exp $
 */
public class SshPgImpl implements SshInterface {
    
    /**
     * dmerce properties
     */
    private XmlPropertiesReader dmerceProperties;
    
    /**
     * Wird in init() per DEBUG = dmerceProperties... ueberschrieben!
     */
    private boolean DEBUG = false;
    
    /** 
     * holds remote host information
     */
    private InetAddress remoteHost;
    
    /**
     * user name for login via ssh
     */
    private String user;
    
    // password for login via ssh
    private String passwd;
    
    // this is the client connection itself
    private SshClient ssh;
    
    private boolean connectionStatus = false;
    
    /**
     * call ssh with no parameter
     */
    public SshPgImpl() throws XmlPropertiesFormatException {
        //initialise
        init();
    }
    
    /**
     * call ssh class with inetaddress as parameter
     */
    public SshPgImpl(InetAddress in) throws XmlPropertiesFormatException {
        //initialise
        init();
        //set given address
        setInetAddress(in);
    }
    
    /**
     * Konnektiert den Host
     *
     * @param command
     */
    public void connect() throws Exception {
        
        //result of authentication, could be PARTIAL, FAILED, COMPLETE
        int connectionResult = -1;
        
        try {
            
            // instantiate ssh object and apply parameter
            // make client connection
            ssh = new SshClient();
            SshConnectionProperties properties = new SshConnectionProperties();
            
            //get the hostname from the inetaddress object
            properties.setHost(remoteHost.getHostName());
            
            // connect to the host
            //@todo ConsoleHostKeyVerification problem with yes/no input
            ssh.connect(properties, new SshVerification());
            //ssh.connect(properties);
            
            // create password authentication instance
            PasswordAuthenticationClient pwd =
            new PasswordAuthenticationClient();
            //set parameters
            pwd.setUsername(user);
            pwd.setPassword(passwd);
            
            //now try connection
            connectionResult = ssh.authenticate(pwd);
            LangUtil.consoleDebug(DEBUG, "connectionResult=" + connectionResult);
            
        }
        catch (Exception e) {
            //errors?
            e.printStackTrace();
        }
        
        if (connectionResult == AuthenticationProtocolState.COMPLETE)
            //set connectionstatus to true
            //isConnected asks for the status
            connectionStatus = true;
        else
            connectionStatus = false;
    }
    
    /**
     * Beendet die Verbindung mit dem Host
     * @param command
     */
    public void disconnect() {
        
        LangUtil.consoleDebug(DEBUG, "Disconneting from ssh server");
        
        //reset connection status
        connectionStatus = false;
        //disconnect from ssh server
        ssh.disconnect();
    }
    
    /**
     * checks connection status of object
     * results only a boolean
     * @return boolean
     */
    public boolean isConnected() {
        return connectionStatus;
    }
    
    /**
     * execute a shell command on the remote host
     *
     * this method executes a command on the user shell and
     * returns a buffered reader on the output
     * @param command
     * @throws IOException
     */
    public BufferedReader executeShellCommandAsStream(String command)
    throws IOException {
        
        LangUtil.consoleDebug(DEBUG, "Executing command (as stream): " + command);
        
        //only if a ssh connection established to the server
        if (connectionStatus == true) {
            
            // open session channel for work
            SessionChannelClient session = ssh.openSessionChannel();
            boolean executed = session.executeCommand(command);
            
            /*
            if (executed) {
                return new BufferedReader(
                new InputStreamReader(session.getInputStream()));
            }
            else
                return null;
            */
            
            return null;
            
        }
        else
            throw new IOException("SSH connection not established");
        
    }
    
    /**
     * Fuehrt ein Shell-Kommando aus und gibt das
     * Ergebniss als Vector zurueck, der zeilenweise den Output
     * enthaelt
     *
     * @param command
     * @return Vector Output des Shell-Kommandos
     */
    public Vector executeShellCommand(String command) throws IOException {
        
        LangUtil.consoleDebug(DEBUG, "Executing command: " + command);
        
        //initialise all variables
        Vector v = new Vector();
        StringBuffer sb = new StringBuffer();
        BufferedReader r = executeShellCommandAsStream(command);
        int i;
        boolean newline = false;
        
        //go through the stream and write to vector
        //problem is handling of the different newline-sequences
        //windows:\r\n   unix:\n    mac:\r
        while ((i = r.read()) != -1) {
            
            //a newline sign
            if (i == 10 || i == 13) {
                
                if (!newline)
                    //it is the first newline sign in the sequence
                    newline = true;
                else {
                    
                    //newline is true and i is the second newline sign
                    //now add the line and reset newline and clear stringbuffer
                    v.addElement(sb.toString());
                    
                    sb = new StringBuffer();
                    newline = false;
                    
                }
                
            }
            else {
                if (!newline)
                    //no newline character, we are somewhere in the middle of the line
                    //just append i to the line
                    sb.append((char) i);
                else {
                    //newline is true and i is the second newline sign
                    //now add the line and reset newline and clear stringbuffer
                    v.addElement(sb.toString());
                    
                    sb = new StringBuffer();
                    newline = false;
                }
            }
        }
        
        //the string buffer may be filled if the last sign is not a new line sign
        //then write the buffer
        if (sb.length() > 0)
            v.addElement(sb.toString());
        
        //give back the result vector
        return v;
        
    }
    
    /**
     * Execute commands from vector
     */
    public void executeShellCommands(Vector v) throws IOException {
        
        Iterator i = v.iterator();
        while (i.hasNext()) {
            executeShellCommand((String) i.next());
        }
        
    }
    
    /**
     * the method receives a file given by remoteFilename and stores it with name localFilename
     * it results the status per boolean
     *
     * @param filename
     * @return success? (true/false)
     */
    public boolean receiveFile(String remoteFilename, String localFilename) {
        
        try {
            
            PrintWriter out = new PrintWriter(new FileWriter(localFilename));
            BufferedWriter o = new BufferedWriter(out);
            BufferedReader r =
            executeShellCommandAsStream("cat " + remoteFilename);
            
            int i;
            char[] c = new char[128 * 1024];
            while ((i = r.read(c)) != -1) {
                o.write(c);
                //o.write((char) i);
                //System.out.print((char) i);
            }
            
            o.close();
            
            return true;
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
        
    }
    
    /**
     * Sendet die Datei, die durch ein File-Objekt angegeben ist
     * auf den Host und speichert diese unter dem angegebenen
     * Dateinamen ab
     * @param File
     * @param String
     * @return success? (true/false)
     */
    public boolean sendFile(File file, String hostFilename) {
        
        try {
            
            // assume the connection is authenticated we can now do some real work!
            SessionChannelClient session = ssh.openSessionChannel();
            // start sftp subsystem
            SftpSubsystemClient sftp = new SftpSubsystemClient();
            session.startSubsystem(sftp);
            
            //setter for file attribs
            FileAttributes attrs = new FileAttributes();
            attrs.setPermissions("rwxrwxrwx");
            attrs.setPermissions(new UnsignedInteger32(
            attrs.getPermissions().intValue() | FileAttributes.S_IFREG));
            
            // Now try to write to a file without creating it!
            SftpFile sftpFile = sftp.openFile(hostFilename,
            SftpSubsystemClient.OPEN_CREATE | SftpSubsystemClient.OPEN_WRITE,
            attrs);
            
            //apply attribs again
            attrs.setPermissions("rwxrwx---");
            sftp.setAttributes(sftpFile, attrs);
            
            //first open output stream on remote host
            BufferedOutputStream out = new BufferedOutputStream(
            new SftpFileOutputStream(sftpFile));
            
            //then open stream on given input file
            BufferedInputStream in = new BufferedInputStream(
            new FileInputStream(file));
            
            //write content of file (buffered)
            byte[] b = new byte[128 * 1024];
            int c;
            int byteCounter = 0;
            while ((c = in.read(b)) != -1) {
                
                out.write(b);
                
                if (DEBUG) {
                    byteCounter += b.length;
                    if (byteCounter >= 100 * 1024) {
                        LangUtil.consoleDebug(DEBUG, "Wrote " + byteCounter + " bytes!");
                        byteCounter = 0;
                    }
                }
                
            }
            
            in.close();
            out.close();
            
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    
    /**
     * Setzt den Host, auf dem gearbeitet werden soll
     * @param in
     */
    public void setInetAddress(InetAddress in) {
        //set the inetaddress
        remoteHost = in;
    }
    
    public void init() throws XmlPropertiesFormatException {
        
        dmerceProperties = XmlPropertiesReader.getInstance();
        DEBUG = dmerceProperties.getPropertyAsBoolean("ssh.debug");
        
        //these steps are necessary
        try {
            
            //setting property for sshtools home (xml-files have to reside in local conf dir)
            System.setProperty("sshtools.home", ".");
            
            //logger instantiation
            
            RollingFileAppender log =
            new RollingFileAppender(
            new PatternLayout(),
            System.getProperty("user.dir")
            + System.getProperty("file.separator")
            + "etc"
            + System.getProperty("file.separator")
            + "ssh.log",
            true);
            
            //logger logfile
            log.setMaxFileSize("10KB");
            
            //apply logfile
            BasicConfigurator.configure(log);
            
            
        }
        catch (Exception e) {
            //errors?
            e.printStackTrace();
        }
        
    }
    
    public void initData() {
        //initialize all variables
        user = dmerceProperties.getProperty("ssh.login");
        passwd = dmerceProperties.getProperty("ssh.passwd");
    }
    
    /**
     * getter for passwd
     * @return passwd
     */
    public String getPasswd() {
        return passwd;
    }
    
    /**
     * getter for user
     * @return userstring
     */
    public String getUser() {
        return user;
    }
    
    /**
     * setter for passwd
     * @param password
     */
    public void setPasswd(String string) {
        passwd = string;
    }
    
    /**
     * setter for user
     * @param userstring
     */
    public void setUser(String string) {
        user = string;
    }
    
    /**
     * main method
     * sets properties for sshtools and starts logging mechanism
     * @param args
     */
    public static void main(String args[]) {
        
        boolean DEBUG = true;
        Boot.printCopyright("SSH");
        
        SshPgImpl s;
        try {
            
            //these are all variables needed for the test routines
            String hostname = "gandalf.muenster1-3.de.1ci.net";
            String command = "pwd";
            String receiveFilenameRemote = "docs/javacollection.txt";
            String receiveFilenameLocal = "h:/javacoll.txt";
            String sendFilenameRemote = "test.txt";
            String sendFilenameLocal = "test.txt";
            String userhere = "pg";
            String passwdhere = "st79gh";
            
            //test for executeShellCommand
            s = new SshPgImpl(InetAddress.getByName(hostname));
            s.initData();
            if (userhere != "") {
                s.setUser(userhere);
                s.setPasswd(passwdhere);
            }
            s.connect();
            if (s.isConnected()) {
                LangUtil.dumpVector(s.executeShellCommand(command));
                s.disconnect();
            }
            
            //test for receiveFile
            s = new SshPgImpl(InetAddress.getByName(hostname));
            s.initData();
            if (userhere != "") {
                s.setUser(userhere);
                s.setPasswd(passwdhere);
            }
            s.connect();
            if (s.isConnected()) {
                if (!s
                .receiveFile(receiveFilenameRemote, receiveFilenameLocal))
                    LangUtil.consoleDebug(DEBUG, "file receive failed");
                else
                    LangUtil.consoleDebug(DEBUG, "file received");
                s.disconnect();
            }
            
            //test for sendFile
            s = new SshPgImpl(InetAddress.getByName(hostname));
            s.initData();
            if (userhere != "") {
                s.setUser(userhere);
                s.setPasswd(passwdhere);
            }
            s.connect();
            if (s.isConnected()) {
                File f = new File(sendFilenameLocal);
                if (!s.sendFile(f, sendFilenameRemote))
                    LangUtil.consoleDebug(DEBUG, "file not sent");
                else
                    LangUtil.consoleDebug(DEBUG, "file sent");
                s.disconnect();
            }
            
        }
        catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
}
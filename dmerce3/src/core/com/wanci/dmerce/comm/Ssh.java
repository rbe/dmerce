/*
 * Ssh.java
 *
 * Created on September 13, 2003, 7:52 PM
 */

package com.wanci.dmerce.comm;
import java.io.BufferedReader;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.sshtools.j2ssh.SftpClient;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.session.SessionChannelClient;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;
import com.sshtools.j2ssh.transport.TransportProtocolState;
import com.wanci.java.LangUtil;

/**
 *
 * @author  rb
 */
public class Ssh {
    
    /**
     * Debug flag
     */
    private boolean DEBUG = true;
    
    /**
     * Name of server we connect to
     */
    private String serverName;
    
    /**
     * Port to connect to; standard is 22
     */
    private int serverPort = 22;
    
    /**
     * Ssh client from J2SSH
     */
    private SshClient sshClient = new SshClient();
    
    //private SshConnectionProperties sshConnectionProperties =
    //new SshConnectionProperties();
    
    /**
     * The actual state of transport protocol
     * Should be updated after every action (every method) by calling
     * the private method updateTransportProtocolState()
     */
    private TransportProtocolState transportProtocolState;
    
    /**
     * Describes the status of the authentication protocol:
     * AuthenticationProtocolState.FAILED, .PARTIAL, .COMPLETE
     */
    private int authenticationProtocolState;
    
    /**
     * Flag indicating that password based authentication is to be used
     */
    private boolean usePasswordAuthentication = false;
    
    /**
     * A password authentication client. Used when constructors
     * with username and password are called
     */
    private PasswordAuthenticationClient pwdAuthClient =
    new PasswordAuthenticationClient();
    
    /**
     * Session channel that is used for input/output to the ssh server
     */
    //private SessionChannelClient session;
    
    /** Creates a new instance of Ssh */
    public Ssh(String serverName, String username, String password)
    throws IOException {
        
        this.serverName = serverName;
        usePasswordAuthentication = true;
        pwdAuthClient.setUsername(username);
        pwdAuthClient.setPassword(password);
        
        init();
        
    }
    
    public Ssh(String serverName, int serverPort, String username,
    String password) throws IOException {
        
        this.serverName = serverName;
        this.serverPort = serverPort;
        
        usePasswordAuthentication = true;
        pwdAuthClient.setUsername(username);
        pwdAuthClient.setPassword(password);
        
        init();
        
    }
    
    private void closeSessionChannelClient(SessionChannelClient session) {
        try {
            session.close();
            LangUtil.consoleDebug(DEBUG, "Closed session channel");
        }
        catch (IOException e) {
        }
    }
    
    /**
     * Connect to the server and setup everything needed to execute commands,
     * send and receive files (session channel, streams)
     */
    public void connect() throws Exception {
        
        sshClient.connect(serverName, new IgnoreHostKeyVerification());
        LangUtil.consoleDebug(DEBUG, "Connected to ssh server");
        
        if (usePasswordAuthentication) {
            LangUtil.consoleDebug(DEBUG, "Authenticating using password");
            authenticationProtocolState = sshClient.authenticate(pwdAuthClient);
        }
        
        /*
        SessionChannelClient s1 = sshClient.openSessionChannel();
        s1.executeCommand("ls -l");
        InputStream i1 = s1.getInputStream();
        byte[] b1 = new byte[1024];
        i1.read(b1);
        LangUtil.consoleDebug(DEBUG, "1: " + b1);
        SessionChannelClient s2 = sshClient.openSessionChannel();
        s2.executeCommand("ls -l");
        InputStream i2 = s2.getInputStream();
        byte[] b2 = new byte[1024];
        i2.read(b2);
        LangUtil.consoleDebug(DEBUG, "2: " + b2);
        SessionChannelClient s3 = sshClient.openSessionChannel();
        s3.executeCommand("ls -l");
        InputStream i3 = s3.getInputStream();
        byte[] b3 = new byte[1024];
        i3.read(b3);
        LangUtil.consoleDebug(DEBUG, "3: " + b3);
        SessionChannelClient s4 = sshClient.openSessionChannel();
        s4.executeCommand("ls -l");
        InputStream i4 = s4.getInputStream();
        byte[] b4 = new byte[1024];
        i4.read(b4);
        LangUtil.consoleDebug(DEBUG, "4: " + b4);
         
        String s;
        BufferedReader r;
         
        SessionChannelClient scc = sshClient.openSessionChannel();
        scc.executeCommand("ls -l");
        r = new BufferedReader(new InputStreamReader(
        scc.getInputStream()));
        while ((s = r.readLine()) != null) {
            System.out.println(s);
        }
        scc.close();
         
        SessionChannelClient scc2 = sshClient.openSessionChannel();
        scc2.executeCommand("ls -l bin");
        r = new BufferedReader(new InputStreamReader(
        scc2.getInputStream()));
        while ((s = r.readLine()) != null) {
            System.out.println(s);
        }
         
        SessionChannelClient scc3 = sshClient.openSessionChannel();
        scc3.executeCommand("ls -l install");
        r = new BufferedReader(new InputStreamReader(
        scc3.getInputStream()));
        while ((s = r.readLine()) != null) {
            System.out.println(s);
        }
         */
        
        updateTransportProtocolState();
        
    }
    
    /**
     * Closes sessions, streams and disconnects from the server
     */
    public void disconnect() {
        
        sshClient.disconnect();
        LangUtil.consoleDebug(DEBUG, "Disconnected from ssh server");
        
        updateTransportProtocolState();
        
    }
    
    /**
     * Executes a command and returns BufferedReader for reading the
     * output
     */
    public BufferedReader getExecuteCommandBufferedReader(String command)
    throws IOException {
        
        LangUtil.consoleDebug(DEBUG, "About to execute command: " + command);
        openSessionChannelClient().executeCommand(command.trim());
        /*
        BufferedReader r = new BufferedReader(new InputStreamReader(
        s.getInputStream()));
        s.close();
         */
        
        updateTransportProtocolState();
        
        return null;
        
        /*
        OutputStream out = session.getOutputStream();
        out.write("ls -l\n".getBytes());
         
        byte[] b = new byte[255];
        int read;
        while ((read = in.read(b)) > 0) {
            System.out.println(new String(b, 0, read));
        }
         
        String s;
        while ((s = r.readLine()) != null) {
            System.out.println(s);
        }
         */
        
    }
    
    /**
     * Initialize J2SSH. It needs log4j.
     */
    public void init() throws IOException {
        
        RollingFileAppender log = new RollingFileAppender(new PatternLayout(),
        System.getProperty("user.dir")
        + System.getProperty("file.separator")
        + "etc"
        + System.getProperty("file.separator")
        + "ssh.log", true);
        
        //logger logfile
        log.setMaxFileSize("10KB");
        
        //apply logfile
        BasicConfigurator.configure(log);
        
    }
    
    /**
     * Returns true, if we are correctly authenticated and the connection
     * can be used
     */
    public boolean isAuthenticated() {
        return (authenticationProtocolState == AuthenticationProtocolState.COMPLETE);
    }
    
    /**
     * Returns true if we are connected to a ssh server, false if not
     */
    public boolean isConnected() {
        
        if (transportProtocolState.getValue() == TransportProtocolState.CONNECTED)
            return true;
        else
            return false;
        
    }
    
    /**
     * Opens a new session channel client
     */
    public SessionChannelClient openSessionChannelClient() throws IOException {
        LangUtil.consoleDebug(DEBUG, "Openening session channel client");
        return sshClient.openSessionChannel();
    }
    
    /**
     * Get a file from a remote host using SFTP
     */
    public void receiveFile(String remoteFilename) throws IOException {
        
        SftpClient sftpClient = sshClient.openSftpClient();
        sftpClient.get(remoteFilename);
        sftpClient.quit();
        
        updateTransportProtocolState();
        
    }
    
    /**
     * Get a file from a remote host using SFTP
     */
    public void receiveFile(String remoteFilename, String localFilename)
    throws IOException {
        
        SftpClient sftpClient = sshClient.openSftpClient();
        sftpClient.get(remoteFilename, localFilename);
        sftpClient.quit();
        
        updateTransportProtocolState();
    }
    
    /**
     * Send a file to remote host using SFTP
     */
    public void sendFile(String localFilename, String remoteFilename)
    throws IOException {
        
        SftpClient sftpClient = sshClient.openSftpClient();
        sftpClient.put(localFilename, remoteFilename);
        sftpClient.quit();
        
        updateTransportProtocolState();
        
    }
    
    /**
     * Send a file to remote host using SFTP
     */
    public void sendFile(String localFilename)
    throws IOException {
        
        SftpClient sftpClient = sshClient.openSftpClient();
        sftpClient.put(localFilename);
        sftpClient.quit();
        
        updateTransportProtocolState();
        
    }
    
    /**
     * Sends a whole directory to remote host using SFTP
    public void sendLocalDirectory(String localDirectory,
    String remoteDirectory) throws IOException {
        
        SftpClient sftpClient = sshClient.openSftpClient();
        sftpClient.copyLocalDirectory(localDirectory, remoteDirectory,
        true, false, true, null);
        sftpClient.quit();
        
        updateTransportProtocolState();
        
    }
     */
    
    /**
     * Gets connection state from sshClient object and saves it to a
     * local variable "transportProtocolState"
     */
    private void updateTransportProtocolState() {
        transportProtocolState = sshClient.getConnectionState();
    }
    
    public static void main(String[] args) throws Exception {
        
        Ssh rb = new Ssh("localhost", "dmerce", "dm789dm");
        rb.connect();
        rb.disconnect();
        
    }
    
}
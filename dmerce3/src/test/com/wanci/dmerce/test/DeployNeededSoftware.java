/*
 * DeployNeededSoftware.java
 *
 * Created on September 12, 2003, 2:06 PM
 */

package com.wanci.dmerce.test;


import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Vector;

import com.wanci.dmerce.comm.Ssh;
import com.wanci.dmerce.comm.SshSendFileThread;
import com.wanci.dmerce.comm.SshShellCommand;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.java.LangUtil;

/**
 * Use SSH to transfer needed software packages from 1Ci software repository
 * to destination server
 *
 * XML is used to describe software with its version numbers etc.
 *
 * <depoly-software>
 *    <product-software>
 *        <name value="apache">
 *        <version value="1.3.28">
 *    </product-software>
 *    <product-software>
 *        <name value="mysql">
 *        <version value="3.23.57">
 *    </product-software>
 *    <product-software>
 *        <name value="mod_python">
 *        <version value="2.7.8">
 *    </product-software>
 * </deploy-software>
 *
 * The software will be copied to the destination server:$DMERCE_HOME/install
 * directory.
 *
 * @version $Id: DeployNeededSoftware.java,v 1.3 2004/06/30 13:53:19 rb Exp $
 * @author  rb
 */
public class DeployNeededSoftware {
    
    /**
     * Debug flag
     */
    private boolean DEBUG = true;
    
    /**
     * Secure Shell object
     */
    private Ssh ssh;
    
    /**
     * Name of ssh server
     */
    private String server;
    
    /**
     * User for ssh server. Default: dmerce
     */
    private String user = "dmerce";
    
    /**
     * Password for ssh server. Default: dmerce
     */
    private String password = "dmerce";
    
    /**
     * Base directory where software is found
     */
    private String softwareDistribDir = "/export/swdocs";
    private String apacheDistrib = softwareDistribDir
    + "/server/apache/apache_1.3.28.tar.gz";
    private String modPythonDistrib = softwareDistribDir
    + "/server/apache/mod_python-2.7.8.tgz";
    private String pythonDistrib = softwareDistribDir
    + "/program/python/2.2/Python-2.2.2.tgz";
    private String mysqlPythonDistrib = softwareDistribDir
    + "/program/python/lib/database/mysql/MySQL-python-0.9.2.tar.gz";
    private String mysqlDistrib = softwareDistribDir
    + "/database/mysql/mysql-3.23.57.tar.gz";
    private String pgsqlDistrib = softwareDistribDir
    + "/database/postgresql/postgresql-7.3.4.tar.gz";
    private String egenixMxDistrib = softwareDistribDir
    + "/program/python/lib/datetime/egenix-mx-base-2.0.3.tar.gz";
    private String psycopgDistrib = softwareDistribDir
    + "/program/python/lib/database/postgresql/psycopg-1.0.13.tar.gz";
    private String dcoracleDistrib = softwareDistribDir
    + "/program/python/lib/database/oracle/DCOracle2-1.2.tgz";
    
    /**
     * Base directory for dmerce distribution
     */
    private String dmerceDistribDir = "/opt/dmerce/distrib";
    private String dmerceExamplesDistrib = dmerceDistribDir + "/examples";
    
    //private Thread[] sendThreads = new Thread[10];
    private Vector sendThreads = new Vector();
    
    /**
     * Counter for started file transfer threads
     */
    private int sendThreadCounter = 0;
    
    /** Creates a new instance of DeployNeededSoftware */
    public DeployNeededSoftware(String server, String user, String password)
    throws XmlPropertiesFormatException, UnknownHostException, IOException {
        
        this.server = server;
        
        if (user != null)
            this.user = user;
        if (password != null)
            this.password = password;
        
    }
    
    /**
     * Connect to ssh server
     */
    public void connect() throws Exception {
        
        ssh = new Ssh(server, user, password);
        ssh.connect();
        
    }
    
    /**
     * Initialiazes a dmerce login environment:
     * - Copy .profile
     * - Copy dmercerc
     * - Create .ssh directory and set permissions
     */
    public void createLoginEnvironment() throws IOException {
        
        ssh.sendFile(dmerceDistribDir + "/examples/dot-profile", ".profile");
        new SshShellCommand(ssh, "mkdir .ssh").execute();
        new SshShellCommand(ssh, "chmod 700 .ssh").execute();
        
    }
    
    /**
     * Disconnect from ssh server
     */
    public void disconnect() {
        if (ssh.isConnected())
            ssh.disconnect();
    }
    
    /**
     * Retrieves actual dmercerc from ssh server
     */
    public void getDmerceRc() throws IOException {
        ssh.receiveFile("dmercerc", "/tmp/test-dmercerc");
    }
    
    public String getServer() {
        return server;
    }
    
    public String getUser() {
        return user;
    }
    
    public String getPassword() {
        return password;
    }
    
    /**
     * Is the ssh session correctly authenticated?
     */
    public boolean isSshAuthenticated() {
        return ssh.isAuthenticated();
    }
    
    /**
     * Are we connected to an ssh server?
     */
    public boolean isSshConnected() {
        return ssh.isConnected();
    }
    
    /**
     * Create all neccessary directories for dmerce
     */
    public void makeDirectories() throws IOException {
        
        Vector v = new Vector();
        v.add("mkdir -p backup");
        v.add("mkdir -p bin");
        v.add("mkdir -p build");
        v.add("mkdir -p doc");
        v.add("mkdir -p etc");
        v.add("mkdir -p examples");
        v.add("mkdir -p install");
        v.add("mkdir -p lib");
        v.add("mkdir -p log");
        v.add("mkdir -p product");
        v.add("mkdir -p product/apache");
        v.add("mkdir -p product/dmerce");
        v.add("mkdir -p product/java");
        v.add("mkdir -p product/java/j2sdk");
        v.add("mkdir -p product/mysql");
        v.add("mkdir -p product/mysql/dump");
        v.add("mkdir -p product/oracle");
        v.add("mkdir -p product/oracle/dump");
        v.add("mkdir -p product/postgresql");
        v.add("mkdir -p product/postgresql/dump");
        v.add("mkdir -p scratch");
        v.add("mkdir -p sql");
        v.add("mkdir -p sql/mysql");
        v.add("mkdir -p sql/oracle");
        v.add("mkdir -p sql/postgresql");
        v.add("mkdir -p websites");
        
        new SshShellCommand(ssh, v).execute();
        
    }
    
    /**
     * Starts a new thread that copies a file to remote ssh server
     */
    public void sendToDirectory(String directory, String localFilename) {
        
        SshSendFileThread sendThread = new SshSendFileThread(ssh,
        localFilename, directory + "/" + new File(localFilename).getName());
        
        Thread t = new Thread(sendThread);
        sendThreads.add(t);
        t.start();
        
        LangUtil.consoleDebug(DEBUG, "Started transfer thread "
        + sendThreadCounter + " for " + localFilename);
        
        sendThreadCounter++;
        
    }
    
    /**
     * Send all files to destionation server
     */
    public void sendAllFiles() {
        
        sendToDirectory("examples", dmerceExamplesDistrib + "/dot-profile");
        sendToDirectory("examples", dmerceExamplesDistrib + "/etc-init.d-dmerce");
         
        sendToDirectory("install", apacheDistrib);
        sendToDirectory("install", modPythonDistrib);
        sendToDirectory("install", pythonDistrib);
        sendToDirectory("install", mysqlPythonDistrib);
        sendToDirectory("install", mysqlDistrib);
        sendToDirectory("install", pgsqlDistrib);
        sendToDirectory("install", psycopgDistrib);
        sendToDirectory("install", egenixMxDistrib);
        sendToDirectory("install", dcoracleDistrib);
        
        try {
            
            //LangUtil.consoleDebug(DEBUG, "Waiting for threads to end");
            Iterator sendThreadsIterator = sendThreads.iterator();
            int i = 0;
            while (sendThreadsIterator.hasNext()) {
                
                ((Thread) sendThreadsIterator.next()).join();
                
                LangUtil.consoleDebug(DEBUG, "Waiting for thread " + i
                + " to end");
                
                i++;
                
            }
            
        }
        catch (InterruptedException e) {
        }
        
    }
    
    /**
     * Transfer the dmerce install script to destination server
     */
    public void sendInstallScript() throws IOException {
        sendToDirectory("bin", dmerceDistribDir + "/bin/qdd_setup");
    }
    
    /**
     * Sets permissions for dmerce and all of its components
     */
    public void setPermissions() throws IOException {
        
        Vector v = new Vector();
        v.add("chmod -R o= .");
        v.add("chmod -R g=u backup");
        v.add("chmod -R g=u bin");
        v.add("chmod -R g=u build");
        v.add("chmod -R g=u doc");
        v.add("chmod -R g=u etc");
        v.add("chmod -R g=u examples");
        v.add("chmod -R g=u install");
        v.add("chmod -R g=u lib");
        v.add("chmod -R g=u log");
        v.add("chmod -R g=u product/apache");
        v.add("chmod -R g=u product/java");
        v.add("chmod -R g=u product/mysql");
        v.add("chown -R oracle:oinstall product/oracle");
        v.add("chmod -R g= product/postgresql/*/data");
        v.add("chmod -R g=u scratch");
        v.add("chmod -R g=u websites");
        v.add("chmod 700 .ssh");
        v.add("chmod 600 .ssh/*");
        v.add("chmod 750 .");
        
        new SshShellCommand(ssh, v).execute();
        
    }
    
    /**
     * Log into destination server and execute installation script
     */
    public void startInstallation() throws IOException {
    }
    
    public static void main(String[] args) throws Exception {
        
        boolean DEBUG = true;
        
        LangUtil.consoleDebug(DEBUG, "Start");
        
        DeployNeededSoftware dns = new DeployNeededSoftware("213.203.245.71",
        "dmerce", "dmFr2x90dm");
        
        dns.connect();
        
        if (dns.isSshConnected() && dns.isSshAuthenticated()) {
            
            LangUtil.consoleDebug(DEBUG, "Connected to ssh server");
            
            LangUtil.consoleDebug(DEBUG, "Creating directories");
            dns.makeDirectories();
            
            LangUtil.consoleDebug(DEBUG, "Starting transfer of all needed files");
            dns.sendAllFiles();
            
            LangUtil.consoleDebug(DEBUG, "Transfering install script");
            //dns.sendInstallScript();
            
            LangUtil.consoleDebug(DEBUG, "Starting installation");
            //dns.startInstallation();
            
            LangUtil.consoleDebug(DEBUG, "Setting permissions");
            dns.setPermissions();
            
            LangUtil.consoleDebug(DEBUG, "Disconnecting from ssh server");
            dns.disconnect();
            
        }
        else if (!dns.isSshConnected())
            LangUtil.consoleDebug(DEBUG, "Could not connect to ssh server "
            + dns.getServer());
        else if (!dns.isSshAuthenticated())
            LangUtil.consoleDebug(DEBUG, "Could not authenticate as "
            + dns.getUser() + "@" + dns.getServer()
            + " using password " + dns.getPassword());
        
        LangUtil.consoleDebug(DEBUG, "Stop");
        
    }
    
}
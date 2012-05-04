/*
 * JdbcPointbase.java
 *
 * Created on 21. Dezember 2002, 22:42
 */

package com.wanci.dmerce.jdbcal;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.java.LangUtil;

/**
 *
 * @author  rb
 */
public class Pointbase extends DatabaseHandler {
    
    private static boolean DEBUG = true;
    
    public String driver = "com.pointbase.jdbc.jdbcUniversalDriver";
    
    public String URL = "jdbc:pointbase:server://localhost:9092/sample";
    
    public int port = 9092;
    
    public String user  = "pbpublic";
    
    public String password = "pbpublic";
    
    public PointbaseDatabaseDefinition databaseDefinition = new PointbaseDatabaseDefinition();
    
    public Pointbase() throws DmerceException {
        
        super.loadDriver(driver);
        super.setUrl(URL);
        super.setUsername(user);
        super.setPassword(password);
    }
    
    public Pointbase(String url, String username, String password) throws DmerceException {
        
        super.loadDriver(driver);
        super.setUrl(url);
        super.setUsername(username);
        super.setPassword(password);
    }
    
    public Pointbase(String hostname, String databaseName, String username,
    String password) throws DmerceException {
        
        super.loadDriver(driver);
        super.setHostname(hostname);
        super.setPort(port);
        super.setDatabaseName(databaseName);
        super.setUsername(username);
        super.setPassword(password);
        generateUrl();
    }
    
    public Pointbase(String hostname, int port, String databaseName,
    String username, String password) throws DmerceException {
        
        super.loadDriver(driver);
        super.setHostname(hostname);
        super.setPort(port);
        super.setDatabaseName(databaseName);
        super.setUsername(username);
        super.setPassword(password);
        generateUrl();
    }
    
    /**
     * @see com.wanci.dmerce.dmf.JdbcDatabase#getDatabaseDefinition()
     */
    public DatabaseDefinition getDatabaseDefinition() {
        return databaseDefinition;
    }
    
    public void generateUrl() {
        setUrl("jdbc:pointbase:server://"
        + getHostname() + ":" + getPort() + "/" + getDatabaseName());
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            Pointbase j = new Pointbase();
            j.test();
        }
        catch (Exception e) {
            LangUtil.consoleDebug(DEBUG, e.getMessage());
        }
    }
    
}
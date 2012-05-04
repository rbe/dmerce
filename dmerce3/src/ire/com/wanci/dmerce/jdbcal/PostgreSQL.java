/*
 * JdbcPostgreSQL.java
 *
 * Created on January 6, 2003, 8:53 PM
 */

package com.wanci.dmerce.jdbcal;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.java.LangUtil;

/** dmerce/PostgreSQL database handler
 *
 * @author  rb
 */
public class PostgreSQL extends DatabaseHandler {
    
    private static boolean DEBUG = false;
    
    public String driver = "org.postgresql.Driver";
    
    public String URL = "jdbc:postgresql://localhost/template1";
    
    public int port = 5432;
    
    public String user = "postgres";
    
    public String password = "";
    
    public PostgreSQLDatabaseDefinition databaseDefinition = new PostgreSQLDatabaseDefinition();
    
    /** Creates a new instance of JdbcPostgreSQL */
    public PostgreSQL() throws DmerceException {
        
        super.loadDriver(driver);
        super.setUrl(URL);
        super.setUsername(user);
        super.setPassword(password);
    }
    
    public PostgreSQL(String url, String username, String password) throws DmerceException {
        
        super.loadDriver(driver);
        super.setUrl(url);
        super.setUsername(username);
        super.setPassword(password);
    }
    
    public PostgreSQL(
        String hostname,
        String databaseName,
        String username,
        String password) throws DmerceException {
        
        super.loadDriver(driver);
        super.setHostname(hostname);
        super.setPort(port);
        super.setDatabaseName(databaseName);
        super.setUsername(username);
        super.setPassword(password);
        
    }
    
    public PostgreSQL(
        String hostname,
        int port,
        String databaseName,
        String username,
        String password) throws DmerceException {
        
        super.loadDriver(driver);
        super.setHostname(hostname);
        super.setPort(port);
        super.setDatabaseName(databaseName);
        super.setUsername(username);
        super.setPassword(password);
        
    }
    
    /**
     * @see com.wanci.dmerce.dmf.JdbcDatabase#getDatabaseDefinition()
     */
    public DatabaseDefinition getDatabaseDefinition() {
        return databaseDefinition;
    }
    
    public void generateUrl() {
        setUrl(
        "jdbc:postgresql://"
        + getHostname()
        + ":"
        + getPort()
        + "/"
        + getDatabaseName());
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            PostgreSQL j = new PostgreSQL();
            j.test();
        }
        catch (DmerceException e) {
            LangUtil.consoleDebug(DEBUG, e.getMessage());
        }
    }
    
}
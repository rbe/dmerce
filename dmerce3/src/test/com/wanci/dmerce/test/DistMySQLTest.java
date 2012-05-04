/*
 * Created on Sep 3, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.wanci.dmerce.test;

import java.sql.ResultSet;
import java.sql.Statement;

import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;

/**
 * @author mf
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DistMySQLTest {
    
    public DistMySQLTest() throws Exception{
        
        Database d = DatabaseHandler.getDatabaseConnection("test1");
        d.openConnection();
        
        // INSERT
        /*
        // nextval der Sequenz holen:
        Statement hs = d.getStatement();
        if (hs.execute("SELECT VKUNDE_SEQ.nextval FROM DUAL")) {
            ResultSet hsres = hs.getResultSet();
            hsres.next();
            System.out.println("N‰chte ID ist "+hsres.getString(1));
            hsres.close();
            hs.close();
        }
         */
        Statement s = d.getStatement();
        String sqlStatement = "INSERT INTO VKUNDE (KUNDENNR, NAME, VORNAME, EMAIL) VALUES (VKUNDE_SEQ.nextval, 'Testmann', 'VOrname', 'hallo@ghj.de')";
        int returnval = s.executeUpdate(sqlStatement, Statement.RETURN_GENERATED_KEYS);
        ResultSet testres = s.getGeneratedKeys();
        /*
        PreparedStatement s = d.getStatement("INSERT INTO VKUNDE (KUNDENNR, NAME, VORNAME, EMAIL) VALUES (VKUNDE_SEQ.nextval, ?, ?, ?)");
        s.setString(1, "Hasenfuﬂ4");
        s.setString(2, "Ottokar4");
        s.setString(3, "ottokar4@hasenfuss.de");
        ResultSet testres = s.getResultSet();
         */
        System.out.println("s.executeUpdate(), Erfolg: "+returnval);
        String ausgabe;
        while (testres.next()) {
            ausgabe = "";
            for (int i = 1; i <= testres.getMetaData().getColumnCount(); i++) {
                ausgabe += testres.getString(i)+", ";
            }
            System.out.println(ausgabe);
        }
        
        // SELECT
        ResultSet result =  d.executeQuery("SELECT * FROM VKUNDE");
        for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
            System.out.println(result.getMetaData().getColumnTypeName(i));
        }
        while (result.next()) {
            ausgabe = "";
            for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                ausgabe += result.getString(i)+", ";
            }
            System.out.println(ausgabe);
        }
        result.close();
        s.close();
        d.closeConnection();
    }
    
    public static void main(String[] args) throws Exception {
        new DistMySQLTest();
    }
}
/*
 * Created on May 2, 2004
 *
 */
package com.wanci.dmerce.jdbcal;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author rb
 * @version $Id: StatementTest.java,v 1.1 2004/05/16 19:44:07 rb Exp $
 *  
 */
public class StatementTest {

    static Database jdbc;

    public static void eins() throws Exception {

        ResultSet rs = jdbc.executeQuery("select sysdate from dual");
        ResultSet rs2 = jdbc.executeQuery("select sysdate+14 from dual");

        rs.next();
        System.out.println("sysdate: "+rs.getString(1));
        
        rs2.next();
        System.out.println("sysdate+14: "+rs2.getString(1));
        
        Statement stmt2 = jdbc.getStatement();
        //ResultSet rs2 = stmt2.executeQuery("select sysdate from dual");

        Statement stmt4 = jdbc.getStatement();
        ResultSet rs4 = stmt4.executeQuery("select sysdate from dual");

        Statement stmt5 = jdbc.getStatement();
        ResultSet rs5 = stmt5.executeQuery("select sysdate from dual");

        Statement stmt3 = jdbc.getStatement();
        ResultSet rs3 = stmt3.executeQuery("select sysdate from dual");
        jdbc.putStatement(stmt3);

        jdbc.putStatement(stmt5);
        jdbc.putStatement(stmt4);
        jdbc.putStatement(stmt2);

    }

    public static void main(String[] args) throws Exception {

        jdbc = DatabaseHandler.getDatabaseConnection("oratestrb");
        jdbc.openConnection();
        eins();
        eins();
        jdbc.closeConnection();

    }
}
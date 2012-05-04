/*
 * StoredProcs.java
 * 
 * Created on September 6, 2003, 11:06 PM
 */

package com.wanci.customer.bvk;

import java.sql.CallableStatement;
import java.sql.SQLException;

import com.wanci.dmerce.jdbcal.Database;
import com.wanci.java.LangUtil;

/**
 * @author rb
 */
public class StoredProcs {

    private Database jdbcDatabase;

    /** Creates a new instance of StoredProcs */
    public StoredProcs(Database jdbcDatabase) {
        this.jdbcDatabase = jdbcDatabase;
    }

    /**
     * Ruft eine Stored Proc auf, die die Platzhalter für Umlaute wieder in
     * Umlaute uebersetzt
     */
    protected void placeholderToUmlauts(int id) {

        CallableStatement p2u = null;
        try {

            p2u = jdbcDatabase
                .getCallableStatement("{call placeholder2umlauts(?)}");

            LangUtil.consoleDebug(true,
                "Executing stored procedure placeholder2umlauts for MIT-NR "
                    + id);
            p2u.setInt(1, id);
            p2u.execute();
            LangUtil.consoleDebug(true, "placeholder2umlauts done");

        }
        catch (SQLException e) {
            //e.printStackTrace();
            LangUtil.consoleDebug(true, e.getMessage() + " ID=" + id);
            //System.exit(2);
        }

    }

    /**
     * Ruft eine Stored Proc auf, die Umlaute in Platzhalter umwandelt
     */
    protected void umlautsToPlaceholder(int id) {

        CallableStatement u2p = null;
        try {

            u2p = jdbcDatabase
                .getCallableStatement("{call umlauts2placeholder(?)}");

            LangUtil.consoleDebug(true,
                "Executing stored procedure umlauts2placeholder for MIT-NR "
                    + id);
            u2p.setInt(1, id);
            u2p.execute();
            LangUtil.consoleDebug(true, "umlauts2placeholder: Done");

        }
        catch (SQLException e) {
            //e.printStackTrace();
            LangUtil.consoleDebug(true, e.getMessage() + " ID=" + id);
            //System.exit(2);
        }

    }

    protected void checkMemberAfterImport(int id) {

        CallableStatement u2p = null;
        try {

            u2p = jdbcDatabase
                .getCallableStatement("{call checkmemberafterimport(?)}");

            LangUtil.consoleDebug(true,
                "Executing stored procedure checkmemberafterimport for MIT-NR "
                    + id);
            u2p.setInt(1, id);
            u2p.execute();
            LangUtil.consoleDebug(true, "checkmemberafterimport: Done");

        }
        catch (SQLException e) {
            //e.printStackTrace();
            LangUtil.consoleDebug(true, e.getMessage() + " ID=" + id);
            //System.exit(2);
        }

    }

}
/*
 * Created on Jul 25, 2003
 *
 */
package com.wanci.customer.bvk;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.PreparedStatementsPool;

/**
 * @author rb
 * @version $Id: BvkSqlPool.java,v 1.3 2004/05/16 19:44:08 rb Exp $
 * 
 * PreparedStatements fuer den BVK
 *  
 */
public class BvkSqlPool implements PreparedStatementsPool {

    private Database jdbcDatabase;

    public PreparedStatement iMemberCompany;

    public PreparedStatement iMemberEducationProfile;

    public BvkSqlPool(Database jdbcDatabase) throws SQLException {
        
        this.jdbcDatabase = jdbcDatabase;
        
        iMemberCompany = jdbcDatabase
            .getPreparedStatement("INSERT INTO membercompany (id, active, memberid, companyid)"
                + " VALUES (nextval('s_membercompany'), 1, ?, ?)");
        
        iMemberEducationProfile = jdbcDatabase
            .getPreparedStatement("INSERT INTO membereducationprofile"
                + " (id, active, memberid, educationprofileid)"
                + " VALUES (nextval('s_membereducationprofile'), 1, ?, ?)");
        
    }

}
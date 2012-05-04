/*
 * Created on Jul 25, 2003
 *
 */
package com.wanci.dmerce.test;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Date;

import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;

/**
 * @author rb
 * @version $Id: CallableStatementTest.java,v 1.1 2004/01/30 17:00:57 rb Exp $
 *
 * 
 * 
 */
public class CallableStatementTest {

	public static void main(String[] args) throws SQLException {

		System.out.println(new Date() + " START");

		try {
			Database jdbcDatabase =
				DatabaseHandler.getDatabaseConnection("pgsql-sp-test");

			jdbcDatabase.openConnection();

			System.out.println(new Date() + " placeholder2umlauts");
			CallableStatement cstmt1 =
				jdbcDatabase.getCallableStatement(
					"{call placeholder2umlauts(?)}");
			cstmt1.setInt(1, 21801);
			cstmt1.execute();

			System.out.println(new Date() + " umlauts2placeholder");
			CallableStatement cstmt2 =
				jdbcDatabase.getCallableStatement(
					"{call umlauts2placeholder(?)}");
			cstmt2.setInt(1, 21801);
			cstmt2.execute();

			jdbcDatabase.closeConnection();

			System.out.println(new Date() + " STOP");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
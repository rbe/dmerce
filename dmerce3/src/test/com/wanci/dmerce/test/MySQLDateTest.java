/*
 * Created on Jul 30, 2003
 *
 */
package com.wanci.dmerce.test;

import java.sql.PreparedStatement;

import com.wanci.dmerce.csv.Helper;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;

/**
 * @author mf
 * @version $Id: MySQLDateTest.java,v 1.1 2004/01/30 17:00:57 rb Exp $
 */
public class MySQLDateTest {

	public static void main(String[] args) {

		try {
			Database d = DatabaseHandler.getDatabaseConnection("defaultDS");
			d.openConnection();
			PreparedStatement s =
				d.getPreparedStatement("INSERT INTO vdate (datetime, date, time, timestamp) VALUES (?,?,?,?)");
			s.setDate(1, new Helper().parseGermanDate("01.01.2004"));
			s.setDate(2, new Helper().parseGermanDate("02.01.2004"));
			s.setDate(3, new Helper().parseGermanDate("03.01.2004"));
			s.setDate(4, new Helper().parseGermanDate("04.01.2004"));
			s.execute();
			d.closeConnection();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
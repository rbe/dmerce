/*
 * Created on Jul 30, 2003
 *
 */
package com.wanci.dmerce.test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import com.wanci.dmerce.csv.Helper;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;

/**
 * @author rb
 * @version $Id: PostgreSqlJdbcDateTest.java,v 1.1 2004/01/30 17:00:57 rb Exp $
 *
 * 
 * 
 */
public class PostgreSqlJdbcDateTest {

	public static void main(String[] args)
		throws SQLException, ParseException, XmlPropertiesFormatException {

		try {
			Database d = DatabaseHandler.getDatabaseConnection("bvk");
	        Helper h = new Helper();
	
			d.openConnection();
	
			PreparedStatement s =
				d.getPreparedStatement("INSERT INTO jdbcdatetest VALUES (?)");
	
			s.setDate(1, h.parseGermanDate("01.05.1950"));
			s.execute();
	
	        s.setDate(1, h.parseGermanDate("01.01.2003"));
	        s.execute();
	
	        s.setDate(1, h.parseGermanDate("31.12.1950"));
	        s.execute();
	
			d.closeConnection();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
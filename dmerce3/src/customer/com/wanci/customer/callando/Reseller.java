/*
 * Created on Nov 28, 2003
 *  
 */
package com.wanci.customer.callando;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.wanci.dmerce.jdbcal.Database;

class AReseller {

	int id;

	String company;

	int maxFreeAccounts;

	AReseller(int id, String company, int maxFreeAccounts) {
		this.id = id;
		this.company = company;
		this.maxFreeAccounts = maxFreeAccounts;
	}

	public int getMaxFreeAccounts() {
		return maxFreeAccounts;
	}

}

/**
 * @author rb
 * @version $Id: Reseller.java,v 1.3 2004/07/16 13:36:31 rb Exp $ Reseller
 */

public class Reseller {

	Database jdbcDatabase;

	HashMap m = new HashMap();

	public Reseller() {
		//jdbcDatabase = DatabaseHandler.getDatabaseConnection("callandodsl");
		jdbcDatabase = Configuration.getInstance().jdbcDatabase;
	}

	public void loadResellersFromDatabase() throws IllegalArgumentException,
			SQLException {

		//jdbcDatabase.openConnection();

		String stmt = "SELECT ID, Company, MaxFreeAccounts" + " FROM Reseller"
				+ " WHERE active = 1" + " AND MaxFreeAccounts > 0";
		ResultSet rs = jdbcDatabase.executeQuery(stmt);
		
		while (rs.next()) {
			
			m.put(rs.getString(2), new AReseller(rs.getInt(1), rs.getString(2),
					rs.getInt(3)));
			
		}
		
		rs.close();

		//jdbcDatabase.closeConnection();

	}

	public AReseller getReseller(String company) {
		return (AReseller) m.get(company);
	}

}
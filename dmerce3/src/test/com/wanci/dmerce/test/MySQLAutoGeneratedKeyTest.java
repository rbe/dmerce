/*
 * Datei angelegt am 17.11.2003
 */
package com.wanci.dmerce.test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;

/**
 * @author Masanori Fujita
 */
public class MySQLAutoGeneratedKeyTest {
	
	private Database database;

	/**
	 * 
	 */
	public MySQLAutoGeneratedKeyTest() throws SQLException {
		database = DatabaseHandler.getDatabaseConnection("defaultDS");
		database.openConnection();
	}

	public static void main(String[] args) throws SQLException {
		MySQLAutoGeneratedKeyTest instance = new MySQLAutoGeneratedKeyTest();
		/*
		PreparedStatement statement = instance.database.getPreparedStatement("INSERT INTO partyinfo (Name, Telefon, Teilnahme, Trinken) VALUES ('Testmann', '0251-12345', 'komme', 3)");
		int res = statement.executeUpdate();
		System.out.println("Update Result: "+res);
		ResultSet resset =  statement.getGeneratedKeys();
		resset.next();
		System.out.println("Key:"+resset.getInt(1));
		resset.close();
		*/
		PreparedStatement statement = instance.database.getPreparedStatement("SELECT Bier FROM partyinfo");
		ResultSet res = statement.executeQuery();
		while (res.next()) {
			System.out.println(res.getObject(1).getClass().getName());
		}
		statement.close();
	}
}

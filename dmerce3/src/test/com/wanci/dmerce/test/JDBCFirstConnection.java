/*
 * Created on Oct 8, 2003
 *
 */
package com.wanci.dmerce.test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author tw2
 * @version $Id: JDBCFirstConnection.java,v 1.1 2004/01/30 17:00:57 rb Exp $
 *
 */

public class JDBCFirstConnection {

	public static void main(String[] args) {

		int i;
		Connection con;
		Statement stmt = null;

		try {
			Class.forName("oracle.jdbc.OracleDriver");
			con =
				DriverManager.getConnection(
					"jdbc:oracle:thin:pg/pg@10.48.35.3:1521:wanci2");
			stmt = con.createStatement();
			ResultSet r = stmt.executeQuery("SELECT kundennr,name FROM vkunde");
			while (r.next()) {
				System.out.println(
					r.getInt("kundennr") + "   " + r.getString("name"));
			}
			r.close();
			stmt.close();

			DatabaseMetaData db = con.getMetaData();
			String[] t = {"TABLE"};
			ResultSet tables = db.getTables(null, "PG", null, t);
			i = 0;
			while (tables.next()) {
				i++;
				System.out.println(tables.getString("TABLE_NAME"));
			}
			System.out.println("Tabellen gefunden: " + i);
			tables.close();

			ResultSet columns = db.getColumns(null, "PG", "VKUNDE", null);
			i = 0;
			while (columns.next()) {
				i++;
				System.out.println(columns.getString("COLUMN_NAME"));
			}
			System.out.println("Spalten gefunden: " + i);
			columns.close();

			con.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*	
		try {
			stmt.executeUpdate("DROP TABLE jdbctest");
		} catch (SQLException e) {
			// Nichts zu tun
		}
		
		stmt.executeUpdate(
			"CREATE TABLE jdbctest (" + "ID int," + "name char(255)");
		stmt.executeUpdate("CREATE INDEX jdbctestID ON jdbctest ( ID )");
		*/

	}

}
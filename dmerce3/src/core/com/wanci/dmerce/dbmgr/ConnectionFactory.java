/*
 * Created on Oct 27, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.wanci.dmerce.dbmgr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author tw
 *
 * @version $Id: ConnectionFactory.java,v 1.4 2003/10/30 13:10:31 pg Exp $
 */
public class ConnectionFactory {

	/**
	 * create a connection
	 * at this time it can create oracle connections only
	 * @param ph
	 * @return
	 */
	public static Connection create(PropertiesHandler ph) {
		String jdbcString = "";

		//compose oracle-jdbc-string
		//e.g. jdbc:oracle:thin:pg/pg@10.48.35.3:1521:wanci2")
		if (ph.getDatabase().equals("oracle")) {
			jdbcString =
				"jdbc:"
					+ ph.getDatabase()
					+ ":"
					+ ph.getDriver()
					+ ":"
					+ ph.getUser()
					+ "/"
					+ ph.getPassword()
					+ "@"
					+ ph.getHost()
					+ ":"
					+ ph.getPort()
					+ ":"
					+ ph.getSchema();

			//return oracle object
			try {
				Class.forName("oracle.jdbc.OracleDriver");
				return DriverManager.getConnection(jdbcString);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		} //if oracle

		//no applicable database type found
		return null;
	}

	/**
	 * usage example and test
	 * @param args
	 */
	public static void main(String[] args) {

		PropertiesHandler ph = PropertiesHandler.getSingleInstance();

		ph.load("1");
		try {
			Connection con = ConnectionFactory.create(ph);
			Statement stmt = con.createStatement();
			ResultSet r;
			r = stmt.executeQuery("SELECT kundennr,name FROM vkunde");
			while (r.next()) {
				System.out.println(
					r.getInt("kundennr") + "   " + r.getString("name"));
			}
			r.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	} //main
}

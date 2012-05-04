package com.wanci.dmerce.dbmgr;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * @author tw
 *
 * @version $Id: DBmgr.java,v 1.13 2003/10/30 17:51:32 pg Exp $
 */

public class DBmgr {

	/**
	 * connection
	 */
	private Connection con;


	private DBmgrGUI dBmgrGUI;

	/**
	 * set a java connection
	 * @param con
	 */
	public void setConnection(Connection con) {
		this.con = con;
	}

	/**
	 * getter for java connection
	 * @return
	 */
	public Connection getConnection() {
		return con;
	}

	/**
	 * end connection
	 * @return
	 */
	public boolean closeConnection() {

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			System.out.println("Connection was not closed!");
			return false;
		}

		//connection successfully closed
		System.out.println("Connection successfully closed!");
		return true;

	}

	/**
	 * query the database
	 * @param stmt
	 * @return
	 */
	public ResultSet executeQuery(String stmt) {

		if (stmt == null)
			return null;

		ResultSet rs = null;

		try {
			Statement statement = con.createStatement();
			rs = statement.executeQuery(stmt);
			//statement.close();
			//rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("The statement was not executed!");
			return null;
		}

		return rs;

	}

	/**
	 * query database for tables
	 * @return
	 */
	public ResultSet getTables() {

		ResultSet rs = null;
		PropertiesHandler ph = PropertiesHandler.getSingleInstance();

		try {
			DatabaseMetaData db = con.getMetaData();
			String[] t = { "TABLE" };
			rs = db.getTables(null, ph.getUser(), null, t);
			//statement.close();
			//rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("The statement was not executed!");
			return null;
		}

		return rs;

	}

	/**
	 * get Columns of database
	 * @param table
	 * @return
	 */
	public Vector getColumns(String table) {

		if (table == null)
			return new Vector();

		PropertiesHandler ph = PropertiesHandler.getSingleInstance();
		Vector v = new Vector();

		try {
			DatabaseMetaData db = con.getMetaData();

			ResultSet columns = db.getColumns(null, ph.getUser(), table, null);

			while (columns.next()) {
					v.add(columns.getString("COLUMN_NAME"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("The statement was not executed!");
			return new Vector();
		}

		return v;

	}

	public Vector getData(String table, int columnCount) {
		
		Vector result = new Vector();

		try {
			ResultSet rs = executeQuery("SELECT * FROM " + table);
			while (rs.next()) {
				Vector row = new Vector();
				for (int i = 1; i <= columnCount; i++) {
					row.add(rs.getString(i));
				}
				result.add(row);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		return result;				
	
	}
	
	public DefaultTableModel convertToTableModel(Vector data, Vector columns) {
		
		 return new DefaultTableModel(data, columns);
		 
	}

	public static void main(String[] args) {

		System.out.println("DBmgr CLI - please use DbmgrGUI");

	}

	public DBmgrGUI getDBmgrGUI() {
		return dBmgrGUI;
	}

	public void setDBmgrGUI(DBmgrGUI dBmgrGUI) {
		this.dBmgrGUI = dBmgrGUI;
	}

}

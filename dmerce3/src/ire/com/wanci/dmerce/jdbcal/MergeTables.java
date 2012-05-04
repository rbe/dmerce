/*
 * Created on May 14, 2003
 *
 */
package com.wanci.dmerce.jdbcal;

import java.sql.SQLException;

//import com.wanci.dmerce.lib.DmerceProperties;

/**
 * @author rb
 * @version $Id: MergeTables.java,v 1.1 2004/03/29 13:39:31 rb Exp $
 *
 */
public class MergeTables {

	private Database jdbcDatabase;

	private Table table1;

	private Table table2;

	public MergeTables(Database jdbcDatabase) {
		this.jdbcDatabase = jdbcDatabase;
	}

	public MergeTables(Database jdbcDatabase, Table table1, Table table2) {
		this.jdbcDatabase = jdbcDatabase;
		setTable1(table1);
		setTable2(table2);
	}

	/**
	 * IDs von Tabelle 2 die nicht in Tabelle 1 vorhanden sind 
	 *
	 */
	/*
    private ResultSet idNotInTable1() {

		ResultSet rs = null;

		String stmt =
			"SELECT ID"
				+ "  FROM "
				+ table2.getName()
				+ " WHERE ID NOT IN"
				+ " (SELECT ID"
				+ "    FROM "
				+ table1.getName()
				+ ")";

		try {
			rs = jdbcDatabase.executeQuery(stmt);
		}
		catch (IllegalArgumentException e) {
		}
		catch (SQLException e) {
		}

		return rs;

	}
    */

	/**
	 * IDs von Tabelle 1 die nicht in Tabelle 2 vorhanden sind 
	 *
	 */
	/*
    private ResultSet idNotInTable2() {

		ResultSet rs = null;

		String stmt =
			"SELECT ID"
				+ "  FROM "
				+ table1.getName()
				+ " WHERE ID NOT IN"
				+ " (SELECT ID"
				+ "    FROM "
				+ table2.getName()
				+ ")";

		try {
			rs = jdbcDatabase.executeQuery(stmt);
		}
		catch (IllegalArgumentException e) {
		}
		catch (SQLException e) {
		}

		return rs;

	}
    */

	/**
	 * Gleiche Inhalt von Tabelle 1 und Tabelle 2 ab
	 * Tabelle 1 wird dabei als Master angesehen!
	 * 
	 * IDs, die in Tabelle 1 aber nicht in Tabelle 2 vorhanden sind,
	 * werden nach Tabelle 2 kopiert
	 * 
	 * IDs, die in Tabelle 2 aber nicht in Tabelle 1 vorhanden sind,
	 * werden in Tabelle 2 geloescht
	 * 
	 * Voraussetzung: die Tabellen sind exakt gleich
	 *
	 */
	public void merge() throws SQLException {

		String insertStmt =
			"INSERT INTO "
				+ table2.getName()
				+ " SELECT * FROM "
				+ table1.getName()
				+ " WHERE ID NOT IN (SELECT ID FROM "
				+ table2.getName()
				+ ")";

		String deleteStmt =
			"DELETE FROM "
				+ table2.getName()
				+ " WHERE ID NOT IN (SELECT ID FROM "
				+ table1.getName()
				+ ")";

		//System.out.println(insertStmt);
		jdbcDatabase.executeUpdate(insertStmt);

		//System.out.println(deleteStmt);
		jdbcDatabase.executeUpdate(deleteStmt);

	}

	public void setTable1(Table table1) {
		this.table1 = table1;
	}

	public void setTable2(Table table2) {
		this.table2 = table2;
	}

	public static void main(String[] args) throws SQLException {

		/*
		
		System.out.println("START");
		
		DmerceProperties dp = new DmerceProperties();
		Database d = dp.getDatabaseConnection("rb2");
		d.openConnection();
		MergeTables mt =
			new MergeTables(
				d,
				new Table("x1"),
				new Table("x2"));
		mt.merge();
		d.closeConnection();
		
		System.out.println("DONE");
		
		*/

	}

}
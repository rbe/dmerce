/*
 * Created on Apr 21, 2003
 *
 */
package com.wanci.customer.bvk;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.dmerce.jdbcal.MergeTables;
import com.wanci.dmerce.jdbcal.Table;
import com.wanci.dmerce.kernel.Boot;

/**
 * @author rb
 * @version $Id: GenerateContentAttributeTables.java,v 1.2 2003/11/26 16:16:58 rb Exp $
 *
 */
public class GenerateContentAttributeTables {

	private Database jdbcDatabase;

	private Vector attributeIds = new Vector();

	private boolean haveAttributeIds = false;

	private boolean dropOnly = false;

	private boolean dropAllOnly = false;

	private String actExtContentQuery =
		" SELECT ID, Navigationid, c.CreatedDateTime"
			+ "   FROM Content c"
			+ "  WHERE ((SELECT COUNT(*) FROM AttributeEverything"
			+ "           WHERE Chart = 'Content'"
			+ "             AND ChartID = c.ID) = 0)"
			+ "             AND DisplayRange = 2"
			+ "             AND CheckStatusID = 1"
			+ "             AND BVID = 0"
			+ "             AND ("
			+ "                  (ValidFrom <= Current_Date"
			+ "                 AND"
			+ "                   ValidUntil >= Current_Date)"
			+ "                  OR"
			+ "                  (ValidFrom IS NULL"
			+ "                 AND"
			+ "                   ValidUntil IS NULL)"
			+ "                  OR"
			+ "                  (ValidFrom IS NULL"
			+ "                 AND"
			+ "                   ValidUntil >= Current_Date)"
			+ "                  OR"
			+ "                  (ValidFrom <= Current_Date"
			+ "                 AND"
			+ "                   ValidUntil IS NULL)"
			+ "        )";

	private String actIntContentAttrIDQuery1 =
		" SELECT c.ID, c.CreatedDateTime"
			+ "   FROM AttributeEverything ae, Content c"
			+ "  WHERE ae.AttributeID = ";

	private String actIntContentAttrIDQuery2 =
		"   AND c.DisplayRange = 1"
			+ "   AND c.CheckStatusID = 1"
			+ "   AND c.BVID = 0"
			+ "   AND ("
			+ "        (c.ValidFrom <= Current_Date"
			+ "       AND"
			+ "         c.ValidUntil >= Current_Date)"
			+ "        OR"
			+ "        (c.ValidFrom IS NULL"
			+ "       AND"
			+ "         c.ValidUntil IS NULL)"
			+ "        OR"
			+ "        (c.ValidFrom IS NULL"
			+ "       AND"
			+ "         c.ValidUntil >= Current_Date)"
			+ "        OR"
			+ "        (c.ValidFrom <= Current_Date"
			+ "       AND"
			+ "         c.ValidUntil IS NULL)"
			+ "         )"
			+ "  AND ae.ChartID = c.ID";

	private String actIntContentNoAttrQuery =
		" SELECT ID, NavigationID, c.CreatedDateTime"
			+ "   FROM Content c"
			+ "  WHERE ((SELECT COUNT(*)"
			+ "            FROM AttributeEverything"
			+ "           WHERE Chart = 'Content'"
			+ "             AND ChartID = c.ID) = 0)"
			+ "             AND DisplayRange = 1"
			+ "             AND CheckStatusID = 1"
			+ "             AND BVID = 0"
			+ "             AND ("
			+ "                   (ValidFrom <= Current_Date"
			+ "                  AND"
			+ "                    ValidUntil >= Current_Date)"
			+ "                   OR"
			+ "                   (ValidFrom IS NULL"
			+ "                  AND"
			+ "                    ValidUntil IS NULL)"
			+ "                   OR"
			+ "                   (ValidFrom IS NULL"
			+ "                  AND"
			+ "                    ValidUntil >= Current_Date)"
			+ "                   OR"
			+ "                   (ValidFrom <= Current_Date"
			+ "                  AND"
			+ "                    ValidUntil IS NULL)"
			+ "                 )";

	private String archContentAttrIDQuery1 =
		" SELECT c.ID, c.ArchiveFolderID, c.CreatedDateTime"
			+ "   FROM AttributeEverything ae, Content c"
			+ "  WHERE ae.AttributeID = ";

	private String archContentAttrIDQuery2 =
		"    AND c.DisplayRange = 1"
			+ "    AND c.CheckStatusID = 1"
			+ "    AND c.BVID = 0"
			+ "    AND ValidUntil < Current_Date"
			+ "    AND ae.ChartID = c.ID";

	private String archExtContentQuery =
		" SELECT ID, ArchiveFolderID, c.CreatedDateTime"
			+ "   FROM Content c"
			+ "  WHERE ((SELECT COUNT(*) FROM AttributeEverything "
			+ "           WHERE Chart = 'Content'"
			+ "             AND ChartID = c.ID) = 0)"
			+ "    AND DisplayRange = 2"
			+ "    AND CheckStatusID =  1"
			+ "    AND BVID = 0"
			+ "    AND ValidUntil < Current_Date";

	private String archIntContentNoAttrQuery =
		" SELECT ID, ArchiveFolderID, c.CreatedDateTime"
			+ "   FROM Content c"
			+ "  WHERE ((SELECT COUNT(*) FROM AttributeEverything"
			+ "           WHERE Chart = 'Content'"
			+ "             AND ChartID = c.ID) = 0)"
			+ "             AND DisplayRange = 1"
			+ "             AND CheckStatusID = 1"
			+ "             AND BVID = 0"
			+ "             AND ValidUntil < Current_Date";

	public GenerateContentAttributeTables() throws DmerceException {
		jdbcDatabase = DatabaseHandler.getDatabaseConnection("bvk");
	}

	private void createTable(String name, String query) {

		String stmt;
		String qdName = "qd_" + name;

		dropTable(qdName);

		if (dropAllOnly)
			dropTable(name);

		if (!dropOnly && !dropAllOnly) {

			try {

				e("  Creating table '" + qdName + "'");
				stmt = "CREATE TABLE " + qdName + " AS" + query;
				jdbcDatabase.executeQuery(stmt);

			} catch (Exception e) {
				e(
					"  Cannot create table '"
						+ qdName
						+ "': "
						+ e.getMessage().trim());
			}

			try {

				e("  Creating table '" + name + "'");
				stmt = "CREATE TABLE " + name + " AS" + query;
				jdbcDatabase.executeQuery(stmt);

			} catch (Exception e) {
				e(
					"  Cannot create table '"
						+ name
						+ "': "
						+ e.getMessage().trim());
			}

		} else {
			e("  Drop/DropAll mode. Won't create '" + name + "'");
		}

	}

	public void createIdTables(String name, String query1, String query2) {

		int attributeId;
		String stmt;

		try {

			Iterator i = getAttributeIdIterator();

			while (i.hasNext()) {

				attributeId = ((Integer) i.next()).intValue();
				String nameId = name + attributeId;
				String qdNameId = "qd_" + name + attributeId;

				dropTable(qdNameId);

				if (dropAllOnly)
					dropTable(nameId);

				if (!dropOnly && !dropAllOnly) {

					try {

						stmt =
							"CREATE TABLE "
								+ qdNameId
								+ " AS"
								+ query1
								+ attributeId
								+ query2;

						e("  Creating id table '" + qdNameId + "'");
						jdbcDatabase.executeQuery(stmt);

					} catch (Exception e) {
						e(
							"  Cannot create id table '"
								+ qdNameId
								+ "': "
								+ e.getMessage().trim());
					}

					try {

						stmt =
							"CREATE TABLE "
								+ nameId
								+ " AS"
								+ query1
								+ attributeId
								+ query2;
						e("  Creating id table '" + nameId + "'");
						jdbcDatabase.executeQuery(stmt);

					} catch (Exception e) {
						e(
							"  Cannot create id table '"
								+ nameId
								+ "': "
								+ e.getMessage().trim());
					}

				} else {
					e("  Drop/DropAll mode. Won't create '" + nameId + "'");
				}

			}

		} catch (Exception e) {
			e("  Cannot get attribute ids: " + e.getMessage().trim());
		}

	}

	public void createActExtContent() {
		createTable("ActExtContent", actExtContentQuery);
	}

	public void createActIntContentAttrID() {

		createIdTables(
			"ActIntContentAttrID",
			actIntContentAttrIDQuery1,
			actIntContentAttrIDQuery2);

	}

	public void createActIntContentNoAttr() {
		createTable("ActIntContentNoAttr", actIntContentNoAttrQuery);
	}

	public void createArchContentAttrID() {

		createIdTables(
			"ArchContentAttrID",
			archContentAttrIDQuery1,
			archContentAttrIDQuery2);

	}

	public void createArchExtContent() {
		createTable("ArchExtContent", archExtContentQuery);
	}

	public void createArchIntContentNoAttr() {
		createTable("ArchIntContentNoAttr", archIntContentNoAttrQuery);
	}

	public void closeConnection() throws SQLException {
		jdbcDatabase.closeConnection();
	}

	private void dropTable(String name) {

		e("  Dropping table '" + name + "'");

		try {
			jdbcDatabase.executeQuery("DROP TABLE " + name);
		} catch (SQLException e) {
			e("  Cannot drop table '" + name + "': " + e.getMessage().trim());
		}

	}

	public void e(Object o) {
		System.out.println(new Date() + " " + o);
	}

	private Iterator getAttributeIdIterator() throws SQLException {

		if (!haveAttributeIds) {

			String stmt = "SELECT ID FROM Attribute";

			ResultSet rs = jdbcDatabase.executeQuery(stmt);
			while (rs.next()) {
				attributeIds.add(new Integer(rs.getInt(1)));
			}

			haveAttributeIds = true;

		}

		return attributeIds.iterator();

	}

	public void openConnection() throws SQLException {
		jdbcDatabase.openConnection();
	}

	public void setDropOnly() {
		dropOnly = true;
	}

	public void setDropAllOnly() {
		dropAllOnly = true;
	}

	private void updateTable(String name) {

		if (!dropOnly && !dropAllOnly) {

			try {

				MergeTables mt =
					new MergeTables(
						jdbcDatabase,
						new Table("qd_" + name),
						new Table(name));

				mt.merge();

			} catch (Exception e) {
				e(
					"  Cannot update table '"
						+ name
						+ "': "
						+ e.getMessage().trim());
			}

		} else {
			e("  Drop/DropAll mode. Won't update '" + name + "'");
		}

	}

	private void updateIdTables(String name) {

		int attributeId;

		if (!dropOnly && !dropAllOnly) {

			try {

				Iterator i = getAttributeIdIterator();

				while (i.hasNext()) {

					attributeId = ((Integer) i.next()).intValue();
					String tname = name + attributeId;

					e("  Updating table '" + tname + "'");
					updateTable(tname);

				}

			} catch (Exception e) {
				e(
					"  Cannot update table '"
						+ name
						+ "': "
						+ e.getMessage().trim());
			}

		} else {
			e("  Drop/DropAll mode. Won't update '" + name + "'");
		}

	}

	public void updateActExtContent() {
		updateTable("ActExtContent");
	}

	public void updateActIntContentAttrID() {
		updateIdTables("ActIntContentAttrID");
	}

	public void updateActIntContentNoAttr() {
		updateTable("ActIntContentNoAttr");
	}

	public void updateArchContentAttrID() {
		updateIdTables("ArchContentAttrID");
	}

	public void updateArchExtContent() {
		updateTable("ArchExtContent");
	}

	public void updateArchIntContentNoAttr() {
		updateTable("ArchIntContentNoAttr");
	}

	public static void main(String[] args) {

		Boot.printCopyright();

		try {
			GenerateContentAttributeTables gt =
				new GenerateContentAttributeTables();

			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("--drop"))
					gt.setDropOnly();
				else if (args[i].equals("--dropall"))
					gt.setDropAllOnly();
			}

			try {
				gt.e("Opening connection to SQL database");
				gt.openConnection();
			} catch (SQLException se) {
				System.out.println(
					"Cannot open connection to database: "
						+ se.getMessage().trim());
				System.exit(2);
				//se.printStackTrace();
			}

			System.out.println(new Date() + " *** Creating new tables ***");

			gt.e("'ActExtContent'");
			gt.createActExtContent();

			gt.e("'ActIntContentAttr<ID>'");
			gt.createActIntContentAttrID();

			gt.e("'ActIntContentNoAttr'");
			gt.createActIntContentNoAttr();

			gt.e("'ArchContentAttr<ID>'");
			gt.createArchContentAttrID();

			gt.e("'ArchExtContent'");
			gt.createArchExtContent();

			gt.e("'ArchIntContentNoAttr'");
			gt.createArchIntContentNoAttr();

			System.out.println(new Date() + " *** Updating tables ***");

			gt.e("'ActExtContent'");
			gt.updateActExtContent();

			gt.e("'ActIntContentAttr<ID>'");
			gt.updateActIntContentAttrID();

			gt.e("'ActIntContentNoAttr'");
			gt.updateActIntContentNoAttr();

			gt.e("'ArchContentAttr<ID>'");
			gt.updateArchContentAttrID();

			gt.e("'ArchExtContent'");
			gt.updateArchExtContent();

			gt.e("'ArchIntContentNoAttr'");
			gt.updateArchIntContentNoAttr();

			try {
				gt.e("Closing connection to SQL database");
				gt.closeConnection();
			} catch (SQLException se) {
				System.out.println(
					"Cannot close connection to database: "
						+ se.getMessage().trim());
				//se.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
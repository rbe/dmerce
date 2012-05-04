/**
 * Created on 18.01.2003
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package com.wanci.dmerce.jdbcal;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author rb
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ManageTable {

	private Database jdbcDatabase;

	private Table table;

	private boolean dropBeforeCreate = false;

	public ManageTable(Database jdbcDatabase, Table table) {
		this.jdbcDatabase = jdbcDatabase;
		this.table = table;
	}

	public void create() throws SQLException {

		if (dropBeforeCreate)
			drop();

		StringBuffer stmt =
			new StringBuffer("CREATE TABLE " + table.getName() + " (");

		Iterator i = table.getFieldIterator();

		while (i.hasNext()) {

			Map.Entry o = (Map.Entry) i.next();
			String fieldName = (String) o.getKey();

			Field jdbcField = (Field) o.getValue();
			jdbcField.setDatabase(jdbcDatabase);

			stmt.append(fieldName).append(" ").append(
				jdbcField.getDefinition());
			if (i.hasNext())
				stmt.append(", ");

		}

		stmt.append(")");

		jdbcDatabase.executeQuery(stmt.toString());
	}

	public void createIndexes() throws SQLException {
		Iterator i = table.getFieldIterator();

		while (i.hasNext()) {

			Map.Entry o = (Map.Entry) i.next();
			String fieldName = (String) o.getKey();

			Field jdbcField = (Field) o.getValue();

			if (jdbcField.isIndex() || jdbcField.isUniqueIndex()) {

				StringBuffer sb = new StringBuffer("CREATE ");

				if (jdbcField.isUniqueIndex())
					sb.append("UNIQUE ");

				sb.append(
					"INDEX i_"
						+ table.getName()
						+ "_"
						+ fieldName
						+ " ON "
						+ table.getName()
						+ "("
						+ fieldName
						+ ")");

				jdbcDatabase.executeQuery(sb.toString());
			} //if field
		} //while
	}

	public void drop() throws SQLException {
		jdbcDatabase.executeQuery("DROP TABLE " + table.getName());
	}

	public Table getTable() {
		return table;
	}

	public void insert(TableRow row) throws SQLException {
		jdbcDatabase.executeUpdate(row.generateInsertStatement());
	}

	public boolean isDropBeforeCreate() {
		return dropBeforeCreate;
	}

	public void setDropBeforeCreate(boolean dropBeforeCreate) {
		this.dropBeforeCreate = dropBeforeCreate;
	}

	public static void main(String[] args) {
		Database jdbc = null;
		try {
			jdbc = DatabaseHandler.getDatabaseConnection("ncc");
			jdbc.openConnection();
		} catch (SQLException e) {
			try {
				jdbc.dumpSqlException(e);
			} catch (SQLException se) {
				;
			}
		}

		Table table1 = new Table("table1");

		Field f1 = new Field("field1", new DatatypeNumber(38, 2));
		f1.setDefault(0);
		f1.setNotNull();
		f1.setUniqueIndex();

		table1.addField(f1);

		Field f2 = new Field("field2", new DatatypeNumber());
		f2.unsetDefault();
		f2.setNull();
		f2.setIndex();

		table1.addField(f2);

		Field f3 = new Field("field3", new DatatypeVarchar(200));

		table1.addField(f3);

		ManageTable mtable1 = new ManageTable(jdbc, table1);

		try {
			mtable1.create();
			mtable1.createIndexes();

			TableRow row = new TableRow(table1);
			row.addValue(f1, 1);
			row.addValue(f2, 2);

			mtable1.insert(row);

			mtable1.drop();

			jdbc.closeConnection();
		} catch (SQLException e) {
			try {
				jdbc.dumpSqlException(e);
				jdbc.closeConnection();
			} catch (SQLException se) {
			}
		}

	}

}
package com.wanci.dmerce.jdbcal;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

/**
 * @author rb
 * @version $Id: ManageSchema.java,v 1.1 2004/03/29 13:39:31 rb Exp $
 *
 */
public abstract class ManageSchema {

	/**
	 * 
	 */
	private Database jdbcDatabase;

	/**
	 * 
	 */
	private Vector jdbcTables = new Vector();

	/**
	 * 
	 */
	private boolean dropAllBeforeCreate = false;

	/** Creates a new instance of ManageSchema
	 */
	public ManageSchema(Database jdbcDatabase) {
		this.jdbcDatabase = jdbcDatabase;
	}

	/**
	 * 
	 * @param t
	 */
	public void addTable(Table t) {
		jdbcTables.add(new ManageTable(jdbcDatabase, t));
	}

	/**
	 * 
	 * @param jdbcManageTable
	 */
	public void addTable(ManageTable jdbcManageTable) {
		jdbcTables.add(jdbcManageTable);
	}

	/**
	 * 
	 * @throws SQLException
	 */
	public void createTables() throws SQLException {

		Iterator i = jdbcTables.iterator();

		while (i.hasNext()) {

			ManageTable j = (ManageTable) i.next();

			if (dropAllBeforeCreate) {
				//j.setDropBeforeCreate(false);
				try {
					j.drop();
				}
				catch (SQLException e) {
				}
			}

			j.create();
			j.createIndexes();

		}
	}

	/**
	 * 
	 * @throws SQLException
	 */
	public void dropTables() throws SQLException {

		Iterator i = jdbcTables.iterator();

		while (i.hasNext()) {

			ManageTable j = (ManageTable) i.next();
			j.drop();

		}

	}
    
	/**
	 * 
	 * @return
	 */
	public boolean isDropAllBeforeCreate() {
		return dropAllBeforeCreate;
	}

	/**
	 * 
	 * @param dropAllBeforeCreate
	 */
	public void setDropAllBeforeCreate(boolean dropAllBeforeCreate) {
		this.dropAllBeforeCreate = dropAllBeforeCreate;
	}

}
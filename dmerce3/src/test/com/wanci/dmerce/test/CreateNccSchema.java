/*
 * Created on Jan 30, 2003
 *
 */
package com.wanci.dmerce.test;

import java.sql.SQLException;

import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.dmerce.jdbcal.DatatypeNumber;
import com.wanci.dmerce.jdbcal.DatatypeVarchar;
import com.wanci.dmerce.jdbcal.Field;
import com.wanci.dmerce.jdbcal.ManageSchema;
import com.wanci.dmerce.jdbcal.Table;

/**
 * @author rb
 * @version $Id: CreateNccSchema.java,v 1.1 2004/01/30 17:00:57 rb Exp $
 *
 * Klasse legt alle Tabellen in einer Datenbank an,
 * die das NCC braucht.
 * 
 */
public class CreateNccSchema extends ManageSchema {

	/**
	 * 
	 * @param jdbcDatabase
	 */
	public CreateNccSchema(Database jdbcDatabase) {
		super(jdbcDatabase);
	}

	/**
	 * 
	 *
	 */
	public void createTableSrvServerSvcs() {

		Table t = new Table("SrvServerSvcs_RB");
		t.addDmerceFields();

		Field serverId =
			new Field("ServerID", new DatatypeNumber());
		serverId.setIndex();
		t.addField(serverId);

		Field serviceId =
			new Field("ServiceID", new DatatypeNumber());
		serviceId.setIndex();
		t.addField(serviceId);

		Field statusId =
			new Field("StatusID", new DatatypeNumber());
		statusId.setIndex();
		t.addField(statusId);

		Field statusMessage =
			new Field("StatusMessage", new DatatypeVarchar(100));
		t.addField(statusMessage);

		addTable(t);
		
	}

	/**
	 * 
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {

		try {
			Database jdbcDatabase = DatabaseHandler.getDatabaseConnection("ncc");
			jdbcDatabase.openConnection();
	
			CreateNccSchema c = new CreateNccSchema(jdbcDatabase);
			c.createTableSrvServerSvcs();
			c.setDropAllBeforeCreate(true);
			c.createTables();
	
			jdbcDatabase.closeConnection();
		}
		catch (Exception e)  {
			e.printStackTrace();
		}
		
	}

}
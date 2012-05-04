/*
 * Datei angelegt am 27.01.2004
 */
package com.wanci.dmerce.jdbcal;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.hsqldb.jdbcResultSet;

/**
 * HsqldbHelper
 *
 * @author Masanori Fujita
 */
public class HsqldbHelper {
	
	private ResultSet resultset;
	
	public HsqldbHelper(ResultSetMetaData resultset) {
		
		jdbcResultSet hsqldbResultset = (jdbcResultSet) resultset;
		this.resultset = hsqldbResultset;
	}
	
	public String getColumnClassName(int i) {
		Object o;
		try {
			resultset.first();
			o = resultset.getObject(i);
			resultset.beforeFirst();
			if (o == null)
				return Object.class.getName();
			else
				return o.getClass().getName();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Object.class.getName();
	}

}

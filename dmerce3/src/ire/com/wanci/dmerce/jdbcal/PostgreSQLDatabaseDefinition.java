/**
 * Created on Jan 29, 2003
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package com.wanci.dmerce.jdbcal;

/**
 * @author rb
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class PostgreSQLDatabaseDefinition implements DatabaseDefinition {

	public static String vendor = "PostgreSQL";

	public static String URL = "http://www.postgresql.org/";

	public PostgreSQLDatabaseDefinition() {
	}

	/**
	 * @see com.wanci.dmerce.dmf.JdbcDatabaseDefinition#isMySQL()
	 */
	public boolean isMySQL() {
		return false;
	}

	/**
	 * @see com.wanci.dmerce.dmf.JdbcDatabaseDefinition#isOracle()
	 */
	public boolean isOracle() {
		return false;
	}

	/**
	 * @see com.wanci.dmerce.dmf.JdbcDatabaseDefinition#isPostgreSQL()
	 */
	public boolean isPostgreSQL() {
		return true;
	}

	/**
	 * @see com.wanci.dmerce.dmf.JdbcDatabaseDefinition#isPointbase()
	 */
	public boolean isPointbase() {
		return false;
	}

	/**
	 * @see com.wanci.dmerce.dmf.JdbcDatabaseDefinition#isHsqldb()
	 */
	public boolean isHsqldb() {
		return false;
	}

}
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
public interface DatabaseDefinition {
	
	boolean isMySQL();
	
	boolean isOracle();
	
	boolean isPostgreSQL();
	
	boolean isPointbase();
	
	boolean isHsqldb();

}
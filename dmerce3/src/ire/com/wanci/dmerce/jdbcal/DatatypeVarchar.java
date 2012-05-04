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
public class DatatypeVarchar extends AbstractDatatype {

	private int length;

	public DatatypeVarchar(int length) {
		super(length);
		this.length = length;
	}

	public String getMySQLDefinition() {
		return "VARCHAR(" + length + ")";
	}

	public String getOracleDefinition() {
		return "VARCHAR2(" + length + ")";
	}

	public String getPointbaseDefinition() {
		return "VARCHAR(" + length + ")";
	}

	public String getPostgreSQLDefinition() {
		return "VARCHAR(" + length + ")";
	}

}
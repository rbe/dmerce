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
public class Field {

	private Datatype jdbcDatatype;

	private Database jdbcDatabase;

	private String name;

	private boolean isNull = true;

	private Object defaultValue;
	
	private boolean index = false;
	
	private boolean uniqueIndex = false;

	public Field(String name, Datatype jd) {
		this.name = name;
		this.jdbcDatatype = jd;
	}

	public String getDefinition() {
		StringBuffer s = new StringBuffer();

		DatabaseDefinition dd = jdbcDatabase.getDatabaseDefinition();
		if (dd.isOracle())
			s.append(jdbcDatatype.getOracleDefinition());
		else if (dd.isPostgreSQL())
			s.append(jdbcDatatype.getPostgreSQLDefinition());
		else if (dd.isMySQL())
			s.append(jdbcDatatype.getMySQLDefinition());
		else if (dd.isPointbase())
			s.append(jdbcDatatype.getPointbaseDefinition());

		if (defaultValue != null)
			if (defaultValue instanceof String)
				s.append(" DEFAULT '" + defaultValue + "'");
			else
				s.append(" DEFAULT " + defaultValue);

		if (isNull)
			s.append(" NULL");
		else if (!isNull)
			s.append(" NOT NULL");

		return s.toString();
	}

	public Datatype getDatatype() {
		return jdbcDatatype;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}
	
	public String getName() {
		return name;
	}

	public boolean isIndex() {
		return index;
	}

	public boolean isNull() {
		return isNull;	
	}
	
	public boolean isUniqueIndex() {
		return uniqueIndex;
	}

	public void setDatabase(Database jdbcDatabase) {
		this.jdbcDatabase = jdbcDatabase;
	}

	public void setNull() {
		isNull = true;
	}

	public void setNotNull() {
		isNull = false;
	}

	public void setDefault(Object value) {
		defaultValue = value;
	}
	
	public void setDefault(int value) {
		defaultValue = new Integer(value);
	}

	public void setDefault(float value) {
		defaultValue = new Float(value);
	}

	public void setDefault(double value) {
		defaultValue = new Double(value);
	}
	
	public void setDefault(byte value) {
		defaultValue = new Byte(value);
	}
	
	public void setIndex() {
		index = true;
	}
	
	public void setDefault(boolean value) {
		defaultValue = new Boolean(value);
	}
	
	public void setUniqueIndex() {
		uniqueIndex = true;
	}
	
	public void unsetDefault() {
		defaultValue = null;
	}

}
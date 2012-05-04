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
public class DatatypeNumber extends AbstractDatatype {

	//private int length;

	//private int precision;

	public DatatypeNumber() {
		super();
	}

	public DatatypeNumber(int length) {
		super(length);
		//this.length = length;
	}

	public DatatypeNumber(int length, int precision) {
		super(length, precision);
		//this.length = length;
		//this.precision = precision;
	}

	public String getMySQLDefinition() {
		String s = new String();

		int length = getLength();
		int precision = getPrecision();

		if (length > 0 && precision == 0) {
			if (length <= 11)
				s = "INT(" + length + ")";
			else if (length > 11)
				s = "BIGINT(" + length + ")";
		}
		else if (precision > 0) {
			if (precision <= 5)
				s = "FLOAT(" + length + ", " + precision + ")";
			if (precision > 5)
				s = "DOUBLE(" + length + ", " + precision + ")";
		}
		else if (length == 0 && precision == 0)
			s = "INT";

		return s;
	}

	public String getOracleDefinition() {
		String s = new String();

		int length = getLength();
		int precision = getPrecision();

		if (length > 0 && precision == 0)
			s = "NUMBER(" + length + ")";
		else if (length > 0 && precision > 0)
			s = "NUMBER(" + length + ", " + precision + ")";
		else if (length == 0)
			s = "NUMBER";

		return s;
	}

	public String getPointbaseDefinition() {
		return "?";
	}

	public String getPostgreSQLDefinition() {
		String s = new String();

		int length = getLength();
		int precision = getPrecision();

		if (length > 0 && precision > 0)
			s = "NUMBER(" + length + ", " + precision + ")";
		else if (length == 0)
			s = "INT";

		return s;
	}

}
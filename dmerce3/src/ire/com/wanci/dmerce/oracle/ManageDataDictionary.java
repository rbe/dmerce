/*
 * Created on Mar 16, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.wanci.dmerce.jdbcal.Oracle;

/**
 * @author rb2
 * @version $Id: ManageDataDictionary.java,v 1.1 2004/03/29 13:39:37 rb Exp $
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class ManageDataDictionary
	implements ManageDataDictionaryView {

	/**
	 * 
	 */
	protected Oracle jdbcOracle = null;

	/**
	 * 
	 */
	protected ResultSet result = null;

	/**
	 * 
	 */
	protected String stmt;

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.OracleDataDictionaryView#discover()
	 */
	public void discover() {
		if (jdbcOracle != null) {
			try {
				result = jdbcOracle.executeQuery(getStatement());
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.OracleDataDictionaryView#getStatement()
	 */
	public String getStatement() {
		return stmt;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.OracleDataDictionaryView#recreate()
	 */
	public abstract String recreate();

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.OracleDataDictionaryView#setStatement(java.lang.String)
	 */
	public abstract void setStatement();

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.ncc.OracleDataDictionaryView#dump()
	 */
	public void dump() {
		if (result != null)
			try {
				jdbcOracle.dumpResultSet(result);
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
	}

}
/*
 * Created on 22.07.2003
 *
 */
package com.wanci.dmerce.qxtl;

import java.sql.CallableStatement;
import java.util.List;

import org.jdom.Element;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;

/**
 * @author pg
 * @version $Id: SqlTxm.java,v 1.5 2003/09/30 14:27:37 pg Exp $
 *
 */
public class SqlTxm {

	/**
	 * Erreichbarkeit der Datenbank.
	 */
	private boolean databaseAvailable = false;

	/**
	 * gewöhnliche Fehlersuch-Variable
	 */
	private boolean DEBUG = false;

	/**
	 * database handler
	 */
	private Database jdbcDatabase;

	/**
	 * Versucht eine Datenbankverbindung zu erstellen.
	 *
	 * @throws XmlPropertiesFormatException
	 */
	public SqlTxm() throws XmlPropertiesFormatException {

		try {
			jdbcDatabase = DatabaseHandler.getDatabaseConnection("test1");
			jdbcDatabase.openConnection();
			databaseAvailable = true;
		} catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}
	}

	/**
	 * write sql query to database if database available
	 * else: write to queue
	 *
	 * @param command
	 */
	public int execute(Element step, String command) {

		if (databaseAvailable) {
			//now check type
			String type = step.getAttributeValue("type");

			if (type.equals("") || type == null) {
				//send query to database
				try {
					jdbcDatabase.executeUpdate(command);
					return 0;
				} catch (Exception e) {
					e.printStackTrace();
					return -1;
				}
			} else if (type.equals("sp")) {
				/*
				CallableStatement cs;
				try {
				                        cs =
				                                jdbcDatabase.prepareCall(
				                                        "{call "
				                                                + step.getChild("command").getAttributeValue(
				                                                        "value")
				                                                + "}");
				                        // Set the value for the IN parameter
				                        cs.setString(1, "a string");
				                        // Register the type of the OUT parameter
				                        cs.registerOutParameter(1, "Types.VARCHAR");
				                        cs.execute();
				} catch (Exception e) {
				}
				 */;
			} //else
		}
		return -1;
	}

	/**
	 * Führt einen Stored Proc aus.
	 *
	 * @param step
	 * @param command
	 * @return
	 */
	public int executeStoredProc(Element step) {

		String procname = step.getChild("cmd").getAttributeValue("value");
		List parameter = step.getChildren("parameter");

		int inParameterCount = parameter.size();

		//String returntype =
		//	step.getChild("returnvalue").getAttributeValue("type");
		//String returnvalue = step.getChild("returnvalue").getAttributeValue("value");
		//String name;

		String type;
		String value;

		int i;
		String questionmarks = "";

		for (i = 0; i < inParameterCount; i++) {
			if (!questionmarks.equals(""))
				questionmarks = questionmarks + ",";
			questionmarks = questionmarks + "?";
		}
		//prüfe, ob ein Returnwert vorhanden sein soll,
		//dann noch ein Fragezeichen mehr
		/*
		if (returntype != null && !returntype.equals("")) {
		        if (!questionmarks.equals(""))
		                questionmarks = questionmarks + ",";
		        questionmarks = questionmarks + "?";
		}
		 */

		try {
			CallableStatement cstmt =
				jdbcDatabase.getCallableStatement(
					"{call " + procname + "(" + questionmarks + ")}");

			for (i = 0; i < inParameterCount; i++) {

				//name = ((Element) parameter.get(i)).getAttributeValue("name");
				type = ((Element) parameter.get(i)).getAttributeValue("type");
				value = ((Element) parameter.get(i)).getAttributeValue("value");

				if (type.equals("int")) {

					if (value.equals("null"))
						cstmt.setNull(i + 1, java.sql.Types.INTEGER);
					else
						cstmt.setInt(i + 1, Integer.parseInt(value));
				} else if (type.equals("string")) {
					cstmt.setString(i + 1, value);
				}

			}

			cstmt.execute();
		} catch (Exception e) {

			e.printStackTrace();
			return -1;
		}

		return 0;
	}

	/**
	 * testing routine
	 */
	public static void main(String[] args)
		throws XmlPropertiesFormatException {

		Database db;
		try {
			db = DatabaseHandler.getDatabaseConnection("test1");
			db.openConnection();
			CallableStatement cstmt =
				db.getCallableStatement(
					"{call sp_nccSavePortScanResult (?,?,?,?,?,?)}");
			cstmt.setString(1, "10.48.35.3");
			cstmt.setInt(2, 80);
			cstmt.setString(3, "tcp");
			cstmt.setString(4, "closed");
			cstmt.setInt(5, 1000);
			cstmt.setNull(6, java.sql.Types.INTEGER);
			//			cstmt.registerOutParameter(7, java.sql.Types.INTEGER);
			cstmt.execute();
		} catch (Exception e) {
			System.err.println("exception");
		}

	}

}

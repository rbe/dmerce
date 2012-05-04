/*
 * Created on Mar 24, 2003
 *
 */
package com.wanci.dmerce.csv;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.wanci.dmerce.exceptions.NoCsvDataPresentException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: CsvToSqlInsert.java,v 1.1 2004/03/29 13:39:35 rb Exp $
 *
 */
public class CsvToSqlInsert {

	/**
	 * 
	 */
	private boolean debug = false;

	/**
	 * 
	 */
	private Helper helper;

	/**
	 * 
	 */
	private CsvSettings csvSettings;

	/**
	 * 
	 */
	private CsvHeader csvHeader;

	/**
	 * 
	 */
	//private int csvHeaderFieldCount;

	/**
	 * 
	 */
	private Mapping csvMapping;

	/**
	 * 
	 */
	private CsvData csvDataLine;

	/**
	 * 
	 */
	private String stmt;

	/**
	 * 
	 */
	private Database jdbcDatabase;

	/**
	 * 
	 */
	private PreparedStatement pstmt;

	/**
	 * 
	 * @param csvHeader
	 * @param csvMapping
	 * @param jdbcDatabase
	 */
	public CsvToSqlInsert(
		CsvSettings csvSettings,
		CsvHeader csvHeader,
		Mapping csvMapping,
		Database jdbcDatabase) {

		this.csvSettings = csvSettings;
		this.csvHeader = csvHeader;
		//csvHeaderFieldCount = csvHeader.getFieldCount();
		this.csvMapping = csvMapping;
		this.jdbcDatabase = jdbcDatabase;
		initHelper();

	}

	/**
	 * 
	 *
	 */
	public void dumpAction() {
		LangUtil.consoleDebug(
			debug,
			"About to execute SQL statement: "
				+ stmt
				+ " with following data: "
				+ csvDataLine.getLine());
	}

	/**
	 * 
	 * @throws SQLException
	 */
	public void generatePreparedStatement() throws SQLException {

		Iterator fieldNamesIterator;
		int qmarks = 1;
		boolean foundIdField = false;

		stmt = "INSERT INTO " + csvMapping.getSqlTableName() + " (";

		//int fieldPos = 0;
		fieldNamesIterator = csvMapping.getFieldNameMapIterator();
		while (fieldNamesIterator.hasNext()) {

			Map.Entry m = (Map.Entry) fieldNamesIterator.next();
			String csvFieldName = (String) m.getKey();

			String sqlFieldName = csvMapping.getMappedFieldName(csvFieldName);
			if (sqlFieldName.equalsIgnoreCase("ID"))
				foundIdField = true;

			/*
			LangUtil.consoleDebug(
				debug,
				"FIELD#="
					+ ++fieldPos
					+ " "
					+ csvFieldName
					+ " -> "
					+ sqlFieldName);
			*/

			stmt += sqlFieldName;

			if (fieldNamesIterator.hasNext())
				stmt = stmt + ", ";

			qmarks++;

		}

		if (!foundIdField) {
			stmt += ", ID";
			qmarks++;
		}

		stmt += ") VALUES (";

		for (int i = 1; i < qmarks; i++) {
			stmt += "?";
			if (i < qmarks - 1)
				stmt += ", ";
		}

		stmt += ")";

		pstmt = jdbcDatabase.getPreparedStatement(stmt);
		LangUtil.consoleDebug(debug, "Generated prepared statement: " + stmt);

	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement getInsertStatement()
		throws SQLException, NoCsvDataPresentException {

		//LangUtil.consoleDebug(debug, "About to execute: SQL=" + stmt);

		Iterator fieldNamesIterator = csvMapping.getFieldNameMapIterator();
		int i = 1;
		boolean foundIdField = false;
		boolean xTo1 = false;
		boolean xTo1AllFieldsSet = true;

		while (fieldNamesIterator.hasNext()) {

			Map.Entry m = (Map.Entry) fieldNamesIterator.next();
			String csvFieldName = (String) m.getKey();
			String sqlFieldName = csvMapping.getMappedFieldName(csvFieldName);

			if (sqlFieldName.equalsIgnoreCase("ID"))
				foundIdField = true;

			MapRules csvMapRules = csvMapping.getCsvMapRules(csvFieldName);
			String fieldType = csvMapRules.getFieldType();

			int fieldPosition = csvHeader.getFieldPositionByName(csvFieldName);
			String data;
			data = csvDataLine.getFieldDataByPosition(fieldPosition);

			LangUtil.consoleDebug(
				debug,
				"FIELD#="
					+ i
					+ " "
					+ csvFieldName
					+ " -> "
					+ sqlFieldName
					+ "/"
					+ fieldType
					+ " XTO1="
					+ csvMapRules.isXto1()
					+ " POS="
					+ fieldPosition
					+ "/"
					+ csvDataLine.getFieldCount()
					+ " DATA="
					+ data);

			setValues(i, fieldType, data);

			if (csvMapRules.isXto1()) {
				xTo1 = true;
				if (data == null || data.equals(""))
					xTo1AllFieldsSet &= false;
			}

			i++;

		}

		if (!foundIdField)
			pstmt.setInt(
				i,
				selectHighestValue(csvMapping.getSqlTableName()) + 1);

		if (xTo1 && !xTo1AllFieldsSet)
			return null;
		else
			return pstmt;

	}

	/**
	 * 
	 *
	 */
	private void initHelper() {
		helper = new Helper(csvSettings, csvHeader, csvMapping);
	}

	/**
	 * 
	 * @param table
	 * @return
	 */
	public int selectHighestValue(String table) {

		int highestValue = 0;

		try {
			Statement s = jdbcDatabase.getStatement();
			s.execute("SElECT MAX(ID) FROM " + table);
			ResultSet rs = s.getResultSet();
			if (rs.next())
				highestValue = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return highestValue;

	}

	/**
	 * 
	 * @param table
	 * @param hashMap
	 * @return
	 */
	public boolean selectCount(String table, HashMap hashMap) {

		Iterator i = hashMap.entrySet().iterator();
		int count = 0;
		stmt = "SELECT COUNT(*) FROM " + table + " WHERE ";

		while (i.hasNext()) {

			Map.Entry e = (Map.Entry) i.next();
			String field = (String) e.getKey();
			String value = (String) e.getValue();

			stmt += field + " = " + value;

			if (i.hasNext())
				stmt += " AND ";

		}

		try {
			Statement s = jdbcDatabase.getStatement();
			s.execute(stmt);
			ResultSet rs = s.getResultSet();
			if (rs.next())
				count = rs.getInt(1);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		if (count > 0)
			return true;
		else
			return false;

	}

	/**
	 * 
	 * @param csvDataLine
	 */
	public void setCsvDataLine(CsvData csvDataLine) {
		csvDataLine.splitLine();
		this.csvDataLine = csvDataLine;
	}

	/**
	 * 
	 * @param i
	 * @param fieldType
	 * @param data
	 * @throws SQLException
	 */
	public void setValues(int i, String fieldType, String data)
		throws SQLException {

		if (fieldType.equalsIgnoreCase("varchar")) {

			if (data != null)
				pstmt.setString(i, data);
			else
				pstmt.setNull(i, Types.VARCHAR);

		}
		else if (fieldType.equalsIgnoreCase("number")) {

			if (data != null)
				pstmt.setInt(i, Integer.valueOf(data).intValue());
			else
				pstmt.setNull(i, Types.INTEGER);

		}
		else if (fieldType.equalsIgnoreCase("date")) {

			if (data != null) {

				try {
					pstmt.setDate(i, helper.parseGermanDate(data));
				}
				catch (ParseException e) {

					LangUtil.consoleDebug(
						debug,
						"Error while parsing date: " + data);
					pstmt.setNull(i, Types.DATE);

				}

			}
			else
				pstmt.setNull(i, Types.DATE);

		}

		//LangUtil.consoleDebug(debug, "Set value for parameter# " + i);

	}

}
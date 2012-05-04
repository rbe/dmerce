/*
 * Created on Jun 5, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: ExportSqlToCsv.java,v 1.1 2004/03/29 13:39:35 rb Exp $
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ExportSqlToCsv {

	/**
	 * 
	 */
	private boolean debug = true;

	/**
	 * 
	 */
	private CsvSettings csvSettings;

	/**
	 * 
	 */
	private Mapping csvMapping;

	/**
	 * 
	 */
	//private String csvTableName;

	/**
	 * 
	 */
	private String sqlTableName;

	/**
	 * 
	 */
	private Database jdbcDatabase;

	/**
	 * 
	 */
	private String fileName;

	/**
	 * 
	 */
	private FileWriter file;

	/**
	 * 
	 */
	private Vector sqlFieldNames = new Vector();

	/**
	 * 
	 */
	private Vector csvFieldNames = new Vector();

	/**
	 * 
	 * @param csvSettings
	 */
	public ExportSqlToCsv(CsvSettings csvSettings) throws DmerceException {

		this.csvSettings = csvSettings;
		jdbcDatabase = DatabaseHandler.getDatabaseConnection("csv");

	}

	/**
	 * 
	 *
	 */
	public void closeFile() {

		try {
			file.close();
		} catch (IOException ioe) {
			LangUtil.consoleDebug(
				true,
				"Error while writing output file '"
					+ fileName
					+ "' for writing: "
					+ ioe.getMessage()
					+ "'");
			System.exit(2);
		}

	}

	/**
	 * Gleicht Feldnamen einer SQL-Tabelle mit dem CSV-Mapping
	 * ab, da wir unabhaengig von der Seite auf der CSV- und SQL-
	 * Feldnamen in der Mapping-Datei stehen, sein wollen 
	 *
	 * @return Vector Vector mit Strings: alle Feldnamen aus der SQL-Tabelle, die gemapped werden
	 */
	private void analyzeFieldNames() {

		try {

			String stmt = "SELECT * FROM " + sqlTableName + " WHERE 1 = 0";
			ResultSet rs = jdbcDatabase.executeQuery(stmt);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();

			for (int i = 1; i < columnCount + 1; i++) {

				String columnName = rsmd.getColumnName(i);
				String mappedFieldName =
					csvMapping.getMappedFieldName(columnName);

				if (mappedFieldName != null) {
					sqlFieldNames.add(columnName);
					csvFieldNames.add(mappedFieldName);
				}

			}

		} catch (SQLException se) {
			LangUtil.consoleDebug(
				true,
				"Error while analyzing SQL table '"
					+ sqlTableName
					+ "': '"
					+ se.getMessage()
					+ "'");
		}

	}

	/**
	 * 
	 * @return
	 */
	private String getSelectStatementForMappedFields() {

		Iterator sqlFieldNamesIterator = sqlFieldNames.iterator();
		StringBuffer stmt = new StringBuffer("SELECT ");
		int y = sqlFieldNames.size();
		int z = 0;

		while (sqlFieldNamesIterator.hasNext()) {

			z++;
			stmt.append((String) sqlFieldNamesIterator.next());
			if (z < y)
				stmt.append(", ");

		}

		stmt.append(" FROM " + sqlTableName);

		return stmt.toString();

	}

	/**
	 * 
	 * @throws IOException
	 */
	private void writeCsvHeaderLine() throws IOException {

		int y = csvFieldNames.size();
		int z = 0;
		StringBuffer header = new StringBuffer();

		Iterator csvFieldNamesIterator = csvFieldNames.iterator();
		while (csvFieldNamesIterator.hasNext()) {

			z++;
			header.append((String) csvFieldNamesIterator.next());
			if (z < y)
				header.append(csvSettings.getDelimiter());

		}

		file.write(header.toString() + "\n");

	}

	/**
	 * 
	 *
	 */
	public void processMap() {

		int sucessfullyExportedLines = 0;
		int unsucessfullyLines = 0;

		try {

			LangUtil.consoleDebug(debug, "Opening database connection");
			jdbcDatabase.openConnection();

			analyzeFieldNames();
			writeCsvHeaderLine();

			ResultSet rs =
				jdbcDatabase.executeQuery(getSelectStatementForMappedFields());
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			while (rs.next()) {

				String s = "";

				try {

					for (int i = 1; i < columnCount + 1; i++) {

						Object o = rs.getObject(i);

						if (i > 1)
							s += csvSettings.getDelimiter();

						if (o != null)
							s += o;

					}

					file.write(s + "\n");
					sucessfullyExportedLines++;

				} catch (SQLException se) {
					unsucessfullyLines++;
					LangUtil.consoleDebug(
						true,
						"Error while exporting: '" + se.getMessage() + "'");
				}

			}

			LangUtil.consoleDebug(debug, "Closing database connection");
			jdbcDatabase.closeConnection();

		} catch (SQLException se) {
			se.printStackTrace();
			System.exit(2);
		} catch (IOException ioe) {
			LangUtil.consoleDebug(
				true,
				"Error while writing output file '"
					+ fileName
					+ "' for writing: "
					+ ioe.getMessage()
					+ "'");
			System.exit(2);
		}

		LangUtil.consoleDebug(
			true,
			"Processed "
				+ (sucessfullyExportedLines + unsucessfullyLines)
				+ " lines. Sucessfully exported "
				+ sucessfullyExportedLines
				+ " lines. "
				+ unsucessfullyLines
				+ " had errors!");

	}

	/**
	 * 
	 * @param fileName
	 */
	public void openFile(String fileName) {

		this.fileName = fileName;

		try {
			file = new FileWriter(fileName);
		} catch (IOException e) {
			LangUtil.consoleDebug(
				true,
				"Error while opening file '"
					+ fileName
					+ "' for writing: "
					+ e.getMessage()
					+ "'");
		}
	}

	/**
	 * 
	 * @param csvMapping
	 */
	public void setMap(Mapping csvMapping) {

		this.csvMapping = csvMapping;

		//csvTableName = csvMapping.getCsvTableName();
		sqlTableName = csvMapping.getSqlTableName();

	}

}
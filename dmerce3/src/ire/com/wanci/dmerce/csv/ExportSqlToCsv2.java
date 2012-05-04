/*
 * Created on Jun 30, 2003
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: ExportSqlToCsv2.java,v 1.1 2004/03/29 13:39:35 rb Exp $
 * 
 */
public class ExportSqlToCsv2 {

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
	private LinkedList csvMaps;

	/**
	 * 
	 */
	private StringBuffer header = new StringBuffer();

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
	int sucessfullyExportedLines = 0;
	int unsucessfullyLines = 0;

	/**
	 * 
	 * @param csvSettings
	 */
	public ExportSqlToCsv2(CsvSettings csvSettings) throws DmerceException {

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
	 * @return HashMap key = SQL Feldname Value = CSV Feldname
	 */
	private HashMap checkMappedFieldNames(Mapping m) {

		String sqlTableName = m.getSqlTableName();
		HashMap hm = new HashMap();

		try {

			String stmt = "SELECT * FROM " + sqlTableName + " WHERE 1 = 0";
			ResultSet rs = jdbcDatabase.executeQuery(stmt);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();

			for (int i = 1; i < columnCount + 1; i++) {

				String columnName = rsmd.getColumnName(i);
				String mappedFieldName = m.getMappedFieldName(columnName);

				if (mappedFieldName != null)
					hm.put(columnName, mappedFieldName);

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

		return hm;

	}

	/**
	 * 
	 * @return
	 */
	private String getSelectStatementForMappedFields(
		Mapping m,
		HashMap fieldNames) {

		Iterator fieldNamesIterator = fieldNames.entrySet().iterator();
		StringBuffer stmt = new StringBuffer("SELECT ");
		int y = fieldNames.size();
		int z = 0;

		while (fieldNamesIterator.hasNext()) {

			z++;

			Map.Entry me = (Map.Entry) fieldNamesIterator.next();
			stmt.append((String) me.getKey());
			if (z < y)
				stmt.append(", ");

		}

		stmt.append(" FROM " + m.getSqlTableName());

		return stmt.toString();

	}

	/**
	 * 
	 * @throws IOException
	 */
	private void writeCsvHeaderLine() throws IOException {
		file.write(header.toString() + "\n");
	}

	/**
	 * Erweitert eine bestehende CSV Header Line beispielsweise mit Feldern
	 * aus einer weiteren SQL-Tabelle
	 *
	 */
	private void extendCsvHeaderLine(HashMap fieldNames) {

		Iterator f = fieldNames.entrySet().iterator();
		int y = fieldNames.size();
		int z = 0;

		// if header is to be extended
		if (header.length() > 0)
			header.append(",");

		while (f.hasNext()) {

			z++;

			Map.Entry m = (Map.Entry) f.next();
			header.append((String) m.getValue());
			if (z < y)
				header.append(csvSettings.getDelimiter());

		}

	}

	/**
	 * Arbeitet eine einzelne Map ab
	 * 
	 * @param m
	 * @return
	 */
	private Vector processMap(Mapping m) {

		Vector lines = new Vector();
		StringBuffer sb = new StringBuffer();

		try {

			HashMap afn = checkMappedFieldNames(m);
			ResultSet rs =
				jdbcDatabase.executeQuery(
					getSelectStatementForMappedFields(m, afn));
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			while (rs.next()) {

				try {

					for (int i = 1; i < columnCount + 1; i++) {

						Object o = rs.getObject(i);

						if (i > 1)
							sb.append(csvSettings.getDelimiter());

						if (o != null)
							sb.append(o);

					}

					lines.add(sb.toString());
					sucessfullyExportedLines++;

				} catch (SQLException se) {
					unsucessfullyLines++;
					LangUtil.consoleDebug(
						true,
						"Error while exporting: '" + se.getMessage() + "'");
				}

			}

		} catch (SQLException se) {
			se.printStackTrace();
			System.exit(2);
		}

		return lines;

	}

	/**
	 * Arbeitet alle Maps ab, um eine CSV Header Line zu erzeugen
	 *
	 */
	private void generateHeaderLine(Mapping m) {
		HashMap afn = checkMappedFieldNames(m);
		extendCsvHeaderLine(afn);
	}

	/**
	 * Arbeitet alle Maps fuer den ExportToSql ab
	 *
	 */
	public void processMaps() {

		Iterator i;

		try {
			LangUtil.consoleDebug(debug, "Opening database connection");
			jdbcDatabase.openConnection();
		} catch (SQLException se) {
			se.printStackTrace();
			System.exit(2);
		}

		LangUtil.consoleDebug(debug, "Generating CSV header line");
		i = csvMaps.iterator();
		while (i.hasNext()) {
			Mapping m = (Mapping) i.next();
			generateHeaderLine(m);
		}
		try {
			writeCsvHeaderLine();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(2);
		}

		Vector v = new Vector();

		LangUtil.consoleDebug(debug, "Processing maps");
		i = csvMaps.iterator();
		while (i.hasNext()) {

			Mapping m = (Mapping) i.next();
			v.add(processMap(m).iterator());

		}

		try {

			LangUtil.consoleDebug(debug, "Writing file");

			Iterator i2 = v.iterator();
			while (i2.hasNext()) {

				String s = (String) i.next();
				file.write(s + "\n");

			}

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

		try {
			LangUtil.consoleDebug(debug, "Closing database connection");
			jdbcDatabase.closeConnection();
		} catch (SQLException se) {
			se.printStackTrace();
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
	public void setMaps(LinkedList csvMaps) {
		this.csvMaps = csvMaps;
	}

	/**
	 * 
	 *
	 */
	public static void main(String[] args) {

		System.out.println(
			"\nTESTER - please supply map for export as first argument\n");

		ExportMapReader2 em = new ExportMapReader2();
		em.setMapFileName(args[0]);
		em.readMapFile();

		try {
			ExportSqlToCsv2 es = new ExportSqlToCsv2(new CsvSettings());
			es.setMaps(em.getMaps());
			es.openFile("exportSqlToCsv.test.csv");
			es.processMaps();
			es.closeFile();
		} catch (DmerceException e) {
			e.printStackTrace();
		}

	}

}
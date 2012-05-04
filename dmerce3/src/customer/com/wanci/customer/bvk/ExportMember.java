/*
 * Created on Jun 5, 2003
 *  
 */
package com.wanci.customer.bvk;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import com.wanci.dmerce.cli.ArgumentParser;
import com.wanci.dmerce.csv.CsvSettings;
import com.wanci.dmerce.csv.ExportMapReader;
import com.wanci.dmerce.csv.Mapping;
import com.wanci.dmerce.exceptions.CsvMapRuleException;
import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.jdbcal.Database;
import com.wanci.dmerce.jdbcal.DatabaseHandler;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.java.LangUtil;
import com.wanci.java.TimeUtil;

/**
 * @author rb
 * @version $Id: ExportMember.java,v 1.7 2004/08/04 17:30:43 rb Exp $
 *  
 */
public class ExportMember {

	/**
	 *  
	 */
	private boolean DEBUG = true;

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
	private ExportMapReader csvMapReader;

	/**
	 *  
	 */
	private String csvTableName;

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
	 * @param csvSettings
	 */
	public ExportMember(CsvSettings csvSettings, ExportMapReader csvMapReader)
			throws DmerceException {

		this.csvSettings = csvSettings;
		this.csvMapReader = csvMapReader;
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
			LangUtil.consoleDebug(true, "Error while writing output file '"
					+ fileName + "' for writing: " + ioe.getMessage() + "'");
			System.exit(2);
		}

	}

	/**
	 * Gleicht Feldnamen einer SQL-Tabelle mit dem CSV-Mapping ab, da wir
	 * unabhaengig von der Seite auf der CSV- und SQL- Feldnamen in der
	 * Mapping-Datei stehen, sein wollen
	 * 
	 * @return Vector Vector mit Strings: alle Feldnamen aus der SQL-Tabelle,
	 *         die gemapped werden
	 */
	private void analyzeFieldNames() {

		try {

			String stmt = "SELECT * FROM " + sqlTableName + " WHERE 1 = 0";
			ResultSet rs = jdbcDatabase.executeQuery(stmt);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();

			for (int i = 1; i < columnCount + 1; i++) {

				String columnName = rsmd.getColumnName(i);
				String mappedFieldName = csvMapping
						.getMappedFieldName(columnName);

				if (mappedFieldName != null) {
					sqlFieldNames.add(columnName);
					csvFieldNames.add(mappedFieldName);
				}

			}

		} catch (SQLException se) {
			LangUtil.consoleDebug(true, "Error while analyzing SQL table '"
					+ sqlTableName + "': '" + se.getMessage() + "'");
		}

	}

	/**
	 * @return
	 */
	private PreparedStatement getSelectStatementForMappedFields()
			throws SQLException {

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

		stmt.append(" FROM " + sqlTableName + " WHERE id = ?");

		LangUtil.consoleDebug(DEBUG, "Stmt: " + stmt.toString());

		return jdbcDatabase.getPreparedStatement(stmt.toString());

	}

	/**
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

		file.write(header.toString() + ";MIT-AUSBILDUNG1" + ";MIT-AUSBILDUNG2"
				+ ";MIT-AUSBILDUNG3" + ";MIT-UNTERNEHMEN1"
				+ ";MIT-UNTERNEHMEN2" + ";MIT-UNTERNEHMEN3"
				+ ";MIT-UNTERNEHMEN4" + ";MIT-UNTERNEHMEN5"
				+ ";MIT-UNTERNEHMEN6" + ";MIT-UNTERNEHMEN7"
				+ ";MIT-UNTERNEHMEN8" + ";MIT-UNTERNEHMEN9"
				+ ";MIT-UNTERNEHMEN10" + "\r\n");

	}

	/*
	 * private String resultSetToCsvLine(ResultSet rs) throws SQLException {
	 * 
	 * StringBuffer sb = new StringBuffer(); ResultSetMetaData rsmd =
	 * rs.getMetaData(); int columnCount = rsmd.getColumnCount();
	 * 
	 * System.out.println("ResultCount=" + columnCount);
	 * 
	 * try {
	 * 
	 * while (rs.next()) {
	 * 
	 * for (int i = 1; i < columnCount + 1; i++) {
	 * 
	 * Object o = rs.getObject(i);
	 * 
	 * if (i > 1) sb.append(csvSettings.getDelimiter());
	 * 
	 * if (o != null) sb.append(o); } } } catch (Exception e) { }
	 * 
	 * return sb.toString(); }
	 */

	/**
	 * @return @throws
	 *         SQLException
	 */
	private HashMap rb1() throws SQLException {

		int c = 0;
		HashMap hm_mep = new HashMap();
		ResultSet rs_mep = jdbcDatabase
				.executeQuery("SELECT MemberID, EducationProfileID FROM MemberEducationProfile");
		while (rs_mep.next()) {

			Integer memberId = new Integer(rs_mep.getInt(1));
			Integer i2 = new Integer(rs_mep.getInt(2));
			String s = "" + i2;

			if (!hm_mep.containsKey(memberId)) {
				hm_mep.put(memberId, s);
				c = 1;
			} else {
				String i3 = (String) hm_mep.get(memberId);
				hm_mep.put(memberId, i3 + ";" + s);
				c++;
			}

		}

		return hm_mep;

	}

	private HashMap rb2() throws SQLException {

		int c = 0;
		HashMap hm_mc = new HashMap();
		ResultSet rs_mc = jdbcDatabase
				.executeQuery("SELECT MemberID, CompanyID FROM MemberCompany");
		while (rs_mc.next()) {

			Integer memberId = new Integer(rs_mc.getInt(1));
			Integer i2 = new Integer(rs_mc.getInt(2));
			String s = "" + i2;

			if (!hm_mc.containsKey(memberId)) {
				hm_mc.put(memberId, s);
				c = 1;
			} else {
				String i3 = (String) hm_mc.get(memberId);
				hm_mc.put(memberId, i3 + ";" + s);
				c++;
			}

		}

		return hm_mc;

	}

	/**
	 * @param s
	 * @param i
	 * @return
	 */
	private String comma(String s, int i) {

		String s2 = "";

		if (s != null) {

			StringTokenizer st = new StringTokenizer(s, ";");
			int ct = st.countTokens();

			if (ct < i) {

				int delta = i - ct;
				for (int j = 0; j < delta; j++)
					s2 += ";";

				return s + s2;

			} else
				return s;

		}

		for (int j = 0; j < i; j++)
			s2 += ";";

		return s2;

	}

	/**
	 * 
	 *  
	 */
	public void processMap() {

		int sucessfullyExportedLines = 0;
		int unsucessfullyLines = 0;

		try {

			LangUtil.consoleDebug(DEBUG, "Opening database connection");
			jdbcDatabase.openConnection();

			StoredProcs sp = new StoredProcs(jdbcDatabase);

			analyzeFieldNames();
			writeCsvHeaderLine();
			HashMap m1 = rb1();
			HashMap m2 = rb2();

			String timestamp = "SELECT id FROM member";
			// Wenn eine Datei timestamp.txt gefunden wird, enthaltenen
			// Wert in WHERE ChangedDateTime >= einsetzen
			try {

				BufferedReader r = new BufferedReader(new FileReader(
						"timestamp.txt"));
				timestamp += " WHERE ChangedDateTime >= " + r.readLine();

			} catch (FileNotFoundException fnfe) {

				LangUtil.consoleDebug(DEBUG,
						"Cannot find timestamp.txt! Using changeddatetime > 0");
				timestamp += " WHERE ChangedDateTime > 0";

			} catch (IOException ioe) {
			}

			timestamp += " ORDER BY id, membershipnumber";

			ResultSet rs = jdbcDatabase.executeQuery(timestamp);
			//ResultSetMetaData rsmd = rs.getMetaData();

			PreparedStatement pstmt = getSelectStatementForMappedFields();

			while (rs.next()) {

				Integer mitnr = new Integer(rs.getInt(1));
				sp.umlautsToPlaceholder(mitnr.intValue());

				StringBuffer sb = new StringBuffer();

				pstmt.setInt(1, mitnr.intValue());
				ResultSet rsMember = pstmt.executeQuery();
				ResultSetMetaData rsMemberMetaData = rsMember.getMetaData();
				int columnCount = rsMemberMetaData.getColumnCount();

				rsMember.next();

				try {

					for (int i = 1; i < columnCount + 1; i++) {

						if (i > 1)
							sb.append(csvSettings.getDelimiter());

						String s = rsMember.getString(i);
						// Debug
						// System.out.println("s=" + s);
						if (s != null) {

							// Replace ;
							sb.append(((String) s).replace(';', ' '));

						}

					}

					String memberEducationProfile = (String) m1.get(mitnr);

					if (memberEducationProfile != null)
						sb.append(";" + comma(memberEducationProfile, 3));
					else
						sb.append(comma("", 3));

					String memberCompany = (String) m2.get(mitnr);
					if (memberCompany != null)
						sb.append(";" + comma(memberCompany, 10));
					else
						sb.append(comma("", 10));

					// Debug!
					// System.out.println(sb.toString());

					file.write(sb.toString() + "\r\n");
					file.flush();

					sucessfullyExportedLines++;

				} catch (SQLException se) {
					unsucessfullyLines++;
					LangUtil.consoleDebug(true, "Error while exporting: '"
							+ se.getMessage() + "'");
				}

				sp.placeholderToUmlauts(mitnr.intValue());

			}

		} catch (SQLException se) {

			LangUtil.consoleDebug(true, "Error while writing output file '"
					+ fileName + "' for writing: " + se.getMessage() + "'");
			System.exit(2);

		} catch (IOException ioe) {

			LangUtil.consoleDebug(true, "Error while writing output file '"
					+ fileName + "' for writing: " + ioe.getMessage() + "'");
			System.exit(2);

		}

		try {

			LangUtil.consoleDebug(true, "Resetting changeddatetime");

			// Update changeddatetime == 0
			jdbcDatabase
					.executeUpdate("UPDATE member" + " SET changeddatetime = 0"
							+ " WHERE changeddatetime > 0");

		} catch (IllegalArgumentException e) {
			LangUtil.consoleDebug(true, "Cannot reset changeddatetime");
			e.printStackTrace();
		} catch (SQLException e) {
			LangUtil.consoleDebug(true, "Cannot reset changeddatetime");
			e.printStackTrace();
		}

		try {

			LangUtil.consoleDebug(DEBUG, "Closing database connection");
			jdbcDatabase.closeConnection();

		} catch (SQLException se) {
			se.printStackTrace();
			System.exit(2);
		}

		LangUtil.consoleDebug(true, "Processed "
				+ (sucessfullyExportedLines + unsucessfullyLines)
				+ " lines. Sucessfully exported " + sucessfullyExportedLines
				+ " lines. " + unsucessfullyLines + " had errors!");

	}

	/**
	 * @param fileName
	 */
	public void openFile(String fileName) {

		this.fileName = fileName;

		try {
			file = new FileWriter(fileName);
		} catch (IOException e) {
			LangUtil.consoleDebug(true, "Error while opening file '" + fileName
					+ "' for writing: " + e.getMessage() + "'");
		}
	}

	/**
	 * @param csvMapping
	 */
	public void setMap(Mapping csvMapping) {

		this.csvMapping = csvMapping;

		csvTableName = csvMapping.getCsvTableName();
		sqlTableName = csvMapping.getSqlTableName();

	}

	/**
	 * 
	 *  
	 */
	public void sqlToCsv() {

		Iterator csvMaps = csvMapReader.getMapsIterator();
		while (csvMaps.hasNext()) {

			Mapping csvMapping = (Mapping) csvMaps.next();
			String csvTableName = csvMapping.getCsvTableName();
			String sqlTableName = csvMapping.getSqlTableName();

			String exportFileName = csvTableName + ".exp."
					+ TimeUtil.getIsoTimeString() + ".csv";

			LangUtil.consoleDebug(true, "Exporting '" + sqlTableName + "' to '"
					+ exportFileName + "'");

			openFile(exportFileName);

			try {

				setMap(csvMapping);

				LangUtil.consoleDebug(true, "Processing map: " + sqlTableName
						+ " -> " + csvTableName);

				processMap();

				LangUtil.consoleDebug(true, "Processed map: " + sqlTableName
						+ " -> " + csvTableName);

			} catch (Exception e) {
				e.printStackTrace();
				LangUtil.consoleDebug(true, "Cannot find or access file "
						+ csvTableName + ".csv: " + e.getCause() + ": "
						+ e.getMessage());
				continue;
			}

			closeFile();

		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		long startTime = System.currentTimeMillis();
		Boot.printCopyright("CSV<->SQL IMPORT/EXPORT TOOL");

		LangUtil.consoleDebug(true, "START");

		ArgumentParser aap = new ArgumentParser(args);
		aap.add("-l", ";", "Field delimiter. Standard is semicolon (;)");
		aap.add("-e", "", "Field encloser. Standard is none.");
		aap.add("-m", "", "Map file");
		aap.parse();

		if (!aap.hasArgument("-m")) {
			aap.usage();
			System.exit(2);
		}

		CsvSettings csvSettings = new CsvSettings();
		csvSettings.setDelimiter(aap.getString("-l"));
		csvSettings.setEncloser(aap.getString("-e"));

		try {

			LangUtil.consoleDebug(true, "EXPORT MODE");

			ExportMapReader csvMapReader = new ExportMapReader();
			csvMapReader.setMapFileName(aap.getString("-m"));
			csvMapReader.readMapFile();

			ExportMember e = new ExportMember(csvSettings, csvMapReader);
			e.sqlToCsv();

		} catch (CsvMapRuleException e) {
			System.out.println("Cannot parse map file: " + e.getMessage());
		} catch (DmerceException e) {
			e.printStackTrace();
		}

		long endTime = System.currentTimeMillis();
		LangUtil.consoleDebug(true, "Execution took " + (endTime - startTime)
				/ 1000 + " seconds");

		LangUtil.consoleDebug(true, "STOP");

	}

}
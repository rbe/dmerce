/*
 * Created on Jul 24, 2003
 *  
 */
package com.wanci.customer.bvk;

import java.sql.SQLException;
import java.util.Iterator;

import com.wanci.dmerce.cli.ArgumentParser;
import com.wanci.dmerce.csv.CsvData;
import com.wanci.dmerce.csv.CsvSettings;
import com.wanci.dmerce.csv.ImportCsvToSql;
import com.wanci.dmerce.csv.ImportMapReader;
import com.wanci.dmerce.csv.Mapping;
import com.wanci.dmerce.exceptions.CsvMapRuleException;
import com.wanci.dmerce.exceptions.NoCsvDataPresentException;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.Boot;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: ImportMember.java,v 1.6 2004/05/16 19:44:08 rb Exp $
 * 
 * 
 *  
 */
public class ImportMember extends ImportCsvToSql {

	/**
	 *  
	 */
	private ImportMapReader csvMapReader;

	/**
	 * @param csvSettings
	 */
	public ImportMember(CsvSettings csvSettings, ImportMapReader csvMapReader)
		throws XmlPropertiesFormatException {

		super(csvSettings);
		this.csvMapReader = csvMapReader;

	}

	/**
	 * @param updateMode
	 */
	public void csvToSql(boolean updateMode, boolean updateMember) {

		/*
		 * Anstatt pro Map vorzugehen, ist es sinnvoller pro CSV-Datei alle
		 * Maps durchzugehen!
		 */

		Iterator csvMaps = csvMapReader.getMapsIterator();
		while (csvMaps.hasNext()) {

			Mapping csvMapping = (Mapping) csvMaps.next();
			String csvTableName = csvMapping.getCsvTableName();
			String sqlTableName = csvMapping.getSqlTableName();

			LangUtil.consoleDebug(
				true,
				"Importing '"
					+ sqlTableName
					+ "' from '"
					+ csvTableName
					+ ".csv'");

			setFile(csvTableName + ".csv");

			try {

				setMap(csvMapping);
				setUpdateMode(updateMode);

				LangUtil.consoleDebug(
					true,
					"Processing map: " + csvTableName + " -> " + sqlTableName);

				processMap(updateMember);

				LangUtil.consoleDebug(
					true,
					"Processed map: " + csvTableName + " -> " + sqlTableName);

			}
			catch (Exception e) {
				e.printStackTrace();
				LangUtil.consoleDebug(
					true,
					"Cannot find or access file "
						+ csvTableName
						+ ".csv: "
						+ e.getCause()
						+ ": "
						+ e.getMessage());
				continue;
			}

		}

	}

	/**
	 * Importiert MemberCompany
	 *  
	 */
	private void updateMemberEducationProfile(CsvData csvData)
		throws NoCsvDataPresentException, IllegalArgumentException, SQLException {

		int id =
			Integer
				.valueOf(
					csvData.getFieldDataByPosition(
						csvHeader.getFieldPositionByName("MIT-NR")))
				.intValue();

		// 1. Alle Eintraege aus membereducationprofile mit memberid = ?
		//    loeschen
		String stmt = "DELETE FROM membereducationprofile WHERE ID = " + id;
		jdbcDatabase.executeUpdate(stmt);

		// 2. INSERT INTO ...
		BvkSqlPool bvk = new BvkSqlPool(jdbcDatabase);
		bvk.iMemberEducationProfile.setInt(1, id);
		for (int i = 1; i < 4; i++) {

			try {

				bvk.iMemberEducationProfile.setInt(
					2,
					Integer
						.valueOf(
							csvData.getFieldDataByPosition(
								csvHeader.getFieldPositionByName(
									"MIT-AUSBILDUNG" + i)))
						.intValue());
				bvk.iMemberEducationProfile.execute();

			}
			catch (NumberFormatException e) {
			}
			catch (NullPointerException e2) {
			}

		}

		LangUtil.consoleDebug(
			debug,
			"Updated MemberEducationProfile for ID=" + id);

	}

	/**
	 * Importiert MemberEducationProfile
	 *  
	 */
	private void updateMemberCompany(CsvData csvData)
		throws NoCsvDataPresentException, IllegalArgumentException, SQLException {
	    
	    LangUtil.consoleDebug(debug, "updateMemberCompany()");

		int id =
			Integer
				.valueOf(
					csvData.getFieldDataByPosition(
						csvHeader.getFieldPositionByName("MIT-NR")))
				.intValue();

		// 1. Alle Eintraege aus membercompany mit memberid = ?
		//    loeschen
		String stmt = "DELETE FROM membercompany WHERE memberid = " + id;
		jdbcDatabase.executeUpdate(stmt);

		// 2. INSERT INTO ...
		BvkSqlPool bvk = new BvkSqlPool(jdbcDatabase);
		bvk.iMemberCompany.setInt(1, id);
		for (int i = 1; i < 11; i++) {

			// try bzgl. leerer Felder!
			try {

				bvk.iMemberCompany.setInt(
					2,
					Integer
						.valueOf(
							csvData.getFieldDataByPosition(
								csvHeader.getFieldPositionByName(
									"MIT-UNTERNEHMEN" + i)))
						.intValue());
				bvk.iMemberCompany.execute();

			}
			catch (NumberFormatException e) {
			}
			catch (NullPointerException e) {
			    e.printStackTrace();
			}

		}

		LangUtil.consoleDebug(debug, "Updated MemberCompany for ID=" + id);

	}

	/**
	 * @param membershipnumber
	 * @throws IllegalArgumentException
	 * @throws SQLException
	 */
	private void deleteMember(int membershipnumber)
		throws IllegalArgumentException, SQLException {

		String stmt =
			"DELETE FROM member WHERE membershipnumber = " + membershipnumber;
		jdbcDatabase.executeUpdate(stmt);

	}

	/**
	 * Ueberschreibt ImportCsvToSql.processMap()
	 *  
	 */
	public void processMap(boolean updateMember)
		throws NumberFormatException, NoCsvDataPresentException {

		try {

			LangUtil.consoleDebug(debug, "Opening database connection");
			jdbcDatabase.openConnection();

		}
		catch (SQLException se) {

			LangUtil.consoleDebug(
				true,
				"Cannot open database connection: " + se.getMessage());
			System.exit(2);

		}

		StoredProcs sp = new StoredProcs(jdbcDatabase);

		try {

			LangUtil.consoleDebug(
				debug,
				"Generating prepared INSERT statement");
			csvToSqlInsert.generatePreparedStatement();

		}
		catch (SQLException se) {

			LangUtil.consoleDebug(
				true,
				"Cannot create prepared INSERT statement");
			System.exit(2);

		}

		String line = getNextLine();
		while (line != null) {

			line.trim();
			CsvData csvData = new CsvData(csvSettings, line);
			csvData.splitLine();

			if (line.length() >= 1) {

				processedLines++;

				// ID des aktuellen Datensatzes holen
				int id =
					Integer
						.valueOf(
							csvData.getFieldDataByPosition(
								csvHeader.getFieldPositionByName("MIT-NR")))
						.intValue();

				// Status des aktuellen Datensatzes holen
				String status =
					csvData.getFieldDataByPosition(
						csvHeader.getFieldPositionByName("MIT-STATUS"));

				// Loeschen eines Mitglieds
				if (status.equals("K")) {

					try {
						deleteMember(id);
						LangUtil.consoleDebug(
							true,
							"#" + processedLines + " DELETED MEMBER " + id);
					}
					catch (Exception e) {

						e.printStackTrace();
						LangUtil.consoleDebug(
							true,
							"#"
								+ processedLines
								+ " ERROR WHEN DELETING MEMBER '"
								+ e.getCause()
								+ "': '"
								+ e.getMessage().trim()
								+ "' WITH CSV-DATA='"
								+ csvData.getLine()
								+ "'");

					}

				}
				else {

					// StoredProc: umlauts2placeholder
					sp.umlautsToPlaceholder(id);

					try {

						// Update-Mode?
						if (updateMode) {

							csvToSqlUpdate.setCsvDataLine(csvData);

							csvToSqlUpdate.check();
							if (csvToSqlUpdate.isInsert())
								insert(csvData);
							else if (csvToSqlUpdate.isUpdate()) {

								csvToSqlUpdate.update();
								LangUtil.consoleDebug(
									true,
									"#"
										+ processedLines
										+ " UPDATED CSV-DATA='"
										+ csvData.getLine()
										+ "'");

								// Update-Mode?
								if (updateMember) {
									updateMemberCompany(csvData);
									updateMemberEducationProfile(csvData);
								}

							}
						}
						else
							insert(csvData);

					}
					catch (Exception e) {

						e.printStackTrace();
						LangUtil.consoleDebug(
							true,
							"#"
								+ processedLines
								+ " ERROR WHEN UPDATING/INSERTING MEMBER '"
								+ e.getCause()
								+ "': '"
								+ e.getMessage().trim()
								+ "' WITH CSV-DATA='"
								+ csvData.getLine()
								+ "'");

					}

					// StoredProc: placeholder2umlauts
					sp.placeholderToUmlauts(id);

					// Pruefe und aendere ggf. Mitgliedsdaten
					sp.checkMemberAfterImport(id);

				}

			}

			line = getNextLine();

		}

		try {
			LangUtil.consoleDebug(debug, "Closing database connection");
			jdbcDatabase.closeConnection();
		}
		catch (SQLException se) {
			se.printStackTrace();
			System.exit(2);
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
		throws XmlPropertiesFormatException {

		long startTime = System.currentTimeMillis();
		Boot.printCopyright("CSV<->SQL IMPORT/EXPORT TOOL");

		LangUtil.consoleDebug(true, "START");

		ArgumentParser aap = new ArgumentParser(args);
		aap.add("-u", null);
		aap.add("-l", ";");
		aap.add("-e", "");
		aap.add("-m", "");
		aap.add("-x", null);
		aap.parse();

		if (!aap.hasArgument("-m")) {
			System.out.println("No map file! Exiting.");
			System.exit(2);
		}

		CsvSettings csvSettings = new CsvSettings();
		csvSettings.setDelimiter(aap.getString("-l"));
		csvSettings.setEncloser(aap.getString("-e"));

		try {

			LangUtil.consoleDebug(true, "IMPORT MODE");
			if (aap.hasArgument("-u"))
				LangUtil.consoleDebug(true, "WITH UPDATE OPTION");

			ImportMapReader csvMapReader = new ImportMapReader();
			csvMapReader.setMapFileName(aap.getString("-m"));
			csvMapReader.readMapFile();

			ImportMember i = new ImportMember(csvSettings, csvMapReader);
			i.csvToSql(aap.hasArgument("-u"), aap.hasArgument("-x"));

		}
		catch (CsvMapRuleException e) {
			System.out.println("Cannot parse map file: " + e.getMessage());
		}

		long endTime = System.currentTimeMillis();
		long seconds = (endTime - startTime) / 1000;
		LangUtil.consoleDebug(
			true,
			"Execution took "
				+ seconds
				+ " seconds = "
				+ seconds / 60
				+ " minutes");

		LangUtil.consoleDebug(true, "STOP");

	}

}
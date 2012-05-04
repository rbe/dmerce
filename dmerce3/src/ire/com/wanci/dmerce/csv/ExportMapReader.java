/*
 * Created on Jun 25, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;

import com.wanci.dmerce.exceptions.CsvMapRuleException;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: ExportMapReader.java,v 1.1 2004/03/29 13:39:35 rb Exp $
 *
 */
public class ExportMapReader extends Mappings {

	/**
	 * 
	 */
	private boolean debug = false;

	/**
	 * 
	 */
	private String filename;

	/**
	 * 
	 */
	private BufferedReader file;

	/**
	 * 
	 * @return
	 */
	public String getMapFileName() {
		return filename;
	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.dmf.csv.MapReader#readMapFile()
	 */
	public void readMapFile() throws CsvMapRuleException {

		String line;

		try {

			file = new BufferedReader(new FileReader(filename));
			while ((line = file.readLine()) != null) {

				line.trim();

				// ignore empty lines or lines beginning with #
				if (line.length() > 1 && line.charAt(0) != '#') {

					try {

						/*
						LangUtil.consoleDebug(
						    debug,
						    "[ExportMapReader] Analyzing line: " + line);
						*/

						MapRule mr = new MapRule(line);
						mr.parse();
						String leftTableName = mr.getLeftTableName();
						String leftFieldName = mr.getLeftFieldName();
						String rightTableName = mr.getRightTableName();
						String rightFieldName = mr.getRightFieldName();
						MapRules csvMapRules;
						csvMapRules = new MapRules(mr.getRules());

						if (leftTableName != null
							&& leftFieldName != null
							&& rightTableName != null
							&& rightFieldName != null) {

							if (csvMapRules.isXto1()) {

								Mapping newCsvMapping =
									createNewMapping(
										leftTableName,
										leftFieldName,
										rightTableName,
										rightFieldName,
										csvMapRules);

								LangUtil.consoleDebug(
									debug,
									"[ExportMapReader] XTO1: Adding mapping: "
										+ csvMapRules.getSetCsvField()
										+ " -> "
										+ csvMapRules.getSetSqlField());

								newCsvMapping.addMapping(
									csvMapRules.getSetCsvField(),
									csvMapRules.getSetSqlField(),
									csvMapRules);

								csvMaps.add(newCsvMapping);

							}
							else {

								if (!addToExistingMapping(leftTableName,
									leftFieldName,
									rightTableName,
									rightFieldName,
									csvMapRules)) {

									Mapping newCsvMapping =
										createNewMapping(
											leftTableName,
											leftFieldName,
											rightTableName,
											rightFieldName,
											csvMapRules);

									csvMaps.add(newCsvMapping);

								}

							}

						}

					}
					catch (NoSuchElementException nsee) {
						LangUtil.consoleDebug(
							debug,
							"[ExportMapReader] Cannot parse csv mapping: "
								+ line);
					}

				}

			}

		}
		catch (IOException e) {
			LangUtil.consoleDebug(
				debug,
				"[ExportMapReader] Error while reading file '"
					+ filename
					+ "': "
					+ e.getMessage());
		}

	}

	/**
	 * 
	 * @param filename
	 */
	public void setMapFileName(String filename) {
		this.filename = filename;
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
	}

}
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
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import com.wanci.dmerce.exceptions.CsvMapRuleException;
import com.wanci.java.LangUtil;

/**
 * @author rb
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ImportMapReader extends Mappings {

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

	/**
	 * 
	 *
	 */
	public void readMapFile() throws CsvMapRuleException {

		String line;
        MapRules csvMapRules;

		try {

			file = new BufferedReader(new FileReader(filename));
			while ((line = file.readLine()) != null) {

				line.trim();

				// ignore empty lines or lines beginning with #
				if (line.length() > 1 && line.charAt(0) != '#') {

					try {

						LangUtil.consoleDebug(
						    debug,
						    "[CSVMapReader] Analyzing line: " + line);

						MapRule mr = new MapRule(line);
						mr.parse();
						String leftTableName = mr.getLeftTableName();
						String leftFieldName = mr.getLeftFieldName();
						String rightTableName = mr.getRightTableName();
						String rightFieldName = mr.getRightFieldName();
						try {
							csvMapRules = new MapRules(mr.getRules());
						}
						catch (CsvMapRuleException e) {
							throw new CsvMapRuleException(line);
						}

						if (leftTableName != null
							&& leftFieldName != null
							&& rightTableName != null
							&& rightFieldName != null) {
						    
						    LangUtil.consoleDebug(debug, "[MapReader] Adding mapping: "
						        + leftTableName+"."+leftFieldName+" -> "+rightTableName
						        +"."+rightFieldName);

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
									"[MapReader] XTO1: Adding mapping: "
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
							" Cannot parse csv mapping: " + line);
					}

				}

			}

		}
		catch (IOException e) {
			LangUtil.consoleDebug(
				debug,
				"Error while reading file " + filename);
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
	 * Test-Main
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws CsvMapRuleException {

		ImportMapReader csvMapReader = new ImportMapReader();
		csvMapReader.setMapFileName("csv.map");
		csvMapReader.readMapFile();

		Iterator i = csvMapReader.getMapsIterator();
		while (i.hasNext()) {

			Mapping c = (Mapping) i.next();
			Iterator s = c.getFieldNameMapIterator();
			while (s.hasNext()) {

				Map.Entry m = (Map.Entry) s.next();
				String fieldName = (String) m.getKey();
				MapRules r = c.getCsvMapRules(fieldName);

				System.out.println(
					c.getCsvTableName()
						+ "."
						+ fieldName
						+ " -> "
						+ c.getSqlTableName()
						+ "."
						+ m.getValue()
						+ " type:"
						+ r.getFieldType()
						+ " values:"
						+ r.getPossibleValues()
						+ " xto1:"
						+ r.isXto1()
						+ " set:"
						+ r.getSetValue()
						+ " ignore:"
						+ r.isIgnore());

			}

		}

	}

}
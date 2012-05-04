/*
 * Created on Jun 30, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: ExportMapReader2.java,v 1.1 2004/03/29 13:39:35 rb Exp $
 *
 */
public class ExportMapReader2 extends Mappings {

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
	 * Zeigt alle in den Maps enthaltenen Informationen an
	 *
	 */
	private void dump() {

		// Dump csvMaps
		Iterator i = csvMaps.iterator();
		while (i.hasNext()) {
			Mapping mr = (Mapping) i.next();
			String csvtn = mr.getCsvTableName();
			String sqltn = mr.getSqlTableName();
			System.out.println(csvtn);
			System.out.println(sqltn);
			Iterator i2 = mr.getFieldNameMapIterator();
			while (i2.hasNext()) {
				Map.Entry m = (Map.Entry) i2.next();
				String k = (String) m.getKey();
				String v = (String) m.getValue();
				System.out.println(sqltn + "." + k + " -> " + csvtn + "." + v);
			}
			System.out.println(mr.getFieldCount());
		}

	}
    
    /**
     * 
     * @return
     */
    public LinkedList getMaps() {
        return csvMaps;
    }

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.dmf.csv.Mappings#getMapFileName()
	 */
	public String getMapFileName() {
		return filename;
	}

	/**
	 * Enthaelt die Logik wie Mappings fuer den ExportToSql aus dem Mapfile
	 * erzeugt werden:
	 * 
	 * Auf der linken Seite des Mappings stehen die Felder fuer die CSV-Datei
	 * Auf der rechten Seite die der SQL-Datenbank
	 * 
	 * Sofern ein xTo1-Mapping vorliegt, muss nicht wie beim ImportFromFile ein
	 * neues Map-Objekt angelegt werden, sondern in das bestehende Mapping
	 * aufgenommen werden. Pro x xTo1-Mappings wird also nur 1 Objekt
	 * erzeugt
	 * 
	 * @param lt
	 * @param lf
	 * @param rt
	 * @param rf
	 * @param xTo1
	 */
	private void generateMap(
		String leftTableName,
		String leftFieldName,
		String rightTableName,
		String rightFieldName,
		MapRules csvMapRules) {

		boolean haveExistantMapping;
		Mapping newCsvMapping;

		// Ist ein Mapping fuer den leftTableName vorhanden?
		haveExistantMapping =
			addToExistingMapping(
				leftTableName,
				leftFieldName,
				rightTableName,
				rightFieldName,
				csvMapRules);

		// Mapping neu anlegen ,wenn keines existiert
		if (!haveExistantMapping) {

			LangUtil.consoleDebug(
				debug,
				"Created new mapping object for " + rightTableName);

			newCsvMapping =
				createNewMapping(
					leftTableName,
					leftFieldName,
					rightTableName,
					rightFieldName,
					csvMapRules);

			csvMaps.add(newCsvMapping);

		}
		else
			LangUtil.consoleDebug(
				debug,
				"Existing mapping object for " + rightTableName);

	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.dmf.csv.Mappings#readMapFile()
	 */
	public void readMapFile() {

		String line;

		try {

			file = new BufferedReader(new FileReader(filename));
			while ((line = file.readLine()) != null) {

				line.trim();

				// ignore empty lines or lines beginning with #
				if (line.length() > 1 && line.charAt(0) != '#') {

					try {

						// Parse rule
						MapRule mr = new MapRule(line);
						mr.parse();
						String leftTableName = mr.getLeftTableName();
						String leftFieldName = mr.getLeftFieldName();
						String rightTableName = mr.getRightTableName();
						String rightFieldName = mr.getRightFieldName();
						MapRules csvMapRules = new MapRules(mr.getRules());

						// we must have a complete mapping of tables and fields
						if (leftTableName != null
							&& leftFieldName != null
							&& rightTableName != null
							&& rightFieldName != null) {

							LangUtil.consoleDebug(
								debug,
								"[ExportMapReader] Analyzed: "
									+ rightTableName
									+ "."
									+ rightFieldName
									+ " -> "
									+ leftTableName
									+ "."
									+ leftFieldName
									+ " xTo1="
									+ csvMapRules.isXto1());

							generateMap(
								leftTableName,
								leftFieldName,
								rightTableName,
								rightFieldName,
								csvMapRules);

						}

					}
					catch (Exception e) {
					}

				}

			}

		}
		catch (Exception e) {
		}

		dump();

	}

	/* (non-Javadoc)
	 * @see com.wanci.dmerce.dmf.csv.Mappings#setMapFileName(java.lang.String)
	 */
	public void setMapFileName(String filename) {
		this.filename = filename;
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println(
			"\nTESTER - please supply map for export as first argument\n");

		ExportMapReader2 e = new ExportMapReader2();
		e.setMapFileName(args[0]);
		e.readMapFile();

	}

}
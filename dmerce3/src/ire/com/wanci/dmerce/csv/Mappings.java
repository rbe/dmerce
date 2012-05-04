/*
 * Created on Mar 26, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.csv;

import java.util.Iterator;
import java.util.LinkedList;

import com.wanci.dmerce.exceptions.CsvMapRuleException;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: Mappings.java,v 1.1 2004/03/29 13:39:35 rb Exp $
 *
 * Stellt alle Mappings dar, die aus einer Map-Datei gelesen wurden
 * 
 */
public abstract class Mappings {

	/**
	 * 
	 */
	private boolean debug = false;

	/**
	 * 
	 */
	protected LinkedList csvMaps = new LinkedList();

	/**
	 * 
	 *
	 */
	public Mappings() {
	}

	/**
	 * 
	 * @return
	 */
	public Iterator getMapsIterator() {
		return csvMaps.iterator();
	}

	/**
	 * 
	 * @param csvTableName
	 * @param sqlTableName
	 * @return
	 */
	public Mapping getCsvMapping(String csvTableName, String sqlTableName) {

		if (csvTableName != null && sqlTableName != null) {

            Iterator i = csvMaps.iterator();
			while (i.hasNext()) {

				Mapping csvMapping = (Mapping) i.next();

				if (csvMapping.getCsvTableName().equals(csvTableName)
					&& csvMapping.getSqlTableName().equals(sqlTableName))
					return csvMapping;

			}

		}

		return null;
	}

	/**
	 * 
	 * @param leftTableName
	 * @param leftFieldName
	 * @param rightTableName
	 * @param rightFieldName
	 * @param csvMapRules
	 * @return
	 */
	public boolean addToExistingMapping(
		String leftTableName,
		String leftFieldName,
		String rightTableName,
		String rightFieldName,
		MapRules csvMapRules) {

		Mapping existantCsvMapping =
			getCsvMapping(leftTableName, rightTableName);

		if (existantCsvMapping != null) {

			LangUtil.consoleDebug(
				debug,
				"[MapReader] Found existant mapping for '"
					+ leftTableName
					+ "' to '"
					+ rightTableName
					+ "'. Adding "
					+ leftFieldName
					+ " -> "
					+ rightFieldName);

			existantCsvMapping.addMapping(
				leftFieldName,
				rightFieldName,
				csvMapRules);

			return true;

		}
		else
			return false;

	}

	/**
	 * Legt ein neues Mapping fuer Tabellen an
	 * 
	 * @param leftTableName
	 * @param leftFieldName
	 * @param rightTableName
	 * @param rightFieldName
	 * @param csvMapRules
	 * @return Mapping Objekt, das ein neues Mapping von Tabellen enthaelt
	 */
	public Mapping createNewMapping(
		String leftTableName,
		String leftFieldName,
		String rightTableName,
		String rightFieldName,
		MapRules csvMapRules) {

		LangUtil.consoleDebug(
			debug,
			"[MapReader] Created new mapping for '"
				+ leftTableName
				+ "' to '"
				+ rightTableName
				+ "'. Adding "
				+ leftFieldName
				+ " -> "
				+ rightFieldName);

		Mapping newCsvMapping = new Mapping();

		newCsvMapping.setCsvTableName(leftTableName);
		newCsvMapping.setSqlTableName(rightTableName);
		newCsvMapping.addMapping(leftFieldName, rightFieldName, csvMapRules);

		return newCsvMapping;

	}
    
    /**
     * 
     *
     */
    public abstract String getMapFileName();
    
    /**
     * 
     *
     */
    public abstract void setMapFileName(String filename);
    
    /**
     * 
     *
     */
    public abstract void readMapFile() throws CsvMapRuleException;

}
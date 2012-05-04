package com.wanci.dmerce.csv;

import java.util.HashMap;
import java.util.Iterator;

import com.wanci.java.UtilUtil;

/**
 * @author rb
 * @version $Id: Mapping.java,v 1.1 2004/03/29 13:39:35 rb Exp $
 *
 * Stellt das Mapping einer CSV-Datei zu einer SQL-Tabelle dar
 * Verwaltet werden Felder, die gemapped werden sollen
 * 
 */
public class Mapping {

	/**
     * 
	 */
    private HashMap fieldNameMap = new HashMap();
    
    /**
     * 
     */
    private int fieldCount = 0;

	/**
     * 
	 */
    private HashMap csvMapRulesMap = new HashMap();

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
	 * @param left
	 * @param right
	 * @param csvMapRules
	 */
    public void addMapping(
		String left,
		String right,
		MapRules csvMapRules) {

		fieldNameMap.put(left, right);
        fieldCount++;
		csvMapRulesMap.put(left, csvMapRules);

	}

	/**
     * 
	 * @return
	 */
    public String getCsvTableName() {
		return csvTableName;
	}

	/**
     * 
	 * @param fieldName
	 * @return
	 */
    public MapRules getCsvMapRules(String fieldName) {
		return (MapRules) csvMapRulesMap.get(fieldName);
	}

    /**
     * 
     * @return
     */
    public int getFieldCount() {
        return fieldCount;
    }

	/**
     * 
	 * @return
	 */
    public Iterator getFieldNameMapIterator() {
        return fieldNameMap.entrySet().iterator();
	}

	/**
     * 
	 * @param fieldName
	 * @return
	 */
    public String getMappedFieldName(String fieldName) {
		return UtilUtil.searchHashMapIgnoreCase(fieldNameMap, fieldName);
	}

	/**
     * 
	 * @return
	 */
    public String getSqlTableName() {
		return sqlTableName;
	}

	/**
     * 
	 * @param csvTableName
	 */
    public void setCsvTableName(String csvTableName) {
		this.csvTableName = csvTableName;
	}

	/**
     * 
	 * @param sqlTableName
	 */
    public void setSqlTableName(String sqlTableName) {
		this.sqlTableName = sqlTableName;
	}

}
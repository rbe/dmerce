/*
 * Created on Mar 24, 2003
 *  
 */
package com.wanci.dmerce.csv;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: CsvHeader.java,v 1.1 2004/03/29 13:39:35 rb Exp $
 * 
 * Stellt den Header einer CSV-Datei dar
 *  
 */
public class CsvHeader {

	/**
	 *  
	 */
	private boolean DEBUG = false;

	/**
	 *  
	 */
	private CsvSettings csvSettings;

	/**
	 *  
	 */
	private String header;

	/**
	 *  
	 */
	private int addFieldCounter = 0;

	/**
	 *  
	 */
	private LinkedList fieldNames = new LinkedList();

	/**
	 *  
	 */
	private HashMap fieldPositions = new HashMap();

	/**
	 * @param csvSettings
	 */
	public CsvHeader(CsvSettings csvSettings) {
		this.csvSettings = csvSettings;
	}

	/**
	 * @param fieldName
	 */
	public void addField(String fieldName) {

		String fn = fieldName; //.toLowerCase();

		fieldNames.add(fn);
		fieldPositions.put(fn, new Integer(addFieldCounter));

		addFieldCounter++;

	}

	/**
	 * @return
	 */
	public String createHeader() {

		StringBuffer sb = new StringBuffer();
		Iterator i = fieldNames.iterator();

		while (i.hasNext()) {
			sb.append((String) i.next());
			if (i.hasNext())
				sb.append(csvSettings.getDelimiter());
		}

		return sb.toString();

	}

	/**
	 * 
	 *  
	 */
	public void dump() {

		System.out.println("getFieldCount: " + getFieldCount());

		for (int i = 0; i < getFieldCount(); i++) {
			System.out.println(
				"Position " + i + " = " + getFieldNameByPosition(i));
		}

	}

	/**
	 * @return
	 */
	public LinkedList getFields() {
		return fieldNames;
	}

	/**
	 * @return
	 */
	public int getFieldCount() {
		return fieldNames.size();
	}

	/**
	 * @param fieldName
	 * @return
	 */
	public int getFieldPositionByName(String fieldName) {

		String fn = fieldName; //.toLowerCase();
		int i = -1;

		LangUtil.consoleDebug(DEBUG, "getFieldPositionByName(" + fn + ")");

		try {
			i = ((Integer) fieldPositions.get(fn)).intValue();
		}
		catch (NullPointerException e) {

			LangUtil.consoleDebug(true, " Cannot find field name '" + fn + "'");

		}

		return i;

	}

	/**
	 * @param i
	 * @return
	 */
	public String getFieldNameByPosition(int i) {
		return (String) fieldNames.get(i);
	}

	/**
	 * @return
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @param fieldNames
	 */
	public void setFields(LinkedList fieldNames) {
		this.fieldNames = fieldNames;
	}

	/**
	 * @param header
	 */
	public void setHeader(String header) {
		this.header = header;

		StringTokenizer st =
			new StringTokenizer(header, csvSettings.getDelimiter());

		while (st.hasMoreTokens())
			addField(st.nextToken());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		CsvSettings csvSettings = new CsvSettings();
		csvSettings.setDelimiter(";");

		CsvHeader csvHeader = new CsvHeader(csvSettings);
		csvHeader.setHeader("a;b;c;d");

		csvHeader.dump();

	}

}
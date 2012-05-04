/*
 * Created on Mar 27, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.csv;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.wanci.dmerce.exceptions.CsvMapRuleException;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $Id: MapRules.java,v 1.1 2004/03/29 13:39:35 rb Exp $
 * 
 */
public class MapRules {

	private boolean debug = false;

	private String rules;

	private String fieldType;

	private String checkExists;

	private boolean checkPossibleValues = false;

	private LinkedList possibleValues = new LinkedList();

	private boolean set = false;

	private String setSqlTableField;

	private String setSqlTable;

	private String setSqlField;

	private String setCsvTableField;

	private String setCsvTable;

	private String setCsvField;

	private String setValue;

	private boolean xto1 = false;

	private boolean ignore = false;

	public MapRules() {
	}

	public MapRules(String rules) throws CsvMapRuleException {
		parse(rules);
		analyze();
	}

	public void analyze() throws CsvMapRuleException {

		LangUtil.consoleDebug(
			debug,
			"[MapRules] Analyzing rules: '" + rules + "'...");

		StringTokenizer stRules = new StringTokenizer(rules, "|");
		while (stRules.hasMoreTokens()) {

			try {

				String token = stRules.nextToken();

				StringTokenizer stRule = new StringTokenizer(token, ":");
				String rule = stRule.nextToken();
				String value = null;

				try {
					value = stRule.nextToken();
				}
				catch (NoSuchElementException nsee) {
				}

				if (rule.equalsIgnoreCase("checkexists"))
					checkExists = value;

				else if (rule.equalsIgnoreCase("type"))
					fieldType = value;

				else if (rule.equalsIgnoreCase("values")) {

					checkPossibleValues = true;

					StringTokenizer stPossibleValues =
						new StringTokenizer(value, "#");

					while (stPossibleValues.hasMoreTokens()) {
						possibleValues.add(stPossibleValues.nextToken());
					}

				}

				else if (rule.equalsIgnoreCase("xto1")) {
					xto1 = true;
				}

				else if (rule.equalsIgnoreCase("set")) {

					//set = true;
					setValue = value;

					StringTokenizer stSetValue =
						new StringTokenizer(value, "=");

					setSqlTableField = stSetValue.nextToken();
					setCsvTableField = stSetValue.nextToken();

					StringTokenizer stSqlTableField =
						new StringTokenizer(setSqlTableField, ".");
					setSqlTable = stSqlTableField.nextToken();
					setSqlField = stSqlTableField.nextToken();

					StringTokenizer stCsvTableField =
						new StringTokenizer(setCsvTableField, ".");
					setCsvTable = stCsvTableField.nextToken();
					setCsvField = stCsvTableField.nextToken();

				}

				else if (rule.equalsIgnoreCase("ignore")) {
					ignore = true;
				}

			}
			catch (NoSuchElementException nsee) {
				LangUtil.consoleDebug(
					debug,
					"[MapRules] Cannot parse rules... Skipping");
			}

		}

		if (!ignore && fieldType == null)
			throw new CsvMapRuleException(rules);

	}

	public String getFieldType() {
		return fieldType;
	}

	/**
	 * @return LinkedList
	 */
	public LinkedList getPossibleValues() {
		return possibleValues;
	}

	/**
	 * @return String
	 */
	public String getRules() {
		return rules;
	}

	public String getSetValue() {
		return setValue;
	}

	public String getSetCsvTable() {
		return setCsvTable;
	}

	public String getSetCsvField() {
		return setCsvField;
	}

	public String getSetSqlTable() {
		return setSqlTable;
	}

	public String getSetSqlField() {
		return setSqlField;
	}

	/**
	 * @return boolean
	 */
	public boolean isCheckPossibleValues() {
		return checkPossibleValues;
	}

	/**
	 * @return boolean
	 */
	public boolean isIgnore() {
		return ignore;
	}

	public boolean isXto1() {
		return xto1;
	}

	public void parse(String rules) {
		this.rules = rules;
	}

}
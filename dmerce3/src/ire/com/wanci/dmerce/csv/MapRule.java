/*
 * Created on Apr 4, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.csv;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * @author rb
 * @version $Id: MapRule.java,v 1.1 2004/03/29 13:39:35 rb Exp $
 *
 * Stellt eine einzelne Regel und die Informationen eines Mappings dar
 * 
 */
public class MapRule {

	/**
     * 
	 */
    private String line;

    /**
     * 
     */
    private String leftTableName;

    /**
     * 
     */
    private String leftFieldName;

    /**
     * 
     */
    private String rightTableName;

    /**
     * 
     */
    private String rightFieldName;

    /**
     * 
     */
    private String rules;

	/**
     * 
	 * @param line
	 */
    public MapRule(String line) {
		this.line = line;
	}

	/**
	 * @return String
	 */
	public String getLeftFieldName() {
		return leftFieldName;
	}

	/**
	 * @return String
	 */
	public String getLeftTableName() {
		return leftTableName;
	}

	/**
	 * @return String
	 */
	public String getRightFieldName() {
		return rightFieldName;
	}

	/**
	 * @return String
	 */
	public String getRightTableName() {
		return rightTableName;
	}

	/**
	 * @return String
	 */
	public String getRules() {
		return rules;
	}

	/**
     * 
	 *
	 */
    public void parse() {

		StringTokenizer st = new StringTokenizer(line, ",");
		String fieldMap = st.nextToken();
		rules = st.nextToken();

		StringTokenizer stFieldMap = new StringTokenizer(fieldMap, "=");
		String left = stFieldMap.nextToken();
		StringTokenizer stLeft = new StringTokenizer(left, ".");
		leftTableName = stLeft.nextToken();
		leftFieldName = stLeft.nextToken();

		String right = null;
		rightTableName = null;
		rightFieldName = null;
		try {
			right = stFieldMap.nextToken();
			StringTokenizer stRight = new StringTokenizer(right, ".");
			rightTableName = stRight.nextToken();
			rightFieldName = stRight.nextToken();
		}
		catch (NoSuchElementException nsee) {
		}

	}

}
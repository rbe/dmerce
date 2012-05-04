/*
 * Created on Mar 25, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.csv;

/**
 * @author rb
 * @version $Id: CsvSettings.java,v 1.1 2004/03/29 13:39:35 rb Exp $
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CsvSettings {

    private String delimiter = ";";

    private String encloser = "";

    public CsvSettings() {
    }

	/**
	 * @return String
	 */
	public String getDelimiter() {
		return delimiter;
	}

    /**
     * @return String
     */
    public String getEncloser() {
        return encloser;
    }

	/**
	 * Sets the delimiter.
	 * @param delimiter The delimiter to set
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * Sets the encloser.
	 * @param encloser The encloser to set
	 */
	public void setEncloser(String encloser) {
		this.encloser = encloser;
	}

}
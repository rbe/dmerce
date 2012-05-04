/*
 * Created on Mar 16, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.oracle;

/**
 * @author rb2
 * @version $Id: ManageDataDictionaryView.java,v 1.1 2004/03/29 13:39:37 rb Exp $
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface ManageDataDictionaryView {

	//String stmt;
	//ResultSet rs;

	/** Hole alle Informationen aus einer View
	 */
	void discover();

	/** Liefere das gesetzte SQL-Statement zur Abfrage
	 * der View zureuck
	 * 
	 * @return String SQL-Statement
	 */
	String getStatement();

	/** Erzeuge das aus dem Data Dictonary abgebildete
	 * Objekt neu (Erzeugen der DDL)
	 * 
	 * @return String SQL-Statement
	 */
	String recreate();

	/**
	 * 
	 *
	 */
	void setStatement();

	/** Zeige alle Informationen auf der Konsole an
	 */
	void dump();

}
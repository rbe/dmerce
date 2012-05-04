/*
 * Datei angelegt am 20.10.2003
 */
package com.wanci.dmerce.workflow.webapp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Diese Klasse wird dazu verwendet, im WebappState (also in einer PAGE)
 * das Mapping zwischen Formularfeldern und Datenbankfeldern abzubilden.
 *  
 * @author Masanori Fujita
 */
public class Formmap {
	
	private String table;
	private String primaryKeyName;
	private String formid;
	private HashMap fieldmap;
	private Set fileFields;
	
	/**
	 * Initialisiert eine Formmap
	 */
	public Formmap() {
		fieldmap = new HashMap();
		fileFields = new HashSet();
	}
	
	/**
	 * Gibt den Namen des zugeordneten Datenbankfeldes zurück.
	 * @param fieldname Name des Formularfeldes
	 * @return Name des Datenbankfeldes als String
	 */
	public String getDbFieldName(String fieldname) {
		return (String) fieldmap.get(fieldname);
	}
	
	/**
	 * Setzt den Namen des Datenbankfeldes zu einem Formularfeld 
	 * @param fieldname Name des Formularfeldes
	 * @param dbfieldname Name des Datenbankfeldes
	 */
	public void setDbFieldName(String fieldname, String dbfieldname) {
		fieldmap.put(fieldname, dbfieldname);
	}
	
	/**
	 * Gibt die Namen der enthaltenen Felder als Set von Strings
	 * zurück.
	 */
	public Set getFieldnames() {
		return fieldmap.keySet();
	}
	
	/**
	 * @return Name der Primary-Key-Spalte in der Datenbank
	 */
	public String getPrimaryKeyName() {
		return primaryKeyName;
	}

	/**
	 * @param primaryKeyName Name der Primary-Key-Spalte in der Datenbank
	 */
	public void setPrimaryKeyName(String primaryKeyName) {
		this.primaryKeyName = primaryKeyName;
	}

	/**
	 * @return Name der Datenbank-Tabelle
	 */
	public String getTable() {
		return table;
	}

	/**
	 * @param table Name der Datenbank-Tabelle
	 */
	public void setTable(String table) {
		this.table = table;
	}
	
	/**
	 * 
	 * @param dbFieldName
	 */
	public void addFileField(String dbFieldName) {
	    this.fileFields.add(dbFieldName);
	}
	
	public boolean isFileField(String dbFieldName) {
	    return fileFields.contains(dbFieldName);
	}

}

/*
 * Datei angelegt am 06.01.2004
 */
package com.wanci.dmerce.exceptions;

/**
 * @author Masanori Fujita
 */
public class FieldNotFoundException extends Exception {
	
	private String pageId;
	private String formId;
	private String fieldName;
	
	/**
	 * @param pageId
	 * @param formId
	 * @param fieldName
	 */
	public FieldNotFoundException(String pageId, String formId, String fieldName) {
		this.pageId = pageId;
		this.formId = formId;
		this.fieldName = fieldName;
	}
	
	public String getMessage() {
		return "Es konnte kein Formularfeld " + pageId + "." + formId + "." + fieldName + " gefunden werden.";
	}

}

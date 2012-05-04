package com.wanci.dmerce.taglib.form;

import java.util.Iterator;
import java.util.Map;

/**
 * abstract class for implementing Html form elements 
 * 
 * @author pg
 * @version $Id: AbstractHtmlFormElement.java,v 1.6 2004/06/03 23:51:52 rb Exp $
 *
 */
public abstract class AbstractHtmlFormElement implements HtmlFormElement {

	/**
	 * marker for required fields
	 */
	protected String requiredMarker = " *";

	/**
	 * marker for wrong filled out fields
	 * marker for not filled out required fields
	 */
	protected String errorMarker = "<font color=\"red\">" + requiredMarker + "</font>";

	/**
	 * holds actual value of formfield
	 */
	protected String value;

	/**
	 * holds actual values of formfield
	 */
	protected String[] values;

	/**
	 * attribute key-value pairs
	 */
	private Map attributes;

	/**
	 * object representation of 
	 */
	protected FormField field;

	/**
	 * 
	 * @param field
	 */
	public AbstractHtmlFormElement(FormField field) {
		this.field = field;
	}

	/**
	 * setter for Field
	 * @param field to set
	 */
	public void setField(FormField field) {
		this.field = field;
	}

	/**
	 * setter for attribute map
	 * @see com.wanci.dmerce.taglib.form.HtmlFormElement#setAttributes(java.util.Map)
	 */
	public void setAttributes(Map attributes) {
		this.attributes = attributes;
	}

	/**
	 * return attribute map for Html (key="value")
	 * @return html string in key="value" style
	 */
	public String attributesToHtml() {

		//String str = " ";
		String str = "";

		if (attributes == null)
			return str;

		Iterator iterator = attributes.keySet().iterator();
		while (iterator.hasNext()) {

			String key = (String) iterator.next();
			String value = (String) attributes.get(key);
			if (value == null)
				value = "";
			str += key + "=\"" + value + "\" ";

		}

		return str;

	}

	/**
	 * setter for value
	 * @see com.wanci.dmerce.taglib.form.HtmlFormElement#setValue(java.lang.String)
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * check if there is a marker to display
	 * @return marker string
	 */
	public String getMarker() {

		// zuerst nach Fehlern im FELD-Objekt suchen. Falls es Fehler gibt,
		// wird auf jeden Fall der Fehlermarker (rotes Sternchen?) ausgegeben
		if (field.getErrorMsgs().size() > 0) {
			return errorMarker;
		}

		// kein fehler, also schauen, ob Feld Pflichfeld ist. Dann Sternchen (?) ausgeben.
		if (field.isRequired()) {
			return requiredMarker;
		}

		// kein Pflichtfeld, kein Fehler, also nichts ausgeben
		return "";

	}

	/**
	 * getter for value
	 * @return value of html element
	 */
	public String getValue() {
		return value;
	}

	/**
	 * setter for values: used in lists
	 */
	public void setValues(String[] values) {
		this.values = values;
	}

	/**
	 * attribut hinzufügen (key-value paar)
	 * @param key Schlüssel
	 * @param value Wert
	 */
	protected void addAttribute(String key, String value) {
		//simply overwrite attribute
		if (key != null)
			attributes.put(key, value);

	}

	/**
	 * prüfroutine ob ein attribute existiert
	 * @param key Schlüssel
	 * @return checkresult true or false
	 */
	protected boolean checkForAttribute(String key) {
		if (attributes == null)
			return false;
		if (!attributes.containsKey(key))
			return false;
		if (attributes.get(key) == null)
			return false;
		return true;
	}

}

package com.wanci.dmerce.taglib.form;


/**
 * text element
 * 
 * @author pg
 */
public class HtmlTextElement extends AbstractHtmlFormElement {

	private String format;

	/**
	 * @param field
	 */
	public HtmlTextElement(FormField field) {
		super(field);
	}

	/**
	 * output method for displaying html text input field
	 * 
	 * @see com.wanci.dmerce.taglib.form.HtmlFormElement#toHtml()
	 */
	public String toHtml() {

		String string = "<input type=\"text\" name=\"" + field.getName() + "\"";
		// Sonderbehandlung für Datumsfelder einbauen
		string += attributesToHtml() + "value=\"" + getValue() + "\"" + "/>";
		string += getMarker();

		return string;
	}

}
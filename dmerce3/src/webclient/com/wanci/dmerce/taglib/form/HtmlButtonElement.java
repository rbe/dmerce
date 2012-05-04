/*
 * Created on Nov 5, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.wanci.dmerce.taglib.form;

/**
 * text element
 * @author pg
 */
public class HtmlButtonElement extends AbstractHtmlFormElement {

	/**
	 * type: submit, button, reset
	 */
	private String type;

	/**
	 * getter for type
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * setter for type
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}


	/**
	 * constructor erwartet Formfield-Objekt
	 * @param field
	 */
	public HtmlButtonElement(FormField field) {
		super(field);
	}

	/**
	 * output method for displaying html text input field
	 * @see com.wanci.dmerce.taglib.form.HtmlFormElement#toHtml()
	 */
	public String toHtml() {

		String string = "<input type=\"" + getType() + "\" name=\"" + field.getName() + "\"";
		string += attributesToHtml() + "value=\"" + getValue() + "\""; 
		string += "/>";

		return string;
	}

}
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
public class HtmlTextareaElement extends AbstractHtmlFormElement {

	/**
	 * @param field
	 */
	public HtmlTextareaElement(FormField field) {
		super(field);
	}

	/**
	 * output method for displaying html text input field
	 * @see com.wanci.dmerce.taglib.form.HtmlFormElement#toHtml()
	 */
	public String toHtml() {

		String string = "<textarea name=\"" + field.getName() + "\"";
		string += attributesToHtml() + ">" + getValue() + "</textarea>";
		string += getMarker();

		return string;
	}

}
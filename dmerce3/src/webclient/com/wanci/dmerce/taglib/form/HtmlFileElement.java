package com.wanci.dmerce.taglib.form;

/**
 * file element
 * 
 * @author pg
 */
public class HtmlFileElement extends AbstractHtmlFormElement {

	private static boolean DEBUG = false;

	/**
	 * path for file
	 */
	private String path;

	/**
	 * @param field
	 */
	public HtmlFileElement(FormField field) {
		super(field);
	}

	/**
	 * output method for displaying html text input field
	 * 
	 * @see com.wanci.dmerce.taglib.form.HtmlFormElement#toHtml()
	 */
	public String toHtml() {
		String output = "";

		//then we present a normal file input element
		output += "<input type=\"file\" name=\"" + field.getName() + "\"";
		output += attributesToHtml() + "/>";
		output += getMarker();

		return output;
	}

}
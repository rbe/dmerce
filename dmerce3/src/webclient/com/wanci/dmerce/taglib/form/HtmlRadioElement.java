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
public class HtmlRadioElement extends AbstractHtmlFormElement {

	/**
	 * @param field
	 */
	public HtmlRadioElement(FormField field) {
		super(field);
	}

	/**
	 * output method for displaying a single radio button 
	 * implemented as html-table
	 * 
	 * @see com.wanci.dmerce.taglib.form.HtmlFormElement#toHtml()
	 */
	public String toHtml() {


		String string = "";
		string += "<table class=\"radiotable\">";
		string += "<tr><td>Ja</td><td>Nein</td></tr>";
		string += "<tr><td>";
		
		//System.out.println("VALUE OF RADIO" + field.getValue());
		
		string += "<input type=\"radio\" name=\"" + field.getName() + "\"";
		string += attributesToHtml() + "value=\"true\"";
		string += (getValue().equals("true")) ? " checked=\"checked\"" : ""; 
		string += "/>";

		string += "</td><td>";
		
		string += "<input type=\"radio\" name=\"" + field.getName() + "\"";
		string += attributesToHtml() + "value=\"false\"";
		string += (!getValue().equals("true")) ? " checked=\"checked\"" : ""; 
		string += "/>";
		
		string += "</td></tr>";
		string += "</table>";

		return string;
	}



}
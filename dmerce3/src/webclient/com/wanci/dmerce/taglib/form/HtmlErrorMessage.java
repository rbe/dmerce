/*
 * Created on Nov 5, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.wanci.dmerce.taglib.form;

import java.util.Collection;
import java.util.Iterator;

/**
 * text element
 * @author pg
 */
public class HtmlErrorMessage extends AbstractHtmlFormElement {

	private Collection fieldList;

	/**
	 * @param field
	 */
	public HtmlErrorMessage(FormField field) {
		super(field);
		if (field != null)
			fieldList.add(field);
	}

	/**
	 * setter for FieldList
	 * @param fieldList
	 */
	public void setFieldList(Collection fieldList) {
		if (fieldList != null) {
			this.fieldList = fieldList;
		}
	}

	/**
	 * output method for displaying html text input field
	 * @see com.wanci.dmerce.taglib.form.HtmlFormElement#toHtml()
	 */
	public String toHtml() {

		String output = "";

		Iterator iterator = fieldList.iterator();
		while (iterator.hasNext()) {
			FormField field = (FormField) iterator.next();
			//if field is null then display an errormessage
			if (field == null) {
				output += "Fehlerhafte Feldbezeichnung für <errormessage ...>";
			} else {
				//field is valid, go through all errormessages and display errors
				Iterator errorIterator = field.getErrorMsgs().iterator();
				while (errorIterator.hasNext()) {
					if (!output.equals(""))
						output += "<br/>";
					output += "<span class=\"error\">" + (String) errorIterator.next() + "</span>";
				} //while errormessages

			} //field valid

		}

		return output;
	}

}
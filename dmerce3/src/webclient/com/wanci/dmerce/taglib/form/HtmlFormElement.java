/*
 * Created on Nov 5, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.wanci.dmerce.taglib.form;

import java.util.Map;

/**
 * @author pg
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface HtmlFormElement {

	/**
	 * set formfield object
	 * @param field
	 */
	public void setField(FormField field);

	/**
	 * set attributes like style, class, width...
	 * @param map
	 */
	public void setAttributes(Map map);

	/**
	 * setter for value
	 * @param value
	 */
	public void setValue(String value);

	/**
	 * output html
	 * @return html String
	 */
	public String toHtml();

	/**
	 * attribute map for Html (key="value")
	 * @return html string as attributes (key="value")
	 */
	public String attributesToHtml();

	/**
	 * check and return marker string
	 * @return html string for mark as required field (default is "*")
	 */
	public String getMarker();

	/**
	 * set Array
	 * @param values
	 */
	public void setValues(String[] values);

}

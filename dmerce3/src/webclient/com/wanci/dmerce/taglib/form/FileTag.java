package com.wanci.dmerce.taglib.form;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.wanci.dmerce.exceptions.FieldNotFoundException;

/**
 *
 * @author pg
 * @author mm
 * @version $Id: FileTag.java,v 1.2 2004/02/02 16:44:32 mf Exp $
 *
 */
public class FileTag extends TagSupport {

	private String name = null;

	private Map attribs = new HashMap();

	/**
	 * TAG: doStartTag-Method
	 * @return int tag-key
	 */
	public int doStartTag() {
		return SKIP_BODY;
	}

	/**
	 * Holt sich die "field" Elemente und erzeugt nach Prüfung ein
	 * "<input type="file"...>" Feld bzw. ein Feld mit delete-Link
	 * @return int tag-key
	 */
	public int doEndTag() throws JspTagException {

		FormTag form;
		HtmlFormElement htmlField;
		String output = "";
		form = (FormTag) findAncestorWithClass(this, com.wanci.dmerce.taglib.form.FormTag.class);

		FormField f;
		try {
			f = form.getField(name);
		} catch (FieldNotFoundException e) {
			String message = e.getMessage();
			String jspPath = ((HttpServletRequest) pageContext.getRequest()).getRequestURI();
			message += "<br/>Bitte überprüfen Sie in der JSP "+jspPath+" die &lt;qform:file &gt;-Tags.";
			JspTagException jspe = new JspTagException(message);
			jspe.setStackTrace(e.getStackTrace());
			throw jspe;
		}
		if (f != null) {
			htmlField = new HtmlFileElement(f);
			htmlField.setAttributes(attribs);
			// htmlField.setValue(value);
			output = htmlField.toHtml();
		} else
			output = "Field " + name + " not present";
		try {
			pageContext.getOut().print(output);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return SKIP_BODY;
	}

	/**
	 * add an attribute
	 * 
	 * @param key
	 * @param value
	 */
	private void addAttribute(String key, String value) {
		if (key != null)
			attribs.put(key, value);
	}
	/**
	 * TAGLIB: setter for name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * TAGLIB: setter for style
	 * @param str 
	 */
	public void setStyle(String str) {
		addAttribute("style", str);
	}

	/**
	 * TAGLIB: setter for class
	 * @param str
	 */
	public void setClass(String str) {
		addAttribute("class", str);
	}

}
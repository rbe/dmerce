package com.wanci.dmerce.taglib.form;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.wanci.dmerce.exceptions.FieldNotFoundException;

/**
 * @author mm
 * @version $Id: TextareaTag.java,v 1.12 2004/02/15 18:17:29 rb Exp $
 */
public class TextareaTag extends BodyTagSupport implements Attributable {

	/**
	 * name des Feldes
	 */
	private String name = null;

	/**
	 * html column attribute
	 */
	private String cols = null;

	/**
	 * html row attribute
	 */
	private String rows = null;

	/**
	 * attribute die beim rendern des Feldes zusätzlich ausgegeben werden
	 * sollen
	 */
	private HashMap attribs = new HashMap();

	/**
	 * tag: start tag methode
	 */
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	/**
	 * Holt sich die "field" Elemente und erzeugt nach Prüfung ein "
	 * <input type="textarea"...>" Tag mit den Werten.
	 */
	public int doEndTag() throws JspTagException {

		HtmlFormElement htmlField;
		String output = "";
		FormTag form =
			(FormTag) findAncestorWithClass(this,
				com.wanci.dmerce.taglib.form.FormTag.class);

		FormField f;
		try {
			f = form.getField(name);
		}
		catch (FieldNotFoundException e) {
			String message = e.getMessage();
			String jspPath =
				((HttpServletRequest) pageContext.getRequest()).getRequestURI();
			message += "<br/>Bitte überprüfen Sie in der JSP "
				+ jspPath
				+ " die &lt;qform:textarea &gt;-Tags.";
			JspTagException jspe = new JspTagException(message);
			jspe.setStackTrace(e.getStackTrace());
			throw jspe;
		}
		if (f != null) {
			String value = f.getValue();

			htmlField = new HtmlTextareaElement(f);
			htmlField.setAttributes(attribs);
			htmlField.setValue(value);
			output = htmlField.toHtml();
		}
		else
			output = "Form " + name + " not present";
		try {
			pageContext.getOut().print(output);
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}

		return SKIP_BODY;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * TAGLIB: setter for html cols
	 * 
	 * @param cols
	 */
	public void setCols(String cols) {
		this.cols = cols;
		addAttribute("cols", cols);
	}

	/**
	 * TAGLIB: setter for rows
	 * 
	 * @param rows
	 */
	public void setRows(String rows) {
		this.rows = rows;
		addAttribute("rows", rows);
	}

	/**
	 * key und value Paar werden in der HashMap gespeichert, damit sie im
	 * doEndTag ausgegeben werden können.
	 */
	public void addAttribute(String name, String value) {
		attribs.put(name, value);
	}
}
package com.wanci.dmerce.taglib.form;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.wanci.dmerce.exceptions.FieldNotFoundException;

/**
 *
 * @author pg
 * @author mm
 * @version $Id: CheckboxTag.java,v 1.11 2004/01/06 12:20:53 mf Exp $
 *
 */
public class CheckboxTag extends TagSupport {

	private String name = null;

	private HashMap attribs = new HashMap();

	/**
	 * 
	 */
	public int doStartTag() {
		return SKIP_BODY;
	}

	/**
	 * Holt sich die "field" Elemente und erzeugt nach Prüfung ein
	 * "<input type="text"...>" Tag mit den Werten.
	 */
	public int doEndTag() throws JspTagException {

		HtmlFormElement htmlField;
		String output = "";
		FormTag form = (FormTag) findAncestorWithClass(this, com.wanci.dmerce.taglib.form.FormTag.class);

		FormField f;
		try {
			f = form.getField(name);
		} catch (FieldNotFoundException e) {
			String message = e.getMessage();
			String jspPath = ((HttpServletRequest) pageContext.getRequest()).getRequestURI();
			message += "<br/>Bitte überprüfen Sie in der JSP "+jspPath+" die &lt;qform:checkbox &gt;-Tags.";
			JspTagException jspe = new JspTagException(message);
			jspe.setStackTrace(e.getStackTrace());
			throw jspe;
		}
		String value = f.getValue();

		htmlField = new HtmlCheckboxElement(f);
		htmlField.setAttributes(attribs);
		htmlField.setValue(value);
		output = htmlField.toHtml();
		try {
			pageContext.getOut().print(output);
		} catch (IOException e1) {
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

}
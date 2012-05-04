package com.wanci.dmerce.taglib.form;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.wanci.dmerce.exceptions.FieldNotFoundException;

/**
 * Das Tag FormFieldEmptyTag wertet den Body nur aus, wenn der Wert des FormFields nicht null ist.
 * @author mf
 *
 */
public class FormFieldEmptyTag extends BodyTagSupport {

	private String name = null;

	private boolean isInverted;
	
	private boolean evalbody;

	/**
	 * TAG: doStartTag-Method
	 * @return int tag-key
	 */
	public int doStartTag() throws JspTagException {
		FormTag form;
		form = (FormTag) findAncestorWithClass(this, com.wanci.dmerce.taglib.form.FormTag.class);
		FormField f;
		try {
			f = form.getField(name);
		} catch (FieldNotFoundException e) {
			String message = e.getMessage();
			String jspPath = ((HttpServletRequest) pageContext.getRequest()).getRequestURI();
			message += "<br/>Bitte überprüfen Sie in der JSP "
				+ jspPath
				+ " die &lt;qform:text &gt;-Tags.";
			JspTagException jspe = new JspTagException(message);
			jspe.setStackTrace(e.getStackTrace());
			throw jspe;
		}
		System.out.println("FormFieldEmptyTag: isInverted="+isInverted+", value="+f.getValue());
		if ((!isInverted && f.getValue().equals("")) | (isInverted && !f.getValue().equals(""))) {
			evalbody = true;
			return EVAL_BODY_BUFFERED;
		} else {
			evalbody = false;
			return SKIP_BODY;
		}
	}

	/**
	 * Holt sich die "field" Elemente und erzeugt nach Prüfung ein
	 * "<input type="text"...>" Tag mit den Werten.
	 * @return int tag-key
	 */
	public int doEndTag() throws JspException {
		if (evalbody) {
			String body = getBodyContent().getString();
			System.out.println("Body is : "+body);
			if (body != null) {
				try {
					pageContext.getOut().print(body);
				} catch (IOException e) {
					throw new JspException(e);
				}
			}
		}
		return EVAL_PAGE;
	}

	/**
	 * TAGLIB: setter for name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public void setInverted(boolean value) {
		this.isInverted = value;
	}

}
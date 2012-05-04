package com.wanci.dmerce.taglib.form;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.wanci.dmerce.exceptions.FieldNotFoundException;
import com.wanci.dmerce.workflow.webapp.WebappWorkflowEngine;

/**
 * @author mf
 * @version $Id: HiddenTag.java,v 1.4 2004/02/19 13:09:32 mf Exp $
 *  
 */
public class HiddenTag extends TagSupport {

	private String name;
	private String value;
	private String contextvarname;

	/**
	 * Holt sich die "field" Elemente und erzeugt nach Prüfung ein "
	 * <input type="text"...>" Tag mit den Werten.
	 * 
	 * Der Wert des Hidden-Fields wird nach der folgenden Reihenfolge gesetzt:
	 * Zuerst wird geprüft, ob ein Wert explizit per value="xxx" gesetzt wurde.
	 * Falls dies nicht der Fall ist, wird geprüft, ob sich der Wert per contextvarname="xxx"
	 * auf einen zuvor gesetzten Request-Parameter bezieht. Falls auch dies nicht der Fall
	 * ist, wird ein FormField mit dem spezifizierten Namen (name="xxx") im Workflow-Context
	 * gesucht. 
	 * 
	 * @return int tag-key
	 */
	public int doEndTag() throws JspTagException {

		FormTag form;
		String output;
		HtmlFormElement htmlField;
		form =
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
				+ " die &lt;qform:hidden&gt;-Tags.";
			JspTagException jspe = new JspTagException(message);
			jspe.setStackTrace(e.getStackTrace());
			throw jspe;
		}
		if (f != null) {
			htmlField = new HtmlHiddenElement(f);
			if (this.value != null) {
				// Zuerst explizit gesetzten Wert berücksichtigen
				htmlField.setValue(this.value);
			} else if (this.contextvarname != null) {
				// dann auf contextvarname prüfen
				WebappWorkflowEngine wwe = (WebappWorkflowEngine) pageContext.getSession().getAttribute("qWorkflowEngine");
				assert wwe != null;
				Object contextObject = wwe.getWorkflowContext(wwe.getCurrentWorkflow()).get(this.contextvarname);
				htmlField.setValue((contextObject == null ? "" : contextObject.toString()));
			} else {
				// erst dann FormField-Wert übernehmen
				htmlField.setValue(f.getValue());
			}
			output = htmlField.toHtml();
		}
		else
			output = "Field " + name + " not present";
		try {
			pageContext.getOut().print(output);
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}

		return SKIP_BODY;
	}

	/**
	 * TAGLIB: setter for name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param value
	 *            The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @param contextvarname The contextvarname to set.
	 */
	public void setContextvarname(String contextvarname) {
		this.contextvarname = contextvarname;
	}

}
package com.wanci.dmerce.taglib.form;

import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.wanci.dmerce.exceptions.FieldNotFoundException;
import com.wanci.dmerce.workflow.webapp.WebappState;
import com.wanci.dmerce.workflow.webapp.WebappWorkflowContext;

/**
 * @author pg
 * @author mm
 * @version $Id: ErrorMessageTag.java,v 1.10 2004/03/09 17:47:07 mf Exp $
 *  
 */
public class ErrorMessageTag extends TagSupport {

	/**
	 * TAGLIB: name
	 */
	private String name = null;

	public int doStartTag() {
		return SKIP_BODY;
	}

	/**
	 * Holt sich die "field" Elemente und erzeugt nach Prüfung ein " <input type="text"...>" Tag
	 * mit den Werten.
	 */
	public int doEndTag() throws JspTagException {

		FormTag form = (FormTag) findAncestorWithClass(this,
				com.wanci.dmerce.taglib.form.FormTag.class);
		if (form == null)
				throw new JspTagException(
						"Das Tag &lt;errormessage&gt; darf nur innerhalb eines Formulars verwendet werden.");

		WebappWorkflowContext context = form.context;

		String output = "";

		//create HtmlErrormessage Object with dummy field given in constructor
		//HtmlErrorMessage derives from AbstractHtmlElement and must have a field in its
		// constructor
		HtmlErrorMessage errorMsg = new HtmlErrorMessage(null);

		if (name != null) {
			//name is given, so grab fieldelement and insert this into the HtmlErrorMessage-Object
			FormField field;
			if (context.containsFormField(((WebappState) context.getCurrentState()).getPageId(),
					form.getId(), this.name)) {
				try {
					field = form.getField(name);
				} catch (FieldNotFoundException e) {
					String message = e.getMessage();
					String jspPath = ((HttpServletRequest) pageContext.getRequest())
							.getRequestURI();
					message += "<br/>Bitte überprüfen Sie in der JSP " + jspPath
							+ " die &lt;qform:errormessage &gt;-Tags.";
					JspTagException jspe = new JspTagException(message);
					jspe.setStackTrace(e.getStackTrace());
					throw jspe;
				}
			} else {
				field = null;
			}

			Collection fieldList = new Vector();
			fieldList.add(field);
			errorMsg.setFieldList(fieldList);

		} else {
			//no name is given, so check all FormElements in context
			Collection fieldList = null;
			if (context.getFormFields() != null) {
				fieldList = context.getFormFields(((WebappState) context.getCurrentState())
						.getPageId());
			} //if not null
			errorMsg.setFieldList(fieldList);
		} //else

		output = errorMsg.toHtml();

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
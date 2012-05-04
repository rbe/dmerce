/*
 * Datei angelegt am 03.02.2004
 */
package com.wanci.dmerce.taglib.form;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.wanci.dmerce.workflow.webapp.WebappState;
import com.wanci.dmerce.workflow.webapp.WebappWorkflowEngine;

/**
 * Das ClearFieldTag erzeugt einen Link (<a href...>), der ein bestimmtes Formularfeld
 * im Workflow-Kontext auf einen leeren Wert setzen kann. 
 *
 * @author Masanori Fujita
 */
public class ClearFieldTag extends BodyTagSupport {

	private String pageid;
	private String formid;
	private String name;
	private String style;
	private String styleClass;
	
	public void setFormid(String formid) {
		this.formid = formid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPageid(String pageid) {
		this.pageid = pageid;
	}
	
	public void setStyle(String style) {
		this.style = style;
	}

	public void setClass(String styleClass) {
		this.styleClass = styleClass;
	}

	
	public int doStartTag() throws JspException {
		FormTag form;
		form = (FormTag) findAncestorWithClass(this, FormTag.class);
		if (form == null) {
			throw new JspException("Das Tag &lt;qform:clearfield&gt; darf nur innerhalb eines &lt;qform:form&gt;-Tags verwendet werden.");
		}
		// Baue Query-Parameter zusammen
		if (pageid == null){
			WebappWorkflowEngine wwe = (WebappWorkflowEngine) pageContext.getSession().getAttribute("qWorkflowEngine");
			pageid = ((WebappState) wwe.getWorkflowContext(wwe.getCurrentWorkflow()).getCurrentState()).getPageId();
		}
		if (formid == null)
			formid = form.getId();
		try {
			pageContext.getOut().print("<a ");
			if (style != null)
				pageContext.getOut().print("style=\""+style+"\" ");
			if (styleClass != null)
				pageContext.getOut().print("class=\""+styleClass+"\" ");
			pageContext.getOut().print("href=\"clearfield.do?");
			pageContext.getOut().print("page="+pageid+"&");
			pageContext.getOut().print("form="+formid+"&");
			pageContext.getOut().print("field="+name+"\"");
			pageContext.getOut().print(">");
			return EVAL_BODY_BUFFERED;
		} catch (IOException e) {
			throw new JspException(e);
		}
	}

	public int doEndTag() throws JspException {
		String body = getBodyContent().getString();
		try {
			pageContext.getOut().print(body);
			pageContext.getOut().print("</a>");
		} catch (IOException e) {
			throw new JspException(e);
		}
		return EVAL_PAGE;
	}

}

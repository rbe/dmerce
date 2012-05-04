package com.wanci.dmerce.taglib;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

/**
 * A simple Tag that displays a Headline
 * @deprecated
 * @author pg
 */
public class HeadlineTag implements Tag {
	
	private PageContext pageContext;
	private Tag parent;

	private String name = "";

	/**
	 * Constructor
	 */
	public HeadlineTag() {
		super();
	}

	/**
	 * Method called at start of Tag
	 * @return either a EVAL_BODY_INCLUDE or a SKIP_BODY
	 */
	public int doStartTag() throws javax.servlet.jsp.JspTagException {
		return SKIP_BODY;
	}

	/**
	 * Method Called at end of Tag
	 * @return either EVAL_PAGE or SKIP_PAGE
	 */
	public int doEndTag() throws javax.servlet.jsp.JspTagException {
		try {
			pageContext.getOut().write("<h1>" + name + "</h1>");
		} catch (java.io.IOException e) {
			throw new JspTagException("IO Error: " + e.getMessage());
		}
		return EVAL_PAGE;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Method called to releases all resources
	 */
	public void release() {
	}

	/** Method used by the JSP container to set the current PageContext
	 * @param pageContext the current PageContext
	 */
	public void setPageContext(
		final javax.servlet.jsp.PageContext pageContext) {
		this.pageContext = pageContext;
	}

	/** Method used by the JSP container to set the parent of the Tag
	 * @param parent the parent Tag
	 */
	public void setParent(final javax.servlet.jsp.tagext.Tag parent) {
		this.parent = parent;
	}

	/** Method for retrieving the parent
	 * @return the parent
	 */
	public javax.servlet.jsp.tagext.Tag getParent() {
		return parent;
	}
}

/*
 * Datei angelegt am 21.11.2003
 */
package com.wanci.dmerce.taglib.template;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author Masanori Fujita
 */
public class SetBrickTag extends BodyTagSupport {
	
	private String name;
	private String path;
	
	public int doStartTag() throws JspException {
		BrickSet brickset = (BrickSet) pageContext.getRequest().getAttribute("qBrickSet");
		if (path!=null)
			brickset.addTemplate(name, path);
		return EVAL_BODY_BUFFERED;
	}
	
	public int doAfterBody() throws JspException {
		if (this.path == null) {
			BrickSet brickset = (BrickSet) pageContext.getRequest().getAttribute("qBrickSet2");
			brickset.addTemplate(name, getBodyContent().getString());
		}
		return SKIP_BODY;
	}

	
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param url The url to set.
	 */
	public void setPath(String path) {
		this.path = path;
	}

}

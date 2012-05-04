package com.wanci.dmerce.taglib.sql;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 *
 * @author pg
 * @author mm
 * @version $Id: EditTag.java,v 1.10 2004/01/21 14:38:33 pg Exp $
 *
 */
public class EditTag extends BodyTagSupport {

	/**
	 * value
	 */
	private String value;

	/**
	 * workflow
	 */
	private String workflow;

	/**
	 * key
	 */
	private String key;

	/**
	 * Beschreibung des Links
	 */
	private String linkstring;

	/**
	 * setter for key
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key.toLowerCase();
	}

	/**
	 * setter for workflow
	 * @param str
	 */
	public void setWorkflow(String str) {
		this.workflow = str;
	}

	/**
	 * Beginn des Delete Tags
	 * EVAL_BODY_BUFFERED wertet den Body aus
	 *
	 * @throws JspTagException
	 * @return TAG-int
	 */
	public int doStartTag() {
		try {
			RowTag rowTag = (RowTag) findAncestorWithClass(this, com.wanci.dmerce.taglib.sql.RowTag.class);
			value = rowTag.getRow().getFields().get(key).toString();
		} catch (Exception e) {
			value = null;
		}
		return EVAL_BODY_BUFFERED;
	}

	/**
	 * Ende des Edit Tags
	 *
	 * @return TAG-int
	 */
	public int doEndTag() throws JspTagException {
		String str = "workflow.do?qWorkflow=" + workflow;
		str += "&id=" + value;
		str += "&restart";
		str = "<a href=\"" + str + "\">";
		if (linkstring != null)
			str = str + linkstring;
		else
			str = str + "edit";
		str = str + "</a>";
		try {
			if (value == null) {
				pageContext.getOut().write("!?" + linkstring + "?!: key \"" + key + "\" doesn't exist in field collection");
			} else {
				pageContext.getOut().write(str);
			}
		} catch (Exception e) {
			throw new JspTagException(e.toString());
		}
		return EVAL_PAGE;
	}

	/**
	 * @throws JspTagException
	 * @return TAG-int
	 */
	public int doAfterBody() {
		linkstring = getBodyContent().getString().trim();
		return SKIP_BODY;
	}

}
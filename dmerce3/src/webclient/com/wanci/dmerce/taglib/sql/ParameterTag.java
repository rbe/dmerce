package com.wanci.dmerce.taglib.sql;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

public class ParameterTag extends TagSupport {

	/**
	 * Parameter name
	 */
	private String name = "";

	/**
	 * Parameter value
	 */
	private String value = "";

	/**
	 * Parameter name und value werden für das Prepare Tag gesetzt
	 *
	 * @throws JspTagException
	 * @return TAG-int
	 */
	public int doStartTag() throws JspTagException {
		try {

			PrepareTag prepareTag = (PrepareTag) findAncestorWithClass(this, Class.forName("com.wanci.dmerce.taglib.sql.PrepareTag"));

			prepareTag.setParameter(name, value);

		} catch (Exception e) {
			throw new JspTagException(e.toString());
		}
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * setter for name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * setter for value
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * getter for name
	 * @throws JspTagException
	 * @return String name
	 */
	public String getName() {
		return this.name;
	}


}
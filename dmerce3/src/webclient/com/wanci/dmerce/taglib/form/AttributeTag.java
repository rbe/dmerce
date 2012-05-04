package com.wanci.dmerce.taglib.form;

import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author mm
 * @version $Id: AttributeTag.java,v 1.2 2003/11/11 15:15:29 pg Exp $
 */
public class AttributeTag extends TagSupport {

	private String name;

	private String value;
	
	/**
	 * getter for name
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * setter for name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * getter for value
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * setter for value
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * start tag
	 * @return TAG: int
	 */
	public int doStartTag() {

		Attributable parentTag =
			(Attributable) findAncestorWithClass(this,
				com.wanci.dmerce.taglib.form.Attributable.class);

		parentTag.addAttribute(getName(), getValue());

		return SKIP_BODY;
	}
}
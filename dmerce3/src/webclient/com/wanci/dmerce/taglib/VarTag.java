package com.wanci.dmerce.taglib;

import javax.servlet.jsp.tagext.TagSupport;

/**
 * eine einfaches Tag, das eine Variable aus der Session ausliest und ausgibt
 * 
 * @author Magnus Rydin
 */
public class VarTag extends TagSupport {

	/**
	 * name der variable
	 */
	private String name;

	/**
	 * scope
	 * @deprecated
	 */
	private String scope;

	/**
	 * setter for scope
	 * @param scope
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * Method called at start of Tag
	 * @return SKIP_BODY
	 */
	public int doStartTag() {
		return SKIP_BODY;
	}

	/**
	 * Methode die die angegebene variable ausliest und direkt ausgibt
	 * falls kein scope angegeben wurde, liest die methode erst aus dem request, dann aus der session
	 * ansonsten wird direkt aus der session oder aus dem request gelesen
	 * @return EVAL_PAGE
	 */
	public int doEndTag() {
		String value = null;
		//read value
		if (scope == null || scope.equals("")) {
			value = getValueFromRequest();
			if (value == null)
				value = getValueFromSession();
		} else if (scope.equals("session")) {
			value = getValueFromSession();
		} else if (scope.equals("request")) {
			value = getValueFromRequest();
		}

		//output
		try {
			if (value != null)
				pageContext.getOut().write(value);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return EVAL_PAGE;
	}

	/**
	 * getter for request
	 * @return Variable aus dem Request
	 */
	private String getValueFromRequest() {
		String str = null;
		str = pageContext.getRequest().getParameter(name);
		return str;
	}

	/**
	 * getter for session variable
	 * @return value
	 */
	private String getValueFromSession() {
		String str = null;
		str = (String) pageContext.getSession().getAttribute(name);
		return str;
	}

	/**
	 * setter for name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}


}

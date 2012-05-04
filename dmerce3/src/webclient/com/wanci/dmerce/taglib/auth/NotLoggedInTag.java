/*
 * Created on 12.09.2004
 *
 */
package com.wanci.dmerce.taglib.auth;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.wanci.dmerce.servlet.AuthInfo;

/**
 * @author rb
 * @version $Id$
 * 
 * Tag, welches seinen Body nur dann ausgibt, wenn kein Benutzer eingeloggt ist
 *  
 */
public class NotLoggedInTag extends BodyTagSupport {

	public int doStartTag() throws JspTagException {

		// Hole AuthInfo aus Session Context
		AuthInfo authInfo = (AuthInfo) pageContext.getSession().getAttribute(
				"qAuthInfo");

		if (authInfo == null)
			return EVAL_BODY_INCLUDE;
		else
			return SKIP_BODY;

	}

	public int doEndTag() {
		return EVAL_PAGE;
	}

}
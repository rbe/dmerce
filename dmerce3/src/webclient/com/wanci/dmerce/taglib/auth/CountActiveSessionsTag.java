/*
 * Created on 03.10.2004
 *
 */
package com.wanci.dmerce.taglib.auth;

import java.io.IOException;

import javax.servlet.jsp.tagext.TagSupport;

import com.wanci.dmerce.servlet.AuthInfo;

/**
 * @author rb
 * @version $Id$
 *  
 */
public class CountActiveSessionsTag extends TagSupport {

	public int doEndTag() {

		AuthInfo authInfo = (AuthInfo) pageContext.getSession().getAttribute(
				"qAuthInfo");

		try {
			pageContext.getOut().print(authInfo.getActiveSessionsCount());
		} catch (IOException e) {
		}

		return EVAL_PAGE;

	}

}
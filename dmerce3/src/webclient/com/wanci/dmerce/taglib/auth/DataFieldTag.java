/*
 * Created on 14.09.2004
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
 * Sucht Daten aus dem AuthInfo Objekt und gibt sie aus
 *  
 */
public class DataFieldTag extends TagSupport {

	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public int doEndTag() {

		// Hole AuthInfo aus Session Context
		AuthInfo authInfo = (AuthInfo) pageContext.getSession().getAttribute(
				"qAuthInfo");

		String text = "unknown";

		if (authInfo != null) {
			text = authInfo.getDataField(name);
		}

		try {
			pageContext.getOut().print(text);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return EVAL_PAGE;

	}

}
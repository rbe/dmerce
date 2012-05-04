/*
 * Created on 05.09.2003
 *
 */
package com.wanci.dmerce.taglib;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Helper Class um einige immer wiederkehrenden Funktionen zu kapseln
 * @author pg
 * @version $Id: TaglibHelper.java,v 1.3 2003/11/14 14:00:02 pg Exp $
 *
 */
public class TaglibHelper {

	/**
	 * forward to errorPage
	 * @param pageContext
	 * @param message Nachricht die auf der Fehlerseite angezeigt werden soll
	 */
	public static void errorPage(PageContext pageContext, String message) {
		pageContext.getRequest().setAttribute("qMessage", message);
		//forward
		try {
			pageContext.getResponse().flushBuffer();
			pageContext.forward("error.jsp");
		} catch (ServletException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * auf Fehlerseite umleiten
	 * @param pageContext
	 * @param message Nachricht die an die Fehlerseite übergeben werden soll
	 */
	public static void errorPageRedirect(PageContext pageContext, String message) {
		pageContext.getSession().setAttribute("qMessage", message);

		try {
			((HttpServletResponse) pageContext.getResponse()).sendRedirect("error.jsp");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

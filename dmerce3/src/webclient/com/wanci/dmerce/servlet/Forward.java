/*
 * Datei angelegt am 18.11.2003
 */
package com.wanci.dmerce.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Masanori Fujita
 * @version $Id$
 *  
 */
public class Forward extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Ziel holen
		String destinationJsp = (String) request.getSession().getAttribute(
				"qTemplate");
		// Ziel prüfen und dorthin umleiten
		if (destinationJsp != null) {

			request.getRequestDispatcher(destinationJsp).forward(request,
					response);

		} else {

			request.getSession().setAttribute("qMessage",
					"No destination template found");

			request.getRequestDispatcher("error.jsp")
					.forward(request, response);

		}

	}

}
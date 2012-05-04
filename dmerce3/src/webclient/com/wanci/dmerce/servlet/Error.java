/*
 * Created on 03.11.2003
 *  
 */
package com.wanci.dmerce.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanci.dmerce.workflow.webapp.WebappWorkflowBuilder;
import com.wanci.java.LangUtil;

/**
 * @author Masanori Fujita
 * @version $$Id: Error.java,v 1.5 2004/03/15 11:51:07 rb Exp $$
 */
public class Error extends HttpServlet {

	private boolean DEBUG = true;

	private WebappWorkflowBuilder workflowbuilder;

	/**
	 * Zentrale Methode zur Verarbeitung von Requests
	 * 
	 * @param request
	 * @param response
	 */
	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) {

		Throwable t = (Throwable) request
				.getAttribute("javax.servlet.error.exception");

		ServletException e = null;
		try {
			e = (ServletException) request
					.getAttribute("javax.servlet.error.exception");
		} catch (Exception e2) {
		}

		String s = "";
		boolean rootcausefound = false;

		//display request uri and message
		if (t != null) {

			s += t.toString()
					+ " ("
					+ (String) request
							.getAttribute("javax.servlet.error.request_uri")
					+ ")";

			//s += " Typ: " + (String)
			// request.getAttribute("javax.servlet.error.exception");

		}

		//try to display the root cause (the initial error)
		if (e != null) {

			if (e.getRootCause() != null)
				t = e.getRootCause();

			s += "<h3>StackTrace (RootCause)</h3>";
			s += "<pre>";

			StackTraceElement[] stes = t.getStackTrace();
			for (int i = 0; i < stes.length; i++) {
				StackTraceElement ste = stes[i];
				s += ste.toString() + "\n";
			}

			s += "</pre>";

			rootcausefound = true;

		}

		//if no initial error present, display the normal throwable error
		if (t != null && !rootcausefound) {

			while (t.getCause() != null)
				t = t.getCause();

			s += "<h3>StackTrace</h3>";
			s += "<pre>";

			StackTraceElement[] stes = t.getStackTrace();
			for (int i = 0; i < stes.length; i++) {
				StackTraceElement ste = stes[i];
				s += ste.toString() + "\n";
			}

			s += "</pre>";

		}

		//call errorpage
		errorPage(request, response, s);

	}

	//	when using an error-page to catch ServletException thrown from a
	// servlet
	// the
	//	implicit JSP object 'exception' is not initialised because it is
	// looking
	// for an
	//	attribute called 'javax.servlet.jsp.jspException' which has not been
	// set
	//	(the attribute 'javax.servlet.error.exception' is set, however, but
	// is
	// not
	//	checked).
	//
	//	this scenario does not happen if the exception is thrown from a JSP
	// -
	//	'javax.servlet.jsp.jspException' is set as expected, as is
	//	'javax.servlet.error.exception'.
	//
	//	for example, these are the attributes available in the error-page
	// when
	// an
	//	exception is thrown from a servlet:
	//
	//	  javax.servlet.error.message = dang
	//	  javax.servlet.error.exception = javax.servlet.ServletException: dang
	//	  javax.servlet.error.servlet_name = S1
	//	  javax.servlet.error.request_uri = /test/servlet/S1
	//	  javax.servlet.error.exception_type = class
	// javax.servlet.ServletException
	//
	//	while these are the attributes when the exception is thrown from a
	// JSP:
	//
	//	  javax.servlet.error.message = damn
	//	  javax.servlet.error.exception = javax.servlet.ServletException: damn
	//	  javax.servlet.jsp.jspException = javax.servlet.ServletException:
	// damn
	//	  javax.servlet.error.servlet_name = jsp
	//	  javax.servlet.error.request_uri = /test/damn.jsp
	//	  javax.servlet.error.exception_type = class
	// javax.servlet.ServletException
	//
	//	my work-around is to explicitly check for the
	// 'javax.servlet.error.exception' in
	//	my error-page. the real fix should be done in
	//	'jasper/src/share/org/apache/jasper/runtime/PageContextImpl.java'

	/**
	 * Leitet zur Fehlerseite um.
	 * 
	 * @param request
	 * @param response
	 * @param message
	 */
	public void errorPage(HttpServletRequest request,
			HttpServletResponse response, String message) {

		request.getSession().setAttribute("qMessage", message);

		try {

			LangUtil.consoleDebug(DEBUG, "Forwarding to error page /error.jsp");

			RequestDispatcher rd = request.getRequestDispatcher("/error.jsp");
			rd.forward(request, response);

		} catch (Exception e) {

			//zu error.jsp konnte nicht umgeleitet werden, daher lokaler
			// output
			System.out.println("ERRPAGE: LOCAL");

			try {
				response.getWriter().print(message);
			} catch (Exception e2) {
				System.out.println("ERRPAGE: LOCAL OUTPUT FAILED");
			}

		}

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		processRequest(request, response);

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		processRequest(request, response);

	}

	/**
	 * In dieser Methode werden alle einmaligen Initialisierungen für die
	 * gesamte Laufzeit der Anwendung durchgeführt.
	 */
	public void init() throws ServletException {

		try {
			this.workflowbuilder = new WebappWorkflowBuilder();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}

	}

}
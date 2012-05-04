/*
 * Created on 03.11.2003
 *  
 */
package com.wanci.dmerce.servlet;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.exceptions.FieldNotFoundException;
import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.dmerce.taglib.form.FormField;
import com.wanci.dmerce.webservice.SQLService;
import com.wanci.dmerce.webservice.db.QResult;
import com.wanci.dmerce.webservice.db.QRow;
import com.wanci.dmerce.workflow.webapp.WebappWorkflowBuilder;
import com.wanci.dmerce.workflow.webapp.WebappWorkflowContext;
import com.wanci.dmerce.workflow.webapp.WebappWorkflowEngine;
import com.wanci.java.LangUtil;

/**
 * Das zentrale Dmerce-Servlet, das als Request-Dispatcher alle Anfragen an die
 * entsprechenden Komponenten weiterleitet.
 * 
 * @author Masanori Fujita
 */
public class Dmerce3 extends HttpServlet {

	/*
	 * Xml Properties Reader
	 */
	private XmlPropertiesReader xpr;

	private boolean DEBUG = false;

	private boolean DEBUG2 = false;

	private WebappWorkflowBuilder workflowbuilder;

	private SQLService query = null;

	private String authTable;

	private String authUsernameField;

	private String authPasswordField;

	private String authRealmField;

	private String authDataFields;

	private String authLastLoginField;

	private String authLoggedInFlagField;

	private String authLoginSuccessfulPage;

	private String authLoginUnsuccessfulPage;

	private String authLogoutPage;

	/**
	 * 
	 *  
	 */
	public Dmerce3() {

		try {

			xpr = XmlPropertiesReader.getInstance();

			// Debugging
			DEBUG = xpr.getPropertyAsBoolean("debug");
			DEBUG2 = xpr.getPropertyAsBoolean("core.debug");

			// Authentifizierung/Authorisierung
			authTable = xpr.getProperty("auth.table");
			authUsernameField = xpr.getProperty("auth.usernamefield");
			authPasswordField = xpr.getProperty("auth.passwordfield");
			authRealmField = xpr.getProperty("auth.realmfield");
			authDataFields = xpr.getProperty("auth.datafields");
			authLoggedInFlagField = xpr.getProperty("auth.loggedinflagfield");
			authLastLoginField = xpr.getProperty("auth.lastloginfield");
			authLoginSuccessfulPage = xpr
					.getProperty("auth.login.successfullpage");
			authLoginUnsuccessfulPage = xpr
					.getProperty("auth.login.unsuccessfullpage");
			authLogoutPage = xpr.getProperty("auth.logout.page");

		} catch (XmlPropertiesFormatException e) {
		}

	}

	/**
	 * Zentrale Methode zur Verarbeitung von Requests
	 * 
	 * @param request
	 * @param response
	 */
	public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		if (request.getRequestURI().indexOf("/navigate") > -1) {

			processNavigation(request, response);
			return;

		} else if (request.getRequestURI().indexOf("/workflow") > -1) {

			processWorkflow(request, response);
			return;

		} else if (request.getRequestURI().indexOf("/delete") > -1) {

			processDelete(request, response);
			return;

		} else if (request.getRequestURI().indexOf("/clearfield") > -1) {

			if (request.getParameter("field") == null)
				errorPage(request, response,
						"For deletion of a file you have to supply a field");
			clearField(request, response);

			return;

		} else if (request.getRequestURI().indexOf("/login") > -1) {

			processLogin(request, response);
			return;

		} else if (request.getRequestURI().indexOf("/logout") > -1) {

			processLogout(request, response);
			return;

		} else
			errorPage(request, response, "Request URI '"
					+ request.getRequestURI() + " is not supported!");

	}

	/**
	 *  
	 */
	private SQLService getDatabaseConnection(HttpServletRequest request,
			HttpServletResponse response) {

		// Webservice initialisieren, wenn noch nicht geschehen
		try {

			if (query == null)
				query = new SQLService();

		} catch (DmerceException e) {
			errorPage(request, response, e.getMessage());
		}

		return query;

	}

	/**
	 * 
	 * @param request
	 * @param response
	 */
	private void processLogin(HttpServletRequest request,
			HttpServletResponse response) {

		boolean loginSuccessful = false;
		SQLService query = getDatabaseConnection(request, response);
		QResult result;
		String username = null;
		String password = null;

		try {

			username = request.getParameter("qUsername").replace('\'', ' ');
			password = request.getParameter("qPassword").replace('\'', ' ');

		} catch (NullPointerException e) {

			errorPage(request, response,
					"Missing username and/or password for login!");

		}

		// Debug
		LangUtil.consoleDebug(DEBUG, "Starting login procedure for '"
				+ username + "/length of password=" + password.length()
				+ " chars'");

		// Vergleiche eingegebenes Login mit der Datenbank und speichere
		// Ergebnis in AuthInfo-Objekt
		try {

			String stmt;
			Iterator rowIterator;
			QRow qRow;

			stmt = "SELECT id, " + authDataFields + ", " + authRealmField
					+ " FROM " + authTable + " WHERE " + authUsernameField
					+ " = '" + username + "' AND " + authPasswordField + " = '"
					+ password + "'";

			result = query.executeQuery(stmt);
			rowIterator = result.getRows().iterator();

			// Login erfolgreich, wenn eine Zeile gefunden wurde
			// Abspeichern der gefundenen Daten in der Session (AuthInfo)
			if (rowIterator.hasNext()) {

				// Debug
				LangUtil.consoleDebug(DEBUG, "Creating auth info for '"
						+ username + "'" + " (will store in session context)");

				qRow = (QRow) rowIterator.next();

				// AuthInfo Objekt anlegen und in die Session schreiben
				AuthInfo authInfo = new AuthInfo(username, qRow.getFields());
				request.getSession().setAttribute("qAuthInfo", authInfo);

				// Das Login war erfolgreich
				loginSuccessful = true;

				// Map und ID für Datenbankupdate
				Map map;
				Object id = qRow.getFields().get("id");

				// Debug
				LangUtil.consoleDebug(DEBUG2, this, "Updating last login");
				// Update lastlogin
				map = new HashMap();
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				map.put("id", id);
				map.put(authLastLoginField, cal);
				query.updateData(authTable, "id", map);

				// Debug
				LangUtil.consoleDebug(DEBUG2, this, "Setting logged in flag");
				// Logged-in-Flag setzen
				map = new HashMap();
				map.put("id", id);
				map.put(authLoggedInFlagField, new Integer(1));
				query.updateData(authTable, "id", map);

				// Debug
				LangUtil.consoleDebug(DEBUG2, this, "Counting active logins");
				stmt = "SELECT COUNT(*) c" + " FROM " + authTable + " WHERE "
						+ authLoggedInFlagField + " = 1";

				result = query.executeQuery(stmt);
				rowIterator = result.getRows().iterator();

				if (rowIterator.hasNext()) {

					qRow = (QRow) rowIterator.next();

					// Anzahl der aktiven Sessions holen und in das AuthInfo
					// Objekt schreiben
					authInfo.setActiveSessionsCount(((Integer) qRow.getFields()
							.get("c")).intValue());

				}

			}

		} catch (Exception e) {

			// Debug
			LangUtil.consoleDebug(DEBUG, "Could not validate login for '"
					+ username + "': " + e.getMessage());

			// Setze qAuthInfo auf null; damit nicht ein fehlgeschlagenes Login
			// dazu führt, dass ein altes/vorheriges Login aktiv bleibt
			request.getSession().setAttribute("qAuthInfo", null);

			errorPage(request, response, e.getMessage());

		}

		if (loginSuccessful) {

			// Debug
			LangUtil.consoleDebug(DEBUG, "Login for '" + username
					+ "' succesful. " + "" + "Forwarding to page '"
					+ authLoginSuccessfulPage + "'");

			// Login erfolgreich, weiterleiten an
			// "auth.login.succesfulpage"
			forwardTo(request, response, authLoginSuccessfulPage);

		} else {

			// Debug
			LangUtil.consoleDebug(DEBUG, "Login for '" + username
					+ "' NOT succesful. " + "" + "Forwarding to page '"
					+ authLoginUnsuccessfulPage + "'");

			// Setze qAuthInfo auf null
			request.getSession().setAttribute("qAuthInfo", null);

			// Login nicht erfolgreich, weiterleiten an
			// "auth.login.unsuccesfulpage"
			forwardTo(request, response, authLoginUnsuccessfulPage);

		}

	}

	/**
	 * Logout: entferne AuthInfo-Objekt aus Session
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	private void processLogout(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		// Debug
		LangUtil.consoleDebug(DEBUG, "Removing auth info from session");

		// Hole auth info object
		AuthInfo authInfo = (AuthInfo) request.getSession().getAttribute(
				"qAuthInfo");

		// Map für Datenbankupdate
		Map map;
		// Debug
		LangUtil.consoleDebug(DEBUG2, this, "Setting logged in flag (0)");
		// Logged-in-Flag setzen
		map = new HashMap();
		map.put("id", Integer.valueOf(authInfo.getDataField("id")));
		map.put(authLoggedInFlagField, new Integer(0));
		query.updateData(authTable, "id", map);

		// Setze qAuthInfo auf null
		request.getSession().setAttribute("qAuthInfo", null);

		// Forward to "auth.logout.page"
		forwardTo(request, response, authLogoutPage);

	}

	/**
	 * @param request
	 * @param response
	 */
	private void clearField(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		WebappWorkflowEngine engine = (WebappWorkflowEngine) request
				.getSession().getAttribute("qWorkflowEngine");

		assert engine != null : "No workflow engine found in session!";
		String pageId = request.getParameter("page");
		String formId = request.getParameter("form");
		String fieldId = request.getParameter("field");
		assert pageId != null;
		assert formId != null;
		assert fieldId != null;

		WebappWorkflowContext ctx = engine.getWorkflowContext(engine
				.getCurrentWorkflow());

		FormField ff = null;

		try {
			ff = ctx.getFormField(pageId, formId, fieldId);
		} catch (FieldNotFoundException e) {

			throw new ServletException("Requested form field " + pageId + "."
					+ formId + "." + fieldId + " could not be found!");

		}

		// Wert löschen
		ff.setValue(null);
		// zurück umleiten
		processNavigation(request, response);

	}

	/**
	 * @param request
	 * @param response
	 */
	private void processNavigation(HttpServletRequest request,
			HttpServletResponse response) {

		String template = request.getParameter("qTemplate");

		if (template == null) {

			template = (String) request.getSession().getAttribute("qTemplate");

			if (template == null) {
				errorPage(request, response, "No template given!");
				return;
			}

		}

		try {
			response.sendRedirect(template);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Leitet zur Fehlerseite um.
	 * 
	 * @param request
	 * @param response
	 */
	public void errorPage(HttpServletRequest request,
			HttpServletResponse response, Throwable t) {

		// Root-Cause suchen
		Throwable pt = t;
		while (pt.getCause() != null) {
			pt = pt.getCause();
		}

		request.getSession().setAttribute("qMessage", pt.getMessage());
		request.getSession().setAttribute("qException", t);
		request.getSession().setAttribute("qRootCause", pt);

		try {
			response.sendRedirect(request.getContextPath() + "/error.jsp");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
			response.sendRedirect(request.getContextPath() + "/error.jsp");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Leitet zu beliebieger Seite um. Wird kein 'http://' im String 'url'
	 * angegeben, dann wird eine JSP im aktuellen Context der Applikation
	 * angenommen
	 * 
	 * @param url
	 */
	public void forwardTo(HttpServletRequest request,
			HttpServletResponse response, String url) {

		try {

			if (url.startsWith("http://"))
				response.sendRedirect(url);
			else
				response.sendRedirect(request.getContextPath() + "/" + url);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Führt die Workflow-Verarbeitung durch.
	 * 
	 * @param request
	 * @param response
	 */
	private void processWorkflow(HttpServletRequest request,
			HttpServletResponse response) {

		// Workflowengine holen und den Workflow verarbeiten lassen
		try {

			LangUtil.consoleDebug(DEBUG, "Starting workflow for request '"
					+ request.getRequestURI() + "'");

			getWorkflowEngine(request).processWorkflow(request, response);

			LangUtil.consoleDebug(DEBUG, "Workflow for '"
					+ request.getRequestURI() + "' done");

		} catch (Exception e) {
			e.printStackTrace();
			errorPage(request, response, e);
		}

	}

	/**
	 * @param request
	 * @param response
	 */
	private void processDelete(HttpServletRequest request,
			HttpServletResponse response) {

		SQLService query = null;

		// Webservice initialisieren

		try {
			query = new SQLService();
		} catch (Exception e) {
			errorPage(request, response, "Could not initialize SQL webserivce!");
			return;
		}

		int result = query.deleteData(request.getParameter("table"), request
				.getParameter("key"), Integer.valueOf(
				request.getParameter("id")).intValue());
		if (result < 1)
			errorPage(request, response, "Could not delete data!");

		processNavigation(request, response);

	}

	/**
	 * Versucht aus dem Session-Kontext eine Workflow-Engine zu ziehen. Falls
	 * noch keine Engine in dieser Session existiert, wird eine neue Engine
	 * angelegt und gespeichert.
	 * 
	 * @param request
	 * @return WebappWorkflowEngine
	 * @throws DmerceException
	 */
	private WebappWorkflowEngine getWorkflowEngine(HttpServletRequest request)
			throws DmerceException {

		LangUtil.consoleDebug(DEBUG, "Looking for workflow engine");
		WebappWorkflowEngine engine = (WebappWorkflowEngine) request
				.getSession().getAttribute("qWorkflowEngine");

		if (engine == null) {

			try {

				LangUtil.consoleDebug(DEBUG, "Creating new workflow engine");

				engine = new WebappWorkflowEngine();
				workflowbuilder.readForms(engine);
				workflowbuilder.readWorkflows(engine);
				request.getSession().setAttribute("qWorkflowEngine", engine);

				LangUtil.consoleDebug(DEBUG,
						"Workflow engine successfully created");

			} catch (WorkflowConfigurationException e) {
				e.printStackTrace();
				throw new DmerceException(e.getMessage());
			}

		} else {
			LangUtil.consoleDebug(DEBUG, "Found workflow engine");
		}
		return engine;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
		processRequest(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException {
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
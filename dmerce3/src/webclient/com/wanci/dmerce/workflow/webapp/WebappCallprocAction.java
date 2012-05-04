/*
 * Datei angelegt am 30.01.2004
 */
package com.wanci.dmerce.workflow.webapp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.exceptions.FieldNotFoundException;
import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.exceptions.WorkflowRuntimeException;
import com.wanci.dmerce.taglib.form.FormField;
import com.wanci.dmerce.webservice.SQLService;
import com.wanci.dmerce.webservice.db.JAXBTypeMap;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.java.LangUtil;

/**
 * WebappCallprocAction ist ein vordefinierter Action-Typ zum Aufruf eines
 * Stored Procedures. Per <formfield>-Tags können im <action>-Tag Werte an
 * die Stored Procedure übergeben werden. Diese Klasse ist nur für Stored
 * Procedures ohne Rückgabewerte geeignet.
 * 
 * @author Masanori Fujita
 */
public class WebappCallprocAction extends WebappAction {

	private String procName;

	private FieldList fieldlist;

	private SQLService service;

	private JAXBTypeMap typemap;

	/**
	 * @param parent
	 * @throws WorkflowConfigurationException
	 */
	public WebappCallprocAction(
		Transition parent,
		String procName,
		FieldList fieldlist)
		throws WorkflowConfigurationException {

		super(parent);

		this.procName = procName;
		this.fieldlist = fieldlist;
		typemap = new JAXBTypeMap();

		setDescription(
			"Calls a stored procedure '" + procName + "' using the webservice");

	}

	private SQLService getSQLService() throws DmerceException {

		if (service == null) {
			service = new SQLService();
			return service;
		}
		else
			return service;

	}

	public void execute(WebappWorkflowContext context)
		throws WorkflowRuntimeException, WorkflowConfigurationException {

		// Parameterliste, die an die Proc übergeben werden soll,
		// initialisieren
		List parameterList = new ArrayList();
		// Formular-Namen auslesen
		String strFormId = ((WebappState) getParentState()).getFormId();
		LangUtil.consoleDebug(
			DEBUG,
			"WebappCallprocAction.execute mit State "
				+ getParentState().getStateId()
				+ ", Form-ID="
				+ strFormId);
		// Fieldlist durchlaufen
		Iterator it = fieldlist.iterator();
		while (it.hasNext()) {
			// Feldbezeichnung holen
			FieldList.FormField field = (FieldList.FormField) it.next();
			// Betroffene Formmap suchen
			String pageId;
			if (field.getPageId() == null) {
				// Formularfeld liegt in der gleichen Page wie diese Transition
				pageId = ((WebappState) getParentState()).getPageId();
			}
			else {
				// Formularfeld ist auf einer anderen Page
				pageId = field.getPageId();
			}
			// Formmap mittels pageId holen
			/*
			 * Formmap formmap = getFormmap(pageId); String dbFieldName =
			 * formmap.getDbFieldName(fieldName);
			 */
			// FormField im Kontext anhand der Formular-ID und dem Feldnamen
			// suchen
			String fieldName = field.getFieldname();
			FormField formField;
			try {
				formField = context.getFormField(pageId, strFormId, fieldName);
			}
			catch (FieldNotFoundException e) {
				String actionPath =
					"Workflow["
						+ getWorkflow().getWorkflowId()
						+ "]/Page["
						+ ((WebappState) getParentState()).getPageId()
						+ "]/Transition["
						+ getParentTransition().getId()
						+ "]";
				String message =
					"In der workflows.xml wird in der Action zur Transition "
						+ actionPath
						+ " das Formularfeld Page["
						+ pageId
						+ "]/Form["
						+ strFormId
						+ "]/Field["
						+ fieldName
						+ "] referenziert.";
				message
					+= "\nDas angegebene Feld kann jedoch nicht im Workflow-Context gefunden werden.";
				message
					+= "\nÜberprüfen Sie bitte die workflows.xml an der o.g. Stelle.";
				WorkflowConfigurationException wce =
					new WorkflowConfigurationException(message);
				throw wce;
			}
			if (formField.getType().equals("file")) {
				// Sonderbehandlung für Fileupload-Felder, kein
				// JAXB-Typemapping,
				// da Dateinamen als String übernommen werden können.
				parameterList.add(formField.getValues()[0]);
				parameterList.add(formField.getValues()[1]);
			}
			else {
				// String-Wert aus FormField in JAXB-Typ umwandeln
				Object val;
				try {
					val =
						typemap.toJAXBObject(
							formField.getValue(),
							formField.getType(),
							formField.getDisplayFormat());
				}
				catch (DmerceException e) {
					e.printStackTrace();
					throw new WorkflowRuntimeException(
						"WebappCallprocAction: Fehler beim TypeMapping. "
							+ e.getMessage());
				}
				// LangUtil.consoleDebug(DEBUG, "CallprocAction, FormField
				// "+formField.getName()+",
				// Wert: "+val);
				// Wert in die Liste übernehmen
				parameterList.add(val);
			}
		}
		try {
			// Webservice aufrufen
			getSQLService().callProc(procName, parameterList);
		}
		catch (DmerceException e) {
			e.printStackTrace();
			throw new WorkflowRuntimeException(
				"WebappCallprocAction: Fehler beim Aufruf des Webservices: "
					+ e.getMessage());
		}
	}

}

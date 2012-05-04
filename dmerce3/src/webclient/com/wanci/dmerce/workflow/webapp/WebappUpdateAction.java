/*
 * Datei angelegt am 31.10.2003
 */
package com.wanci.dmerce.workflow.webapp;

import java.util.HashMap;
import java.util.Iterator;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.exceptions.FieldNotFoundException;
import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.exceptions.WorkflowRuntimeException;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.dmerce.taglib.form.FormField;
import com.wanci.dmerce.webservice.SQLService;
import com.wanci.dmerce.webservice.db.JAXBTypeMap;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.java.LangUtil;

/**
 * Aktion, die in einem Workflow aufgrund der Formmap einen Update-Vorgang über
 * den dmerce-Webservice ausführt.
 * 
 * @author Masanori Fujita
 */
public class WebappUpdateAction extends WebappAction {

	private boolean DBEUG = false;

	private boolean DEBUG2 = false;

	private FieldList fieldlist;

	private SQLService service;

	private JAXBTypeMap typemap;

	private String SUFFIX_ORIGINAL = "_original";
	private String SUFFIX_SERVER = "_server";

	/**
     * Map, die wiederum Maps enthält, um die Key-Value-Paare zu halten. Key
     * dieser Map sind die Page-IDs Werte sind HashMaps
     */
	private HashMap valueMaps;
	private HashMap primKeyMap;

	/**
     * @param transition
     * @param fieldlist
     * @throws WorkflowConfigurationException
     */
	public WebappUpdateAction(Transition transition, FieldList fieldlist)
		throws WorkflowConfigurationException {

		super(transition);

		try {

			DEBUG =
				XmlPropertiesReader.getInstance().getPropertyAsBoolean("debug");
			DEBUG2 =
				XmlPropertiesReader.getInstance().getPropertyAsBoolean(
					"core.debug");

			// SUFFIX auslesen
			SUFFIX_ORIGINAL =
				XmlPropertiesReader.getInstance().getProperty(
					"fileupload.columnsuffix.originalfile");
			SUFFIX_SERVER =
				XmlPropertiesReader.getInstance().getProperty(
					"fileupload.columnsuffix.serverfile");

		}
		catch (XmlPropertiesFormatException e) {
			e.printStackTrace();
		}

		this.fieldlist = fieldlist;
		this.typemap = new JAXBTypeMap();
		this.valueMaps = new HashMap();
		this.primKeyMap = new HashMap();

		setDescription("Aktualisiert einen Datensatz über den Webservice.");

	}

	public void execute(WebappWorkflowContext context)
		throws WorkflowRuntimeException, WorkflowConfigurationException {

		try {

			// Formular-Namen auslesen
			String strFormId = ((WebappState) getParentState()).getFormId();

			LangUtil.consoleDebug(
				DEBUG2,
				this,
				"execute(): WebappUpdateAction to state '"
					+ getParentState().getStateId()
					+ "', form ID '"
					+ strFormId
					+ "'");

			// Zu speichernde Felder durchlaufen
			Iterator it = fieldlist.iterator();
			while (it.hasNext()) {

				// Feldbezeichnung holen
				FieldList.FormField field = (FieldList.FormField) it.next();
				// Betroffene Formmap suchen
				String pageId;
				if (field.getPageId() == null) {
					// Formmap in der gleichen Page wie diese Transition suchen
					pageId = ((WebappState) getParentState()).getPageId();
				}
				else {
					// Formmap global in diesem Workflow suchen.
					pageId = field.getPageId();
				}

				// Formmap mittels pageId holen
				Formmap formmap = getFormmap(pageId);
				String fieldName = field.getFieldname();
				String dbFieldName = formmap.getDbFieldName(fieldName);

				// FormField im Kontext anhand der Formular-ID und dem
				// Feldnamen suchen
				FormField formField;
				try {
					formField =
						context.getFormField(pageId, strFormId, fieldName);
				}
				catch (FieldNotFoundException e1) {

					String actionPath =
						"Workflow["
							+ getWorkflow().getWorkflowId()
							+ "]/Page["
							+ ((WebappState) getParentState()).getPageId()
							+ "]/Transition["
							+ getParentTransition().getId()
							+ "]";
					String unknownFieldPath =
						"Page["
							+ pageId
							+ "]/Form["
							+ strFormId
							+ "]/Field["
							+ fieldName
							+ "]";
					String message =
						"The reference to field '"
							+ unknownFieldPath
							+ "' in the action of your transition '"
							+ actionPath
							+ "' was not found in the workflow context."
							+ " Please check etc/workflows.xml"
							+ " of your application";

					LangUtil.consoleDebug(DEBUG, message);

					throw new WorkflowConfigurationException(message);

				}

				// Sonderfall: Datei-Uploads sind gleich zwei Felder
				if (formField.getType().equals("file")) {

					LangUtil.consoleDebug(
						DEBUG2,
						this,
						"Found file(upload) in field '"
							+ formField.getName()
							+ " in page "
							+ formField.getPageId());

					if (formField.getValues() != null) {

						rememberUpdate(
							pageId,
							dbFieldName + this.SUFFIX_ORIGINAL,
							formField.getValues()[1]);

						rememberUpdate(
							pageId,
							dbFieldName + this.SUFFIX_SERVER,
							formField.getValues()[0]);

					}
					else {

						rememberUpdate(
							pageId,
							dbFieldName + this.SUFFIX_ORIGINAL,
							null);

						rememberUpdate(
							pageId,
							dbFieldName + this.SUFFIX_SERVER,
							null);

					}

				}
				else {

					// String-Wert aus FormField in JAXB-Typ umwandeln
					Object val =
						typemap.toJAXBObject(
							formField.getValue(),
							formField.getType(),
							formField.getDisplayFormat());

					// Update vormerken
					rememberUpdate(pageId, dbFieldName, val);

				}

				// Primary-Key in in ValueMap vormerken
				// String-Wert aus FormField in JAXB-Typ umwandeln
				Integer primkeyValue = new Integer((String) context.get("id"));
				rememberUpdate(
					pageId,
					formmap.getPrimaryKeyName(),
					primkeyValue);

			}

			// Update tatsächlich ausführen.
			doUpdate();

		}
		catch (DmerceException e) {
			throw new WorkflowRuntimeException(e.getMessage());
		}

	}

	/**
     * @return @throws
     *         DmerceException
     */
	private SQLService getSQLService() throws DmerceException {

		if (service == null) {
			service = new SQLService();
			return service;
		}
		else
			return service;

	}

	/**
     * Merkt sich einen noch zu tätigenden Datenbankeintrag.
     * 
     * @param pageId
     *            Page-ID des betroffenen Formularfeldes
     * @param dbFieldName
     *            DB-Feldname des zu speichernden Formularfeldes
     * @param value
     *            Wert des zu speichernden Formularfeldes
     */
	public void rememberUpdate(
		String pageId,
		String dbFieldName,
		Object value) {

		Formmap formmap = getFormmap(pageId);
		String tableName = formmap.getTable();
		primKeyMap.put(tableName, formmap.getPrimaryKeyName());

		if (valueMaps.get(tableName) == null)
			valueMaps.put(tableName, new HashMap());

		HashMap data = (HashMap) valueMaps.get(tableName);
		data.put(dbFieldName, value);

		LangUtil.consoleDebug(
			DEBUG2,
			this,
			"Remembering database manipulation on "
				+ " table '"
				+ tableName
				+ "' using primary key '"
				+ formmap.getPrimaryKeyName()
				+ "' for field '"
				+ dbFieldName
				+ "' value '"
				+ value
				+ "'");

	}

	/**
     * Führt die tatsächlichen Update-Operationen aus.
     * 
     * @throws DmerceException
     */
	public void doUpdate() throws DmerceException {

		// Map für Key-Value-Paare durchlaufen
		Iterator it = valueMaps.keySet().iterator();
		while (it.hasNext()) {

			String tableName = (String) it.next();
			String primKeyName = (String) primKeyMap.get(tableName);
			HashMap m = (HashMap) valueMaps.get(tableName);

			LangUtil.consoleDebug(
				DEBUG2,
				this,
				"Calling SQLService.updateData("
					+ tableName
					+ ","
					+ primKeyName
					+ ","
					+ m
					+ ")");

			int result = getSQLService().updateData(tableName, primKeyName, m);

			if (result < 0) {

				String message =
					"Error while updating data on table '"
						+ tableName
						+ "' using primary key '"
						+ primKeyName
						+ "': "
						+ getSQLService().getSqlErrorMessage();

				LangUtil.consoleDebug(DEBUG, message);

				throw new DmerceException(message);

			}

		}

	}

	/**
     * Sucht nach einer Formmap zu einer pageid
     */
	private Formmap getFormmap(String pageid) {

		Formmap formmap =
			((WebappState) getWorkflow().getState(pageid)).getFormmap();

		assert formmap
			!= null : "Could not find a <formmap> in page id '" + pageid + "'";

		return formmap;

	}

}

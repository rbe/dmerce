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
 * Aktion, die in einem Workflow aufgrund der Formmap einen Insert-Vorgang über
 * den dmerce-Webservice ausführt.
 * 
 * @author Masanori Fujita
 */
public class WebappInsertAction extends WebappAction {

	private boolean DEBUG2 = false;

	private FieldList fieldlist;
	private SQLService service;
	private JAXBTypeMap typemap;
	private String SUFFIX_ORIGINAL = "_original";
	private String SUFFIX_SERVER = "_server";

	public WebappInsertAction(Transition transition, FieldList fieldlist)
		throws WorkflowConfigurationException {

		super(transition);

		try {
			DEBUG =
				XmlPropertiesReader.getInstance().getPropertyAsBoolean("debug");
			DEBUG2 =
				XmlPropertiesReader.getInstance().getPropertyAsBoolean(
					"core.debug");
		}
		catch (XmlPropertiesFormatException e) {
		}

		try {
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

		// TODO: An dieser Stelle die Fieldlist prüfen! D.h. schauen, ob auf
		// die referenzierten Formularfelder zugegriffen werden kann.
		setDescription("Erzeugt einen Datensatz über den Webservice.");
		typemap = new JAXBTypeMap();
		valueMaps = new HashMap();
		primKeyMap = new HashMap();

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
		try {
			// Formular-Namen auslesen
			String strFormId = ((WebappState) getParentState()).getFormId();
			LangUtil.consoleDebug(
				DEBUG,
				"WebappInsertAction.execute mit State "
					+ getParentState().getStateId()
					+ ", Form-ID="
					+ strFormId);
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
				// Sonderfall: Datei-Uploads sind gleich zwei Felder
				if (formField.getType().equals("file")) {
					if (formField.getValues() != null) {
						rememberInsert(
							pageId,
							dbFieldName + this.SUFFIX_ORIGINAL,
							formField.getValues()[1]);
						rememberInsert(
							pageId,
							dbFieldName + this.SUFFIX_SERVER,
							formField.getValues()[0]);
					}
					else {
						rememberInsert(
							pageId,
							dbFieldName + this.SUFFIX_ORIGINAL,
							null);
						rememberInsert(
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
					LangUtil.consoleDebug(
						DEBUG,
						"InsertAction, FormField "
							+ formField.getName()
							+ ", Wert: "
							+ val);
					// Insert vormerken
					rememberInsert(pageId, dbFieldName, val);
				}
			}
			// Insert tatsächlich ausführen.
			doInsert();
		}
		catch (DmerceException e) {
			throw new WorkflowRuntimeException(e.getMessage());
		}
	}

	/*
	 * Map, die wiederum Maps enthält, um die Key-Value-Paare zu halten. Key
	 * dieser Map sind die Page-IDs Werte sind HashMaps
	 */
	private HashMap valueMaps;
	private HashMap primKeyMap;

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
	public void rememberInsert(
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
	}

	/**
	 * Führt die tatsächlichen Insert-Operationen aus.
	 * 
	 * @throws DmerceException
	 */
	public void doInsert() throws DmerceException {
		// Map für Key-Value-Paare durchlaufen
		Iterator it = valueMaps.keySet().iterator();
		while (it.hasNext()) {
			String tableName = (String) it.next();
			String primKeyName = (String) primKeyMap.get(tableName);
			int result =
				getSQLService().insertData(
					tableName,
					primKeyName,
					(HashMap) valueMaps.get(tableName));
			if (result < 0)
				throw new DmerceException(
					"InsertAction: INSERT ist fehlgeschlagen. Table="
						+ tableName
						+ ", Primary-Key="
						+ primKeyName);
		}
	}

	/**
	 * Sucht nach einer Formmap zu einer pageid
	 */
	private Formmap getFormmap(String pageid) {
		Formmap formmap =
			((WebappState) getWorkflow().getState(pageid)).getFormmap();
		assert formmap
			!= null : "Unter der pageid "
				+ pageid
				+ " ist keine Formmap gespeichert.";
		return formmap;
	}

}

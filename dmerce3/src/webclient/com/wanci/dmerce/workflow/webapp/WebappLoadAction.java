/*
 * Datei angelegt am 04.11.2003
 */
package com.wanci.dmerce.workflow.webapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.exceptions.FieldNotFoundException;
import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.exceptions.WorkflowRuntimeException;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.dmerce.taglib.form.FormField;
import com.wanci.dmerce.webservice.SQLService;
import com.wanci.dmerce.webservice.db.QColumnException;
import com.wanci.dmerce.webservice.db.QColumnTypeException;
import com.wanci.dmerce.webservice.db.QResult;
import com.wanci.dmerce.webservice.db.QRow;
import com.wanci.dmerce.webservice.db.QTypeMissmatchException;
import com.wanci.dmerce.workflow.Transition;

/**
 * @author Masanori Fujita
 */
public class WebappLoadAction extends WebappAction {

	private SQLService service;
	private Formmap formmap;
	private String SUFFIX_ORIGINAL = "_original";
	private String SUFFIX_SERVER = "_server";

	/**
	 * @param parent
	 * @throws WorkflowConfigurationException
	 */
	public WebappLoadAction(Transition parent, Formmap formmap)
		throws WorkflowConfigurationException {
		super(parent);
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
		this.formmap = formmap;
		setDescription(
			"Diese Aktion lädt einen Datensatz anhand der Formmap zur umgebenden Seite. [Tabelle="
				+ formmap.getTable()
				+ "]");
	}

	private SQLService getSQLService() throws DmerceException {
		if (service == null) {
			service = new SQLService();
			return service;
		}
		else
			return service;
	}

	protected void execute(WebappWorkflowContext context)
		throws WorkflowRuntimeException, WorkflowConfigurationException {

		// Primary-Key-Feld holen
		String strPrimKeyName = formmap.getPrimaryKeyName();
		// Primay-Key-Wert des zu ladenen Datensatzes holen
		String strPrimKeyValue = (String) context.get("id");
		assert strPrimKeyValue
			!= null : "Es muss ein Request-Parameter \"id\" übergeben werden.";
		// Tabellenname holen
		String strTableName = formmap.getTable();
		// Feldliste für SQL zusammenbauen
		String strFields = "";
		Iterator it = formmap.getFieldnames().iterator();
		while (it.hasNext()) {
			String fieldName = (String) it.next();
			if (formmap.isFileField(formmap.getDbFieldName(fieldName))) {
				// System.out.println("WebappLoadAction: "+fieldName+" is file
				// field.");
				strFields += (strFields.equals("") ? "" : ",")
					+ formmap.getDbFieldName(fieldName)
					+ SUFFIX_ORIGINAL;
				strFields += ","
					+ formmap.getDbFieldName(fieldName)
					+ SUFFIX_SERVER;
			}
			else {
				// System.out.println("WebappLoadAction: "+fieldName+" is
				// ordinary field.");
				strFields += (strFields.equals("") ? "" : ",")
					+ formmap.getDbFieldName(fieldName);
			}
		}
		// Den Rest der SQL-Query zusammenbauen
		String strSQLQuery =
			"SELECT "
				+ strFields
				+ " FROM "
				+ strTableName
				+ " WHERE "
				+ strPrimKeyName
				+ "="
				+ strPrimKeyValue;
		// Query ausführen
		QResult result = null;
		try {
			result = getSQLService().executeQuery(strSQLQuery);
			if (result.getRowCount() < 1)
				throw new WorkflowRuntimeException("Der gesuchte Datensatz konnte nicht geladen werden.");
		}
		catch (QTypeMissmatchException e) {
			throw new WorkflowRuntimeException(e.getMessage());
		}
		catch (QColumnException e) {
			throw new WorkflowRuntimeException(e.getMessage());
		}
		catch (QColumnTypeException e) {
			throw new WorkflowRuntimeException(e.getMessage());
		}
		catch (DmerceException e) {
			throw new WorkflowRuntimeException(e.getMessage());
		}
		// Zeile holen
		QRow row = (QRow) result.getRows().toArray()[0];
		// Felder aus der Zeile holen
		Map fields = row.getFields();
		// Felder durchlaufen, um sie mit Werten zu füllen
		Iterator it2 = formmap.getFieldnames().iterator();
		while (it2.hasNext()) {
			String pageId;
			String formId;
			try {
				pageId = ((WebappState) getDestinationState()).getPageId();
				formId = ((WebappState) getDestinationState()).getFormId();
			}
			catch (WorkflowConfigurationException e) {
				throw new WorkflowRuntimeException(e.getMessage());
			}
			String fieldName = (String) it2.next();
			// Sonderbehandlung für File-Fields
			if (formmap.isFileField(formmap.getDbFieldName(fieldName))) {
				String[] values = new String[2];
				values[0] =
					fields
						.get(
							formmap.getDbFieldName(fieldName).toLowerCase()
								+ SUFFIX_SERVER)
						.toString();
				values[1] =
					fields
						.get(
							formmap.getDbFieldName(fieldName).toLowerCase()
								+ SUFFIX_ORIGINAL)
						.toString();
				try {
					context.getFormField(pageId, formId, fieldName).setValues(
						values);
				}
				catch (FieldNotFoundException e) {
					WorkflowConfigurationException wce =
						new WorkflowConfigurationException(e.getMessage());
					wce.setStackTrace(e.getStackTrace());
					throw wce;
				}
			}
			else {
				Object value =
					fields.get(formmap.getDbFieldName(fieldName).toLowerCase());
				try {
					if (value instanceof Calendar) {
						// Sonderbehandlung für Calender-Objekte: Wert im
						// Format aus der forms.xml speichern
						Calendar calValue = (Calendar) value;
						FormField calFormField =
							context.getFormField(pageId, formId, fieldName);
						SimpleDateFormat sdf;
						if (calFormField.getDisplayFormat() != null) {
							sdf =
								new SimpleDateFormat(
									calFormField.getDisplayFormat());
						}
						else {
							sdf = new SimpleDateFormat();
						}
						calFormField.setValue(sdf.format(calValue.getTime()));
					}
					else {
						context.getFormField(
							pageId,
							formId,
							fieldName).setValue(
							(value != null ? value.toString() : ""));
					}
				}
				catch (FieldNotFoundException e) {
					WorkflowConfigurationException wce =
						new WorkflowConfigurationException(e.getMessage());
					wce.setStackTrace(e.getStackTrace());
					throw wce;
				}
			}
		}
	}

	public void outHashMap(Map map) {
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			System.out.println(key + ": " + map.get(key));
		}
	}

}

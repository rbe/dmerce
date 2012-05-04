/*
 * Datei angelegt am 13.10.2003
 */
package com.wanci.dmerce.workflow.webapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.wanci.dmerce.exceptions.FieldNotFoundException;
import com.wanci.dmerce.taglib.form.FormField;
import com.wanci.dmerce.workflow.WorkflowContext;
import com.wanci.java.LangUtil;

/**
 * Bietet zusätzlich zum Standard-Kontext eines Workflows spezielle Methoden
 * an, die bei Web-Anwendungen häufig benötigt werden.
 * 
 * Hinweis: Cloneable ist im Voraus schon einmal implementiert, weil in
 * absehbarer Zukunft der WebappContext Multiple-Request-fähig gemacht werden
 * soll.
 * 
 * @author Masanori Fujita
 */
public class WebappWorkflowContext extends WorkflowContext implements Cloneable {

	public static boolean DEBUG = false;

	/**
     * Konstante für die Request-Methode GET
     */
	public static final int GET = 0;

	/**
     * Konstante für die Request-Methode POST
     */
	public static final int POST = 1;

	private int intRequestMethod;
	
	/**
     * LinkedHashMap of FormField-Objects
     */
	private LinkedHashMap mapFormFields;
	private String strRequestURL;
	private WebappWorkflowEngine engine;

	/**
     * Standardkonstruktor
     */
	public WebappWorkflowContext(WebappWorkflowEngine engine) {
		super();
		mapFormFields = new LinkedHashMap();
		
		this.engine = engine;
	}

	/**
     * Setzt die Methode, mit der der aktuelle Request gesendet worden ist.
     * 
     * @param method
     *            GET oder POST
     */
	public void setRequestMethod(int method) {
		assert(method == GET) | (method == POST) : "Ungültige Requst-Methode spezifiziert.";
		this.intRequestMethod = method;
	}

	/**
     * Gibt die aktuelle Requst-Methode zurück.
     * 
     * @return GET oder POST
     */
	public int getRequestMethod() {
		return intRequestMethod;
	}

	/**
     * Setzt den angeforderten URL
     * 
     * @param strUrl
     *            Request-URL als String
     */
	public void setRequestURL(String strUrl) {
		assert strUrl != null : "Der Request-URL darf nicht null sein.";
		assert !strUrl.equals("") : "Der Request-URL darf nicht leer sein.";
		this.strRequestURL = strUrl;
	}

	/**
     * Gibt den angeforderten URL zurück
     * 
     * @return Request-URL als String
     */
	public String getRequestURL() {
		return strRequestURL;
	}

	/**
     * Gibt eine Collection aller Formularfelder dieses Workflows zurück.
     * Veränderungen an dieser Collection werden in das interne Map übernommen.
     * Add kann jedoch nicht auf der Collection ausgeführt werden.
     * 
     * @return
     */
	public Collection getFormFields() {
		return mapFormFields.values();
	}

	/**
     * Fügt ein FormField diesem Kontext hinzu.
     * 
     * @param formId
     * @param fieldName
     * @param field
     */
	public void addFormField(String pageId, String formId, String fieldName, FormField field) {
		assert pageId != null : "Page-ID darf nicht null sein.";
		assert !pageId.equals("") : "Page-ID darf nicht leer sein.";
		assert formId != null : "Formular-ID darf nicht null sein.";
		assert !formId.equals("") : "Formular-ID darf nicht leer sein.";
		assert fieldName != null : "Feld-Name darf nicht null sein.";
		assert !fieldName.equals("") : "Feld-Name darf nicht leer sein.";
		assert field != null : "Übergebenes Feld darf nicht null sein.";
		LangUtil.consoleDebug(DEBUG, "(:WorkflowContext).addFormField(" + pageId + ", " + fieldName + ", (:FormField)");
		mapFormFields.put(pageId + "." + formId + "." + fieldName, field);
	}

	/**
     * Gibt das FormField zu dem angegebenen Formular und dem Namen zurück.
     * 
     * @param formId
     * @param fieldName
     * @return
     */
	public FormField getFormField(String pageId, String formId, String fieldName) throws FieldNotFoundException {
		LangUtil.consoleDebug(DEBUG, "(:WorkflowContext).getFormField(" + pageId + "," + formId + "," + fieldName + ")");
		Object o = mapFormFields.get(pageId + "." + formId + "." + fieldName);
		// Wenn das gesuchte FormField noch nicht im Kontext existiert,
		// wird es aus der Engine gezogen.
		if (o == null) {
			FormField ff = (FormField) engine.getFields(formId).get(fieldName);
			if (ff != null) {
				FormField copiedField = (FormField) ff.clone();
				copiedField.setPageId(pageId);
				addFormField(pageId, copiedField);
				o = mapFormFields.get(pageId + "." + formId + "." + fieldName);
			}
		}
		if (o == null) {
			throw new FieldNotFoundException(pageId, formId, fieldName);
		}
		return (FormField) o;
	}

	/**
     * Gibt alle FormFields zu einer Page-ID zurück
     * 
     * @param pageId
     * @return
     */
	public Collection getFormFields(String pageId) {
		Collection result = new ArrayList();
		Iterator it = mapFormFields.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (key.startsWith(pageId + ".")) {
				result.add(mapFormFields.get(key));
			}
		}
		return result;
	}

	/**
     * Prüft, ob ein FormField zu dem angegebenen Formular und Feldnamen im
     * Kontext gespeichert ist.
     * 
     * @param formId
     * @param fieldName
     * @return true, falls das FormField enthalten ist, false sonst
     */
	public boolean containsFormField(String pageId, String formId, String fieldName) {
		return mapFormFields.containsKey(pageId + "." + formId + "." + fieldName);
	}

	/**
     * Fügt ein FormField zu der Liste der Formularfelder dieses Workflows
     * hinzu.
     * 
     * @param field
     */
	public void addFormField(String pageId, FormField field) {
		assert field != null : "Übergebenes FormField darf nicht null sein.";
		LangUtil.consoleDebug(DEBUG, "(:WorkflowContext).addFormField(" + pageId + ", (:FormField){" + field.getFormId() + "." + field.getName() + "}");
		String key = pageId + "." + field.getFormId() + "." + field.getName();
		mapFormFields.put(key, field);
	}

	public String toString() {
		String result = "";
		Iterator it = mapFormFields.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			result += key + ": " + mapFormFields.get(key) + "\n";
		}
		return result;
	}

}

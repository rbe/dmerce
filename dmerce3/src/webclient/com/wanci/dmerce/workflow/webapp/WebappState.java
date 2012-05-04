/*
 * Datei angelegt am 13.10.2003
 */
package com.wanci.dmerce.workflow.webapp;

import java.util.StringTokenizer;

import com.wanci.dmerce.workflow.State;
import com.wanci.dmerce.workflow.Workflow;

/**
 * Repräsentiert einen Zustand in einer Web-Anwendung.
 * Bei Web-Anwendungen ist ein Zustand stets mit einer anzuzeigenden
 * Seite verbunden, so dass WebappState die generische State-Klasse
 * um das Feld Template erweitert.
 * 
 * Ein WebappState kann dazu genutzt werden, eine Verzweigung von 
 * Transitionen zu realisieren. In diesem Fall darf das Template nicht
 * gesetzt werden.
 * 
 * @author Masanori Fujita
 */
public class WebappState extends State {
	
	private WebappTemplate template;
	private String strFormId;
	private String strPageId;
	private Formmap formmap;

	/**
	 * Initialisiert einen Web-Anwendungszustand mit einem Workflow,
	 * einem Zustandsnamen und einem Template.
	 */
	public WebappState(Workflow workflow, String stateId, String pageId, WebappTemplate template) {
		super(workflow, stateId);
		setTemplate(template);
		setPageId(pageId);
	}

	/**
	 * Initialisiert einen Web-Anwendungszustand mit einem Workflow,
	 * einem Zustandsnamen und einem Template.
	 */
	public WebappState(Workflow workflow, String stateId, String pageId, WebappTemplate template, String formId) {
		this(workflow, stateId, pageId, template);
		assert formId != null : "Formular-ID darf nicht null sein.";
		assert !formId.equals("") : "Formular-ID darf nicht leer sein.";
		this.strFormId = formId;
	}
	
	/**
	 * Privater Konstruktor, der zur Erzeugung eines Dummy-Zustandes eingesetzt wird.
	 * @param workflow
	 * @param stateId
	 */
	private WebappState(Workflow workflow, String stateId) {
		super(workflow, stateId);
	}
	
	/**
	 * Erzeugt einen Dummy-Zustand
	 * @param workflow
	 * @param stateId
	 * @return
	 */
	public static WebappState createDummyState(Workflow workflow, String stateId) {
		WebappState newState = new WebappState(workflow, stateId);
		return newState;
	} 

	/**
	 * Setzt die Template-Seite dieses Zustandes
	 * @param template
	 */
	private void setTemplate(WebappTemplate template) {
		assert template != null : "Das übergebene Template darf nicht null sein.";
		this.template = template;
	}

	/**
	 * Gibt die Template-Seite zu diesem Zustand zurück.
	 * @return
	 */
	public String getTemplate() {
		if (template == null)
			return null;
		else
			return template.getTemplate();
	}
	
	/**
	 * Gibt die ID des mit diesem WebappState verbundenen Formulars zurück.
	 * @return Formular-ID als String
	 */
	public String getFormId() {
		return strFormId;
	}
	
	public void setFormId(String formid) {
		this.strFormId = formid;
	}

	/**
	 * Gibt die Formmap zu diesem Zustand zurück.
	 * @return
	 */
	public Formmap getFormmap() {
		return formmap;
	}

	/**
	 * Setzt die Formmap zu diesem Zustand
	 * @param formmap
	 */
	public void setFormmap(Formmap formmap) {
		this.formmap = formmap;
	}

	/**
	 * Gibt eine String-Repräsentation des Webapp-States zurück.
	 */
	public String toString() {
		String strSuper = super.toString();
		StringTokenizer st = new StringTokenizer(strSuper, "\n");
		// Text "State [ID=xxx]:" übernehmen
		String result = st.nextToken();
		// Template dranhängen
		result += " "+getTemplate()+"\n";
		// Rest dranhängen
		while (st.hasMoreTokens()) {
			result += st.nextToken() + "\n";
		}
		result += "\n";
		// Ergebnis zurückgeben
		return result;
	}
	
	public String toHtml() {
		String strSuper = super.toHtml();
		StringTokenizer st = new StringTokenizer(strSuper, "\n");
		// Text "State [ID=xxx]:" übernehmen
		String result = st.nextToken();
		// Template dranhängen
		result = result.substring(0,result.length()-4)+getTemplate()+"<br>\n";
		// Rest dranhängen
		while (st.hasMoreTokens()) {
			result += st.nextToken() + "\n";
		}
		result += "<br>\n";
		// Ergebnis zurückgeben
		return result;
	}

	/**
	 * @return Gibt die Page-ID zu diesem Zustand zurück.
	 */
	public String getPageId() {
		return strPageId;
	}
	
	public void setPageId(String pageId) {
		this.strPageId = pageId;
	}

}

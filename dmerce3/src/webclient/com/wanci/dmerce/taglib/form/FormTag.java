package com.wanci.dmerce.taglib.form;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import com.wanci.dmerce.ant.NamingConventions;
import com.wanci.dmerce.exceptions.FieldNotFoundException;
import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.dmerce.workflow.webapp.ButtonPressedCondition;
import com.wanci.dmerce.workflow.webapp.WebappState;
import com.wanci.dmerce.workflow.webapp.WebappWorkflowContext;
import com.wanci.dmerce.workflow.webapp.WebappWorkflowEngine;
import com.wanci.java.LangUtil;

/**
 * Execute Tag Class this is part of the dmerce-sql-taglib
 * 
 * Dieser Tag führt eine Abfrage aufgrund vorher definierter Abfrage aus und
 * übernimmt die speicherung des Abfrageergebnisses, das sich von anderen tags
 * aus abfragen lässt.
 * 
 * @author pg
 * @author mm
 * @version $Id: FormTag.java,v 1.69 2004/05/16 19:44:06 rb Exp $
 *  
 */
public class FormTag extends TagSupport {

	public static boolean DEBUG = true;

	private String output = "";
	private String outputFromFields = "";

	WebappWorkflowEngine wwe;
	WebappWorkflowContext context;
	TextTag text;

	/**
     * tag: zeigt an, ob benötigte Felder mit markrequiredstring versehen
     * werden sollen
     */
	private boolean showRequiredMarker = true;

	/**
     * Variable für automatische Formulargenerierung
     */
	private String autoGenerate = null;

	/**
     * String-Variable für benötigte Felder (z.B. Sternchen)
     */
	private String requiredString = "*";

	/**
     * Constructor
     */
	public FormTag() {
	}

	/**
     * setter for id
     * 
     * @param id
     */
	public void setId(String id) {
		this.id = id.trim();
	}

	/**
     * setter for autogenerate tag attribute
     * 
     * @param autoGenerate
     */
	public void setAutogenerate(String autoGenerate) {
		this.autoGenerate = autoGenerate.trim();
	}

	/**
     * setter for required string
     * 
     * @param requiredString
     *            Kennzeichnung eines Formularfeldes, welches in der jsp
     *            angezeigt wird.
     */
	public void setRequiredstring(String requiredString) {
		this.requiredString = requiredString.trim();
	}

	/**
     * setter für Hinweise-Schalter
     * 
     * @param value
     *            true, wenn Hinweise für Required-Fields ausgegeben werden
     *            sollen
     */
	public void setMarkrequired(boolean value) {
		this.showRequiredMarker = value;
	}

	/**
     * Überprüft, ob das Formular automatisch generiert werden soll oder nicht.
     * 
     * @return true, wenn das autoGenerate auf "true" gesetzt wurde
     */
	public boolean isAutomatic() {

		if (autoGenerate == null)
			return false;
		if (autoGenerate.equals("true"))
			return true;
		else
			return false;
		
	}

	/**
     * abfrage ob markup von Pflichtfeldern angeschaltet ist
     * 
     * @return ob markup eingeschaltet ist
     */
	public boolean isMarkrequired() {
		return showRequiredMarker;
	}

	/**
     * Prüfroutine ob Formular automatisch generiert werden soll
     * 
     * @return string autogenerate
     */
	public String getAutogenerate() {
		return autoGenerate;
	}

	/**
     * required string
     * 
     * @return required string
     */
	public String getRequiredString() {
		return requiredString;
	}

	/**
     * Holt sich das FormField mit dem übergebenen Parameter "name" mit der
     * aktuellen FormularId.
     * 
     * @param name
     * @return ein FormField-Objekt
     */
	public FormField getField(String name) throws FieldNotFoundException {
	    
	    WebappState was = (WebappState) context.getCurrentState();
		FormField formfield = context.getFormField(was.getPageId(), getId(), name);
		
		assert formfield != null : "FormTag.getField(String formfield): formfield == null";
		LangUtil.consoleDebug(DEBUG, was.getPageId()
		    + "."
		    + formfield.getFormId() + "."
		    + formfield.getName() + "="
		    + formfield.getValue());
		
		return formfield;
		
	}

	/**
     * Erwartet das gesuchte Formular, damit die " <form action...>"
     * geschrieben werden kann.
     */
	public int doStartTag() throws JspException {

		wwe = (WebappWorkflowEngine) pageContext.getSession().getAttribute("qWorkflowEngine");
		assert wwe != null : "FormTag::doStartTag - form out of workflow";
		if (wwe == null) {
			JspException jspe = new JspException("Ein Formular darf nur innerhalb eines Workflows aufgerufen werden");
			throw jspe;
		}

		context = wwe.getWorkflowContext(wwe.getCurrentWorkflow());
		assert context != null : "FormTag::doStartTag - form out of workflowcontext";
		if (context == null) {
			JspException jspe = new JspException("Ein Formular darf nur innerhalb eines Workflows aufgerufen werden");
			throw jspe;
		}

		// Ausgabe vorbereiten: Formular
		//Encoding thing used for fileuploads...go through all fields and
		// check for Fileupload-Fields
		String enctype = "";
		
		HashMap fieldList = wwe.getFields(id);
		if (fieldList != null) {
			for (Iterator iterator = fieldList.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				FormField f = (FormField) fieldList.get(key);
				if (f != null) {
					if (f.getType().equals("file")) {
						enctype = "enctype=\"multipart/form-data\" ";
					}
				}
			}
		} 
		
		String prefix = "";
		if (wwe.getCurrentWorkflow().isSecured()) {
			prefix = NamingConventions.getRelativeSecuredAreaPathForWorkflow(wwe.getCurrentWorkflow().getWorkflowId())+"/";
		}
		output = "<form " + enctype + "action=\"" + prefix + "workflow.do?qWorkflow=" + wwe.getCurrentWorkflow().getWorkflowId() + "\" method=\"post\">";

		// Ausgabe des Formularheaders
		try {
			pageContext.getOut().print(output);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		output = "";
		
		// wenn Formular automatisch erzeugt werden soll
		if (isAutomatic()) {
			try {
				autoGenerateHtml();
			} catch (WorkflowConfigurationException e) {
				e.printStackTrace();
				throw new JspException(e.getMessage());
			}
		}

		return BodyTagSupport.EVAL_BODY_BUFFERED;
	}

	/**
     * Erstellt automatisch ein Formular. Keine Fehlermeldung wird mitgegeben.
     */
	private void autoGenerateHtml() throws WorkflowConfigurationException {
	    
		//define HtmlFormField Object
		HtmlFormElement htmlField = null;

		//display errormessages
		HtmlErrorMessage errorMsg = new HtmlErrorMessage(null);
		Collection fieldList = null;
		if (context.getFormFields() != null) {
			fieldList = context.getFormFields();
		}
		
		errorMsg.setFieldList(fieldList);
		outputFromFields = errorMsg.toHtml();

		//Tabellenausgabe
		outputFromFields += "<table>\n";
		Collection formfields = wwe.getFields(getId()).keySet();

		//Schleife über alle keys der Formfelder um letztlich Html-Code zu
		// erzeugen
		Iterator iterator = formfields.iterator();
		while (iterator.hasNext()) {
		    
			// FormField setzen
			// im iterator steht der key des Felds, der jetzt aus dem context
			// geholt wird
			FormField f = null;
			try {
				f = context.getFormField(((WebappState) context.getCurrentState()).getPageId(), getId(), (String) iterator.next());
			} catch (FieldNotFoundException e) {
				// Im Autogenerate-Modus kann kein Fehler auftreten.
			}
			
			Iterator iter = f.getErrorMsgs().iterator();
			while (iter.hasNext()) {
				String element = (String) iter.next();
				LangUtil.consoleDebug(DEBUG, "Feldfehler: " + f.getName() + " - " + element);
			}

			//define other attributes needed for field
			HashMap attribs = new HashMap();

			//string - text or textarea
			//TODO determine textarea, if maxlength > 255 or length of text >
			// 255
			if (f.getType().equals("string") || f.getType().equals("number")
			        || f.getType().equals("date")) {
				
			    //TODO check for text or textarea
				String value = f.getValue();

				htmlField = new HtmlTextElement(f);
				htmlField.setAttributes(attribs);
				htmlField.setValue(value);
				
			}
			//boolean
			else if (f.getType().equals("boolean")) {
				
			    String display = f.getDisplayType();
				String value = f.getValue();

				if (display == null || display.equals("checkbox")) {
					//default is checkbox
					htmlField = new HtmlCheckboxElement(f);
					htmlField.setAttributes(attribs);
					htmlField.setValue(value);
				} else if (display.equals("radio")) {
					//explicit radio
					htmlField = new HtmlRadioElement(f);
					htmlField.setAttributes(attribs);
					htmlField.setValue(value);
				}
				
			}
			//list (combobox, listbox, checkbox list or radiolist)
			else if (f.getType().equals("list")) {
				
			    String[] values = f.getValues();

				htmlField = new HtmlListElement(f);
				htmlField.setAttributes(attribs);
				htmlField.setValues(values);
				
			}
			//fileupload
			else if (f.getType().equals("file")) {
			    
				String[] values = f.getValues();
				
				htmlField = new HtmlFileElement(f);
				htmlField.setAttributes(attribs);
				htmlField.setValues(values);
				
			}
			
			//prepare output
			if (htmlField != null) {
				outputFromFields += "<tr class=\"forms\"><td class=\"forms\">"
				    + f.getDescription()
				    + "</td><td class=\"forms\">"
				    + htmlField.toHtml()
				    + "</td></tr>\n";
			}
			
		}

		//jetzt Formularübergänge rendern
		List transitions = context.getCurrentState().getTransitions();

		outputFromFields += "<tr class=\"forms\"><td class=\"forms\" colspan=\"2\">";

		iterator = transitions.iterator();
		while (iterator.hasNext()) {
			Transition transition = (Transition) iterator.next();
			if (ButtonPressedCondition.class.isAssignableFrom(transition.getCondition().getClass())) {

				outputFromFields += "<input type=\"submit\" name=\"submit\" value=\"" + ((ButtonPressedCondition) transition.getCondition()).getBeschriftung() + "\">";
			}
			
		}
		
		outputFromFields += "</td></tr>\n</table>";
		
	}

	/**
     * Wenn in der jsp Seite die taglib geschlossen wird, wird auch das "
     * </form>" tag geschlossen.
     */
	public int doEndTag() {

		//formular schließen
		output += outputFromFields + "</form>";

		try {
			pageContext.getOut().print(output);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Variablen wieder leeren, sonst werden diese wieder mit angezeigt
		output = "";
		outputFromFields = "";

		return EVAL_PAGE;
		
	}
}
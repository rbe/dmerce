/*
 * Datei angelegt am 29.09.2003
 */
package com.wanci.dmerce.workflow;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.exceptions.WorkflowRuntimeException;
import com.wanci.dmerce.workflow.webapp.WebappWorkflowEngine;
import com.wanci.java.LangUtil;

/**
 * Repr�sentiert eine Workflow-Definition, die aus mehreren Zust�nden
 * und den Zustands�berg�ngen besteht.
 * 
 * @author Masanori Fujita
 */
public class Workflow {
	
	public static boolean DEBUG = true;
	
	private LinkedHashMap mapStates;
	private WebappWorkflowEngine workflowEngine;
	private String workflowId;
	private State initialState;

	private boolean secured;
	
	/**
	 * @return
	 */
	public String getWorkflowId() {
		return workflowId;
	}

	/**
	 * @param workflowId
	 */
	void setWorkflowId(String workflowId) {
		assert (workflowId != null) && (!workflowId.equals("")) : "Workflow-ID darf nicht auf null oder \"\" gesetzt werden.";
		this.workflowId = workflowId;
	}

	/**
	 * Erzeugt einen leeren Workflow.
	 * Die Struktur des Workflows muss durch die Erzugung von
	 * zugeh�rigen States aufgebaut werden.
	 */
	public Workflow(String workflowId) {
		mapStates = new LinkedHashMap();
		setWorkflowId(workflowId);
	}
	
	/**
	 * F�gt dem Workflow einen Zustand hinzu.
	 * Diese Methode sollte nicht manuell aufgerufen werden, da sie
	 * nur von der Klasse State zur Selbstverwaltung verwendet wird.
	 * @param state
	 */
	void addState(State state) {
		mapStates.put(state.getStateId(), state);
	}
	
	/**
	 * Liefert den Zustand mit der angegebenen ID zur�ck.
	 * @param stateId
	 * @return Zustand mit der ID stateID
	 */
	public State getState(String stateId) {
		State state = (State) mapStates.get(stateId);
		assert state != null : "Es exisiert kein Zustand mit der ID "+stateId; 
		return state;
	}
	
	/**
	 * F�hrt unter Verwendung des �bergebenen Kontextes die Verarbeitung
	 * einer neuen Anfrage durch. Am Ende der Verarbeitung steht der
	 * neue Zustand des Workflows im Kontext zur Verf�gung.
	 * @param context
	 * @throws WorkflowRuntimeException Wird geworfen, falls bei der Verarbeitung
	 * 		ein gravierender Fehler auftritt.
	 * @throws WorkflowConfigurationException Wirf geworfen, falls dieser Workflow
	 * 		fehlerhaft konfiguriert ist.
	 */
	public void process(WorkflowContext context) throws WorkflowRuntimeException, WorkflowConfigurationException {
		LangUtil.consoleDebug(DEBUG, "Workflow [ID="+getWorkflowId()+"].process");
		context.getCurrentState().process(context);
	}
	
	/**
	 * Gibt die Anzahl der enthaltenen Zust�nde zur�ck.
	 * @return Anzahl der Zust�nde
	 */
	public int getStateCount() {
		return mapStates.size();
	}
	
	/**
	 * Setzt den Anfangszustand auf den �bergebenen Wert.
	 * Diese Methode pr�ft, ob der �bergebene Zustand im Workflow
	 * existiert.
	 * @param state
	 */
	public void setInitialState(State state) {
		assert state != null : "Initial State darf nicht null sein.";
		getState(state.getStateId());
		this.initialState = state;
	}
	
	/**
	 * Gibt den Anfangszustand zur�ck.
	 * @return
	 */
	public State getInitialState() {
		return this.initialState;
	}
	
	/**
	 * Obligatorische Textausgabe
	 */
	public String toString() {
		String result;
		result = "Workflow [ID="+getWorkflowId()+", Initial State="+getInitialState().getStateId()+"]:\n";
		// Alle Zust�nde durchlaufen und einger�ckt ausgeben
		Iterator it = mapStates.keySet().iterator();
		while (it.hasNext()) {
			String subResult = mapStates.get(it.next()).toString();
			StringTokenizer stringTokenizer = new StringTokenizer(subResult,"\n");
			while (stringTokenizer.hasMoreTokens()) {
				result += "    "+stringTokenizer.nextToken()+"\n";
			}
		}
		return result;
	}
	
	/**
	 * Obligatorische Textausgabe
	 */
	public String toHtml() {
		String result;
		result = "<font color=\"#FF8000\"><b>Workflow</b> [ID=<u>"+getWorkflowId()+"</u>, Initial State="+getInitialState().getStateId()+"]:</font><br>\n";
		// Alle Zust�nde durchlaufen und einger�ckt ausgeben
		Iterator it = mapStates.keySet().iterator();
		while (it.hasNext()) {
			String subResult = ((State) mapStates.get(it.next())).toHtml();
			StringTokenizer stringTokenizer = new StringTokenizer(subResult,"\n");
			while (stringTokenizer.hasMoreTokens()) {
				result += "&nbsp;&nbsp;&nbsp;&nbsp;"+stringTokenizer.nextToken()+"\n";
			}
		}
		return result;
	}
	
	/**
	 * @return LinkedHashMap der States
	 */
	public LinkedHashMap getMapStates() {
		return mapStates;
	}
	
	/**
	 * Legt fest, ob dieser Workflow ein gesch�tzer Workflow ist.
	 */
	public void setSecured(boolean secured) {
		LangUtil.consoleDebug(DEBUG, (secured ? "Workflow "+this.getWorkflowId()+" wird gesch�tzt.": "Workflow "+this.getWorkflowId()+" wird nicht gesch�tzt."));
		this.secured = secured;
	}

	/**
	 * Pr�ft, ob es sich bei diesem Workflow um einen gesch�tzten Workflow handelt.
	 * @return
	 */
	public boolean isSecured() {
		return this.secured;
	}

}

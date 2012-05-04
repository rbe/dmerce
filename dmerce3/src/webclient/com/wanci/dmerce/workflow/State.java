/*
 * Datei angelegt am 29.09.2003
 */
package com.wanci.dmerce.workflow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.exceptions.WorkflowRuntimeException;
import com.wanci.java.LangUtil;

/**
 * Repr�sentiert einen Zustand eines Workflows.
 * Ein Zustand kann seinerseits Transitionen enthalten, die Zustands�berg�nge
 * zu anderen Zust�nden erm�glichen.
 * Enth�lt ein Zustand keine Transition zu anderen Zust�nden, so handelt es sich
 * bei dem Zustand um einen Endpoint, d.h. hier endet die Verarbeitung des Workflows.
 *   
 * @author Masanori Fujita
 */
public class State {
	
	public static boolean DEBUG = Workflow.DEBUG;
	
	private Workflow workflow;
	private ArrayList listTransitions;
	
	private String strStateId;
	/**
	 * Initialisiert einen Zustand mit dem umgebenden Workflow.
	 * @param workflow Workflow, zu dem dieser Zustand geh�rt.
	 */
	public State(Workflow workflow, String stateId) {
		assert workflow != null : "Der �bergebne Workflow darf nicht null sein.";
		assert stateId != null && !stateId.equals("") : "Die �bergebene ID darf nicht null oder leer sein.";
		listTransitions = new ArrayList();
		this.workflow = workflow;
		this.strStateId = stateId;
		workflow.addState(this);
	}
	
	/**
	 * @return Workflow, zu dem dieser Zustand geh�rt.
	 */
	public Workflow getWorkflow() {
		return workflow;
	}
	
	/**
	 * Liefert eine Kopie der Transitionsliste zur�ck. 
	 * @return Liste der von diesem Zustand ausgehenden Transitionen.
	 */
	public List getTransitions() {
		return listTransitions;
	}
	
	/**
	 * F�gt dem Zustand einen Zustands�bergang hinzu.
	 * Diese Methode sollte nicht manuell aufgerufen werden, da sie
	 * von der Klase Transistion zur Selbstverwaltung verwendet wird.
	 * @param transition
	 */
	void addTransition(Transition transition) {
		this.listTransitions.add(transition);
	}

	/**
	 * @return Eindeutige ID des Zustandes.
	 */
	public String getStateId() {
		return strStateId;
	}
	
	/**
	 * Pr�ft, ob es sich bei diesem Zustand um einen Endpunkt des
	 * Workflows handelt. Dies ist der Fall, wenn keine Transition
	 * zu diesem Zustand existiert.
	 * @return true, falls es sich um einen Endpunkt handelt.
	 */
	public boolean isEndpoint() {
		boolean isEndpoint = (listTransitions.size() == 0);
		if (isEndpoint) 
			LangUtil.consoleDebug(DEBUG, "State "+getStateId()+" is endpoint.");
		else
			LangUtil.consoleDebug(DEBUG, "State "+getStateId()+" is not endpoint.");
		return isEndpoint;
	}
	
	/**
	 * Mit dem Aufruf dieser Methode wird ein Zustands�bergang
	 * angefordert. Es werden alle Transitionen nacheinander durchlaufen,
	 * bis eine Transition erfolgreich betreten werden kann.
	 * 
	 * Bei Erfolgt setzt diese Methode den n�chsten anzusteuernden Zustand im Kontext.
	 * 
	 * @throws WorkflowRuntimeException Falls unerwartet kein Zielzustand
	 * 		ermittelt werden kann, wird diese Exception geworfen. Dies ist
	 * 		z.B. dann der Fall, wenn keine einzige Transition greift.
	 */
	public void process(WorkflowContext context) throws WorkflowRuntimeException, WorkflowConfigurationException {
		LangUtil.consoleDebug(DEBUG, "State [ID="+getStateId()+"].process");
		// Transitionen durchlaufen
		Iterator it = listTransitions.iterator();
		while (it.hasNext()) {
			Transition transition = (Transition) it.next();
			LangUtil.consoleDebug(DEBUG, "Pr�fe Transition "+transition.getId());
			int result = transition.process(context);
			if (result == Transition.FIRE) {
				LangUtil.consoleDebug(DEBUG, "-> betrete Transition");
				context.setCurrentState(transition.getDestinationState());
				return;
			}
			else {
				LangUtil.consoleDebug(DEBUG, "-> pr�fe n�chste Transition");
			}
		}
		throw new WorkflowRuntimeException("Es wurde keine Transition zum Zustand "+getStateId()+" gefunden, die betreten werden kann.");
	}
	
	/**
	 * Obligatorische Textausgabe
	 */
	public String toString() {
		String result;
		result = "State [ID="+getStateId()+"]:\n";
		// Alle Transitionen durchlaufen und einger�ckt ausgeben
		Iterator it = getTransitions().iterator();
		while (it.hasNext()) {
			String subResult = it.next().toString();
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
		result = "<font color=\"#C00000\"><b>State</b> [ID=<u>"+getStateId()+"</u>]:</font><br>\n";
		// Alle Transitionen durchlaufen und einger�ckt ausgeben
		Iterator it = getTransitions().iterator();
		while (it.hasNext()) {
			String subResult = ((Transition) it.next()).toHtml();
			StringTokenizer stringTokenizer = new StringTokenizer(subResult,"\n");
			while (stringTokenizer.hasMoreTokens()) {
				result += "&nbsp;&nbsp;&nbsp;&nbsp;"+stringTokenizer.nextToken()+"\n";
			}
		}
		return result;		
	}
	

}

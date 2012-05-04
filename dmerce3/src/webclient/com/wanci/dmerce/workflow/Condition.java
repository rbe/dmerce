/*
 * Datei angelegt am 29.09.2003
 */
package com.wanci.dmerce.workflow;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;

/**
 * Eine allgemeingültige Beschreibung einer benutzerdefinierten Bedingung.
 * Diese Klasse muss abgeleitet werden, um sie mit einer sinnvollen Implementierung
 * einer Bedingung zu versehen.
 * 
 * @author Masanori Fujita
 */
public abstract class Condition {
	
	private Transition transition;
	private String description;

	/**
	 * Gibt eine aussagekräftige Beschreibung der Bedingung zurück.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setzt eine aussagekräftige Beschreibung für diese Bedingung
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Initialisiert die Bedingung mit der umgebenden Transition
	 * @param parent
	 */
	public Condition(Transition parent) {
		this();
		transition = parent;
		transition.setCondition(this);
	}
	
	/**
	 * Initialisiert eine Condition.
	 * Sie muss noch mit einer Transition verknüpft werden. 
	 */
	public Condition() {
		setDescription("keine Beschreibung vorhanden");
	}
	
	/**
	 * Gibt die umgebende Transition zurück, um darauf evtl. weitere
	 * Aktionen auszuführen.
	 * @return Umgebende Transition
	 */
	public Transition getParentTransition() {
		return transition;
	}
	
	/**
	 * @return Quellzustand, von dem der Zustandsübergang ausgeht.
	 */
	public State getParentState() {
		return getParentTransition().getParentState();
	}
	
	/**
	 * @return Zielzustand, zu dem der Zustandsübergang führen soll.
	 */
	public State getDestinationState() throws WorkflowConfigurationException {
		return getParentTransition().getDestinationState();
	}
	
	/**
	 * Ermittlt über den Quellzustand den zugehörigen Workflow.
	 * @return Workflow, zu dem diese Aktion gehört.
	 */
	public Workflow getWorkflow() {
		return getParentState().getWorkflow();
	}
	
	/**
	 * Prüft, ob eine bestimmte Bedingung erfüllt ist.
	 * @param context Kontext, aus dem die Informationen für die Bedingungsprüfung
	 * 		bezogen werden sollen.
	 * @return true, falls die Bedingung erfüllt ist, false sonst.
	 */
	public abstract boolean isSatisfied(WorkflowContext context) throws WorkflowConfigurationException;
	
	/**
	 * Obligatorische Textausgabe
	 */
	public String toString() {
		return "Condition (Typ: "+this.getClass().getName()+", "+getDescription()+")";
	}
	
	public String toHtml() {
		return "<font color=\"#0000D8\"><b>Condition</b></font> (Typ: <u>"+this.getClass().getName()+"</u>, "+getDescription()+")";
	}

}

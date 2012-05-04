/*
 * Datei angelegt am 29.09.2003
 */
package com.wanci.dmerce.workflow;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;

/**
 * Eine allgemeing�ltige Beschreibung einer benutzerdefinierten Bedingung.
 * Diese Klasse muss abgeleitet werden, um sie mit einer sinnvollen Implementierung
 * einer Bedingung zu versehen.
 * 
 * @author Masanori Fujita
 */
public abstract class Condition {
	
	private Transition transition;
	private String description;

	/**
	 * Gibt eine aussagekr�ftige Beschreibung der Bedingung zur�ck.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setzt eine aussagekr�ftige Beschreibung f�r diese Bedingung
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
	 * Sie muss noch mit einer Transition verkn�pft werden. 
	 */
	public Condition() {
		setDescription("keine Beschreibung vorhanden");
	}
	
	/**
	 * Gibt die umgebende Transition zur�ck, um darauf evtl. weitere
	 * Aktionen auszuf�hren.
	 * @return Umgebende Transition
	 */
	public Transition getParentTransition() {
		return transition;
	}
	
	/**
	 * @return Quellzustand, von dem der Zustands�bergang ausgeht.
	 */
	public State getParentState() {
		return getParentTransition().getParentState();
	}
	
	/**
	 * @return Zielzustand, zu dem der Zustands�bergang f�hren soll.
	 */
	public State getDestinationState() throws WorkflowConfigurationException {
		return getParentTransition().getDestinationState();
	}
	
	/**
	 * Ermittlt �ber den Quellzustand den zugeh�rigen Workflow.
	 * @return Workflow, zu dem diese Aktion geh�rt.
	 */
	public Workflow getWorkflow() {
		return getParentState().getWorkflow();
	}
	
	/**
	 * Pr�ft, ob eine bestimmte Bedingung erf�llt ist.
	 * @param context Kontext, aus dem die Informationen f�r die Bedingungspr�fung
	 * 		bezogen werden sollen.
	 * @return true, falls die Bedingung erf�llt ist, false sonst.
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

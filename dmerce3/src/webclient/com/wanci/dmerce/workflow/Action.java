/*
 * Datei angelegt am 29.09.2003
 */
package com.wanci.dmerce.workflow;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;
import com.wanci.dmerce.exceptions.WorkflowRuntimeException;

/**
 * Repräsentiert eine allgemeingültige benutzerdefinierte Aktion.
 * Diese Klasse muss abgeleitet werden, damit sie eine sinnvolle Aktion
 * ausführen kann.
 * 
 * @author Masanori Fujita
 */
public abstract class Action {
	
	
	private Transition parent;
	private String description;
	
	protected static boolean DEBUG = Workflow.DEBUG;

	/**
	 * Gibt eine aussagekräftige Beschreibung der Aktion zurück.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setzt eine aussagekräftige Beschreibung für diese Aktion
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * Initialisiert eine Aktion mit der umgebenen Transition.
	 * @param parent Transition, die diese Aktion ausfüren soll.
	 */
	public Action(Transition parent) throws WorkflowConfigurationException {
		if (parent == null)
			throw new WorkflowConfigurationException("Die übergebene Eltern-Transition darf nicht null sein.");
		this.parent = parent;
		parent.setAction(this);
		setDescription("keine Beschreibung vorhanden");
	}
		
	/**
	 * @return Transition, zu der diese Aktion gehört.
	 */
	public Transition getParentTransition() {
		return parent;
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
	 * Führt die benutzerdefinierte Aktion aus.
	 * Diese Methode muss in abgeleiteten Klassen implementiert werden, um
	 * benutzerdefinierte Aktionen auszuführen. Dabei kann über die Methode getParent()
	 * auf die umgebene Transition zugegriffen werden.
	 * @param context Kontext, auf dem die Aktion ausgeführt werden soll.
	 * 		Hier können bei Bedarf Werte in den Kontext geschrieben oder
	 * 		aus dem Kontext bezogen werden.
	 */
	public abstract void execute(WorkflowContext context) throws WorkflowRuntimeException, WorkflowConfigurationException;

	/**
	 * Obligatorische Textausgabe
	 */
	public String toString() {
		return "Action (Typ: "+this.getClass().getName()+", "+getDescription()+")";
	}
	
	public String toHtml() {
		return "<font color=\"#008000\"><b>Action</b></font> (Typ: <u>"+this.getClass().getName()+"</u>, "+getDescription()+")";
	}

}

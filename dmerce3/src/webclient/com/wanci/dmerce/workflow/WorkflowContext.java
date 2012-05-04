/*
 * Datei angelegt am 30.09.2003
 */
package com.wanci.dmerce.workflow;

import java.util.HashMap;
import java.util.Set;


/**
 * Diese Klasse stellt ein Zustandsobjekt eines Workflows zur Verf�gung.
 * In diesem Objekt sollen jegliche zustandsbehaftete Werte gespeichert
 * werden.
 * 
 * @author Masanori Fujita
 */
public class WorkflowContext {
	
	private State currentState;
	private HashMap contextData;
	
	/**
	 * Standard-Konstruktor, der einen leeren Kontext zur Verf�gung stellt.
	 */
	public WorkflowContext() {
		contextData = new HashMap();
	}
	
	/**
	 * Liefert die aktuelle Position im Workflow zur�ck.
	 * @return Aktuelle Position im Workflow.
	 */
	public State getCurrentState() {
		return currentState;
	}

	/**
	 * Setzt den aktuellen Zustand des Workflows
	 * @param currentState
	 */
	public void setCurrentState(State currentState) {
		assert currentState != null : "Der �bergebene Zustand darf nicht null sein.";
		this.currentState = currentState;
	}
	
	/**
	 * Speichert beliebige Werte unter einem Schl�ssel.
	 * @param key Schl�ssel
	 * @param value Wert
	 */
	public void put(String key, Object value) {
		assert key != null || key.equals("") : "Der Schl�ssel darf nicht null oder leer sein.";
		assert value != null : "Der zu speichernde Wert darf nicht null sein.";
		contextData.put(key, value);
	}
	
	/**
	 * Gibt den unter dem Schl�ssel abgelegten Wert zur�ck.
	 * @param key Schl�ssel
	 * @return Gespeicherter Wert, null falls Wert nicht vorhanden.
	 */
	public Object get(String key) {
		assert key != null : "Der Key darf nicht null sein.";
		return contextData.get(key);
	}
	
	/**
	 * Gibt die Menge von Schl�sseln zu den gespeicherten Werten zur�ck.
	 * @return Menge der Schl�ssel
	 */
	public Set getKeys() {
		return contextData.keySet();
	}
	
}

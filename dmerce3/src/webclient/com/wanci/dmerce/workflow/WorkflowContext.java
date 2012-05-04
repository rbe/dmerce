/*
 * Datei angelegt am 30.09.2003
 */
package com.wanci.dmerce.workflow;

import java.util.HashMap;
import java.util.Set;


/**
 * Diese Klasse stellt ein Zustandsobjekt eines Workflows zur Verfügung.
 * In diesem Objekt sollen jegliche zustandsbehaftete Werte gespeichert
 * werden.
 * 
 * @author Masanori Fujita
 */
public class WorkflowContext {
	
	private State currentState;
	private HashMap contextData;
	
	/**
	 * Standard-Konstruktor, der einen leeren Kontext zur Verfügung stellt.
	 */
	public WorkflowContext() {
		contextData = new HashMap();
	}
	
	/**
	 * Liefert die aktuelle Position im Workflow zurück.
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
		assert currentState != null : "Der übergebene Zustand darf nicht null sein.";
		this.currentState = currentState;
	}
	
	/**
	 * Speichert beliebige Werte unter einem Schlüssel.
	 * @param key Schlüssel
	 * @param value Wert
	 */
	public void put(String key, Object value) {
		assert key != null || key.equals("") : "Der Schlüssel darf nicht null oder leer sein.";
		assert value != null : "Der zu speichernde Wert darf nicht null sein.";
		contextData.put(key, value);
	}
	
	/**
	 * Gibt den unter dem Schlüssel abgelegten Wert zurück.
	 * @param key Schlüssel
	 * @return Gespeicherter Wert, null falls Wert nicht vorhanden.
	 */
	public Object get(String key) {
		assert key != null : "Der Key darf nicht null sein.";
		return contextData.get(key);
	}
	
	/**
	 * Gibt die Menge von Schlüsseln zu den gespeicherten Werten zurück.
	 * @return Menge der Schlüssel
	 */
	public Set getKeys() {
		return contextData.keySet();
	}
	
}

/*
 * Datei angelegt am 14.10.2003
 */
package com.wanci.dmerce.workflow;

import java.util.Random;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;

/**
 * Fabrik-Klasse, die komfortabel vorkonfigurierte Transitionen
 * erzeugen kann.
 * 
 * @author Masanori Fujita
 */
public class TransitionFactory {
	
	private static Random rnd;
	
	static {
		rnd = new Random();
	}
	
	/**
	 * Erzeugt eine Transition, die keine Aktion durchführt. Sie ist sinnvoll,
	 * um in Abhängigkeit von Bedingungen im Workflow zu verzweigen.
	 * Die Condition muss daher noch spezifiziert werden.
	 * @param parent umliegender Zustand
	 * @param destination Zielzustand
	 */
	public static Transition createNoActionTransition(State parent, State destination) throws WorkflowConfigurationException {
		String generatedID = "Transition_"+rnd.nextLong(); 
		Transition transition = new Transition(parent, destination, generatedID);
		new DoNothingAction(transition);
		return transition;
	}
	
	/**
	 * Erzeugt eine Transition, die keine Bedingung abprüft. Sie ist sinnvoll,
	 * um einfache Aktionen auszuführen, oder Else-Zweige im Workflow
	 * zu realisieren.
	 * Die Action muss jedoch noch spezifiziert werden.
	 * @param parent umliegender Zustand
	 */
	public static Transition createNoConditionTransition(State parent) throws WorkflowConfigurationException {
		String generatedID = "Transition_"+rnd.nextLong(); 
		Transition transition = new Transition(parent, generatedID);
		new AlwaysTrueCondition(transition);
		return transition;
	}
	
	/**
	 * Erzeugt eine Transition, die keine Bedingung abprüft. Sie ist sinnvoll,
	 * um einfache Aktionen auszuführen, oder Else-Zweige im Workflow
	 * zu realisieren.
	 * Die Action muss jedoch noch spezifiziert werden.
	 * @param parent umliegender Zustand
	 * @param destination Zielzustand
	 */
	public static Transition createNoConditionTransition(State parent, State destination) throws WorkflowConfigurationException {
		String generatedID = "Transition_"+rnd.nextLong(); 
		Transition transition = new Transition(parent, destination, generatedID);
		new AlwaysTrueCondition(transition);
		return transition;
	}

	/**
	 * Erzeugt eine einfache Transition, die keine Bedingung abprüft und
	 * keine Aktion ausführt.
	 * @param parent umliegender Zustand
	 * @param destination Zielzustand
	 */
	public static Transition createStraightTransition(State parent, State destination) throws WorkflowConfigurationException {
		String generatedID = "Transition_"+rnd.nextLong(); 
		Transition transition = new Transition(parent, destination, generatedID);
		new AlwaysTrueCondition(transition);
		new DoNothingAction(transition);
		return transition;
	}
}

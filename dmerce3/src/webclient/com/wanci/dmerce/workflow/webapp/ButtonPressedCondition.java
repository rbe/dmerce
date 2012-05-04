/*
 * Datei angelegt am 21.10.2003
 */
package com.wanci.dmerce.workflow.webapp;

import com.wanci.dmerce.workflow.Condition;
import com.wanci.dmerce.workflow.Transition;
import com.wanci.dmerce.workflow.WorkflowContext;

/**
 * @author Masanori Fujita
 */
public class ButtonPressedCondition extends Condition {	
	
	private String strBeschriftung;

	public ButtonPressedCondition(String beschriftung) {
		super();
		setDescription("Reagiert auf einen Knopfdruck mit der Beschriftung "+beschriftung+".");
		setBeschriftung(beschriftung);
	}

	/**
	 * @param parent
	 */
	public ButtonPressedCondition(Transition parent, String beschriftung) {
		super(parent);
		setDescription("Reagiert auf einen Knopfdruck mit der Beschriftung "+beschriftung+".");
		setBeschriftung(beschriftung);
	}

	/**
	 * @return
	 */
	public String getBeschriftung() {
		return strBeschriftung;
	}

	/**
	 * @param strBeschriftung
	 */
	public void setBeschriftung(String strBeschriftung) {
		this.strBeschriftung = strBeschriftung;
	}

	/**
	 * Gibt true zurück, wenn der gedrückte Knopf mit der Beschriftung
	 * dieser Conditio übereinstimmt.
	 */
	public boolean isSatisfied(WorkflowContext context) {
		String submitValue = (String) context.get("submit");
		assert submitValue != null && !submitValue.equals("") : "Es wurde kein Wert zum Schlüssel \"submit\" gefunden.";
		return submitValue.equals(getBeschriftung());
	}

}

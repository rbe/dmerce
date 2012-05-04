/*
 * Datei angelegt am 20.10.2003
 */
package com.wanci.dmerce.workflow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.wanci.dmerce.exceptions.WorkflowConfigurationException;

/**
 * Eine Condition, die erf�llt ist, wenn alle enthaltenen Conditions
 * erf�llt sind.
 * 
 * @author Masanori Fujita
 */
public class AndCondition extends Condition {
	
	private List listCondition;
	
	public AndCondition() {
		listCondition = new ArrayList();
		setDescription("And-Bedingung, ist erf�llt, wenn alle enthaltenen Bedingungen erf�llt sind.");
	}
	
	public AndCondition(Transition transition) {
		super(transition);
		setDescription("And-Bedingung, ist erf�llt, wenn alle enthaltenen Bedingungen erf�llt sind.");
		listCondition = new ArrayList();
	}
	
	public void addCondition(Condition condition) {
		assert condition != null : "�bergebene Condition darf nicht null sein.";
		listCondition.add(condition);
	}
	
	public void removeCondition(Condition condition) {
		assert condition != null : "Es kann keine Condition null gel�scht werden.";
		int position = listCondition.indexOf(condition);
		if (position != -1) {
			listCondition.remove(position);
		}
	}

	public boolean isSatisfied(WorkflowContext context) throws WorkflowConfigurationException {
		boolean result = true;
		Iterator it = listCondition.iterator();
		while (it.hasNext()) {
			Condition condition = (Condition) it.next();
			result &= condition.isSatisfied(context);
		}
		return result;
	}
	
	//TODO: toString() sollte noch implementiert werden.

}

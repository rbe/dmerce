/*
 * Created on Jan 14, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.wanci.dmerce.gui.listener;

import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JPanel;

/**
 * Listener, der die ComboBox nach den Conditions aktualisiert und dann das
 * zugehörige Panel anzeigt bzw. ausblendet.
 * 
 * @author mm
 * @version $Id: ConditionListener.java,v 1.1 2004/01/19 16:26:04 mm Exp $
 */
public class ConditionListener implements ItemListener {
	
	private JPanel conditionCards;

	public ConditionListener() {
	}

	/**
	 * Wenn sich ein Item ändert, wird das neue Item entweder
	 * angezeigt oder ausgeblendet.
	 */
	public void itemStateChanged(ItemEvent e) {

		CardLayout clCl = (CardLayout) (conditionCards.getLayout());
		clCl.show(conditionCards, (String) e.getItem());

		if (e.getItem().equals("keine Condition")) {
			conditionCards.setVisible(false);
		} else {
			conditionCards.setVisible(true);
		}
	}

	/**
	 * @param conditionCards
	 *            The JPanel to set.
	 */
	public void setComponent(JPanel conditionCards) {
		this.conditionCards = conditionCards;
	}
}
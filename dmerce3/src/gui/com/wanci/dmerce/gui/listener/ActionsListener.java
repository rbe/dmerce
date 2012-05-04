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
 * Listener, der die ComboBox nach den Actions aktualisiert und dann das
 * zugehörige Panel anzeigt bzw. ausblendet.
 * 
 * @author mm
 * @version $Id: ActionsListener.java,v 1.1 2004/01/19 16:26:04 mm Exp $
 */
public class ActionsListener implements ItemListener {

	private JPanel actionCards;
	
	public ActionsListener() {
	}

	/**
	 * Wenn sich ein Item ändert, wird das neue Item entweder
	 * angezeigt oder ausgeblendet.
	 */
	public void itemStateChanged(ItemEvent e) {

		CardLayout alCl = (CardLayout) (actionCards.getLayout());
		alCl.show(actionCards, (String) e.getItem());

		if (e.getItem().equals("keine Action")) {
			actionCards.setVisible(false);
		} else {
			actionCards.setVisible(true);
		}
	}
	
	/**
	 * @param actionCards
	 *            The JPanel to set.
	 */
	public void setComponent(JPanel actionCards) {
		this.actionCards = actionCards;
	}
}
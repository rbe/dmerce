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
 * Listener für die forms.xml.
 * Ändert die Werte in der JComboBox für die Constraints.
 * 
 * @author mm
 * @version $Id: ConstraintsListener.java,v 1.1 2004/01/19 16:26:04 mm Exp $
 */
public class ConstraintsListener implements ItemListener {
	
	private JPanel constraintCards;
	
	public ConstraintsListener() {
	}

	/**
	 * Wenn sich ein Item ändert, wird das neue Item entweder
	 * angezeigt oder ausgeblendet.
	 */
	public void itemStateChanged(ItemEvent e) {
		
		CardLayout clCl = (CardLayout) (constraintCards.getLayout());
		clCl.show(constraintCards, (String) e.getItem());
		
		if (e.getItem().equals("keine constraints"))
			constraintCards.setVisible(false);
		else
			constraintCards.setVisible(true);
	}

	/**
	 * @param constraintCards
	 *             The Jpanel to set.
	 */
	public void setComponent(JPanel constraintCards) {
		this.constraintCards = constraintCards;
	}

}

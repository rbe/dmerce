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
 * ändert die Werte in der JComboBox für die Options.
 * 
 * @author mm
 * @version $Id: OptionsListener.java,v 1.1 2004/01/19 16:26:04 mm Exp $
 */
public class OptionsListener implements ItemListener {
	
	private JPanel optionCards;

	public OptionsListener() {
	}

	/**
	 * Wenn sich ein Item ändert, wird das neue Item entweder
	 * angezeigt oder ausgeblendet.
	 */
	public void itemStateChanged(ItemEvent e) {
		CardLayout olCl = (CardLayout) (optionCards.getLayout());
		olCl.show(optionCards, (String) e.getItem());
		if (e.getItem().equals("keine options"))
			optionCards.setVisible(false);
		else
			optionCards.setVisible(true);
	}

	/**
	 * @param optionCards
	 *            The JPanel to set.
	 */
	public void setComponent(JPanel optionCards) {
		this.optionCards = optionCards;
	}
}
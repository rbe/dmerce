/*
 * Created on Jan 14, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package com.wanci.dmerce.gui.listener;

import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JPanel;

import com.wanci.dmerce.workflow.xmlbridge.ACTION;

/**
 * Listener, der die ComboBox nach dem Action Type aktualisiert und dann das
 * zugehörige Panel anzeigt.
 * 
 * @author mm
 * @version $Id: TypeListener.java,v 1.1 2004/01/19 16:26:04 mm Exp $
 */
public class TypeListener implements ItemListener {

	private JPanel typeCards;
	private ACTION action;
	
	public TypeListener() {
	}

	/**
	 * Wenn sich ein Item ändert, wird das neue Item entweder
	 * angezeigt oder ausgeblendet.
	 */
	public void itemStateChanged(ItemEvent e) {

		CardLayout tlCl = (CardLayout) (typeCards.getLayout());
		tlCl.show(typeCards, (String) e.getItem());

		if (e.getItem().equals("kein type")) {
			typeCards.setVisible(false);
		} else {
			typeCards.setVisible(true);
		}
	}
	
	/**
	 * @param typeCards
	 *            The JPanel to set.
	 */
	public void setComponent(JPanel typeCards) {
		this.typeCards = typeCards;
	}
	
//	/**
//	 * @param action
//	 *            The ACTION object to set.
//	 */
//	public void setAction(ACTION action) {
//		this.action = action;
//	}
//
//	/**
//	 * @return Returns the ACTION object.
//	 */
//	public ACTION getAction() {
//		return action;
//	}
}
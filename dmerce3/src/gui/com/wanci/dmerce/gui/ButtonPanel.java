/*
 * Created on Jan 7, 2004
 */
package com.wanci.dmerce.gui;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * erstellt eine neue Knopf-Schaltfläche mit dem übergebenen Wert als
 * Schaltflächenbezeichnung.
 * 
 * @author mm
 * @version $Id: ButtonPanel.java,v 1.2 2004/01/19 16:26:03 mm Exp $
 */
public class ButtonPanel extends JPanel {

	/**
	 * 
	 * @param buttonDescr JButton Beschriftung in der GUI.
	 */
	public ButtonPanel(String buttonDescr) {

		JButton button = new JButton(buttonDescr);
		
		add(button);

		// FormularAction showAction = new FormularAction();

		// treeButton.addActionListener((ActionListener) showAction);
	}
}
/*
 * Created on Jan 14, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package com.wanci.dmerce.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.wanci.dmerce.gui.Mainframe;
import com.wanci.dmerce.gui.util.DialogCreator;

/**
 * Action, die das Programm schließt.
 * 
 * @author mm
 * @version $Id: CloseMainframeAction.java,v 1.1 2004/02/05 16:30:21 mm Exp $
 */
public class CloseMainframeAction extends AbstractAction {

	private JFrame frame;
	private int chosenOption;
	private WindowEvent wEvent;

	/**
	 * @param mf The Mainframe to set.
	 */
	public CloseMainframeAction(Mainframe mf) {
		frame = mf;
		chosenOption = DialogCreator.closeDialog(frame);
	}

	/**
	 * schliesst das Fenster, sobald im Menüpunkt auf 'beenden' geklickt wird
	 */
	public void actionPerformed(ActionEvent e) {
		closeWindow();
	}

	/**
	 * @param wEvent
	 *            Wenn das Fenster geschlossen wird, wird die Methode
	 *            closeWindow aufgerufen.
	 */
	public void actionPerformed(WindowEvent wEvent) {
		closeWindow();
	}

	/**
	 * Schließt, speichert oder bricht die Aktion anhand des Parameters
	 * "chosenOption" ab.
	 */
	public void closeWindow() {

		switch (chosenOption) {
			case JOptionPane.YES_OPTION :
				frame.dispose();
				System.exit(0);
				break;

			case JOptionPane.NO_OPTION :
				frame.dispose();
				System.exit(0);
				break;

			case JOptionPane.CANCEL_OPTION :
				//nichts machen
				break;
			default :
				break;
		}
	}
}
/*
 * Created on Jan 15, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package com.wanci.dmerce.gui.util;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author mm
 * @version $Id: DialogCreator.java,v 1.3 2004/02/02 14:25:02 mm Exp $
 */
public class DialogCreator {

	/**
	 * erzeugt ein JDialog in dem 3 Optionen zur Auswahl gestellt werden. YES
	 * und NO_OPTION schliessen das Fenster. CANCEL_OPTION bricht den Vorgang
	 * ab (Fenster wird nicht geschlossen).
	 * 
	 * @param frame
	 *            parentFrame to set.
	 */
	public static int closeDialog(JFrame frame) {

		//Array mit der Beschriftung der Buttons.
		Object[] options = { "übernehmen", "nicht übernehmen", "abbrechen" };
		return JOptionPane.showOptionDialog(
			frame,
			"Sie haben Änderungen in diesem Fenster vorgenommen.",
			"Sollen diese Änderungen gespeichert werden?",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[0]);
	}
}
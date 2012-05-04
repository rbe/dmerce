/*
 * Created on Jan 21, 2004
 */
package com.wanci.dmerce.gui.listener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.wanci.dmerce.gui.Mainframe;
import com.wanci.dmerce.gui.forms.FormDetailPanel;

/**
 * Dieser Listener registriert alle Veränderungen in einem Document und setzt
 * die JTextField Felder mit den neuen Werten.
 * 
 * @author mm
 * @version $Id: FormChangeListener.java,v 1.1 2004/02/02 14:25:01 mm Exp $
 */
public class FormChangeListener implements DocumentListener {

	private FormDetailPanel formPanel;
	private Mainframe mainframe;

	public FormChangeListener(FormDetailPanel formPanel) {
		this.formPanel = formPanel;
	}

	public FormChangeListener(Mainframe mainframe) {
		this.mainframe = mainframe;
	}

	/**
	 * Gives notification that an attribute or set of attributes changed.
	 */
	public void changedUpdate(DocumentEvent e) {
		formPanel.setFlag(true);
		formPanel.updateTextfields();
		//wird nicht genutzt
		System.out.println("changeUpdate");
	}

	/**
	 * Gives notification that there was an insert into the document.
	 */
	public void insertUpdate(DocumentEvent e) {
		formPanel.setFlag(true);
		formPanel.updateTextfields();
		System.out.println("insertUpdate");
	}

	/**
	 * Gives notification that a portion of the document has been removed.
	 */
	public void removeUpdate(DocumentEvent e) {
		formPanel.setFlag(true);
		formPanel.updateTextfields();
		System.out.println("removeUpdate");
	}

	//public void 

}

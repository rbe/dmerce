/*
 * Created on Feb 4, 2004
 */
package com.wanci.dmerce.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.wanci.dmerce.dbmgr.DBmgrGUI;

/**
 * Action, die den DB-Manager startet, sobald dieser im Menüpunkt
 * ausgewählt wird.
 * 
 * @author mm
 * @version $Id: DBmgrAction.java,v 1.1 2004/02/05 16:30:21 mm Exp $
 */
public class DBmgrAction extends AbstractAction {

	/**
	 * constructor
	 * @param name
	 */
	public DBmgrAction(String name) {
		super(name);
	}

	/**
	 * action handler
	 */
	public void actionPerformed(ActionEvent e) {

		DBmgrGUI.init();
	}
}
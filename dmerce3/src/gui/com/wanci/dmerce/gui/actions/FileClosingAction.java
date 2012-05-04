/*
 * Created on Feb 17, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.wanci.dmerce.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.wanci.dmerce.gui.Mainframe;

/**
 * @author mm
 * @version $Id: FileClosingAction.java,v 1.1 2004/02/17 16:50:07 mm Exp $
 */
public class FileClosingAction extends AbstractAction {

	private Mainframe mainframe;

	public FileClosingAction(Mainframe mainframe, String name) {
		super(name);
		this.mainframe = mainframe;
	}

	public void actionPerformed(ActionEvent e) {
		//mainframe.
	}
}
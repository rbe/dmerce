package com.wanci.dmerce.dbmgr;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;

/**
 * @author tw
 *
 * @version $Id: EditConnectionAction.java,v 1.2 2003/10/30 17:51:32 pg Exp $
 */

public class EditConnectionAction extends AbstractAction {

	private JDesktopPane desktop;


	/**
	 * constructor
	 * @param name
	 */
	public EditConnectionAction(String name, JDesktopPane desktop) {
		super(name);
		this.desktop = desktop;
	}

	/**
	 * action handler
	 */
	public void actionPerformed(ActionEvent event) {

		EditConnection nw =
			new EditConnection(
				(JFrame) desktop.getTopLevelAncestor(),
				"Create New Connection");
	}

}

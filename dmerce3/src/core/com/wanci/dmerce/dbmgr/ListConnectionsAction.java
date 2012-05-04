package com.wanci.dmerce.dbmgr;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;

/**
 * @author tw
 *
 * @version $Id: ListConnectionsAction.java,v 1.2 2003/10/30 17:51:32 pg Exp $
 */

public class ListConnectionsAction extends AbstractAction {

	private DBmgr dbmgr;

	private JDesktopPane desktop;


	/**
	 * @param name
	 */
	public ListConnectionsAction(
		String name,
		JDesktopPane desktop,
		DBmgr dbmgr) {
		super(name);

		this.desktop = desktop;
		this.dbmgr = dbmgr;
	}

	/**
	 * action handler
	 */
	public void actionPerformed(ActionEvent event) {

		ListConnections lc =
			new ListConnections(
				(JFrame) desktop.getTopLevelAncestor(),
				"Load Connection",
				dbmgr);

	} //method

}

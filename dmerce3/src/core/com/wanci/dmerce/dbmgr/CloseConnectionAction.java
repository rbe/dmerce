package com.wanci.dmerce.dbmgr;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;

/**
 * @author tw
 *
 * @version $Id: CloseConnectionAction.java,v 1.4 2003/10/30 16:37:59 pg Exp $
 */
public class CloseConnectionAction extends AbstractAction {

	/**
	 * member variable: application
	 */
	private DBmgr dbmgr;

	/**
	 * member variable: window frame
	 */
	private JDesktopPane desktop;

	/**
	 * constructor with parameters
	 * @param name
	 */
	public CloseConnectionAction(
		String name,
		JDesktopPane desktop,
		DBmgr dbmgr) {
		super(name);
		// TODO close connection window
		this.desktop = desktop;
		this.dbmgr = dbmgr;
	}


	/**
	 * action handler
	 */
	public void actionPerformed(ActionEvent event) {
		CloseConnection cc =
			new CloseConnection(
				(JFrame) desktop.getTopLevelAncestor(),
				"Close Connection",
				dbmgr);
	} //method

}

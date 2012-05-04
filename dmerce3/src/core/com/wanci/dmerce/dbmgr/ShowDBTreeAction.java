package com.wanci.dmerce.dbmgr;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDesktopPane;

/**
 * @author tw
 *
 * @version $Id: ShowDBTreeAction.java,v 1.4 2003/10/30 16:37:59 pg Exp $
 */
public class ShowDBTreeAction extends AbstractAction {

	private DBmgr dbmgr;

	private JDesktopPane desktop;


	/**
	 * constructor
	 * @param name
	 */
	public ShowDBTreeAction(String name, JDesktopPane desktop, DBmgr dbmgr) {
		super(name);
		this.desktop = desktop;
		this.dbmgr = dbmgr;
	}

	/**
	 * action handler
	 */
	public void actionPerformed(ActionEvent event) {
		
		PropertiesHandler ph = PropertiesHandler.getSingleInstance();

		ShowDBTree sdbt = new ShowDBTree("Connection: " + ph.getName(), dbmgr);
		desktop.add(sdbt);
		sdbt.setLocation(1,1);
		sdbt.setSize(780,580);
		sdbt.setVisible(true);
	}

}

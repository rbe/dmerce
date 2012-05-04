/*
 * Created on Feb 4, 2004
 */
package com.wanci.dmerce.gui.actions;

import java.awt.event.ActionEvent;
import java.io.InputStream;

import javax.swing.AbstractAction;

import com.wanci.dmerce.gui.Mainframe;
import com.wanci.dmerce.gui.XmlFileOpener;

/**
 * Action Klasse, die ein Dateiauswahlfenster öffnet und nur
 * .xml Dateien zulässt.
 * 
 * @author mm
 * @version $Id: FileOpenerAction.java,v 1.3 2004/02/17 16:50:07 mm Exp $
 */
public class FileOpenerAction extends AbstractAction {
	
	private Mainframe mainframe;

	/**
	 * @param mainframe
	 *            The Mainframe object to set.
	 * @param name
	 *            The name to set.
	 */
	public FileOpenerAction(Mainframe mainframe, String name) {
		super(name);
		this.mainframe = mainframe;
	}

	/**
	 * action handler
	 */
	public void actionPerformed(ActionEvent e) {

		//für forms.xml und workflows.xml
		InputStream inputstream = XmlFileOpener.createFileChooser(mainframe);
		
		if (inputstream != null) {
			mainframe.initXmlFiles(inputstream);
			mainframe.setMainInternalFrame(true);
			mainframe.setVisible(true);
		}
	}
}
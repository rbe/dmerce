/*
 * Created on Jan 14, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package com.wanci.dmerce.gui;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.wanci.dmerce.gui.actions.CloseMainframeAction;
import com.wanci.dmerce.gui.actions.DBmgrAction;
import com.wanci.dmerce.gui.actions.FileClosingAction;
import com.wanci.dmerce.gui.actions.FileOpenerAction;

/**
 * @author mm
 * @version $Id: MenuMaker.java,v 1.6 2004/02/17 16:49:19 mm Exp $
 */
public class MenuMaker implements ActionListener {

	private Mainframe mainframe;

	private CloseMainframeAction miListener;

	private JMenuItem menuDBmgrGUI;

	private JMenuItem openXmlFile;

	private JMenuItem closeXmlFile;
	
	private JInternalFrame mainInternalFrame;

	public MenuMaker() {
	}

	/**
	 * @param mainframe
	 *            The Mainframe to set for the actionPerformed Method.
	 */
	public MenuMaker(Mainframe mainframe) {
		this.mainframe = mainframe;
	}
	
//	public MenuMaker(JInternalFrame mainInternalFrame) {
//	}

	/**
	 * action handler
	 */
	public void actionPerformed(ActionEvent event) {

		if (event.getActionCommand().equals("Exit")) {
			miListener = new CloseMainframeAction(mainframe);
			miListener.actionPerformed(event);
		} else
			System.out.println(event.getActionCommand());

	}

	/**
	 * erstellt eine neue Menüleiste mit Menüpunkten. vorerst nur der Menüpunkt
	 * Beenden im Menü Datei
	 * 
	 * @param mLabel
	 * @param mnemonic
	 * @param menuItem
	 * @param miMnemonic
	 */
	public JMenu createFileMenu() {

		JMenu selection = new JMenu("Datei");
		//add shortcut
		selection.setMnemonic('D');

		//create a new connection
		menuDBmgrGUI =
			createMenuItem(
				"DB Manager aufrufen",
				'n',
				new DBmgrAction("DBManager öffnen"),
				'N',
				selection);

		openXmlFile =
			createMenuItem(
				"XML Datei öffnen",
				'o',
				new FileOpenerAction(mainframe, "Xml Datei öffnen"),
				'O',
				selection);

		closeXmlFile =
			createMenuItem(
				"Xml Datei schliessen",
				's',
				new FileClosingAction(mainframe, "Xml Datei schliessen"),
				'S',
				selection);

		//Separator
		selection.addSeparator();

		//Exit
		JMenuItem mi;
		mi = new JMenuItem("Exit", 'e');
		mi.addActionListener(this);
		selection.add(mi);

		return selection;

		//erstellt eine neue Menübar
		//		JMenuBar menuBar = new JMenuBar();
		//		
		//		//erstellt ein neues Menü
		//		JMenu menu = new JMenu("Datei");
		//		menu.setMnemonic(mnemonic);
		//
		//		//erstellt einen neuen Menüpunkt
		//		JMenuItem mi = new JMenuItem(menuItem, miMnemonic);
		//		miListener = new MenuItemQuitListener();
		//		mi.addActionListener(miListener);
		//
		//		menu.add(mi);
		//
		//		menuBar.add(menu);
		//		
		//		return menuBar;
	}

	/**
	 * create menu item
	 * 
	 * @param name
	 * @param mnemonic
	 * @param action
	 * @param accelerator
	 * @param menu
	 * @return
	 */
	private JMenuItem createMenuItem(
		String name,
		char mnemonic,
		AbstractAction action,
		char accelerator,
		JMenu menu) {

		JMenuItem m = new JMenuItem(name, mnemonic);
		m.setAction(action);
		setCtrlAcceleration(m, accelerator);
		menu.add(m);

		return m;

	}

	public void setCtrlAcceleration(JMenuItem mi, char acc) {

		KeyStroke ks = KeyStroke.getKeyStroke(acc, Event.CTRL_MASK);
		mi.setAccelerator(ks);

	}

}
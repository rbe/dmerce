package com.wanci.dmerce.dbmgr;

import java.awt.Color;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.DefaultDesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * @author tw
 * 
 * @version $Id: DBmgrGUI.java,v 1.14 2004/02/05 16:42:41 mm Exp $
 */
public class DBmgrGUI extends JFrame implements ActionListener {

	/**
	 * main window
	 */
	private JDesktopPane desktop;

	/**
	 * application dbmgr encapsuled in variable
	 */
	private DBmgr dbmgr;

	/**
	 * variables for menus
	 */
	private JMenuItem menuItemLoad;
	private JMenuItem menuItemShow;
	private JMenuItem menuItemClose;
	private JMenuItem menuItemCreate;

	/**
	 * action for closing application
	 */
	public final static String ACTION_CLOSE = "close";

	/**
	 * singleton pattern: object variable
	 */
	private static DBmgrGUI singleInstance = null;

	private DBmgr dBmgr;

	/**
	 * static constructor for singleton pattern
	 * 
	 * @return
	 */
	public static DBmgrGUI getSingleInstance() {
		//check if there is an instance of object
		if (singleInstance == null) {
			//if not create one
			singleInstance = new DBmgrGUI();
		}
		//return gui object
		return singleInstance;
	}

	/**
	 * constructor
	 */
	private DBmgrGUI() {
		//call parent constructor: set application name
		super("Database Management Tool");
		//close operation
		super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		//create application object
		dbmgr = new DBmgr();

		//initialize application
		desktop = new JDesktopPane();
		desktop.setDesktopManager(new DefaultDesktopManager());
		desktop.setVisible(true);
		desktop.setBackground(Color.lightGray);
		setContentPane(desktop);
		JMenuBar menubar = new JMenuBar();
		menubar.add(createFileMenu());
		menubar.add(createHelpMenu());
		setJMenuBar(menubar);
		System.out.println(menuItemCreate.toString());

	}

	/**
	 * action handler
	 */
	public void actionPerformed(ActionEvent event) {

		if (event.getActionCommand().equals("Exit"))
			super.dispose();
		else
			System.out.println(event.getActionCommand());

	}

	/**
	 * file menu
	 * 
	 * @return
	 */
	private JMenu createFileMenu() {

		//create menu object
		JMenu selection = new JMenu("File");
		//add shortcut
		selection.setMnemonic('F');

		//create a new connection
		menuItemCreate =
			createMenuItem(
				"Create New Connection",
				'n',
				new EditConnectionAction("Create New Connection", desktop),
				'N',
				selection);

		//load connection
		menuItemLoad =
			createMenuItem(
				"Load Connection",
				'l',
				new ListConnectionsAction("Load Connection", desktop, dbmgr),
				'O',
				selection);

		//show database tree
		menuItemShow =
			createMenuItem(
				"Show Database Tree",
				'l',
				new ShowDBTreeAction("Show Database Tree", desktop, dbmgr),
				'T',
				selection);

		//close connection
		menuItemClose =
			createMenuItem(
				"Close Connection",
				'c',
				new CloseConnectionAction("Close Connection", desktop, dbmgr),
				'C',
				selection);
		menuItemClose.setEnabled(false);

		//Separator
		selection.addSeparator();

		//Exit
		JMenuItem mi;
		mi = new JMenuItem("Exit", 'e');
		mi.addActionListener(this);
		selection.add(mi);

		return selection;
	}

	/**
	 * menu help
	 * 
	 * @return
	 */
	private JMenu createHelpMenu() {

		//create menu entry
		JMenu selection = new JMenu("Help");
		//set shortcut
		selection.setMnemonic('H');

		//about
		JMenuItem mi;
		mi = new JMenuItem("About DBmgr", 'A');
		selection.add(mi);
		mi.setEnabled(false);

		return selection;
	}

	/**
	 * update method for menus
	 */
	public void updateMenu() {
		//check enabled status of menus
		//update connection menu items
		try {
			menuItemClose.setEnabled(!dbmgr.getConnection().isClosed());
			menuItemLoad.setEnabled(dbmgr.getConnection().isClosed());
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	public void dispatchAction(String name) {

		if (name.equals(ACTION_CLOSE))
			menuItemShow.doClick();

	}

	public static void init() {

		// load SplashScreen
		SplashScreen ss =
			new SplashScreen(
				"test/1ci_splashscreen.jpg",
				"(C) Copyright 2000-2003 1Ci GmbH Münster, All Rights Reserved");

		new Thread(ss).start();

		// load GUI
		DBmgrGUI gui = getSingleInstance();
		gui.setLocation(100, 100);
		gui.setSize(800, 600);
		gui.setVisible(true);
	}

	public static void main(String[] args) {

		init();
	}

	public DBmgr getDBmgr() {
		return dBmgr;
	}

	public void setDBmgr(DBmgr dBmgr) {
		this.dBmgr = dBmgr;
	}

}

/*
 * Created on 05.08.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.test.swing;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * @author tw
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LearnSwingMenu extends JFrame implements ActionListener {

	private final String[] crew =
		{
			"Bensmann, Ralf",
			"Ewe, Christian",
			"Aust, Kristian",
			"Exner, Patrick",
			"Murawski, Manuel",
			"Wormuth, Timo" };

	public LearnSwingMenu() {

		super("Mein erstes Menu!");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JMenuBar menubar = new JMenuBar();
		menubar.add(createFileMenu());
		menubar.add(createCrewMenu());
		setJMenuBar(menubar);

	}

	public void actionPerformed(ActionEvent event) {

		System.out.println(event.getActionCommand());

	}

	private JMenu createFileMenu() {

		JMenu selection = new JMenu("Datei");
		selection.setMnemonic('D');
		JMenuItem mi;
		//Öffnen
		mi = new JMenuItem("Öffnen", 'f');
		setCtrlAcceleration(mi, 'O');
		mi.addActionListener(this);
		selection.add(mi);
		//Speichern
		mi = new JMenuItem("Speichern", 'p');
		setCtrlAcceleration(mi, 'S');
		mi.addActionListener(this);
		selection.add(mi);
		//Seperator
		selection.addSeparator();
		//Beenden
		mi = new JMenuItem("Beenden", 'e');
		mi.addActionListener(this);
		selection.add(mi);

		return selection;

	}

	private JMenu createCrewMenu() {

		JMenu selection = new JMenu("Mitabeiter");
		selection.setMnemonic('M');
		JMenuItem mi;

		for (int i = 0; i < crew.length; i++) {

			//crew[]
			mi = new JMenuItem(crew[i]);
			mi.addActionListener(this);
			selection.add(mi);

		}

		return selection;

	}

	public void setCtrlAcceleration(JMenuItem mi, char acc) {

		KeyStroke ks = KeyStroke.getKeyStroke(acc, Event.CTRL_MASK);
		mi.setAccelerator(ks);

	}

	public static void main(String[] args) {

		LearnSwingMenu window = new LearnSwingMenu();
		window.setLocation(100, 100);
		window.setSize(300, 200);
		window.setVisible(true);

	}
}

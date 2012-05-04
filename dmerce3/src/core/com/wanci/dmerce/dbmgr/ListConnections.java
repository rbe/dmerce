package com.wanci.dmerce.dbmgr;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * @author tw
 *
 * @version $Id: ListConnections.java,v 1.2 2003/10/30 17:51:32 pg Exp $
 */

public class ListConnections extends JDialog implements ActionListener {

	private DBmgr dbmgr;
	private List cl;
	private JButton jButtonLoad;
	private JButton jButtonCancel;

	public ListConnections(JFrame frame, String title, DBmgr dbmgr) {

		super(frame, true);
		setTitle(title);
		setBackground(Color.lightGray);
		getContentPane().setLayout(new GridBagLayout());

		this.dbmgr = dbmgr;

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;

		cl = new List(11, false);
		jButtonLoad = new JButton();
		jButtonCancel = new JButton();

		Properties c = new Properties();

		// loads all stored connections and puts it in a properties-object
		PropertiesHandler ph = PropertiesHandler.getSingleInstance();
		c = ph.loadProperties();

		if (c != null) {
			//loads all keys out of the properties-object into the list
			Enumeration names = c.propertyNames();

			while (names.hasMoreElements()) {

				cl.add((String) names.nextElement());

			}

		}

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		getContentPane().add(cl, gridBagConstraints);
		cl.addActionListener(this);

		jButtonLoad.setText("Load");
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.SOUTHEAST;
		getContentPane().add(jButtonLoad, gridBagConstraints);
		jButtonLoad.addActionListener(this);

		jButtonCancel.setText("Cancel");
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		getContentPane().add(jButtonCancel, gridBagConstraints);
		jButtonCancel.addActionListener(this);

		pack();

		setLocationRelativeTo(frame);
		setVisible(true);

	}

	public void actionPerformed(ActionEvent event) {

		String selected;

		if (event.getActionCommand().equals("Load")) {

			PropertiesHandler ph = PropertiesHandler.getSingleInstance();

			System.out.println(cl);
			if (cl.getSelectedIndex() > -1) {

				selected = cl.getSelectedItem();

				if (ph.load(selected)) {

					dbmgr.setConnection(ConnectionFactory.create(ph));
					DBmgrGUI.getSingleInstance().updateMenu();
					DBmgrGUI.getSingleInstance().dispatchAction(
						DBmgrGUI.ACTION_CLOSE);

				} else {

					//TODO: PopUp Error
					System.out.println("Unable to connect!");

				}

			} else {

				//TODO: PopUp Error
				System.out.println("Please select a Connection to load!");

			}

		}

		dispose();

	}

}
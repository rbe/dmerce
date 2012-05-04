package com.wanci.dmerce.dbmgr;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * dialog window for confirmation of close connection action
 * @author tw
 *
 * @version $Id: CloseConnection.java,v 1.6 2003/10/30 16:37:59 pg Exp $
 */
public class CloseConnection extends JDialog implements ActionListener {

	private DBmgr dbmgr;

	/**
	 * close button
	 */
	private JButton jButtonClose;
	/**
	 * cancel button
	 */
	private JButton jButtonCancel;

	/**
	 * constructor
	 * @param frame
	 * @param title
	 * @param dbmgr
	 */
	public CloseConnection(JFrame frame, String title, DBmgr dbmgr) {

		super(frame, true);
		//set color
		setBackground(Color.lightGray);
		//set layout
		getContentPane().setLayout(new GridBagLayout());

		//hold application object as member variable
		this.dbmgr = dbmgr;

		//init buttons
		jButtonClose = new JButton();
		jButtonCancel = new JButton();

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;

		//fill and place button close
		jButtonClose.setText("Close");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.SOUTHEAST;
		getContentPane().add(jButtonClose, gridBagConstraints);
		jButtonClose.addActionListener(this);

		//fill and place button cancel
		jButtonCancel.setText("Cancel");
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		getContentPane().add(jButtonCancel, gridBagConstraints);
		jButtonCancel.addActionListener(this);

		pack();

		// set window location
		setLocationRelativeTo(frame);
		//make it visible
		setVisible(true);

	}

	/**
	 * action handler
	 */
	public void actionPerformed(ActionEvent event) {

		//close action
		if (event.getActionCommand().equals("Close")) {

			PropertiesHandler ph = PropertiesHandler.getSingleInstance();

			dbmgr.closeConnection();
			DBmgrGUI.getSingleInstance().updateMenu();

		} else {
			//???
			//TODO: PopUp: Connection can't be closed
			System.out.println("The Connection was not closed!");

		}

		//dispose dialog window
		dispose();
	} //method

}

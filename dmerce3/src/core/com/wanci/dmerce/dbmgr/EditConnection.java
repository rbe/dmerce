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
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;

/**
 * @author tw
 *
 * @version $Id: EditConnection.java,v 1.1 2003/10/30 16:50:50 pg Exp $
 */
public class EditConnection extends JDialog implements ActionListener {

	private JButton jButtonCreate;
	private JButton jButtonCancel;
	private JLabel jLabelName;
	private JLabel jLabelDatabase;
	private JLabel jLabelDriver;
	private JLabel jLabelUser;
	private JLabel jLabelPassword;
	private JLabel jLabelHost;
	private JLabel jLabelPort;
	private JLabel jLabelSchema;
	private JPasswordField jPasswordField;
	private JSeparator jSeparator1;
	private JSeparator jSeparator2;
	private JTextField jTextFieldName;
	private JTextField jTextFieldDatabase;
	private JTextField jTextFieldDriver;
	private JTextField jTextFieldUser;
	private JTextField jTextFieldHost;
	private JTextField jTextFieldPort;
	private JTextField jTextFieldSchema;

	/**
	 * constructor
	 * @param frame
	 * @param title
	 */
	public EditConnection(JFrame frame, String title) {

		super(frame, true);
		setTitle(title);
		setBackground(Color.lightGray);
		getContentPane().setLayout(new GridBagLayout());

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(2, 5, 2, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;

		jLabelName = new JLabel();
		jButtonCreate = new JButton();
		jLabelDatabase = new JLabel();
		jLabelDriver = new JLabel();
		jLabelUser = new JLabel();
		jLabelPassword = new JLabel();
		jLabelHost = new JLabel();
		jLabelPort = new JLabel();
		jLabelSchema = new JLabel();
		jPasswordField = new javax.swing.JPasswordField(10);
		jTextFieldName = new JTextField(15);
		jTextFieldDatabase = new JTextField(15);
		jTextFieldDriver = new JTextField(15);
		jTextFieldUser = new JTextField(10);
		jTextFieldHost = new JTextField(15);
		jTextFieldPort = new JTextField(4);
		jTextFieldSchema = new JTextField(15);
		jButtonCancel = new JButton();
		jSeparator1 = new JSeparator();
		jSeparator2 = new JSeparator();

		jLabelName.setText("Name:");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		getContentPane().add(jLabelName, gridBagConstraints);

		jLabelDatabase.setText("Database:");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		getContentPane().add(jLabelDatabase, gridBagConstraints);

		jLabelDriver.setText("Driver:");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		getContentPane().add(jLabelDriver, gridBagConstraints);

		jLabelUser.setText("User:");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		getContentPane().add(jLabelUser, gridBagConstraints);

		jLabelPassword.setText("Password:");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		getContentPane().add(jLabelPassword, gridBagConstraints);

		jLabelHost.setText("Host:");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		getContentPane().add(jLabelHost, gridBagConstraints);

		jLabelPort.setText("Port:");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		getContentPane().add(jLabelPort, gridBagConstraints);

		jLabelSchema.setText("Schema:");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 7;
		getContentPane().add(jLabelSchema, gridBagConstraints);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		getContentPane().add(jTextFieldName, gridBagConstraints);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		getContentPane().add(jTextFieldDatabase, gridBagConstraints);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		getContentPane().add(jTextFieldDriver, gridBagConstraints);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		getContentPane().add(jTextFieldUser, gridBagConstraints);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		getContentPane().add(jPasswordField, gridBagConstraints);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		getContentPane().add(jTextFieldHost, gridBagConstraints);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 6;
		getContentPane().add(jTextFieldPort, gridBagConstraints);

		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 7;
		getContentPane().add(jTextFieldSchema, gridBagConstraints);

		jButtonCreate.setText("Create");
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 9;
		getContentPane().add(jButtonCreate, gridBagConstraints);
		jButtonCreate.addActionListener(this);

		jButtonCancel.setText("Cancel");
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 9;
		getContentPane().add(jButtonCancel, gridBagConstraints);
		jButtonCancel.addActionListener(this);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		getContentPane().add(jSeparator1, gridBagConstraints);

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		getContentPane().add(jSeparator2, gridBagConstraints);

		pack();

		setLocationRelativeTo(frame);
		setVisible(true);

	}

	/**
	 * action handler
	 */
	public void actionPerformed(ActionEvent event) {

		if (event.getActionCommand().equals("Create")) {

			PropertiesHandler ph = PropertiesHandler.getSingleInstance();
			ph.setDatabase(jTextFieldDatabase.getText());
			ph.setDriver(jTextFieldDriver.getText());
			ph.setUser(jTextFieldUser.getText());
			ph.setPassword(String.valueOf(jPasswordField.getPassword()));
			ph.setHost(jTextFieldHost.getText());
			ph.setPort(jTextFieldPort.getText());
			ph.setSchema(jTextFieldSchema.getText());

			if (!ph.store(jTextFieldName.getText())) {
				System.out.println("Fehler beim Speichern");
			}
		}
		dispose();

	} //method

}

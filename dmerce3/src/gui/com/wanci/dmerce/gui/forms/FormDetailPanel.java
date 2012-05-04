/*
 * Created on Nov 26, 2003
 */
package com.wanci.dmerce.gui.forms;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wanci.dmerce.forms.FORM;
import com.wanci.dmerce.gui.listener.FormChangeListener;

/**
 * gehört zu forms.xml Erzeugt ein Tabellenlayout, in dem schon mal die Sruktur
 * des Panels festgelegt wird. Die Werte werden vom Mainframe ausgefüllt. Es
 * werden auf der linken Seite JLabels und auf der rechten Seite JTextFields
 * benutzt.
 * 
 * @author mm
 * @version $Id: FormDetailPanel.java,v 1.7 2004/02/02 14:25:02 mm Exp $
 */
public class FormDetailPanel extends JPanel {

	private JTextField tfFormname;
	private JTextField tfDescription;
	private JTextField tfFormversion;

	private JPanel topPanel;
	private FORM form;

	private FormChangeListener formChangeListener;

	private String newDescr;
	private String newName;
	private String newVersion;

	/**
	 * flag Variable default-Wert ist false. Sobald sich ein JTextField
	 * geändert hat, wird die Variable auf true gesetzt.
	 */
	private boolean flag = false;

	/**
	 * Konstruktor GridBagLayout setzen und die einzelnen Tabellenfelder
	 * erzeugen und beschriften.
	 */
	public FormDetailPanel() {
		super(new BorderLayout());

		this.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Form Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		//neues JPanel mit Layout für die Felder erzeugen.
		topPanel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;

		JLabel leftText;

		//komplette linke Seite erstellen
		//Formname Feld links erstellen
		c.gridx = 0;
		c.gridy = 0;
		leftText = new JLabel("Formularname:");
		topPanel.add(leftText, c);

		//Description Feld links erstellen
		c.gridx = 0;
		c.gridy = 1;
		leftText = new JLabel("Description:");
		topPanel.add(leftText, c);

		//Formversion Feld links erstellen
		c.gridx = 0;
		c.gridy = 2;
		leftText = new JLabel("Formularversion:");
		topPanel.add(leftText, c);

		//komplette rechte Seite erstellen
		//Formname Feld rechts erstellen
		c.gridx = 1;
		c.gridy = 0;
		tfFormname = new JTextField(20);
		formChangeListener = new FormChangeListener(this);
		tfFormname.getDocument().addDocumentListener(formChangeListener);
		//tfFormname.addActionListener(formActionListener);
		topPanel.add(tfFormname, c);

		//Description Feld rechts erstellen
		c.gridx = 1;
		c.gridy = 1;
		tfDescription = new JTextField(20);
		tfDescription.getDocument().addDocumentListener(formChangeListener);
		topPanel.add(tfDescription, c);

		//Formversion Feld rechts erstellen
		c.gridx = 1;
		c.gridy = 2;
		tfFormversion = new JTextField(20);
		tfFormversion.getDocument().addDocumentListener(formChangeListener);
		topPanel.add(tfFormversion, c);

		add(topPanel, BorderLayout.NORTH);

		//erzeuge Buttons zum Speichern und Abbrechen
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		//		JButton cancelButton = new JButton("Abbrechen");
		//		cancelButton.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//				//TODO: Alte Werte wieder aufnehmen!
		//				
		//			}
		//		});
		//		buttonPanel.add(cancelButton);

		//erstmal nur "übernehmen" Button
		JButton saveButton = new JButton("Übernehmen");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO: wenn Button geklickt, dann "Übernehmen"!
				if (flag == true) {
					setDescription(newDescr);
					setFormname(newName);
					setFormversion(newVersion);
					repaint();
					setFlag(false);
				} else
					System.out.println("flag != true");

			}
		});
		buttonPanel.add(saveButton);

		buttonPanel.setVisible(true);

		add(buttonPanel, BorderLayout.SOUTH);
	}

	public void updateTextfields() {
		if (!getDescription().equals(tfDescription.getText()))
			newDescr = tfDescription.getText();
		else
			newDescr = getDescription();

		if (!getFormname().equals(tfFormname.getText()))
			newName = tfFormname.getText();
		else
			newName = getFormname();

		if (!getFormversion().equals(tfFormversion.getText()))
			newVersion = tfFormversion.getText();
		else
			newVersion = getFormversion();
	}

	/**
	 * @return Returns the textfield tfDescription.
	 */
	public String getDescription() {
		return form.getDescription();
	}

	/**
	 * @param description
	 *            The textfield tfDescription to set.
	 */
	public void setDescription(String description) {
		this.tfDescription.setText(description);
		form.setDescription(description);
	}

	/**
	 * @return Returns the textfield tfFormname.
	 */
	public String getFormname() {
		return form.getName();
	}

	/**
	 * @param formname
	 *            The textfield tfFormname to set.
	 */
	public void setFormname(String formname) {
		this.tfFormname.setText(formname);
		form.setName(formname);
	}

	/**
	 * @return Returns the textfield tfFormversion.
	 */
	public String getFormversion() {
		return form.getFormversion();
	}

	/**
	 * @param formversion
	 *            The textfield tfFormversion to set.
	 */
	public void setFormversion(String formversion) {
		this.tfFormversion.setText(formversion);
		form.setFormversion(formversion);
	}

	/**
	 * @param form
	 *            The FORM object to set.
	 */
	public void setForm(FORM form) {
		this.form = form;
		setDescription(form.getDescription());
		setFormname(form.getName());
		setFormversion(form.getFormversion());
	}

	/**
	 * @return Returns the FORM object.
	 */
	public FORM getForm() {
		return form;
	}

	/**
	 * @param b
	 *            Wert zum Setzen ob sich was geändert hat.
	 */
	public void setFlag(boolean b) {
		this.flag = b;
	}

	/**
	 * @return Returned, ob sich was im JTextField geändert hat.
	 */
	public boolean getFlag() {
		return flag;
	}
}
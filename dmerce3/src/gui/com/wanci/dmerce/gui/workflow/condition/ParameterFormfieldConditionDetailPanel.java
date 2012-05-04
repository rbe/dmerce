/*
 * Created on Jan 7, 2004
 */
package com.wanci.dmerce.gui.workflow.condition;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wanci.dmerce.workflow.xmlbridge.FORMFIELD;
import com.wanci.dmerce.workflow.xmlbridge.PARAMETER;

/**
 * Diese Klasse zeigt die Parameter und Formfield Werte von der Condition an.
 * Die Werte werden in JTextField-Feldern angezeigt.
 * 
 * @author mm
 * @version $Id: ParameterFormfieldConditionDetailPanel.java,v 1.1 2004/01/13
 *          16:41:26 mm Exp $
 */
public class ParameterFormfieldConditionDetailPanel extends JPanel {

	private JPanel paraFormPanel;
	private JPanel parameterPanel;
	private JPanel parameterLayoutPanel;
	private JPanel formfieldPanel;
	private JPanel formfieldLayoutPanel;

	private JTextField tfParameterName;
	private JTextField tfExpected;
	private JTextField tfPageid;
	private JTextField tfFormid;
	private JTextField tfFormfieldName;

	private FORMFIELD formfield;
	private PARAMETER parameter;

	public ParameterFormfieldConditionDetailPanel() {

		super(new BorderLayout());

		//paraFormPanel = new JPanel(new BorderLayout());
		
		/*
		 * PARAMETER Attribute in einem eigenen JPanel
		 */
		parameterPanel = new JPanel(new BorderLayout());

		parameterPanel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Parameter Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		//erzeuge neues JPanel mit Layout für die Felder.
		parameterLayoutPanel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;

		JLabel parameterText;

		//linke Seite erstellen
		//name Feld links
		c.gridx = 0;
		c.gridy = 0;
		parameterText = new JLabel("name:");
		parameterLayoutPanel.add(parameterText, c);

		//expected Feld links
		c.gridx = 0;
		c.gridy = 1;
		parameterText = new JLabel("expected:");
		parameterLayoutPanel.add(parameterText, c);

		//rechte Seite erstellen
		//type Feld rechts
		c.gridx = 1;
		c.gridy = 0;
		tfParameterName = new JTextField(20);
		parameterLayoutPanel.add(tfParameterName, c);

		//value Feld rechts
		c.gridx = 1;
		c.gridy = 1;
		tfExpected = new JTextField(20);
		parameterLayoutPanel.add(tfExpected, c);

		parameterPanel.add(parameterLayoutPanel, BorderLayout.CENTER);
		
		/*
		 * FORMFIELD Attribute in einem eigenen JPanel
		 */
		formfieldPanel = new JPanel(new BorderLayout());
		
		formfieldPanel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Formfield Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		
		formfieldLayoutPanel = new JPanel(new GridBagLayout());
		
		JLabel formfieldText;

		//linke Seite erstellen
		//pageid Feld links
		c.gridx = 0;
		c.gridy = 0;
		formfieldText = new JLabel("pageid:");
		formfieldLayoutPanel.add(formfieldText, c);

		//formid Feld links
		c.gridx = 0;
		c.gridy = 1;
		formfieldText = new JLabel("formid:");
		formfieldLayoutPanel.add(formfieldText, c);

		//name Feld links
		c.gridx = 0;
		c.gridy = 2;
		formfieldText = new JLabel("name:");
		formfieldLayoutPanel.add(formfieldText, c);

		//rechte Seite erstellen
		//type Feld rechts
		c.gridx = 1;
		c.gridy = 0;
		tfPageid = new JTextField(20);
		formfieldLayoutPanel.add(tfPageid, c);

		//value Feld rechts
		c.gridx = 1;
		c.gridy = 1;
		tfFormid = new JTextField(20);
		formfieldLayoutPanel.add(tfFormid, c);

		//name Feld rechts
		c.gridx = 1;
		c.gridy = 2;
		tfFormfieldName = new JTextField(20);
		formfieldLayoutPanel.add(tfFormfieldName, c);
		
		formfieldPanel.add(formfieldLayoutPanel, BorderLayout.CENTER);
		
		add(parameterPanel, BorderLayout.NORTH);
		add(formfieldPanel, BorderLayout.SOUTH);
	}

	/**
	 * @return Returns the textfield tfName.
	 */
	public String getParameterTfName() {
		return parameter.getName();
	}

	/**
	 * @param tfName
	 *            The textfield tfName to set.
	 */
	public void setTfParameterName(String tfName) {
		this.tfParameterName.setText(tfName);
		parameter.setName(tfName);
	}

	/**
	 * @return Returns the textfield tfExpected.
	 */
	public String getTfExpected() {
		return parameter.getValue();
	}

	/**
	 * @param tfExpected
	 *            The textfield tfExpected to set.
	 */
	public void setTfExpected(String tfExpected) {
		this.tfExpected.setText(tfExpected);
		parameter.setValue(tfExpected);
	}

	/**
	 * @param tfPageid
	 *            The pageid to set.
	 */
	public void setTfPageid(String tfPageid) {
		this.tfPageid.setText(tfPageid);
		formfield.setPageid(tfPageid);
	}

	/**
	 * @return Returns the textfield tfPageid.
	 */
	public String getTfPageid() {
		return formfield.getPageid();
	}

	/**
	 * @param tfFormid
	 *            The formid to set.
	 */
	public void setTfFormid(String tfFormid) {
		this.tfFormid.setText(tfFormid);
		formfield.setFormid(tfFormid);
	}

	/**
	 * @return Returns the textfield tfFormid.
	 */
	public String getTfFormid() {
		return formfield.getFormid();
	}

	/**
	 * @param tfName
	 *            The formfield name to set.
	 */
	public void setTfFormfieldName(String tfName) {
		this.tfFormfieldName.setText(tfName);
		formfield.setName(tfName);
	}

	/**
	 * @return Returns the textfield tfFormfieldName.
	 */
	public String getTfFormfieldName() {
		return formfield.getName();
	}

	/**
	 * @param parameter
	 *            The PARAMETER object to set.
	 */
	public void setParameter(PARAMETER parameter) {
		this.parameter = parameter;
		setTfParameterName(parameter.getName());
		setTfExpected(parameter.getValue());
	}

	/**
	 * @return Returns the PARAMETER object.
	 */
	public PARAMETER getParameter() {
		return parameter;
	}

	/**
	 * @param formfield
	 *            The FORMFIELD object to set.
	 */
	public void setFormfield(FORMFIELD formfield) {
		this.formfield = formfield;
		setTfFormfieldName(formfield.getName());
		setTfFormid(formfield.getFormid());
		setTfPageid(formfield.getPageid());
	}

	/**
	 * @return Returns the FORMFIELD object.
	 */
	public FORMFIELD getFormfield() {
		return formfield;
	}
}
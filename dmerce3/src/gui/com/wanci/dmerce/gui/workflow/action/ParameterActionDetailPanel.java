/*
 * Created on Jan 7, 2004
 */
package com.wanci.dmerce.gui.workflow.action;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wanci.dmerce.workflow.xmlbridge.ACTION;
import com.wanci.dmerce.workflow.xmlbridge.PARAMETER;
import com.wanci.dmerce.workflow.xmlbridge.TRANSITION;

/**
 * Klasse zum Anzeigen der PARAMETER Attribute der ACTION.
 * 
 * @author mm
 * @version $Id: ParameterActionDetailPanel.java,v 1.3 2004/01/16 16:32:37 mm Exp $
 */
public class ParameterActionDetailPanel extends JPanel {
	
	private JPanel topPanel;
	private JTextField tfName;
	private JTextField tfValue;
	private TRANSITION transition;
	private ACTION action;
	private PARAMETER parameter;

	public ParameterActionDetailPanel() {
		
		super(new BorderLayout());

		this.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Parameter Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		//erzeuge neues JPanel mit Layout für die Felder
		topPanel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		
		JLabel leftText;
		
		//linke Seite erstellen
		//type Feld links
		c.gridx = 0;
		c.gridy = 0;
		leftText = new JLabel("name:");
		topPanel.add(leftText, c);
		
		//value Feld links
		c.gridx = 0;
		c.gridy = 1;
		leftText = new JLabel("value:");
		topPanel.add(leftText, c);
		
		//rechte Seite erstellen
		//type Feld rechts
		c.gridx = 1;
		c.gridy = 0;
		tfName = new JTextField(20);
		topPanel.add(tfName, c);
		
		//value Feld rechts
		c.gridx = 1;
		c.gridy = 1;
		tfValue = new JTextField(20);
		topPanel.add(tfValue, c);
		
		add(topPanel, BorderLayout.CENTER);
	}

	/**
	 * @return Returns the textfield tfName.
	 */
	public String getTfName() {
		return parameter.getName();
	}

	/**
	 * @param tfType
	 *            The textfield tfType to set.
	 */
	public void setTfName(String tfName) {
		this.tfName.setText(tfName);
		this.parameter.setName(tfName);
	}

	/**
	 * @return Returns the textfield tfExpected.
	 */
	public String getTfValue() {
		return parameter.getValue();
	}

	/**
	 * @param tfValue
	 *            The textfield tfValue to set.
	 */
	public void setTfValue(String tfValue) {
		this.tfValue.setText(tfValue);
		this.parameter.setValue(tfValue);
	}

	/**
	 * @param parameter
	 *            The PARAMETER object to set.
	 */
	public void setParameter(PARAMETER parameter) {
		this.parameter = parameter;
		setTfName(parameter.getName());
		setTfValue(parameter.getValue());
	}

	/**
	 * @return Returns the PARAMETER object.
	 */
	public PARAMETER getParameter() {
		return parameter;
	}
}
/*
 * Created on Jan 6, 2004
 */
package com.wanci.dmerce.gui.workflow.condition;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wanci.dmerce.workflow.xmlbridge.CONDITION;

/**
 * Dieses JPanel zeigt die type-value Werte in der Condition an.
 * 
 * @author mm
 * @version $Id: TypeValueConditionDetailPanel.java,v 1.4 2004/02/03 15:14:28 mm Exp $
 */
public class TypeValueConditionDetailPanel extends JPanel {
	
	private JPanel topPanel;
	private JTextField tfType;
	private JTextField tfValue;
	private CONDITION condition;

	public TypeValueConditionDetailPanel() {
		
		super(new BorderLayout());

		this.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("buttonpressed Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		//erzeuge neues JPanel mit Layout für die Felder
		topPanel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		
		JLabel leftText;
		
		//linke Seite erstellen
		//value Feld links
		c.gridx = 0;
		c.gridy = 0;
		leftText = new JLabel("value:");
		topPanel.add(leftText, c);
		
		//rechte Seite erstellen
		//value Feld rechts
		c.gridx = 1;
		c.gridy = 0;
		tfValue = new JTextField(20);
		topPanel.add(tfValue, c);
		
		add(topPanel, BorderLayout.CENTER);
	}

	/**
	 * @return Returns the textfield tfValue.
	 */
	public String getTfValue() {
		return condition.getValue();
	}

	/**
	 * @param tfValue
	 *            The textfield tfValue to set.
	 */
	public void setTfValue(String tfValue) {
		this.tfValue.setText(tfValue);
		condition.setValue(tfValue);
	}

	/**
	 * @param condition
	 *            The CONDITION object to set.
	 */
	public void setCondition(CONDITION condition) {
		this.condition = condition;
		setTfValue(condition.getValue());
	}

	/**
	 * @return Returns the CONDITION object.
	 */
	public CONDITION getCondition() {
		return condition;
	}
}
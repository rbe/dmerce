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
 * Klasse zum Anzeigen der "javaclass" Attribute in der CONDITION.
 * 
 * @author mm
 * @version $Id: JavaclassConditionDetailPanel.java,v 1.3 2004/01/16 16:32:36 mm Exp $
 */
public class JavaclassConditionDetailPanel extends JPanel {

	private JPanel topPanel;
	private JTextField tfJavaclass;
	private CONDITION condition;

	public JavaclassConditionDetailPanel() {
		
		super(new BorderLayout());

		this.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Javaclass Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		//erzeuge neues JPanel mit Layout für die Felder
		topPanel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		
		JLabel leftText;
		
		//linke Seite erstellen
		c.gridx = 0;
		c.gridy = 0;
		leftText = new JLabel("javaclass:");
		topPanel.add(leftText, c);
		
		//rechte Seite erstellen
		c.gridx = 1;
		c.gridy = 0;
		tfJavaclass = new JTextField(20);
		topPanel.add(tfJavaclass, c);
		
		add(topPanel, BorderLayout.CENTER);
	}

	/**
	 * @return Returns the tfJavaclass.
	 */
	public String getTfJavaclass() {
		return condition.getJavaclass();
	}

	/**
	 * @param tfJc
	 *            The textfield tfJavaclass to set.
	 */
	public void setTfJavaclass(String tfJc) {
		this.tfJavaclass.setText(tfJc);
		condition.setJavaclass(tfJc);
	}

	/**
	 * @param condition
	 *            The CONDITION object to set.
	 */
	public void setCondition(CONDITION condition) {
		this.condition = condition;
		setTfJavaclass(condition.getJavaclass());
	}

	/**
	 * @return Returns the CONDITION object.
	 */
	public CONDITION getCondition() {
		return condition;
	}
}
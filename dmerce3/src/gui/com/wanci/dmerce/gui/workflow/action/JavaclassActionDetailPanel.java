/*
 * Created on Jan 6, 2004
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
import com.wanci.dmerce.workflow.xmlbridge.TRANSITION;

/**
 * Klasse zum Anzeigen der "javaclass" Attribute der ACTION.
 * 
 * @author mm
 * @version $Id: JavaclassActionDetailPanel.java,v 1.3 2004/01/16 16:32:37 mm Exp $
 */
public class JavaclassActionDetailPanel extends JPanel {

	private JPanel topPanel;
	private JTextField tfJavaclass;
	private TRANSITION transition;
	private ACTION action;

	public JavaclassActionDetailPanel() {
		
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
	 * @return Returns the textfield tfJavaclass.
	 */
	public String getTfJavaclass() {
		return action.getJavaclass();
	}

	/**
	 * @param tfJc
	 *            The textfield tfJavaclass to set.
	 */
	public void setTfJavaclass(String tfJc) {
		this.tfJavaclass.setText(tfJc);
		action.setJavaclass(tfJc);
	}

	/**
	 * @param action
	 *            The ACTION object to set.
	 */
	public void setAction(ACTION action) {
		this.action = action;
		setTfJavaclass(action.getJavaclass());
	}

	/**
	 * @return Returns the ACTION object.
	 */
	public ACTION getAction() {
		return action;
	}
}
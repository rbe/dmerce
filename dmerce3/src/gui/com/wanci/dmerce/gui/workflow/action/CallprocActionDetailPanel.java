/*
 * Created on Feb 3, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package com.wanci.dmerce.gui.workflow.action;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.wanci.dmerce.gui.tablemodel.FormfieldTableModel;
import com.wanci.dmerce.workflow.xmlbridge.ACTION;
import com.wanci.dmerce.workflow.xmlbridge.FORMFIELD;
import com.wanci.dmerce.workflow.xmlbridge.PARAMETER;

/**
 * @author mm
 * @version $Id: CallprocActionDetailPanel.java,v 1.1 2004/02/03 15:14:28 mm Exp $
 */
public class CallprocActionDetailPanel extends JPanel {

	private JPanel parameterPanel;
	private JTextField tfName;
	private JTextField tfValue;
	private FormfieldTableModel tableModel;
	private JTable table;
	private List formfieldList;
	private ACTION action;
	private FORMFIELD formfield;
	private PARAMETER parameter;

	/**
	 * GridBagLayout setzen und die einzelnen Tabellenfelder erzeugen und
	 * beschriften.
	 */
	public CallprocActionDetailPanel() {

		super(new BorderLayout());

		//erzeuge neues JPanel mit Layout für die Felder
		JPanel parameterPanel = new JPanel(new GridBagLayout());
		parameterPanel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Parameter Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		
		JLabel leftText;
		
		//linke Seite erstellen
		//name Feld links
		c.gridx = 0;
		c.gridy = 0;
		leftText = new JLabel("Name der Stored Procedure:");
		parameterPanel.add(leftText, c);
		
		//rechte Seite erstellen
		//name Feld rechts
		c.gridx = 1;
		c.gridy = 0;
		tfName = new JTextField(20);
		parameterPanel.add(tfName, c);
		
		add(parameterPanel, BorderLayout.NORTH);

		//Formfield Attribute
		JPanel formfieldPanel = new JPanel();
		formfieldPanel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("FORMFIELD Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		//neues TableModel erzeugen
		tableModel = new FormfieldTableModel();
		table = new JTable(tableModel);
		table.setPreferredScrollableViewportSize(new Dimension(450, 100));
		//Bildlaufleiste hinzufügen
		JScrollPane scrollPane = new JScrollPane(table);
		formfieldPanel.add(scrollPane);
		add(formfieldPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * @return Returns the textfield tfName.
	 */
	public String getTfName() {
		return parameter.getValue();
	}

	/**
	 * @param tfType
	 *            The textfield tfType to set.
	 */
	public void setTfName(String tfName) {
		this.tfName.setText(tfName);
		this.parameter.setValue(tfName);
	}

	/**
	 * @param parameter
	 *            The PARAMETER object to set.
	 */
	public void setParameter(PARAMETER parameter) {
		this.parameter = parameter;
		setTfName(parameter.getName());
	}

	/**
	 * @return Returns the PARAMETER object.
	 */
	public PARAMETER getParameter() {
		return parameter;
	}

	/**
	 * @param formfieldList
	 *            The List of FORMFIELD to set.
	 */
	public void setFormfieldList(List formfieldList) {
		tableModel.setFormfieldList(formfieldList);
	}
}
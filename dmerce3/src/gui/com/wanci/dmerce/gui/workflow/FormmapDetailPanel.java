/*
 * Created on Dec 5, 2003
 */
package com.wanci.dmerce.gui.workflow;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wanci.dmerce.workflow.xmlbridge.FORMMAP;

/**
 * Klasse zum Anzeigen der Formmap Attribute.
 * Hier wird auch ein Button zum Anzeigen der Fieldmap Attribute erzeugt. 
 * 
 * @author mm
 * @version $Id: FormmapDetailPanel.java,v 1.3 2004/01/16 16:32:37 mm Exp $
 */
public class FormmapDetailPanel extends JPanel {

	private JTextField tfTable;
	private JTextField tfPrimarykey;
	private FORMMAP formmap;
	private JButton bFieldmap;
	private List fieldmapList;
	
	private FieldmapDetailPanel fieldmapPanel;

	public FormmapDetailPanel() {
		super(new GridBagLayout());

		this.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Formmap Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;

		//table Feld links
		c.gridx = 0;
		c.gridy = 0;
		JLabel leftText = new JLabel("table:");
		add(leftText, c);
		
		//primarykey Feld links
		c.gridx = 0;
		c.gridy = 1;
		leftText = new JLabel("primarykey:");
		add(leftText, c);

		//table Feld rechts
		c.gridx = 1;
		c.gridy = 0;
		tfTable = new JTextField(20);
		add(tfTable, c);
		
		//primarykey Feld rechts
		c.gridx = 1;
		c.gridy = 1;
		tfPrimarykey = new JTextField(20);
		add(tfPrimarykey, c);
		
		//Button erzeugen, damit die fieldmap Attribute
		//angezeigt werden.
		fieldmapPanel = new FieldmapDetailPanel();
		
		//TODO: wie bekomme ich JButton ganz nach rechts?
		c.gridx = 1;
		c.gridy = 2;
		c.fill = GridBagConstraints.REMAINDER;
		bFieldmap = new JButton("show fieldmaps");
		bFieldmap.addActionListener(new ActionListener() {

			/**
			 * wenn der Button gedrückt wird, wird die Klasse
			 * FieldmapDetailPanel aufgerufen und in einem neuen
			 * Fenster angezeigt.
			 */
			public void actionPerformed(ActionEvent e) {
				fieldmapPanel.setFieldmapList(fieldmapList);
				fieldmapPanel.setSize(new Dimension(400, 400));
				//fieldmapPanel.setLocationRelativeTo(mf);
				fieldmapPanel.pack();
				fieldmapPanel.show();
			}
		});
		add(bFieldmap, c);
	}

	/**
	 * @param formmap
	 *            The FORMMAP object to set.
	 */
	public void setFormmap(FORMMAP formmap) {
		this.formmap = formmap;
		setTable(formmap.getTable());
		setPrimarykey(formmap.getPrimarykey());
	}

	/**
	 * @return Returns the textfield tfTable.
	 */
	public String getTable() {
		return formmap.getTable();
	}

	/**
	 * @param tfTable
	 *            The textfield tfTable to set.
	 */
	public void setTable(String table) {
		this.tfTable.setText(table);
		this.formmap.setTable(table);
	}

	/**
	 * @return Returns the textfield tfPrimarykey.
	 */
	public String getPrimarykey() {
		return formmap.getPrimarykey();
	}

	/**
	 * @param tfPrimarykey
	 *            The textfield tfPrimarykey to set.
	 */
	public void setPrimarykey(String primarykey) {
		this.tfPrimarykey.setText(primarykey);
		this.formmap.setPrimarykey(primarykey);
	}

	/**
	 * @param fieldmapList
	 *            The List of FIELDMAP objects to set
	 */
	public void setFieldmapList(List fieldmapList) {
		this.fieldmapList = fieldmapList;
		fieldmapPanel.setFieldmapList(fieldmapList);
	}
}
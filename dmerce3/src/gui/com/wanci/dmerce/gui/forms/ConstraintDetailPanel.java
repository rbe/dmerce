/*
 * Created on Dec 3, 2003
 */
package com.wanci.dmerce.gui.forms;

import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.wanci.dmerce.gui.tablemodel.ConstraintsTableModel;

/**
 * Im rechten Hauptfenster unter dem CardLayout. Wenn das CONSTRAINTS-Objekt
 * nicht null ist wird dieses Panel vom FieldDetailPanel aufgerufen und
 * angezeigt.
 * 
 * @author mm
 * @version $Id: ConstraintDetailPanel.java,v 1.3 2004/01/14 14:12:00 mm Exp $
 */
public class ConstraintDetailPanel extends JPanel {

	private ConstraintsTableModel tableModel;
	
	/**
	 * Konstruktor erzeugt eine neue Tabelle in der die Werte von Description,
	 * Type und Value angezeigt werden.
	 */
	public ConstraintDetailPanel() {

		super();
		this.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Constraint Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		//neues TableModel erzeugen.
		tableModel = new ConstraintsTableModel();
		JTable table = new JTable(tableModel);
		table.setPreferredScrollableViewportSize(new Dimension(450, 100));
		//Bildlaufleiste hinzufügen
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setAutoscrolls(true);
		add(scrollPane);
	}

	/**
	 * @param constraintList
	 *            The List of CONSTRAINT objects to set.
	 */
	public void setConstraintList(List constraintList) {
		tableModel.setConstraintList(constraintList);
	}
}
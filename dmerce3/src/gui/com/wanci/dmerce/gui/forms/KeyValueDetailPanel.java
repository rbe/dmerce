/*
 * Created on Dec 2, 2003
 */
package com.wanci.dmerce.gui.forms;

import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.wanci.dmerce.gui.tablemodel.OptionsTableModel;

/**
 * Im rechten Hauptfenster im CardLayout. Wenn das OPTIONS-Objekt
 * key-value-Attribute beinhaltet, wird dieses Panel vom FieldDetailPanel
 * aufgerufen und angezeigt.
 * 
 * @author mm
 * @version $Id: KeyValueDetailPanel.java,v 1.4 2004/01/14 14:12:00 mm Exp $
 */
public class KeyValueDetailPanel extends JPanel {

	private OptionsTableModel tableModel;
	
	/**
	 * Konstruktor erzeugt eine neue Tabelle in der die key-value-Werte
	 * angezeigt werden.
	 */
	public KeyValueDetailPanel() {
		
		super();
		this.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Key-Value Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		//neues TableModel erzeugen.
		tableModel = new OptionsTableModel();
		JTable table = new JTable(tableModel);
		table.setPreferredScrollableViewportSize(new Dimension(450, 100));
		//Bildlaufleiste hinzufügen.
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setAutoscrolls(true);
		add(scrollPane);
	}

	/**
	 * @param options
	 *            The List of OPTIONS to set.
	 */
	public void setOptionList(List optionList) {
		tableModel.setOptionList(optionList);
	}
}
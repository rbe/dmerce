/*
 * Created on Jan 7, 2004
 */
package com.wanci.dmerce.gui.workflow.action;

import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.wanci.dmerce.gui.tablemodel.FormfieldTableModel;
import com.wanci.dmerce.workflow.xmlbridge.ACTION;
import com.wanci.dmerce.workflow.xmlbridge.FORMFIELD;

/**
 * Klasse zum Anzeigen der FORMFIELD Attribute aus der ACTION.
 * 
 * @author mm
 * @version $Id: FormfieldActionDetailPanel.java,v 1.1 2004/01/09 09:47:21 mm
 *          Exp $
 */
public class FormfieldActionDetailPanel extends JPanel {

	private FormfieldTableModel tableModel;

	private JTable table;

	private List formfieldList;
	
	private ACTION action;

	private FORMFIELD formfield;

	/**
	 * GridBagLayout setzen und die einzelnen Tabellenfelder erzeugen und
	 * beschriften.
	 */
	public FormfieldActionDetailPanel() {

		super();
		this.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("FORMFIELD Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		//neues TableModel erzeugen
		tableModel = new FormfieldTableModel();
		table = new JTable(tableModel);
		table.setPreferredScrollableViewportSize(new Dimension(450, 100));
		//Bildlaufleiste hinzufügen
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);
	}

	/**
	 * @param formfieldList
	 *            The List of FORMFIELD to set.
	 */
	public void setFormfieldList(List formfieldList) {
		tableModel.setFormfieldList(formfieldList);
	}
}
/*
 * Created on Jan 14, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package com.wanci.dmerce.gui.tablemodel;

import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.wanci.dmerce.workflow.xmlbridge.FORMFIELD;

/**
 * Mit diesem TableModel werden die key-value Werte aus der forms.xml in einer
 * Tabelle angezeigt.
 * 
 * @author mm
 * @version $Id: FormfieldTableModel.java,v 1.2 2004/01/16 16:32:37 mm Exp $
 */
public class FormfieldTableModel extends AbstractTableModel {

	private List formfieldList;

	public FormfieldTableModel() {
	}

	/**
	 * @return gibt die Anzahl der Spalten zurück.
	 */
	public int getColumnCount() {
		return 3;
	}

	/**
	 * @return gibt die Anzahl der Zeilen zurück.
	 */
	public int getRowCount() {
		if (formfieldList == null)
			return 0;
		else
			return formfieldList.size();
	}

	/**
	 * @return gibt den Wert in der entsprechenden Spalte und Zeile zurück.
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {

		FORMFIELD formfield = null;
		Iterator iterator = formfieldList.iterator();
		for (int i = 0; i - 1 < rowIndex; i++) {
			formfield = (FORMFIELD) iterator.next();
		}
		if (columnIndex == 0)
			return formfield.getPageid();
		else if (columnIndex == 1)
			return formfield.getFormid();
		else
			return formfield.getName();
	}

	/**
	 * @return gibt den Spaltennamen zurück.
	 */
	public String getColumnName(int columnIndex) {
		int i = columnIndex % 3;
		if (i == 0)
			return "pageid";
		else if (i == 1)
			return "formid";
		else
			return "name";
	}

	/**
	 * @param formfieldList
	 *            The FORMFIELD List to set. Informiert alle Listener, daß sich
	 *            ein oder mehrere Werte in der Tabelle geändert haben könnten.
	 */
	public void setFormfieldList(List formfieldList) {
		this.formfieldList = formfieldList;
		fireTableDataChanged();
	}
}
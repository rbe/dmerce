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

import com.wanci.dmerce.forms.CONSTRAINT;

/**
 * Mit diesem TableModel werden die CONSTRAINT Werte aus der forms.xml in einer
 * Tabelle angezeigt.
 * 
 * @author mm
 * @version $Id: ConstraintsTableModel.java,v 1.1 2004/01/14 14:12:00 mm Exp $
 */
public class ConstraintsTableModel extends AbstractTableModel {

	private List constraintList;

	public ConstraintsTableModel() {
	}

	/**
	 * @param rowIndex
	 */
	public void setType(int rowIndex) {
		constraintList.get(rowIndex);
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
		if (constraintList == null)
			return 0;
		else
			return constraintList.size();
	}

	/**
	 * @return gibt den Wert in der entsprechenden Spalte und Zeile zurück.
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {

		CONSTRAINT constraint = null;
		Iterator iterator = constraintList.iterator();
		for (int i = 0; i - 1 < rowIndex; i++) {
			constraint = (CONSTRAINT) iterator.next();
		}
		if (columnIndex == 0)
			return constraint.getType();
		else if (columnIndex == 1)
			return constraint.getValue();
		else
			return constraint.getDescription();
	}

	/**
	 * @return gibt den Spaltennamen zurück.
	 */
	public String getColumnName(int columnIndex) {
		int i = columnIndex % 3;
		if (i == 0)
			return "type";
		else if (i == 1)
			return "value";
		else
			return "description";
	}

	/**
	 * @param constraintList
	 *            The constraintList to set. Informiert alle Listener, daß sich
	 *            ein oder mehrere Werte in der Tabelle geändert haben könnten.
	 */
	public void setConstraintList(List constraintList) {
		this.constraintList = constraintList;
		fireTableDataChanged();
	}
}
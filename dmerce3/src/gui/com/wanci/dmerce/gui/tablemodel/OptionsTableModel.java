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

import com.wanci.dmerce.forms.OPTION;

/**
 * Mit diesem TableModel werden die key-value Werte aus der forms.xml in einer
 * Tabelle angezeigt.
 * 
 * @author mm
 * @version $Id: OptionsTableModel.java,v 1.1 2004/01/14 14:12:00 mm Exp $
 */
public class OptionsTableModel extends AbstractTableModel {

	private List optionList;

	public OptionsTableModel() {
	}

	/**
	 * @return gibt die Anzahl der Spalten zurück.
	 */
	public int getColumnCount() {
		return 2;
	}

	/**
	 * @return gibt die Anzahl der Zeilen zurück.
	 */
	public int getRowCount() {
		if (optionList == null) {
			return 0;
		} else {
			return optionList.size();
		}
	}

	/**
	 * @return gibt den Wert in der entsprechenden Spalte und Zeile zurück.
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {

		OPTION opt = null;
		Iterator iterator = optionList.iterator();
		for (int i = 0; i - 1 < rowIndex; i++) {
			opt = (OPTION) iterator.next();
		}
		if (columnIndex == 0)
			return opt.getKey();
		else
			return opt.getValue();
	}

	/**
	 * @return gibt den Spaltennamen zurück.
	 */
	public String getColumnName(int columnIndex) {
		int i = columnIndex % 2;
		if (i == 0) {
			return "key";
		} else {
			return "value";
		}
	}

	/**
	 * @param options
	 *            The List of OPTIONS to set. Informiert alle Listener, daß
	 *            sich ein oder mehrere Werte in der Tabelle geändert haben
	 *            könnten.
	 */
	public void setOptionList(List optionList) {
		this.optionList = optionList;
		fireTableDataChanged();
	}
}
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

import com.wanci.dmerce.workflow.xmlbridge.FIELDMAP;

/**
 * Mit diesem TableModel werden die fieldmap Werte aus der workflows.xml in einer
 * Tabelle angezeigt.
 * 
 * @author mm
 * @version $Id: FieldmapTableModel.java,v 1.1 2004/01/14 14:12:00 mm Exp $
 */
public class FieldmapTableModel extends AbstractTableModel {

	private List fieldmapList;

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
		if (fieldmapList == null)
			return 0;
		else
			return fieldmapList.size();
	}

	/**
	 * @return gibt den Wert in der entsprechenden Spalte und Zeile zurück.
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		FIELDMAP fm = null;
		Iterator iterator = fieldmapList.iterator();
		for (int i = 0; i - 1 < rowIndex; i++) {
			fm = (FIELDMAP) iterator.next();
		}
		if (columnIndex == 0)
			return fm.getFormfield();
		else
			return fm.getDbfield();
	}

	/**
	 * @return gibt den Spaltennamen zurück.
	 */
	public String getColumnName(int columnIndex) {
		int i = columnIndex % 2;
		if (i == 0) {
			return "formfield";
		} else {
			return "dbfield";
		}
	}

	/**
	 * @param fieldmapList
	 *            The fieldmapList to set. Informiert alle Listener, daß sich
	 *            ein oder mehrere Werte in der Tabelle geändert haben könnten.
	 */
	public void setFieldmapList(List fieldmapList) {
		this.fieldmapList = fieldmapList;
		fireTableDataChanged();
	}
}
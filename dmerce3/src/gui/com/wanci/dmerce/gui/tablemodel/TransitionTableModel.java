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

import com.wanci.dmerce.workflow.xmlbridge.TRANSITION;

/**
 * Mit diesem TableModel werden die transition Werte aus der workflows.xml in
 * einer Tabelle angezeigt.
 * 
 * @author mm
 * @version $Id: TransitionTableModel.java,v 1.1 2004/01/14 14:12:00 mm Exp $
 */
public class TransitionTableModel extends AbstractTableModel {

	private List transitionList;

	public TransitionTableModel() {
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
		if (transitionList == null)
			return 0;
		else
			return transitionList.size();
	}

	/**
	 * @param rowIndex
	 * @return
	 */
	public Object getValueAtIndex(int rowIndex) {

		TRANSITION t = null;
		//System.out.println(table.getClass().getName());
		Iterator iterator = transitionList.iterator();
		for (int i = 0; i <= rowIndex; i++) {
			t = (TRANSITION) iterator.next();
		}
		return t;
	}

	/**
	 * @return gibt den Wert in der entsprechenden Spalte und Zeile zurück.
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {

		TRANSITION t = null;
		Iterator iterator = transitionList.iterator();
		for (int i = 0; i - 1 < rowIndex; i++) {
			t = (TRANSITION) iterator.next();
		}
		if (columnIndex == 0)
			return t.getName();
		else if (columnIndex == 1)
			return t.getTarget();
		else if (t.isValidation())
			return "true";
		else
			return "false";
	}

	/**
	 * @return gibt den Spaltennamen zurück.
	 */
	public String getColumnName(int columnIndex) {
		int i = columnIndex % 3;
		if (i == 0)
			return "name";
		else if (i == 1)
			return "target";
		else
			return "validation";
	}

	/**
	 * @param transitionList
	 *            The List of TRANSITION objects to set. Informiert alle
	 *            Listener, daß sich ein oder mehrere Werte in der Tabelle
	 *            geändert haben könnten.
	 */
	public void setTransitionList(List transitionList) {
		this.transitionList = transitionList;
		fireTableDataChanged();
	}
}
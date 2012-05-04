package com.wanci.dmerce.gui.monitor;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.wanci.dmerce.gui.util.MonitorConstants;

/**
 * TabellenModell für gefilterte Tabelleneinträge.
 * Anzeige einer Teilmenge von Zeilen aus der Gesamtmenge.
 * @author Ron Kastner
 */
public class FilterTableModel implements TableModel {

	/** index mit anzuzeigenden Zeilen */
	private ArrayList indices = new ArrayList();
	
	/** datenModell */
	private DefaultTableModel model;
	
	/** Filter der Tabelle */
	private TableFilter f = null;
	
	public int getRowCount() {
		
		if (indices == null) {
			return 0;
			/*if (model != null)
				return model.getRowCount();
			else 
				return -1;
				*/
		}
		
		return indices.size();
	}
	
	public FilterTableModel(Object[] cols) { 
		setModel(new DefaultTableModel(cols, 0)); 
	}




	/**
	 * Lösche die gesamten Daten des Tabellenmodells.
	 */
	public void clear() {
		indices.clear();
		model.getDataVector().removeAllElements();
		model.fireTableChanged(new TableModelEvent(this));
	}




	/**
	 * Benachrichtigung: Filter ist verändert worden,
	 * Modell neu anpassen.
	 */
	public void filterChanged() {
		createIndexVector();
	}


	/**
	 * Überschriebene Methode aus DefaultTableModel
	 * [PENDING] eigentlich müsste man hier alle Servicemethoden
	 * von DefaultTableModel "wrappen", aber der LogTable verwendet nur
	 * diese hier.
	 * @param	v	java.util.Vector
	 */
	public void addRow(Vector v) {
		
		// so wird verhindert das der Table eine UpdateEvent feuert bevor die
		// neue Zeile im FilterIndex eingetragen wurde...
		// model.addRow(v);
		model.getDataVector().addElement(v);
		
		addToIndexVector(model.getRowCount()-1);
		model.fireTableChanged(new TableModelEvent(this));	
		
		Monitor.getSingleInstance().setStatus("Einträge: "+ indices.size()+ " / "+ model.getRowCount() );				
	}
	
	public void addRow(Map m) {
		
		Vector v = new Vector();
		
		v.add( m.get( MonitorConstants.TAG_DATE) );
		v.add( m.get( MonitorConstants.TAG_CLIENT ) );
		v.add( m.get( MonitorConstants.TAG_PROXY) );
		v.add( m.get( MonitorConstants.TAG_TEMPLATE) );
		v.add( m.get( MonitorConstants.TAG_MODULE) );
		v.add( m.get( MonitorConstants.TAG_MESSAGE) );
		v.add( m.get( MonitorConstants.TAG_MSGTYPE) );
		
		addRow( v );		
	}
		
	/**
	 * Setze Filter für die Tabelle
	 * @param	f	TableFilter	 
	 */
	public void setFilter(TableFilter f) {
		this.f = f;
	}


	/**
	 * Liefere den aktuellen Filter der Tabelle zurück
	 * @param	f	TableFilter	 
	 */
	public TableFilter getFilter() { return f; }
	
	public void setModel(DefaultTableModel model) {
		
		this.model = model;
		
		createIndexVector();
	}
	
	private void createIndexVector() {
		
		indices.clear();
		
		//
		// Zeilenweise abgleich mit Filterbedingungen 
		// vornehmen
		//
		for (int i=0; i< model.getRowCount(); i++) 
			addToIndexVector(i);		
			
		//model.fireTableChanged(new TableModelEvent(this));
		model.fireTableDataChanged();
	}
	
	/**
	 * Prüft ob eine Zeile den Filterbedingungen genügt und fügt
	 * sie ggfs. hinzu.
	 * @param	row	int
	 */
	private void addToIndexVector(int row) {
		
		if (f.displayRow((Vector)model.getDataVector().elementAt(row))) {
			indices.add(new Integer(row));	
		}
		
	}	
	
	/** LogTable ist nicht editierbar */
	public boolean isCellEditable(int rowIndex,int columnIndex) { return false; }
	public void addTableModelListener(TableModelListener l) { model.addTableModelListener(l); }
	public void removeTableModelListener(TableModelListener l) { model.removeTableModelListener(l); }
	public int getColumnCount() { return model.getColumnCount(); }	
	public String getColumnName(int columnIndex) { return model.getColumnName(columnIndex); }
	public Class getColumnClass(int columnIndex) { return model.getColumnClass(columnIndex); }
	
	
	public Object getValueAt(int rowIndex,int columnIndex) {
		if (f == null) {
			return model.getValueAt(rowIndex,columnIndex);
		}
		else
			return model.getValueAt( ((Integer)indices.get(rowIndex)).intValue(),columnIndex);
	}


	public void setValueAt(Object aValue,int rowIndex,int columnIndex) {
		if (f == null || indices == null) {
			model.setValueAt(aValue,rowIndex,columnIndex);
		}
		else
			model.setValueAt( aValue,((Integer)indices.get(rowIndex)).intValue(),columnIndex);
	}
	
}
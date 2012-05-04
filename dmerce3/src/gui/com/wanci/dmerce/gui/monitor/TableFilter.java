package com.wanci.dmerce.gui.monitor;
import java.util.Vector;
/**
 * Schnittstelle für Tabellen deren Zeilen
 * gefiltert werden können (Anzeige einer Teilmenge
 * der Gesamtdaten).
 * @author	Ron Kastner
 */
public interface TableFilter {
		
	/**
	 * Überprüfe den Inhalt der Zeile und
	 * liefere zurück, ob sie angezeigt werden soll.	 
	 * @param	row	java.util.Vector
	 * @return	boolean
	 */
	public boolean displayRow(Vector row);	
	public void setCondition(int col,int rule,Object[] obj);
	public void removeCondition(int col);
	public void clearAllConditions();
	
}
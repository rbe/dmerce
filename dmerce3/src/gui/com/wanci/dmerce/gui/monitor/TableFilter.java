package com.wanci.dmerce.gui.monitor;
import java.util.Vector;
/**
 * Schnittstelle f�r Tabellen deren Zeilen
 * gefiltert werden k�nnen (Anzeige einer Teilmenge
 * der Gesamtdaten).
 * @author	Ron Kastner
 */
public interface TableFilter {
		
	/**
	 * �berpr�fe den Inhalt der Zeile und
	 * liefere zur�ck, ob sie angezeigt werden soll.	 
	 * @param	row	java.util.Vector
	 * @return	boolean
	 */
	public boolean displayRow(Vector row);	
	public void setCondition(int col,int rule,Object[] obj);
	public void removeCondition(int col);
	public void clearAllConditions();
	
}
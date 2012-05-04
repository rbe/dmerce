package com.wanci.dmerce.gui.util;

import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import com.wanci.dmerce.gui.ApplHelper;
/**
 * Verwaltet die möglichen Einträge der Filter
 * @author Ron Kastner
 */
public class FilterDictionary implements MonitorConstants {
	
	 /** Dictionarys für die möglichen Filtereinträge */
	 protected static HashMap globalMap;
	 
	 protected static HashMap dateMap = new HashMap();
	 protected static HashMap clientMap = new HashMap();
	 protected static HashMap proxyMap = new HashMap();
	 protected static HashMap templateMap = new HashMap();
	 protected static HashMap moduleMap = new HashMap();
	
	/**
	 * Zugriff auf die Hashmap, die alle anderen verwaltet.
	 */
	private static HashMap getGlobalMap() {
		
		// lazy Instanziierung
		if (globalMap == null) {
			globalMap = new HashMap();
			globalMap.put(TAG_DATE,dateMap);
			globalMap.put(TAG_CLIENT,clientMap);
			globalMap.put(TAG_PROXY,proxyMap);
			globalMap.put(TAG_TEMPLATE,templateMap);
			globalMap.put(TAG_MODULE,moduleMap);			
		}
		
		return globalMap;				
	}
 
	/**
	 * Lege einen Wert in der Zugehörigen HashMap ab
	 */ 
	public static void put(String key,Object value) {
		try {
			// zur Zeit wird nur der Key als Value bei der konkreten HashMap 
			// benutzt, da keine spezifischen Values gebraucht werden
			((HashMap)getGlobalMap().get(key)).put(value,"");
		}
		catch (Exception e) {
			if (ApplHelper.DEBUG) e.printStackTrace();			
		}
	}
	
	/**
	 * Aktualisiere die FilterMap. Dies geschieht normalerweise
	 * nach dem Vorbelegen des Filters durch den Setting-Dialog
	 * @param	props		java.util.Properties
	 */
	public static void updateFilterMaps(Properties props) {
		
		insertIntoMap(props,clientMap,TAG_CLIENT);
		insertIntoMap(props,dateMap,TAG_DATE);
		insertIntoMap(props,proxyMap,TAG_PROXY);
		insertIntoMap(props,templateMap,TAG_TEMPLATE);
		insertIntoMap(props,moduleMap,TAG_MODULE);
	}

	private static void insertIntoMap(Properties props,HashMap m,String arg) {
		
		String value = (String)props.get(arg);
		
		if (!(value == null || value.length() == 0))
			m.put(value,"");
		
	}
	
	/**
	 * Liefere Liste für Sucheinträge für eine Spalte der LogTabelle zurück.
	 * @param	col	int
	 * @return	java.util.Vector
	 */
	public static Vector getSortedEntries(int col) {
		
		Vector v = null;
		
		switch (col) {
			case COL_DATE: 
				v = new Vector(dateMap.keySet());
				break;
				
			case COL_CLIENT: 
				v = new Vector(clientMap.keySet());
				break;
				
			case COL_PROXY: 
				v = new Vector(proxyMap.keySet());
				break;
				
			case COL_TEMPLATE: 
				v = new Vector(templateMap.keySet());
				break;
				
			case COL_MODULE: 
				v = new Vector(moduleMap.keySet());
				break;
				
			case COL_MSGTYPE:
				v = new Vector();
				v.add(MSGTYPE_ACCOUNT);
				v.add(MSGTYPE_DEBUG);
				v.add(MSGTYPE_ERROR);
				v.add(MSGTYPE_INFO);
				v.add(MSGTYPE_WARNING);
		}

		if (v != null)		
			// füge Sonderfall (alle anzeigen) hinzu...
			v.insertElementAt(ApplHelper.getLocString("filter.entry.all.text"),0);
			
		return v;
	}	
}


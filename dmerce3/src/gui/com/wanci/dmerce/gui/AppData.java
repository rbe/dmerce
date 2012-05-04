/*
 * Datei angelegt am 15.01.2004
 */
package com.wanci.dmerce.gui;

import java.util.ArrayList;
import java.util.Iterator;

import com.wanci.dmerce.gui.listener.AppDataListener;

/**
 * AppData
 *
 * @author Masanori Fujita
 */
public class AppData {
	
	protected static AppData instance;
	protected ArrayList appDataListener;
	
	private AppData() {
		appDataListener = new ArrayList();
	}
	
	/**
	 * Statischer Konstruktor. Wird automatisch aufgerufen, wenn die VM gestartet wird.
	 */
	static {
		instance = new AppData();
	}
	
	public static AppData getInstance() {
		return instance;
	}
	
	public void addAppDataListener(AppDataListener listener) {
		if (!appDataListener.contains(listener))
			appDataListener.add(listener);
	}
	
	/**
	 * Diese Methode muss aufgerufen werden, wenn eine Datenänderung in der
	 * Anwendung propagiert werden soll.
	 */
	public void fireAppDataChanged() {
		Iterator it = appDataListener.iterator();
		while (it.hasNext()) {
			AppDataListener listener = (AppDataListener) it.next();
			listener.dataChanged();
		}
	}
	
}
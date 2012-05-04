/*
 * Created on Feb 5, 2004
 */
package com.wanci.dmerce.gui.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Ein Dateifilter, der dazu dient, nur xml Dateien mit der GUI
 * aufzurufen.
 * 
 * @author mm
 * @version $Id: XmlFileFilter.java,v 1.1 2004/02/05 16:30:22 mm Exp $
 */
public class XmlFileFilter extends FileFilter {

	public final static String xml = "xml";

	/**
	 * Testet, ob es eine Datei und nicht ein Verzeichnis ist.
	 */
	public boolean accept(File f) {

		if (f.isDirectory()) {
			return true;
		}

		String extension = getExtension(f);
		if (extension != null) {
			if (extension.equals(xml)) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	/**
	 * Die Beschreibung des FileFilters.
	 */
	public String getDescription() {

		return "Nur xml Dateien sind erlaubt.";
	}

	/**
	 * Die Dateiendung holen.
	 */
	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}
}
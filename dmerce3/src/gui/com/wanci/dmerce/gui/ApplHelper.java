package com.wanci.dmerce.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import com.wanci.dmerce.gui.util.BaseAction;

/**
 * Serviceklasse f�r das Einlesen
 * dynamischer Men�s aus einem Property-File
 * @author	Ron Kastner
 */
public class ApplHelper {

	//protected final String RES_PATH = "dmerce"+File.separatorChar+"monitor"+File.separatorChar+"resources"+File.separatorChar;
	/** Konstante f�r Betriebssystem FileSeparator */
	protected static final char SYM = File.separatorChar;

	/** Konstante f�r den Pfad f�r Ressourcen */
	//public static final String RESOURCE_PATH = "dmerce"+SYM+"monitor"+SYM+"client"+SYM+"resources"+SYM;
	//public static final String RESOURCE_PATH = ""+SYM;//dmerce"+SYM+"monitor"+SYM+"client"+SYM+"resources"+SYM;
	public static final String RESOURCE_PATH =
		"com" + SYM + "wanci" + SYM + "dmerce" + SYM + "res" + SYM;

	/** Anzeige von Fehlermeldungen */
	public final static boolean DEBUG = false;

	/** Dictionary f�r User-bezogene Einstellungen */
	private static Properties preset_props;

	/** Dictionary f�r internationalisierte Texte */
	private static ResourceBundle loc_bundle;

	/** Dictionary f�r die internationalisierten Men�-Eintr�ge */
	private static ResourceBundle menu_bundle;

	/** Mappings f�r alle �bergreifenden Aktion der Anwendung (kein asynchroner Zugriff) */
	private static HashMap actionMap = new HashMap();

	/** 
	 * Registriere eine Action in der Mappings.
	 * @param	key	java.lang.String
	 *		Schl�sselwort, mit dem die Action angemeldert werden soll
	 * @param	action	javax.swing.AbstractAction
	 *		die eigentliche Action
	 */
	public static void registerAction(String key, AbstractAction action) {
		actionMap.put(key, action);
	}

	/**
	 * Liefere eine registrierte Action, die durch ein Schl�sselwort
	 * definiert ist, zur�ck. Ist die Action nicht registriert, so 
	 * wird <b>null</b> zur�ckgeliefert.<p>
	 * @return AbstractAction
	 *		die registrierte Action zum key-Wert
	 * @param	key	java.lang.String
	 *		key-Wert f�r die zu suchende Action
	 */
	public static AbstractAction getAction(String key) {
		try {
			return (AbstractAction) actionMap.get(key);
		}
		catch (ClassCastException e) {
			if (DEBUG)
				e.printStackTrace();
			return null;
		}
	}

	/**
	 * Liefere ein Bild mit dem Key aus dem ResourceBundle zur�ck.
	 * Dabei wird versucht das Bild auf verschiedene Arten zu laden.
	 * <ul>
	 * <li>Standardverfahren �ber <tt>new ImageIcon()</tt></li>
	 * <li>�ber einen Stream (n�tig bei Ressourcen Jar-Files)</li>
	 * </ul>
	 * @param	arg	String
	 * @return Icon
	 */
	public static Icon getIcon(String arg) {

		// Separator f�r Bilder in JAR-File erg�nzen
		String filename = "/" + arg;

		// Bild als Ressource laden
		return new ImageIcon(ApplHelper.class.getResource(filename));

	}

	/**
	 * Liefere eine registrierte Action, die durch ein Schl�sselwort
	 * definiert ist, zur�ck. Ist die Action nicht registriert, so 
	 * wird <b>null</b> zur�ckgeliefert.<p>
	 * @return AbstractAction
	 *		die registrierte Action zum key-Wert
	 * @param	key	java.lang.String
	 *		key-Wert f�r die zu suchende Action
	 */
	public static void registerPopupMenu(String key, JComponent comp) {

		JPopupMenu p = new JPopupMenu();
		buildMenu(p, menu_bundle.getString(key));
		p.setInvoker(comp);
	}
	/**
	 * Erzeuge eine Menuleiste aus einem Property-File und
	 * liefere sie zur�ck.<p>
	 * @param	key	java.lang.String
	 *		Schl�sselwort f�r den Eintrag der MenuBar
	 * @return 	javax.swing.JMenuBar
	 *		aus dem Propertyfile erzeuge Men�leiste
	 */
	public static JMenuBar getMenuBar(String key) {

		// Men�leiste definieren		
		JMenuBar m = new JMenuBar();

		// Rekursive Aufschl�sselung der Men�-Inhalte
		buildMenu(m, menu_bundle.getString(key));

		// ... und zur�ckliefern
		return m;
	}

	/**
	 * Erzeuge Men� (rekursiv).
	 * @param	comp	javax.swing.JComponent
	 *		die Parent-Component des Men�s
	 * @param	key	java.lang.String
	 *		Key des/der Men�s
	 * @param	bundle	java.util.ResourceBundle
	 *		Abbildung mit Men�eintr�gen
	 */
	private static void buildMenu(JComponent comp, String key) {

		//
		// ggfs. Loop �ber Untermen�s
		//
		StringTokenizer st = new StringTokenizer(key);

		while (st.hasMoreTokens()) {
			// Eintrag aus ResourceBundle holen
			// wenn es ein Men� ist dann hat es weitere unterpunkte...
			String token = "" + st.nextToken();
			try {
				String entry = menu_bundle.getString(token);
				String text = menu_bundle.getString(token + ".text");
				char mn = menu_bundle.getString(token + ".mnemonic").charAt(0);
				JMenu menu = new JMenu(text);
				menu.setMnemonic(mn);
				comp.add(menu);
				buildMenu(menu, entry);
			}
			catch (MissingResourceException mre) {
				// Untereintr�ge wurde nicht gefunden... also ist dieser Eintrag ein MenuItem
				buildMenuItem(comp, token);
			}
		}
	}

	/**
	 * Erzeuge Men�eintrag.<p>
	 * @param	comp	javax.swing.JComponent
	 *		die Parent-Component des Eintrages
	 * @param	key	java.lang.String
	 *		Key des Eintrages
	 * @param	bundle	java.util.ResourceBundle
	 *		Abbildung mit Men�eintr�gen
	 */
	private static void buildMenuItem(JComponent comp, String key) {

		if (key.equals("-")) {
			// Sonderfall, nur Trennstrich einf�gen...
			comp.add(new JSeparator());
		}
		else {
			JMenuItem item = new JMenuItem();

			//
			// Text setzen
			//
			try {
				String text = menu_bundle.getString(key + ".text");
				item.setText(text);
			}
			catch (MissingResourceException e) {
				if (DEBUG)
					e.printStackTrace();
			}

			//
			// ggfs Mnemonic setzen
			//
			try {
				char mn = menu_bundle.getString(key + ".mnemonic").charAt(0);
				item.setMnemonic(mn);
			}
			catch (MissingResourceException e) {
				// do nothing
			}

			//
			// ggfs Accelerator setzen
			//
			try {
				String val = menu_bundle.getString(key + ".accelerator");
				item.setAccelerator(KeyStroke.getKeyStroke(val));
			}
			catch (MissingResourceException e) {
				// do nothing
			}

			//
			// ggfs Action setzen
			//
			try {
				String val = menu_bundle.getString(key + ".action");
				BaseAction action = (BaseAction) getAction(val);
				action.setComponent(item);
				if (action != null) {
					item.addActionListener(action);
				}
			}
			catch (MissingResourceException e) {
				// do nothing
			}

			comp.add(item);
		} // Ende Else-Bedingung
	}

	/**
	 * Lese Key-Value-Paar Werte f�r die applikationsweiten
	 * internationalisierten GUI-Texte. Auf diese kann
	 * dann �ber <code>getLocString()</code>
	 * zugegriffen werden.<p>
	 * @param	filename	java.lang.String
	 *		Name des Resourcebundles
	 */
	public static void loadGUIBundle(String filename) {
		//loc_bundle = ResourceBundle.getBundle(filename);
		loc_bundle = ResourceBundle.getBundle(filename);
		//loc_bundle = ResourceBundle.getBundle( ResourceBundle.class.getResource( filename ).toString() );
	}

	/**
	 * Lese Key-Value-Paare f�r die applikationsweiten
	 * interntionalisierten Men�-Eintr�ge
	 * @param	filename	java.lang.String
	 *		Name des Resourcebundles
	 */
	public static void loadMenuBundle(String filename) {
		// erzeuge ResourceBundle f�r die Men�eintrage
		//menu_bundle = ResourceBundle.getBundle(filename);
		menu_bundle = ResourceBundle.getBundle(filename);
		//menu_bundle = ResourceBundle.getBundle( filename, Locale.GERMANY, ApplHelper.class.getClassLoader()  );
	}

	/**
	 * Liefere den internationalisierten String f�r diesen key zur�ck
	 * @return java.lang.String
	 *		internationalisierter GUI-String
	 * @param	key	java.lang.String
	 */
	public static String getLocString(String key) {
		return getFromResourceBundle(key, loc_bundle);
	}

	/** 
	 * Hole einen String aus einem ResourceBundle und liefere
	 * ihn zur�ck.<p>
	 * @return java.lang.String
	 * @param	key	java.lang.String
	 * @param	b		java.util.ResourceBundle
	 */
	private static String getFromResourceBundle(String key, ResourceBundle b) {
		try {
			return b.getString(key);
		}
		catch (MissingResourceException e) {
			if (DEBUG)
				e.printStackTrace();
			return "unknown Resource: " + key;
		}
		catch (NullPointerException ne) {
			if (DEBUG)
				ne.printStackTrace();
			return "- no data -";
		}
	}

	/**
	 * Lese Key-Value-Paar Werte f�r die applikationsweiten
	 * Einstellungen ein. Diese werden in der statischen Hashmap
	 * gespeichert auf die �ber <code>getPreset()</code> und 
	 * <code>getIntPreset()</code> zugegriffen werden kann.<p>
	 * @param filename java.lang.String Name der Einstellungsdatei
	 */
	public static void loadPresets(String filename) {
		preset_props = new Properties();
		try {
			//preset_props.load(new FileInputStream("/"+filename+".properties"));
			preset_props.load(
				ApplHelper.class.getResourceAsStream(
					"/" + filename + ".properties"));

		}
		catch (IOException io) {
			if (DEBUG)
				io.printStackTrace();
		}
	}

	/**
	 * Speichere die Einstellungen in einer Property-Datei
	 * @param filename java.lang.String
	 *		der Dateiname (wird um .properties erg�nzt)
	 */
	public static void storePresets(String filename) {
		try {
			preset_props.store(
				new FileOutputStream(filename + ".properties"),
				"Preset-File for Monitor-Application");
		}
		catch (IOException io) {
			if (DEBUG)
				io.printStackTrace();
		}
	}

	/**
	 * Liefere die Voreinstellung zu dem �bergenen key als int-Wert zur�ck.
	 * @param key	java.lang.String
	 *		Name der Voreinstellung
	 * @return int
	 *    Preset als int-Wert
	 */
	public static int getIntPreset(String key) {

		try {
			return Integer.parseInt(getPreset(key));
		}
		catch (MissingResourceException e) {
			if (DEBUG)
				e.printStackTrace();
			return -1;
		}
	};

	/**
	 * Liefere die Voreinstellung zu dem �bergenen key als String-Wert zur�ck.
	 * @param key
	 *		Name der Voreinstellung
	 * @return int
	 */
	public static String getPreset(String key) {
		return preset_props.getProperty(key);
	}

	/**
	 * Liefer eine Resource aus einem Jar-File zur�ck
	 * @return 
	 */
/*
	public static Object getResourceFromJar(String name) {

		//    ClassLoader cl = System.getClassLoader();
		//  InputStream input = cl.getResourceAsStream(name);		

		//		int bytesize = input.available();
		//byte[] b = new byte[bytesize];

		//  		input.read
		//BufferedReader in = new BufferedReader(new InputStreamReader(input));        
		//    InputStreamReader input = new 

		return null;
	}
*/
	/**
	 * Liefere die Voreinstellung zu dem �bergenen key als int-Wert zur�ck.
	 * @param key	java.lang.String
	 *		Name der Voreinstellung
	 * @return boolean
	 *    Preset als boolean-Wert
	 */
	public static boolean getBooleanPreset(String key) {
		return getPreset(key).equalsIgnoreCase("true");
	}

	public static void setPreset(String key, Object value) {
		preset_props.put(key, value);
	}
}
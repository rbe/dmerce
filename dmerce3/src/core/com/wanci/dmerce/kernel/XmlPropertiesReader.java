/*
 * DmerceProperties.java
 * 
 * Created on January 6, 2003, 2:05 PM
 */
package com.wanci.dmerce.kernel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import org.jdom.Element;
import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @author mm
 * @version $Id: XmlPropertiesReader.java,v 1.26 2004/03/11 22:06:55 rb Exp $
 * 
 * Liest, verwaltet und lässt die properties.xml abfragen.
 *  
 */
public class XmlPropertiesReader extends XmlConfigReader {

    /**
     * Singleton design pattern variable
     */
    private static XmlPropertiesReader singletonObject = null;

    /**
     *  
     */
    //private Properties sysProps = System.getProperties();
    /**
     * Deklarierung von FileInputStream
     */
    private FileInputStream fin;

    /**
     * Enthält ein Array von den Properties. Nur zum testen benutzt.
     */
    public String[] loadedFiles;

    /**
     * Erzeugt eine Array mit den System Properties aus einer Xml Datei
     */
    private String[] filenames = new String[] {
            "/com/wanci/dmerce/res/properties.xml", "./properties.xml",
            System.getProperty("user.home") + "/properties.xml",
            "../res/properties.xml"
    };

    /**
     * Creates a new instance of DmerceProperties
     */
    private XmlPropertiesReader() throws XmlPropertiesFormatException {

        loadXmlFiles();
        if (!read())
            throw new XmlPropertiesFormatException(
                    "No properties.xml was read!");

        DEBUG = getPropertyAsBoolean("debug");
        DEBUG2 = getPropertyAsBoolean("core.debug");

    }

    /**
     * singleton pattern
     * 
     * @return singletonObject
     */
    public static synchronized XmlPropertiesReader getInstance()
            throws XmlPropertiesFormatException {

        if (singletonObject == null)
            singletonObject = new XmlPropertiesReader();

        return singletonObject;

    }

    /**
     * reads a property from the read xml-file
     * 
     * @param name
     * @return
     */
    public String getProperty(String name) {

        Element root = document.getRootElement();
        List l = root.getChildren("property");

        Iterator iterator = l.iterator();
        while (iterator.hasNext()) {

            Element el = (Element) iterator.next();
            if (el.getAttributeValue("name").equals(name))
                return el.getAttributeValue("value");

        }

        return "";

    }

    /**
     * Gibt ein Property, dass mit dem Parameter name gesucht wird als Int
     * wider
     * 
     * @param name
     * @return Int
     */
    public int getPropertyAsInt(String name) {

        int i = 0;
        String str = (String) getProperty(name);

        if (str != "") {

            try {
                i = Integer.parseInt(str);
            }
            catch (Exception e) {
            }

        }

        return i;

    }

    /**
     * Gibt ein Property, dass mit dem Parameter name gesucht wird als Boolean
     * wider
     * 
     * @param name
     * @return
     */
    public boolean getPropertyAsBoolean(String name) {

        String str = (String) getProperty(name);
        if (str.equals("true") || str.equals("1"))
            return true;
        else
            return false;

    }

    /**
     * Sucht die properties.xml in den Verzeichnissen:
     * 
     * $USER_DIR/dmerce/properties.xml $DMERCE_HOME/etc/properties.xml
     * /etc/dmerce/properties.xml
     * 
     * und lädt die Datei, die als erstes gefunden wird
     *  
     */
    public void loadXmlFiles() {
        boolean found = false;
        for (int i = 0; i < filenames.length; i++) {
            String urlstring = filenames[i];
            LangUtil.consoleDebug(DEBUG, "Ressource Nr. " + i + ": "
                    + urlstring);
            Class dummyClass = null;
            try {
                dummyClass = Class
                        .forName("com.wanci.dmerce.res.ClassLoaderDummy");
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            InputStream inputStream = dummyClass.getResourceAsStream(urlstring);
            if (inputStream != null) {
                LangUtil.consoleDebug(DEBUG,
                        "Versuche auf Ressource zuzugreifen.");
                setXmlResource(inputStream);
                found = true;
                break;
            }
        }
        if (!found) {
            for (int i = 0; i < filenames.length; i++) {
                try {
                    String urlstring = filenames[i];
                    fin = new FileInputStream(new File(urlstring));
                    fin.close();
                    setXmlResource(urlstring);
                    found = true;
                    break;
                }
                catch (FileNotFoundException fne) {
                    LangUtil.consoleDebug(DEBUG, "Did not find file: "
                            + filenames[i] + " absolute: "
                            + new File(filenames[i]).getAbsolutePath());
                }
                catch (IOException ioe) {
                    LangUtil.consoleDebug(DEBUG, "I/O: " + ioe.getMessage());
                }
            }
        }
    }

    /**
     * Prüft die Property, ob diese existiert anhand des Attributes. Der
     * Attribute Wert muss mit dem angegebenen Parameter name übereinstimmen,
     * damit true widergegeben wird.
     * 
     * @param name
     * @return
     */
    public boolean propertyExists(String name) {
        Element root = document.getRootElement();
        List l = root.getChildren("property");
        Iterator iterator = l.iterator();
        while (iterator.hasNext()) {
            Element el = (Element) iterator.next();
            if (el.getAttributeValue("name").equals(name))
                return true;
        }
        return false;
    }

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        XmlPropertiesReader d;
        try {
            d = new XmlPropertiesReader();
            d.getProperty("");
        }
        catch (XmlPropertiesFormatException e) {
            e.printStackTrace();
        }
        /*
         * System.out.print("Loading dmerce.properties from:"); String[] lf =
         * d.loadedFiles; if (lf.length > 0) { for (int i = 0; i < lf.length;
         * i++) System.out.print(" " + lf[i]); System.out.println("\n\nDumping
         * all properties"); UtilUtil.dumpProperties(d.getDmerceProperties());
         * System.out.println("\n\nDumping Sys properties");
         * UtilUtil.dumpProperties(d.getSysProperties());
         * System.out.println("\n\nDumping NCC properties");
         * UtilUtil.dumpProperties(d.getNccProperties()); System.out.println(
         * "-->" + d.getNccMonitorInetService("localhost", "http", "ports"));
         * d.getNccMonitorInetServers(); System.out.println("\n\nDumping
         * System.properties:");
         * UtilUtil.dumpProperties(System.getProperties()); } else {
         * System.out.println("No dmerce.properties found!");
         */
    }
}
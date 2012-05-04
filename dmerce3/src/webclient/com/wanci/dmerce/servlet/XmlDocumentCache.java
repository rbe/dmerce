/*
 * Created on Sep 1, 2003
 */
package com.wanci.dmerce.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.wanci.dmerce.exceptions.DmerceException;
import com.wanci.dmerce.forms.Forms;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.dmerce.workflow.xmlbridge.Workflows;
import com.wanci.java.LangUtil;

/**
 * Caching Klasse für die xml Documente, die von dem Dmerce Servlet und den
 * Taglibs benötigt werden. Diese Klasse wird automatisch beim Start der VM
 * aufgerufen.
 * 
 * @author mm
 * @version $Id: XmlDocumentCache.java,v 1.28 2004/03/15 11:52:16 rb Exp $
 */
public class XmlDocumentCache {

    private boolean DEBUG = false;

    private boolean DEBUG2 = false;

    private static XmlDocumentCache instance = null;

    /**
     * Workflows-JAXB-Objekt
     */
    private Workflows workflows;

    /**
     * Forms-JAXB-Objekt
     */
    private Forms forms;

    private Document security_xml;

    /**
     * Konstruktorm der vom statischen Konstruktor aufgerufen wird
     */
    private XmlDocumentCache() throws DmerceException {

        LangUtil.consoleDebug(DEBUG2, this, "Initializing");

        try {

            DEBUG = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
                "debug");
            DEBUG2 = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
                "core.debug");

            readForms();
            readWorkflows();
            readSecurity();

        }
        catch (Exception e) {

            if (DEBUG2)
                e.printStackTrace();

            throw new DmerceException(e);

        }

        LangUtil.consoleDebug(DEBUG2, this, "Initialized");

    }

    /**
     * @return Forms-Object
     * @throws JAXBException
     */
    public Forms getForms() {
        return forms;
    }

    /**
     * Gibt das gechachte JDOM-Document der security.xml zurück.
     */
    public Document getSecurity() {
        return security_xml;
    }

    /**
     * @return Workflows-Object
     * @throws JAXBException
     */
    public Workflows getWorkflows() {
        return workflows;
    }

    /**
     * Liest die forms.xml Datei aus der dmerce.war im Tomcat-Home-Verzeichnis
     * unter webapps aus und setzt das forms-Objekt
     */
    private void readForms() throws JAXBException {

        JAXBContext jc = JAXBContext.newInstance("com.wanci.dmerce.forms");

        // Unmarshaller erzeugen
        Unmarshaller u = jc.createUnmarshaller();
        // Unmarshalling
        InputStream inputstream = null;

        try {

            inputstream = Class
                    .forName("com.wanci.dmerce.res.ClassLoaderDummy")
                    .getResourceAsStream("../res/forms.xml");

        }
        catch (ClassNotFoundException e) {
            String message = "Cannot find forms.xml";
            LangUtil.consoleDebug(DEBUG, message);
            throw new JAXBException(message + ": " + e.getMessage());
        }

        if (inputstream == null) {
            LangUtil.consoleDebug(DEBUG2, this, "inputstream == null!");
            throw new JAXBException("Cannot read forms.xml");
        }

        forms = (Forms) u.unmarshal(inputstream);

    }

    /**
     * Liest die security.xml Datei aus der dmerce.war aus.
     */
    private void readSecurity() throws ClassNotFoundException, JDOMException,
            IOException {

        InputStream inputStream = Class.forName(
            "com.wanci.dmerce.res.ClassLoaderDummy").getResourceAsStream(
            "../res/security.xml");
        SAXBuilder builder = new SAXBuilder();
        security_xml = builder.build(inputStream);
        inputStream.close();

    }

    /**
     * Liest die workflows.xml Datei aus der dmerce.war im
     * Tomcat-Home-Verzeichnis unter webapps aus und setzt das Workflows-Object
     */
    private void readWorkflows() throws JAXBException {

        JAXBContext jc = JAXBContext
                .newInstance("com.wanci.dmerce.workflow.xmlbridge");
        // Unmarshaller erzeugen
        Unmarshaller u = jc.createUnmarshaller();
        // Unmarshalling
        InputStream inputstream;
        try {
            inputstream = Class
                    .forName("com.wanci.dmerce.res.ClassLoaderDummy")
                    .getResourceAsStream("../res/workflows.xml");
        }
        catch (ClassNotFoundException e) {
            throw new JAXBException(e.getMessage());
        }

        if (inputstream == null) {
            System.out.println("DEBUG-Info: inputstream darf nicht null sein.");
            throw new JAXBException(
                    "XmlDocumentCache: workflows.xml konnte nicht gefunden werden.");
        }

        workflows = (Workflows) u.unmarshal(inputstream);

    }

    /**
     * statischer Konstruktor ruft Konstruktor auf
     */
    static {

    }

    /**
     * getWorkflowErrorLine nutzt den XmlValidator um die Zeile zurückzugeben,
     * in der der Fehler bzw. die Xml- Datei nicht wohlgeformt ist.
     * 
     * @return String wfErrorLine mit der Fehlermeldung für die GUI.
     */
    public static String getWorkflowErrorLine() {

        XmlValidator checkWorkflow = new XmlValidator();
        String wfErrorLine = "";

        try {

            wfErrorLine = checkWorkflow.getErrorLine(Class.forName(
                "com.wanci.dmerce.res.ClassLoaderDummy").getResourceAsStream(
                "../res/workflows.xml"), "workflows.xml");

        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return wfErrorLine;

    }

    /**
     * getFormsErrorLine nutzt den XmlValidator um die Zeile zurückzugeben, in
     * der der Fehler bzw. die Xml-Datei nicht wohlgeformt ist.
     * 
     * @return String fErrorLine mit der Fehlermeldung für die GUI.
     */
    public static String getFormsErrorLine() {

        XmlValidator checkForms = new XmlValidator();
        String fErrorLine = "";

        try {

            fErrorLine = checkForms.getErrorLine(Class.forName(
                "com.wanci.dmerce.res.ClassLoaderDummy").getResourceAsStream(
                "../res/forms.xml"), "forms.xml");

        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return fErrorLine;

    }

    /**
     * Gibt eine Singleton-Instanz des XmlDocumentCache zurück.
     * 
     * @return Singleton-Instanz dieser Klasse
     */
    public static XmlDocumentCache getInstance() throws DmerceException {

        if (instance == null)
            instance = new XmlDocumentCache();

        return instance;

    }

}
/*
 * Created on Jan 20, 2004
 */
package com.wanci.dmerce.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.swing.JFileChooser;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.wanci.dmerce.forms.Forms;
import com.wanci.dmerce.gui.util.XmlFileFilter;
import com.wanci.dmerce.servlet.XmlValidator;
import com.wanci.dmerce.workflow.xmlbridge.Workflows;

/**
 * Klasse, in der ein FileChooser erzeugt wird, damit das GUI gestartet
 * werden kann. Ohne ausgewählte forms.xml und workflows.xml wird die
 * GUI nicht gestartet.
 * 
 * @author mm
 * @version $Id: XmlFileOpener.java,v 1.4 2004/02/05 16:54:21 mm Exp $
 */
public class XmlFileOpener {

	/**
	 * Workflows-JAXB-Objekt
	 */
	private Workflows workflows;

	/**
	 * Forms-JAXB-Objekt
	 */
	private Forms forms;

	/**
	 * File object
	 */
	private static File file;

	/**
	 * JFileChooser object
	 */
	private static JFileChooser fileChooser;
	
	public XmlFileOpener() {
	}

	/**
	 * Liest die workflows.xml Datei aus der dmerce.war im
	 * Tomcat-Home-Verzeichnis unter webapps aus und setzt das Workflows-Object
	 * 
	 * @param path Der Pfad der workflows.xml.
	 */
	public Workflows readWorkflows(InputStream workflowsXml) throws JAXBException {
		JAXBContext jc =
			JAXBContext.newInstance("com.wanci.dmerce.workflow.xmlbridge");
		// Unmarshaller erzeugen
		Unmarshaller u = jc.createUnmarshaller();
		// Unmarshalling

		if (workflowsXml == null) {
			System.out.println("DEBUG-Info: workflows.xml: inputstream darf nicht null sein.");
			throw new JAXBException("XmlFileOpener: workflows.xml konnte nicht gefunden werden.");
		}
		else
			return (Workflows) u.unmarshal(workflowsXml);
	}

	/**
	 * Liest die forms.xml Datei aus der dmerce.war im Tomcat-Home-Verzeichnis
	 * unter webapps aus und setzt das forms-Objekt
	 * 
	 * @param path Der Pfad der forms.xml.
	 */
	public Forms readForms(InputStream formsXml) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance("com.wanci.dmerce.forms");
		// Unmarshaller erzeugen
		Unmarshaller u = jc.createUnmarshaller();
		// Unmarshalling

		if (formsXml == null) {
			System.out.println("DEBUG-Info: forms.xml: inputstream darf nicht null sein.");
			throw new JAXBException("XmlFileOpener: forms.xml konnte nicht gefunden werden.");
		}
		else
			return (Forms) u.unmarshal(formsXml);
	}

	/**
	 * Erzeugt ein Dateiauswahlfenster.
	 * 
	 * @param mainframe The Mainframe Object to set.
	 * @return Returns a InputStream object.
	 */
	public static InputStream createFileChooser(Mainframe mainframe) {

		InputStream inputstream = null;
		
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.addChoosableFileFilter(new XmlFileFilter());
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		int returnVal = fileChooser.showDialog(mainframe, "Datei öffnen");

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
			//file im Mainframe setzen, damit der Pfad für die forms.xml
			//oder workflows.xml für das automatische öffnen benutzt werden kann.
			mainframe.setFile(file);
			try {
				inputstream = new FileInputStream(file.getPath());
			} catch (FileNotFoundException e) {
				System.out.println("Datei nicht gefunden!");
			}
		} else {
			return null;
		}
		return inputstream;

	}

	/**
	 * getFormsErrorLine nutzt den XmlValidator um die Zeile zurückzugeben, in
	 * der der Fehler bzw. die Xml-Datei nicht wohlgeformt ist.
	 * 
	 * @return String fErrorLine mit der Fehlermeldung für die GUI.
	 */
	public static String getFormsErrorLine(InputStream is) {
		XmlValidator checkForms = new XmlValidator();
		String fErrorLine =
			checkForms.getErrorLine(is,"forms.xml");
		return fErrorLine;
	}

	/**
	 * getWorkflowErrorLine nutzt den XmlValidator um die Zeile zurückzugeben,
	 * in der der Fehler bzw. die Xml- Datei nicht wohlgeformt ist.
	 * 
	 * @return String wfErrorLine mit der Fehlermeldung für die GUI.
	 */
	public static String getWorkflowErrorLine(InputStream is) {
		XmlValidator checkWorkflow = new XmlValidator();
		String wfErrorLine =
			checkWorkflow.getErrorLine(is, "workflows.xml");
		return wfErrorLine;
	}

}
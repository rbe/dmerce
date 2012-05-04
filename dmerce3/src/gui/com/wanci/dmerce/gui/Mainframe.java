/*
 * Mainframe.java
 * 
 * Created on November 4, 2003, 2:55 PM
 */

package com.wanci.dmerce.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;

import com.wanci.dmerce.forms.Forms;
import com.wanci.dmerce.gui.actions.CloseMainframeAction;
import com.wanci.dmerce.gui.forms.FieldDetailPanel;
import com.wanci.dmerce.gui.forms.FormDetailPanel;
import com.wanci.dmerce.gui.listener.FormTreeSelectionListener;
import com.wanci.dmerce.gui.listener.WorkflowTreeSelectionListener;
import com.wanci.dmerce.gui.tree.FormTreeModel;
import com.wanci.dmerce.gui.tree.WorkflowTreeModel;
import com.wanci.dmerce.gui.workflow.FieldmapDetailPanel;
import com.wanci.dmerce.gui.workflow.FormmapDetailPanel;
import com.wanci.dmerce.gui.workflow.PageDetailPanel;
import com.wanci.dmerce.gui.workflow.TransitionDetailPanel;
import com.wanci.dmerce.workflow.xmlbridge.Workflows;
;

/**
 * @author mm
 * @version $Id: Mainframe.java,v 1.33 2004/02/17 16:49:19 mm Exp $
 */

public class Mainframe extends JFrame {

	// Variables declaration
	private JPanel treePanel;
	private JPanel buttonPanel;
	private JPanel detailPanel;
	private JScrollPane scrollPane;
	private JTabbedPane tabbedPane;
	private JTree wfTree;
	private JTree formTree;
	private JSplitPane splitPane;

	private PageDetailPanel pagePanel;
	private ClearDetailPanel clearPanel;
	private FormmapDetailPanel formmapPanel;
	private FieldmapDetailPanel fieldmapPanel;
	private TransitionDetailPanel transitionPanel;
	private FormDetailPanel formPanel;
	private FieldDetailPanel fieldPanel;

	private JFileChooser fileChooser;
	private InputStream formsStream;
	private InputStream workflowsStream;

	private Forms forms;
	private Workflows workflows;

	private CloseMainframeAction closeMainframeAction;

	private Mainframe mainframe;
	private JInternalFrame mainInternalFrame;

	private static boolean documentChanged = false;
	private File file;

	/**
	 * Erzeugt ein neues Fenster mit den Menüpunkten.
	 */
	private Mainframe() {

		setTitle("[1Ci] - dmerce3 gui");

		mainframe = this;

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				closeMainframeAction = new CloseMainframeAction(mainframe);
				closeMainframeAction.actionPerformed(evt);
			}
		});

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		MenuMaker menu = new MenuMaker(mainframe);

		JMenuBar menubar = new JMenuBar();
		menubar.add(menu.createFileMenu());
		setJMenuBar(menubar);

		//Größe des gesamten Fensters.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(
			(screenSize.width - 800) / 2,
			(screenSize.height - 600) / 2,
			800,
			600);
	}

	/**
	 * Ruft lediglich die Methode initComponents auf.
	 * 
	 * @param inputstream
	 *            The InputStream object to set.
	 */
	public void initXmlFiles(InputStream inputstream) {

		initComponents(inputstream);
	}

	/**
	 * Diese Methode öffnet die forms.xml und die workflows.xml
	 * 
	 * @param inputstream
	 *            The InputStream object to set.
	 */
	private void initComponents(InputStream inputstream) {

		//Ein internes Fenster, welches resizable, closable, maximizable ist 
		mainInternalFrame = new JInternalFrame("forms & workflows", true, true, true);

		//Dateiauswahlmenü zum Aussuchen der gewünschten forms und workflows Datei
		XmlFileOpener fileOpener;
		
		/*
		 * Erzeugung von JFileChooser um die forms und workflows.xml Datei
		 * auszuwählen.
		 */
		try {
			fileOpener = new XmlFileOpener();

			if (file.toString().endsWith("forms.xml")) {

				InputStream wfis = getWorkflowsInputstream(file);
				forms = fileOpener.readForms(inputstream);
				workflows = fileOpener.readWorkflows(wfis);

			} if (file.toString().endsWith("workflows.xml")) {
				
				InputStream fis = getFormsInputstream(file);
				forms = fileOpener.readForms(fis);
				workflows = fileOpener.readWorkflows(inputstream);
			}

			/*
			 * Karteireiter für forms.xml und workflows.xml erstellen und nach
			 * unten setzen
			 */
			tabbedPane = new JTabbedPane();
			tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);

			/*
			 * für linkes Panel TreeModel erstellen
			 */
			//Erzeugung eines FormTreeModels
			FormTreeModel model = new FormTreeModel(forms);
			//neues JTree für forms erstellen
			formTree = new JTree(model);
			formTree.setEditable(true);
			formTree.setBorder(new EtchedBorder(Color.gray, Color.darkGray));

			/*
			 * Bildlauffläche im JTree erstellen und in der TabbedPane adden
			 */
			//für forms.xml
			scrollPane =
				new JScrollPane(
					formTree,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			tabbedPane.addTab("forms.xml", scrollPane);

			/*
			 * TreeSelectionListener für forms.xml instanzieren sobald ein
			 * Knoten/Feld im JTree betätigt wird, wird eine Aktion ausgeführt.
			 */
			FormTreeSelectionListener ftsl =
				new FormTreeSelectionListener(this);
			formTree.addTreeSelectionListener(ftsl);

			//Erzeugung eines WorkflowTreeModels
			WorkflowTreeModel wfModel = new WorkflowTreeModel(workflows);
			//neues JTree für workflows erstellen
			wfTree = new JTree(wfModel);
			wfTree.setEditable(true);
			wfTree.setBorder(new EtchedBorder(Color.gray, Color.darkGray));

			//für workflows.xml
			scrollPane =
				new JScrollPane(
					wfTree,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			tabbedPane.addTab("workflows.xml", scrollPane);

			/*
			 * TreeSelectionListener für workflows.xml instanzieren sobald ein
			 * Knoten/Feld im JTree betätigt wird, wird eine Aktion ausgeführt.
			 */
			WorkflowTreeSelectionListener wtsl =
				new WorkflowTreeSelectionListener(this);
			wfTree.addTreeSelectionListener(wtsl);

			//ChangeListener: wenn die tabbedPane gewechselt wird
			tabbedPane.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {

				}
			});

			/*
			 * linkes HauptPanel für die Baumstruktur
			 */
			//neues JPanel mit einem BorderLayout
			treePanel = new JPanel();
			treePanel.setLayout(new BorderLayout());
			treePanel.setPreferredSize(new Dimension(280, 300));
			treePanel.add(tabbedPane, BorderLayout.CENTER);

			/*
			 * rechtes HauptPanel mit Tabellenlayout für die Details
			 */
			//forms.xml Panels
			formPanel = new FormDetailPanel();
			fieldPanel = new FieldDetailPanel();
			//workflows.xml Panels
			pagePanel = new PageDetailPanel(mainInternalFrame);
			transitionPanel = new TransitionDetailPanel(mainInternalFrame);
			//Panel ohne Attribute, leer
			clearPanel = new ClearDetailPanel();

			detailPanel = new JPanel();
			detailPanel.setLayout(new BorderLayout());

			//Buttons zum rechten HauptPanel adden
			//TODO: buttonPanel wird aus dem Speicher entfernt sobald
			//TODO: ein neuer Knoten aktiviert wird!
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
			buttonPanel.add(new ButtonPanel("edit"));
			buttonPanel.add(new ButtonPanel("add"));
			detailPanel.add(buttonPanel, BorderLayout.SOUTH);

			//showPageDetails();

			/*
			 * SplitPane erstellen, damit man die Breite des Fensters dynamisch
			 * ändern kann linke Seite: treePanel (Baumstruktur) rechte Seite:
			 * detailPanel (Die Attribute aus der forms bzw. workflows.xml
			 */
			splitPane = new JSplitPane();
			splitPane.setLeftComponent(treePanel);
			splitPane.setRightComponent(detailPanel);
			splitPane.setOneTouchExpandable(true);
			
			mainInternalFrame.getContentPane().add(splitPane);

			getContentPane().add(mainInternalFrame, BorderLayout.CENTER);

		} catch (JAXBException e) {

			if (!XmlFileOpener
				.getFormsErrorLine(formsStream)
				.endsWith("forms.xml")) {
				JOptionPane.showMessageDialog(
					null,
					XmlFileOpener.getWorkflowErrorLine(workflowsStream),
					"Fehler in Xml-Datei",
					JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			} else {
				JOptionPane.showMessageDialog(
					null,
					XmlFileOpener.getFormsErrorLine(formsStream),
					"Fehler in Xml-Datei",
					JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		}
	}

	/**
	 * Erzeugt einen InputStream aus der forms.xml und der workflows.xml
	 * aus dem gleichen Verzeichnis.
	 * 
	 * @param file
	 *            The file object to set.
	 * @return InputStream of the file.
	 */
	private InputStream getFormsInputstream(File file) {

		//forms aus dem gleichen Verzeichnis öffnen!
		String strFile =
			file.toString().substring(
				0,
				file.toString().lastIndexOf("workflows.xml"));

		strFile = strFile.concat("forms.xml");
		InputStream fInputstream = null;
		try {
			fInputstream = new FileInputStream(strFile);
		} catch (FileNotFoundException e) {
			System.out.println("Datei nicht gefunden!");
		}
		return fInputstream;
	}

	/**
	 * Erzeugt einen InputStream aus der workflows.xml und der forms.xml
	 * aus dem gleichen Verzeichnis.
	 * 
	 * @param file
	 *            The File object to set.
	 * @return InputStream of the file.
	 */
	private InputStream getWorkflowsInputstream(File file) {

		//workflows aus dem gleichen Verzeichnis öffnen!
		String strFile =
			file.toString().substring(
				0,
				file.toString().lastIndexOf("forms.xml"));

		strFile = strFile.concat("workflows.xml");
		InputStream wfInputstream = null;
		try {
			wfInputstream = new FileInputStream(strFile);
		} catch (FileNotFoundException e) {
			System.out.println("Datei nicht gefunden!");
		}
		return wfInputstream;
	}

	/**
	 * workflows.xml pagePanel wird aktualisiert und mit neuen Werten angezeigt
	 */
	public void showPageDetails() {
		//Container leeren, damit die Panels nicht übereinander
		//liegen und die Werte falsch angezeigt werden.
		detailPanel.removeAll();
		detailPanel.add(pagePanel, BorderLayout.CENTER);
		detailPanel.updateUI();
	}

	/**
	 * forms.xml formPanel wird aktualisiert und mit neuen Werten angezeigt
	 */
	public void showFormPanel() {
		detailPanel.removeAll();
		detailPanel.add(formPanel, BorderLayout.CENTER);
		detailPanel.updateUI();
	}

	/**
	 * forms.xml fieldPanel wird aktualisiert und mit neuen Werten angezeigt
	 */
	public void showFieldPanel() {
		detailPanel.removeAll();
		detailPanel.add(fieldPanel, BorderLayout.CENTER);
		detailPanel.updateUI();
	}

	/**
	 * löscht das rechte Fenster, wenn keine Attribute vorhanden sind. Zeigt
	 * dann ein leeres Fenster an.
	 */
	public void clearWindow(String heading) {
		//Container leeren, damit die Panels nicht übereinander
		//liegen und die Werte falsch angezeigt werden.
		detailPanel.removeAll();
		clearPanel = new ClearDetailPanel(heading);
		detailPanel.add(clearPanel, BorderLayout.CENTER);
		detailPanel.updateUI();
	}

	/**
	 * @return Returns the detailPanel. gehört zu workflows.xml
	 */
	public PageDetailPanel getPagePanel() {
		return pagePanel;
	}

	/**
	 * @return Returns the transitionPanel. gehört zu workflows.xml
	 */
	public TransitionDetailPanel getTransitionPanel() {
		return transitionPanel;
	}

	/**
	 * @return Returns the formmapPanel. gehört zu workflows.xml
	 */
	public FormmapDetailPanel getFormmapPanel() {
		return formmapPanel;
	}

	/**
	 * @return Returns the fieldmapPanel. gehört zu workflows.xml
	 */
	public FieldmapDetailPanel getFieldmapPanel() {
		return fieldmapPanel;
	}

	/**
	 * @return Returns the formPanel. gehört zu forms.xml
	 */
	public FormDetailPanel getFormPanel() {
		return formPanel;
	}

	/**
	 * @return Returns the fieldPanel. gehört zu forms.xml
	 */
	public FieldDetailPanel getFieldPanel() {
		return fieldPanel;
	}

	/**
	 * Schließt das Programm mit allen Fenstern.
	 */
	public void exitMainframe() {
		System.exit(0);
	}

	/**
	 * @param changed
	 */
	public static void setDocumentChanged(boolean changed) {
		documentChanged = changed;
	}

	/**
	 * @return
	 */
	public static boolean getDocumentChanged() {
		return documentChanged;
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {

		//rausgenommen, weil man sonst warten muss!
		//zeigt das Logo vor dem Programmstart von 1Ci an :)
		/*
		 * SplashScreen intro = new SplashScreen( "test/1ci_splashscreen.jpg",
		 * "(C) Copyright 2000-2003 1Ci GmbH Münster, All Rights Reserved");
		 * Thread t = new Thread(intro);
		 */

		Mainframe mf = new Mainframe();
		mf.setVisible(true);
	}

	/**
	 * @param file
	 *            The File object to set from xml file.
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return File object of the xml file.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Sets the visibility of the InternalFrame.
	 * 
	 * @param b
	 *            The type of visibility to set.
	 */
	public void setMainInternalFrame(boolean b) {
		mainInternalFrame.setVisible(b);
	}
}
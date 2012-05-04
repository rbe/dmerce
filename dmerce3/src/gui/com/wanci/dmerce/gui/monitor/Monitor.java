package com.wanci.dmerce.gui.monitor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import com.wanci.dmerce.gui.AboutDialog;
import com.wanci.dmerce.gui.ApplHelper;
import com.wanci.dmerce.gui.util.BaseAction;
import com.wanci.dmerce.gui.util.PrintManager;
import com.wanci.dmerce.gui.util.ProcessModel;

/**
 * ClientMonitor für die Verwaltung von Log-Dateien
 * eines zentralen Servers. Als Protokoll wird XML benutzt.<p>
 * 
 *
 * Die Klasse Monitor ist ein Singelton. Die <b>main</b>
 * Methode dieser Klasse startet die gesamte Anwendung.
 * <p>
 * <b>Upcoming Features</b>
 * <ul>
 * <li>Zugriff auf Socket nur nach Authentifizierung</li>
 * <li></li>
 * </ul>
 * 
 * @author Ron Kastner
 * @version 1.1 
 */
public class Monitor {

	/** innerClass: Beenden der Anwendung */
	class ExitAction extends BaseAction {
		public void handleEvent() {
			updatePresets();
			System.exit(0);
		}
	};

	/** innerClass: Anzeige des Einstellungsdialoges */
	class ShowPreferencesAction extends BaseAction {
		public void handleEvent() {
			if (preferenceDialog == null) {
				preferenceDialog =
					new PreferenceDialog(frame, true, Monitor.this);
			}
			preferenceDialog.setVisible(true);
		}
	}

	/** innerClass: TabellenInhalt ins Clipboard stellen */
	class ClipboardAction extends BaseAction implements ClipboardOwner {
		public void lostOwnership(Clipboard clipboard, Transferable contents) {
			// do nothing
		}

		public void handleEvent() {
			// Hier das SystemClipboard benutzen...
			if (ApplHelper.DEBUG)
				System.out.println("Clippe Inhalt der Tabelle...");
			String clipValue =
				ProcessModel.getInstance().convertListToString(
					ProcessModel.getInstance().getTabSeparatedTableEntries(
						logtable));
			StringSelection contents = new StringSelection(clipValue);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				contents,
				this);
		}
	}

	/** innerClass: Anzeige des Einstellungsdialoges */
	class ShowFilterAction extends BaseAction {
		public void handleEvent() {
			if (filterDialog == null) {
				filterDialog = new FilterDialog(frame, true, model);
			}
			filterDialog.setVisible(true);
		}
	}

	/** innerClass: Anzeige des ÜberLogMonitor-Dialoges */
	class ShowAboutDialogAction extends BaseAction {
		public void handleEvent() {
			if (aboutDialog == null) {
				aboutDialog = new AboutDialog(frame, true);
			}
			aboutDialog.setVisible(true);
		}
	}

	/** innerClass: Anzeige des ÜberLogMonitor-Dialoges */
	class PrintAction extends BaseAction {
		public void handleEvent() {
			PrintManager.getInstance().printTable(logtable);
		}
	}

	/** innerClass: Anzeige des ÜberLogMonitor-Dialoges */
	class ClearLogTableAction extends BaseAction {
		public void handleEvent() {

			((TableSorter) logtable.getModel()).clear();
			// Überarbeiten: sehr ineffizient...
			//for (int i=model.getRowCount()-1; i >= 0; i--)
			//	((DefaultTableModel)logtable.getModel()).removeRow(i);

		}
	}

	/** innerClass: Anzeige des ÜberLogMonitor-Dialoges */
	class SaveAsTxtAction extends BaseAction {
		public void handleEvent() {

			int returnVal = openChooser(JFileChooser.SAVE_DIALOG);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					ProcessModel.getInstance().writeTxtFile(
						fileChooser.getSelectedFile(),
						logtable);
				}
				catch (Exception e) {
					if (ApplHelper.DEBUG)
						e.printStackTrace();
				}
			}
			// Note: source for ExampleFileFilter can be found in FileChooserDemo,
			// under the demo/jfc directory in the Java 2 SDK, Standard Edition.
		}
	}

	/** innerClass: Anzeige des ÜberLogMonitor-Dialoges */
	class SaveAsXMLAction extends BaseAction {
		public void handleEvent() {

			int returnVal = openChooser(JFileChooser.SAVE_DIALOG);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					ProcessModel.getInstance().writeXMLFile(
						fileChooser.getSelectedFile(),
						logtable);
				}
				catch (Exception e) {
					if (ApplHelper.DEBUG)
						e.printStackTrace();
				}
			}
			// Note: source for ExampleFileFilter can be found in FileChooserDemo,
			// under the demo/jfc directory in the Java 2 SDK, Standard Edition.
		}
	}

	/** innerClass: Anzeige des Hilfefensters */
	class ShowHelpDialogAction extends BaseAction {
		public void handleEvent() {
			if (helpDialog == null) {
				helpDialog = new HelpDialog(frame, false);
			}
			helpDialog.setVisible(true);
		}
	}

	/** innerClass: Anzeige des Einstellungsdialoges */
	class ReadFromSocketAction extends BaseAction {
		public void handleEvent() {
			//LogManager.openSocketConnnection();
			if (readFromSocketDialog == null) {
				model.clear();
				readFromSocketDialog = new ReadFromSocketDialog(frame, true);
			}
			readFromSocketDialog.setVisible(true);

		}
	}

	/** innerClass: Anzeige des Einstellungsdialoges */
	class OpenLogFromFileAction extends BaseAction {

		public void handleEvent() {

			int returnVal = openChooser(JFileChooser.OPEN_DIALOG);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
					model.clear();
					File f = fileChooser.getSelectedFile();
					ProcessModel.getInstance().parseFile(
						f.getAbsoluteFile().toString());
					// remember directory
					ApplHelper.setPreset(
						"working.dir",
						fileChooser.getCurrentDirectory().toString());
				}
				catch (Exception e) {
					if (ApplHelper.DEBUG)
						e.printStackTrace();
					JOptionPane.showMessageDialog(
						frame,
						ApplHelper.getLocString("error.file.read"),
						"",
						JOptionPane.ERROR_MESSAGE);
					return;
				}

			} // END if		    
		}
	}

	class StartListeningFromSocketAction extends BaseAction {

		public void handleEvent() {
			// 						
			ProcessModel.getInstance().setListening(true);
		}

	}

	class StopListeningFromSocketAction extends BaseAction {

		public void handleEvent() {
			// 						
			ProcessModel.getInstance().setListening(false);

		}

	}

	/**
	 * Öffne Standarddialog und gib Ergebnis (gedrückter Button) als int zurück...
	 * @return	int
	 */
	protected int openChooser(int type) {

		if (fileChooser == null) {
			fileChooser = new JFileChooser();
		}
		// Note: source for ExampleFileFilter can be found in FileChooserDemo,
		// under the demo/jfc directory in the Java 2 SDK, Standard Edition.
		String dir = ApplHelper.getPreset("working.dir");
		if (dir != null || dir.equals("")) {
			fileChooser.setCurrentDirectory(new File(dir));
		}
		if (type == JFileChooser.OPEN_DIALOG)
			return fileChooser.showOpenDialog(frame);
		else
			return fileChooser.showSaveDialog(frame);

	}

	/**
	 * Aktualisiere die Einstellungen
	 */
	private void updatePresets() {
		ApplHelper.setPreset("frame.x", "" + frame.getLocation().x);
		ApplHelper.setPreset("frame.y", "" + frame.getLocation().y);
		ApplHelper.setPreset("frame.width", "" + frame.getWidth());
		ApplHelper.setPreset("frame.height", "" + frame.getHeight());
		//ApplHelper.setPreset("locale",Locale.getDefault().getLanguage());
		ApplHelper.storePresets(ApplHelper.RESOURCE_PATH + "monitor-presets");
	}

	public void updateApplicationUI() {
		updateGUI(frame);
		updateGUI(preferenceDialog);
		updateGUI(filterDialog);
		updateGUI(readFromSocketDialog);
		updateGUI(fileChooser);
		updateGUI(aboutDialog);
		updateGUI(helpDialog);
	}

	private void updateGUI(Component comp) {
		if (comp != null)
			SwingUtilities.updateComponentTreeUI(comp);
	}

	/** Hauptanwendungsfenster */
	private JFrame frame;

	/** Liste der möglichen Filter zur Selektion des LogFile-Inhaltes */
	private JComboBox filterComponent;

	/** Tabelle für die LogInhalte */
	private JTable logtable;

	/** Anzeigekomponente für Statusmeldungen */
	private JLabel statuslabel;

	/** Anzeigekomponente für Statusmeldungen */
	private JLabel connectionlabel;

	public void setConnectionStatus(String msg) {
		connectionlabel.setText(msg);
	}

	protected JDialog preferenceDialog;
	protected JDialog filterDialog;
	protected JDialog readFromSocketDialog;
	protected JFileChooser fileChooser;
	protected JDialog aboutDialog;
	protected JDialog helpDialog;

	/** Modell der Tabelle */
	FilterTableModel model;

	/** Singelton-Instanz */
	private static Monitor singleInstance = null;

	/** DefaultKonstruktor */
	private Monitor() {
	}

	public static Monitor getSingleInstance() {

		if (singleInstance == null)
			singleInstance = new Monitor();

		return singleInstance;
	}

	/** Initialisierung des Anwendungsfensters */
	private void initView() {

		// Voreinstellungen laden
		installPresets();
		installActions();

		Object[] cols =
			new Object[] {
				ApplHelper.getLocString("log.table.column.date"),
				ApplHelper.getLocString("log.table.column.client"),
				ApplHelper.getLocString("log.table.column.proxy"),
				ApplHelper.getLocString("log.table.column.template"),
				ApplHelper.getLocString("log.table.column.module"),
				ApplHelper.getLocString("log.table.column.message"),
				ApplHelper.getLocString("log.table.column.msgtype")};

		//DefaultTableModel model = new DefaultTableModel( cols, 0 );
		model = new FilterTableModel(cols);
		model.setFilter(new DefaultTableFilter());

		TableSorter sorter = new TableSorter(model);

		logtable = new JTable(model);
		logtable.setModel(sorter);
		logtable.getTableHeader().setDefaultRenderer(
			new LogTableHeaderRenderer(sorter));
		logtable.setDefaultRenderer(
			Object.class,
			new CustomTableCellRenderer());
		logtable.getSelectionModel().setSelectionMode(
			ListSelectionModel.SINGLE_SELECTION);
		logtable.setCellSelectionEnabled(false);
		logtable.getTableHeader().setReorderingAllowed(false);

		sorter.addMouseListenerToHeaderInTable(logtable);

		ProcessModel.setTableModel(model);

		//ApplHelper.registerPopupMenu("popup.logdisplay",display);

		JPanel scrollPanel = new JPanel();
		scrollPanel.setLayout(new BorderLayout());
		//scrollPanel.setBorder(BorderFactory.createTitledBorder("Log File: keine Datei geöffnet"));
		scrollPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		scrollPanel.add(new JScrollPane(logtable), BorderLayout.CENTER);

		JMenuBar menubar = ApplHelper.getMenuBar("menubar");

		frame = new JFrame();
		frame.setTitle(ApplHelper.getLocString("appl.frame.title"));
		frame.setLocation(
			ApplHelper.getIntPreset("frame.x"),
			ApplHelper.getIntPreset("frame.y"));
		frame.setSize(
			ApplHelper.getIntPreset("frame.width"),
			ApplHelper.getIntPreset("frame.height"));
		frame.setJMenuBar(menubar);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				updatePresets();
				System.exit(0);
			}
		});

		Container c = frame.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(new HeaderPanel("Monitor", ApplHelper.RESOURCE_PATH + "1ci_logo.gif"), BorderLayout.NORTH);
		c.add(scrollPanel, BorderLayout.CENTER);

		connectionlabel = new JLabel(" ");
		connectionlabel.setBorder(
			BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		connectionlabel.setFont(new Font("SansSerif", Font.PLAIN, 10));

		statuslabel = new JLabel(" ");
		statuslabel.setPreferredSize(new Dimension(120, 5));
		statuslabel.setBorder(
			BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		statuslabel.setFont(new Font("SansSerif", Font.PLAIN, 10));

		JPanel pan = new JPanel(new BorderLayout());
		pan.add(statuslabel, BorderLayout.WEST);
		pan.add(connectionlabel, BorderLayout.CENTER);
		c.add(pan, BorderLayout.SOUTH);

		frame.setVisible(true);

		//
		// Test Initialwerte
		//
		//display.setText("<client nr=4711>\n   Eintrag\n<\\client>");
		filterComponent.addItem("<Keiner>");

	}

	public void setStatus(String msg) {
		statuslabel.setText(msg);
	}

	/** 
	 * Erzeuge dynamisches Menue aus einer Property-Datei
	 * und liefere es zurueck.
	 * @return javax.swing.JMenuBar
	 */
	protected JMenuBar getMonitorMenuBar() {
		JMenuBar mb = new JMenuBar();
		mb.add(new JMenu("Log"));
		mb.add(new JMenu("Einstellungen"));
		mb.add(new JMenu("Hilfe"));
		mb.add(Box.createHorizontalGlue());
		mb.add(new JLabel("LOGMONITOR"));
		return mb;
	}

	/**
	 * Registriere die benötigten Actions im ApplHelper
	 */
	protected void installActions() {
		// init Actions
		ApplHelper.registerAction("exitAction", new ExitAction());
		ApplHelper.registerAction(
			"showPreferencesAction",
			new ShowPreferencesAction());
		ApplHelper.registerAction(
			"openLogFromFileAction",
			new OpenLogFromFileAction());
		ApplHelper.registerAction(
			"readFromSocketAction",
			new ReadFromSocketAction());
		ApplHelper.registerAction("showFilterAction", new ShowFilterAction());
		ApplHelper.registerAction(
			"showAboutDialogAction",
			new ShowAboutDialogAction());
		ApplHelper.registerAction(
			"showHelpDialogAction",
			new ShowHelpDialogAction());
		ApplHelper.registerAction("saveAsTxtAction", new SaveAsTxtAction());
		ApplHelper.registerAction("saveAsXMLAction", new SaveAsXMLAction());
		ApplHelper.registerAction("printAction", new PrintAction());
		ApplHelper.registerAction("clipboardAction", new ClipboardAction());
		ApplHelper.registerAction(
			"clearLogTableAction",
			new ClearLogTableAction());
		ApplHelper.registerAction(
			"startListeningFromSocketAction",
			new StartListeningFromSocketAction());
		ApplHelper.registerAction(
			"stopListeningFromSocketAction",
			new StopListeningFromSocketAction());

		// start & stop action beim start disablen
		ApplHelper.getAction("startListeningFromSocketAction").setEnabled(
			false);
		ApplHelper.getAction("stopListeningFromSocketAction").setEnabled(false);

	}

	/**
	 * Setze die User-definierten Voreinstellungen
	 */
	protected void installPresets() {
		//
		// wird im aktuellen Verzeichnis abgelegt, damit man ggfs. 
		// auch andere Presets ausliefern kann und Veränderungen 
		// durch den Classpath "." beim Start der Anwendung vor 
		// der ausgelieferten Datei im jar-file angezogen werden.
		//
		ApplHelper.loadPresets(ApplHelper.RESOURCE_PATH + "monitor-presets");

		//
		// Sprache setzen
		//
		Locale loc = new Locale(ApplHelper.getPreset("locale"), "");
		Locale.setDefault(loc);
		if (ApplHelper.DEBUG)
			System.out.println(">>>Sprache: " + loc.getDisplayName());

		//ApplHelper.loadMenuBundle("res"+File.separatorChar+"menu");
		ApplHelper.loadMenuBundle(ApplHelper.RESOURCE_PATH + "monitor-menu");

		//
		// Look & Feel setzen		
		//
		try {
			UIManager.setLookAndFeel(ApplHelper.getPreset("lnf"));
			//SwingUtilities.updateComponentTreeUI(this);
		}
		catch (Exception e) {
			if (ApplHelper.DEBUG)
				e.printStackTrace();
		}

		// internationalisierte Texte laden
		//ApplHelper.loadGUIBundle("res"+File.separatorChar+"gui");		
		ApplHelper.loadGUIBundle(ApplHelper.RESOURCE_PATH + "monitor-gui");
	}

	/** Startroutine des LogMonitors */
	public static void main(String[] args) {
		try {
			Monitor.getSingleInstance().initView();
		}
		catch (Exception e) {
			if (ApplHelper.DEBUG)
				e.printStackTrace();
		}
	}
}

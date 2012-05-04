package com.wanci.dmerce.gui.monitor;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import com.wanci.dmerce.gui.ApplHelper;
import com.wanci.dmerce.gui.util.FilterDictionary;
/**
 * Dialog für die Speicherung der benutzerspezifischen 
 * Filter-Einstellungen.
 * @author Ron Kastner
 */
public class SettingDialog extends JDialog implements ActionListener {
	
	/** DefaultButton */
	private JButton btCancel;	
	
	private FilterDialog fd = null;
	
	/** Dictionary für User-bezogene Einstellungen */
	private static Properties setting_props;


	/** enthält den Filenamen für die zu speichernde Setting-Datei */
	private JTextField txStore;
		
	/** Liste der benutzerdefinierten Filter */	
	private JList list;
		
	/** Konstruktor */	
	public SettingDialog(FilterDialog parent,boolean modal) {
			
		super(parent,modal);
		fd = parent;
		initView();
	}
		
	
	/**
	 * Neue Vorbelegung speichern...
	 */	
	protected void storeSetting() {
		
		try {
			String filename = txStore.getText();
			if (!filename.equals("")) {
				setting_props = fd.getCurrentFilterSettings();
				setting_props.store(new FileOutputStream(filename+".set"),"Setting for Filtermask");
			}
		}
		catch (IOException io) {
			if (ApplHelper.DEBUG) io.printStackTrace();
		}
	}	


	/**
	 * Lade Vorbelegung aus Property-Datei...
	 * @param	arg	java.lang.String
	 * 	Name der Verbelegungsdatei
	 */	
	protected void loadSetting(String arg) {
		
		setting_props = new Properties();
		try {
			setting_props.load(new FileInputStream(arg+".set"));
			
			FilterDictionary.updateFilterMaps(setting_props);
			fd.resetFilter();
			fd.updateFilterEntries();
		}
		catch (IOException io) {
			if (ApplHelper.DEBUG) io.printStackTrace();
		}
	}		


	/**
	 * Wende Setting aus PropertyDatei auf die
	 * Filtermaske an...
	 */	
	//protected void applySetting(String arg) {
	//}


	/**
	 * Liefere Liste aller möglichen benutzerdefinierten Filter 	
	 * zurück.
	 * @return ArrayList l
	 */
	protected void getAllSettings() {
		
		// suche im Arbeitsverzeichnis nach allen Dateien mit einer ".set"-Endung
		try {
			Vector v = new Vector();
			File f = new File(System.getProperty("user.dir"));
			String[] names = f.list();
			for (int i=0; i< names.length; i++)
				if (names[i].endsWith(".set"))
					v.add(names[i].substring(0,names[i].length()-4));
	
			list.setListData(v);//f.list());
		}
		catch (Exception e) {
			if (ApplHelper.DEBUG) e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * Eventbehandlung der Aktionen
	 */
	public void actionPerformed(ActionEvent ae) {
		
		String command = ae.getActionCommand();
		
		if (command.equals("load")) {
			if (ApplHelper.DEBUG)
				System.out.println("Loading Settings...");
			loadSetting(list.getSelectedValue().toString());
			fd.applySettings(setting_props);
			//applySetting();
			//refreshSetting
		}
		else if (command.equals("store")) {
			if (ApplHelper.DEBUG)
				System.out.println("Storing Settings...");
			storeSetting();
			//getAllSettings();
			//refreshSetting;
		}
		
		dispose();
	}
	
		
	/**
	 * Initialisierung der View
	 */
	protected void initView() {
		
		setTitle(ApplHelper.getLocString("setting.title"));
		
		JLabel lbLoad = new JLabel(ApplHelper.getLocString("setting.label.load"));		
		list = new JList();


		JLabel lbStore = new JLabel(ApplHelper.getLocString("setting.label.store"));
		txStore = new JTextField();
		txStore.setActionCommand("store");
		txStore.addActionListener(this);


		btCancel = new JButton(ApplHelper.getLocString("setting.button.cancel.text"));
		btCancel.setMnemonic(ApplHelper.getLocString("setting.button.cancel.mnemonic").charAt(0));
		btCancel.addActionListener(this);
		
		JButton btLoad = new JButton(ApplHelper.getLocString("setting.button.load.text"));
		JButton btStore = new JButton(ApplHelper.getLocString("setting.button.store.text"));
		btLoad.setActionCommand("load");
		btLoad.setMnemonic(ApplHelper.getLocString("setting.button.load.mnemonic").charAt(0));
		btLoad.addActionListener(this);
		btStore.setActionCommand("store");
		btStore.addActionListener(this);
		btStore.setMnemonic(ApplHelper.getLocString("setting.button.store.mnemonic").charAt(0));
		//list.addActionCommand("load");
		//list.addActionListener(this);
						
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		
		Container c = getContentPane();
		c.setLayout(gbl);


		gbc.insets = new Insets(4,4,4,4);		
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		c.add(lbLoad,gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		c.add(new JScrollPane(list),gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.weightx = 0;
		gbc.weighty = 0;
		c.add(new JSeparator(),gbc);
		gbc.gridy = 3;
		c.add(lbStore,gbc);
		gbc.gridy = 4;
		c.add(txStore,gbc);
		gbc.gridy = 5;
		c.add(new JSeparator(),gbc);
		gbc.gridy = 6;
		
		JPanel buttonpanel = new JPanel();
		
		buttonpanel.add(btLoad);
		buttonpanel.add(btStore);
		buttonpanel.add(btCancel);
				
		c.add(buttonpanel,gbc);
				
		setSize(300,300);
		setLocationRelativeTo(this.getParent());			
		
		// Liste mit möglichen Filter füllen
		getAllSettings();
	}
	
	public void setVisible(boolean arg) {
		
		if (arg) {
			getRootPane().setDefaultButton(btCancel);
			getAllSettings();
		}
		
		super.setVisible(arg);
	}
	
}
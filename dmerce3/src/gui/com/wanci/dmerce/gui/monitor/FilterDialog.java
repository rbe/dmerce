package com.wanci.dmerce.gui.monitor;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import com.wanci.dmerce.gui.ApplHelper;
import com.wanci.dmerce.gui.util.FilterDictionary;
import com.wanci.dmerce.gui.util.MonitorConstants;

/**
 * Dialog für die Festlegung der Filtereinstellungen für
 * die Anzeige der XML-Log-Einträge
 * @author	Ron Kastner
 */
public class FilterDialog extends JDialog implements ActionListener,MonitorConstants {


	private JComboBox cbFromDate = new JComboBox();
	private JComboBox cbToDate = new JComboBox();		
	private JComboBox cbClient = new JComboBox();
	private JComboBox cbProxy = new JComboBox();
	private JComboBox cbTemplate = new JComboBox();
	private JComboBox cbModule = new JComboBox();
	private JComboBox cbType = new JComboBox();
	private JTextField txMessage = new JTextField();
	private JTextField txExtClient = new JTextField();
	
	/** DefaultButton */
	private JButton btApply;
	
	private Object tmpFromDate = null;
	private Object tmpToDate = null;
	private Object tmpClient = null;
	private Object tmpProxy = null;	
	private Object tmpTemplate = null;
	private Object tmpModule = null;
	private Object tmpType = null;
	private String tmpMessage = null;
	
	/** Referenz auf das Datenmodell der Tabelle des Hauptfensters */
	private FilterTableModel model;
	
	/** SubDialog für die Settings */
	private JDialog settingDialog = null;
	
	
		
	public FilterDialog(Frame parent,boolean modal,FilterTableModel model) {
			
		super(parent,modal);
		this.model = model;
		initView();
		
		
	}
	
	private void saveCurrentFilterSettings() {
			// alte Filterwerte merken
			tmpFromDate = cbFromDate.getSelectedItem();
			tmpToDate = cbToDate.getSelectedItem();
			tmpClient = cbClient.getSelectedItem();
			tmpProxy = cbProxy.getSelectedItem();
			tmpTemplate = cbTemplate.getSelectedItem();
			tmpModule = cbModule.getSelectedItem();
			tmpType = cbType.getSelectedItem();
			tmpMessage = txMessage.getText();		
	}
	
	private void setCurrentFilterSettings() {
		if (cbFromDate != null) {
			cbFromDate.setSelectedItem(tmpFromDate);
			cbToDate.setSelectedItem(tmpToDate);
			cbClient.setSelectedItem(tmpClient);
			cbProxy.setSelectedItem(tmpProxy);
			cbTemplate.setSelectedItem(tmpTemplate);
			cbModule.setSelectedItem(tmpModule);
			cbType.setSelectedItem(tmpType);
			txMessage.setText(tmpMessage);		
		}
	}


	/**
	 * Liefere die aktuellen Inhalte der Filtermaske
	 * in einem Property-Datentyp zurück.
	 * @return Properties
	 *		Dictionary mit Einträgen der Filtermaske
	 */	
	public Properties getCurrentFilterSettings() {
		
		Properties p = new Properties();
		
		updatePropsFromControl(p,TAG_DATE+"_FROM",cbFromDate);
		updatePropsFromControl(p,TAG_DATE+"_TO",cbToDate);
		updatePropsFromControl(p,TAG_CLIENT,cbClient);
		updatePropsFromControl(p,TAG_CLIENT+"_EXT",txExtClient);
		updatePropsFromControl(p,TAG_PROXY,cbProxy);
		updatePropsFromControl(p,TAG_MODULE,cbModule);
		updatePropsFromControl(p,TAG_MSGTYPE,cbType);
		updatePropsFromControl(p,TAG_MESSAGE,txMessage);

		return p;
	}
	
	/**
	 * Hole aus einer Komponente den aktuellen Wert heraus und
	 * speichere ihn in der Property-Mappings.
	 * Wenn es der DefaultWert für <alle> - Einträge ist, dann
	 * merke ihn dir nicht
	 */
	private void updatePropsFromControl(Properties prop,String tag,JComponent c) {
		
		String value = "";
		
		if (c instanceof JComboBox) {
			JComboBox cb = (JComboBox)c;
			if (cb.getSelectedIndex() > 0)
				value = cb.getSelectedItem().toString();
		}
		else if (c instanceof JTextField) {
			JTextField tx = (JTextField)c;			
			value = tx.getText();
		}
	
		prop.put(tag,value);				
	}
	
	
	/**
	 * Liefere die aktuellen Inhalte der Filtermaske
	 * in einem Property-Datentyp zurück.
	 * @return Properties
	 *		Dictionary mit Einträgen der Filtermaske
	 */	
	public void applySettings(Properties props) {
		
		cbFromDate.setSelectedItem(""+props.get(TAG_DATE+"_FROM"));
		cbToDate.setSelectedItem(""+props.get(TAG_DATE+"_TO"));
		cbClient.setSelectedItem(""+props.get(TAG_CLIENT));
		cbProxy.setSelectedItem(""+props.get(TAG_PROXY));
		cbModule.setSelectedItem(""+props.get(TAG_MODULE));
		cbType.setSelectedItem(""+props.get(TAG_MSGTYPE));
		txMessage.setText(""+props.get(TAG_MESSAGE));
		txExtClient.setText(""+props.get(TAG_CLIENT+"_EXT"));		
	}
	
	
	/**
	 * Eventbehandlung der Aktionen
	 */
	public void actionPerformed(ActionEvent ae) {
		
		if (ae.getActionCommand().equals("apply")) {
			updateFilterFromView();
			saveCurrentFilterSettings();			
		}
		if (ae.getActionCommand().equals("reset")) {
			resetFilter();
			// dialogfenster NICHT schliessen
			return;
		}
		if (ae.getActionCommand().equals("setting")) {
			
			if (settingDialog == null)
				settingDialog = new SettingDialog(this,false);
			
			settingDialog.setVisible(true);
			return;
		}
				
		this.dispose();
	}
	
	
	private void updateFilterFromView() {
		
			TableFilter f = model.getFilter();
		
			setDefaultCond(COL_MODULE,DefaultTableFilter.RULE_EQUALS,cbModule);
			setDefaultCond(COL_PROXY,DefaultTableFilter.RULE_EQUALS,cbProxy);
			setDefaultCond(COL_TEMPLATE,DefaultTableFilter.RULE_EQUALS,cbTemplate);
			setDefaultCond(COL_MSGTYPE,DefaultTableFilter.RULE_EQUALS,cbType);
			
			// Date
			Object obj_from = cbFromDate.getSelectedItem();
			Object obj_to = cbToDate.getSelectedItem();
			
			if (cbFromDate.getSelectedIndex() == 0 && cbToDate.getSelectedIndex() == 0)
				f.removeCondition(COL_DATE);
			else {
				
				if (cbFromDate.getSelectedIndex() == 0) 
					obj_from = cbFromDate.getItemAt(1);
				if (cbToDate.getSelectedIndex() == 0) 
					obj_to = cbToDate.getItemAt(cbToDate.getItemCount()-1);
					
				f.setCondition(COL_DATE,DefaultTableFilter.RULE_FROM_TO,new Object[]{ obj_from, obj_to } );
			}			
			
			// Message
			if (txMessage.getText().equals(""))
				f.removeCondition(COL_MESSAGE);
			else
				f.setCondition(COL_MESSAGE,DefaultTableFilter.RULE_CONTAINS,new Object[]{ txMessage.getText() });
			
			// Client
			StringTokenizer st = new StringTokenizer(txExtClient.getText(),",");
			ArrayList l = new ArrayList();


			if (cbClient.getSelectedIndex() > 0)
				l.add(cbClient.getSelectedItem().toString());
			
			while (st.hasMoreTokens()) {
				String value = st.nextToken();
				if (!(value == null || value.equals("")))
					l.add(value);
			}
			
			if (l.size() == 0)
				f.removeCondition(COL_CLIENT);
			else
				f.setCondition(COL_CLIENT,DefaultTableFilter.RULE_BEGINS_WITH,l.toArray());
			
			model.filterChanged();		
	}
	
	
	private void setDefaultCond(int col,int rule,JComboBox cb) {
		
		// 0 = <alle>, also in dieser Spalte keinen Filter anwenden
		if (cb.getSelectedIndex() == 0) {
			model.getFilter().removeCondition(col);
		}
		else
			model.getFilter().setCondition(col,rule,new Object[] {cb.getSelectedItem()});
	}
	
	/**
	 * Initialisierung der View
	 */
	protected void initView() {
		
		setTitle(ApplHelper.getLocString("filter.title"));

		// Type ComboBox muss nur einmal mit Werten gefüllt werden
		cbType.setModel(getCbModel(COL_MSGTYPE));
						
		JPanel content = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		
		content.setLayout(gbl);		
		
		
		btApply = new JButton(ApplHelper.getLocString("filter.button.apply.text"));
		btApply.setMnemonic(ApplHelper.getLocString("filter.button.apply.mnemonic").charAt(0));
		btApply.setActionCommand("apply");
		btApply.addActionListener(this);
		JButton btReset = new JButton(ApplHelper.getLocString("filter.button.reset.text"));
		btReset.setMnemonic(ApplHelper.getLocString("filter.button.reset.mnemonic").charAt(0));
		btReset.setActionCommand("reset");
		btReset.addActionListener(this);
		JButton btCancel = new JButton(ApplHelper.getLocString("filter.button.cancel.text"));
		btCancel.setMnemonic(ApplHelper.getLocString("filter.button.cancel.mnemonic").charAt(0));
		btCancel.addActionListener(this);
		JButton btSetting = new JButton(ApplHelper.getLocString("filter.button.setting.text"));
		btSetting.setMnemonic(ApplHelper.getLocString("filter.button.setting.mnemonic").charAt(0));
		btSetting.setActionCommand("setting");
		btSetting.addActionListener(this);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(btApply);
		buttonPanel.add(btReset);
		buttonPanel.add(btSetting);
		buttonPanel.add(btCancel);
		
		JPanel sub = new JPanel();
		sub.setLayout(new BorderLayout());
		sub.add(new JSeparator(),BorderLayout.NORTH);
		sub.add(buttonPanel,BorderLayout.SOUTH);

		Form date = new Form("Datum");
		date.setFirstColumnWidth( 100 );
		date.setContentFirstColumn(new Object[] { ApplHelper.getLocString("log.table.column.date"), "Bis" });
		date.setContentSecondColumn(new Object[] { cbFromDate, cbToDate}, new boolean[] { true, true } );


		Form client = new Form("Client");
		client.setFirstColumnWidth( 100 );
		client.setContentFirstColumn(new Object[] { 	
			ApplHelper.getLocString("log.table.column.client"),	
			"freie Eingabe:",		
			ApplHelper.getLocString("log.table.column.proxy")
		});
		client.setContentSecondColumn(new Object[] { cbClient, txExtClient, cbProxy }, new boolean[] { true, true, true } );
		
		
		
		Form dmerce = new Form("DMerce");
		dmerce.setFirstColumnWidth( 100 );
		dmerce.setContentFirstColumn(new Object[] {
			ApplHelper.getLocString("log.table.column.template"),
			ApplHelper.getLocString("log.table.column.module"),
			ApplHelper.getLocString("log.table.column.message"),
			ApplHelper.getLocString("log.table.column.msgtype")			
		});
		dmerce.setContentSecondColumn(new Object[] {
			cbTemplate,
			cbModule,
			txMessage,
			cbType			
		}, new boolean[] { true, true, true, true }
		);

		JPanel formcontainer = new JPanel();
		formcontainer.setLayout( new GridBagLayout() );
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		formcontainer.add( client, gbc );
		gbc.gridy = 1;
		formcontainer.add( dmerce , gbc );
		gbc.gridy = 2;
		formcontainer.add( date, gbc );
		gbc.gridy = 3;
		gbc.weighty = 1.0;
		formcontainer.add( new JLabel(), gbc);

		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());
		pan.add( new HeaderPanel("Filter-Einstellungen"),BorderLayout.NORTH );
		pan.add(formcontainer,BorderLayout.CENTER);
		pan.add(sub,BorderLayout.SOUTH);
		
		content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(pan,BorderLayout.CENTER);
		
		
		pack();//setSize(550,410);
		setLocationRelativeTo(this.getParent());			
		
	}




	private ComboBoxModel getCbModel(int col) {
		return new DefaultComboBoxModel(FilterDictionary.getSortedEntries(col));
	}	


	
	public void resetFilter() {
		cbFromDate.setSelectedIndex(0);
		cbToDate.setSelectedIndex(0);
		cbClient.setSelectedIndex(0);
		cbProxy.setSelectedIndex(0);
		cbTemplate.setSelectedIndex(0);
		cbModule.setSelectedIndex(0);
		cbType.setSelectedIndex(0);
		txMessage.setText("");
		txExtClient.setText("");
	}




	/**
	 * Aktualisiere die Einträge der FilterComboBoxen durch
	 * die Daten des LogManagers.<p>
	 */	 
	public void updateFilterEntries() {
		//
		// Daten aus dem LogManager holen
		//
		cbFromDate.setModel(getCbModel(COL_DATE));
		cbToDate.setModel(getCbModel(COL_DATE));
		cbClient.setModel(getCbModel(COL_CLIENT));
		cbProxy.setModel(getCbModel(COL_PROXY));
		cbTemplate.setModel(getCbModel(COL_TEMPLATE));
		cbModule.setModel(getCbModel(COL_MODULE));
	}
	
	/** Überschriebene Methode -> DefaultButton */
	public void setVisible(boolean arg) {
		
		if (arg) {
			
			updateFilterEntries();
			setCurrentFilterSettings();
			
			// Startinitialisierung
			if (cbFromDate.getSelectedIndex() == -1)
				resetFilter();		
			
			if (btApply != null)
				this.getRootPane().setDefaultButton(btApply);
		}		
		
		super.setVisible(arg);
	}
}
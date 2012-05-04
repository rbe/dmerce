package com.wanci.dmerce.gui.monitor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.wanci.dmerce.gui.ApplHelper;

/**
 * Dialog für Speicherung der Benutzer-spezifischen Einstellungen
 * (Sprache & LnF).
 * 
 * @author	Ron Kastner
 * @version $Id: PreferenceDialog.java,v 1.1 2003/11/10 18:47:39 rb Exp $
 * 
 */
public class PreferenceDialog extends JDialog implements ActionListener {
	
	/** Referenz auf die Hauptanwendung (für UIupdate) */
	private Monitor m;
		
	/** DefaultButton */
	private JButton btOk;
		
	public PreferenceDialog(Frame parent,boolean modal,Monitor m) {
		super(parent,modal);
		this.m = m;
		initView();
	}
	
	/**
	 * Eventbehandlung
	 */
	public void actionPerformed(ActionEvent ae) {
				
		// fenster schliessen
		this.dispose();
		
	}


	/**
	 * Initialisierung der View
	 */
	protected void initView() {
		
		setTitle(ApplHelper.getLocString("pref.title"));
		
		JTabbedPane tab = new JTabbedPane();
		tab.add(getLanguageTab());
		tab.add(getLnFTab());


		btOk = new JButton(ApplHelper.getLocString("pref.button.ok.text"));
		btOk.setMnemonic(ApplHelper.getLocString("pref.button.ok.mnemonic").charAt(0));
		btOk.addActionListener(this);
		
		JPanel buttonpanel = new JPanel();
		buttonpanel.add(btOk);
						
		JPanel sub  = new JPanel();
		sub.setLayout(new BorderLayout());
		sub.add(new JSeparator(),BorderLayout.NORTH);
		sub.add(buttonpanel ,BorderLayout.SOUTH);


		
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(tab,BorderLayout.CENTER);
		c.add(sub,BorderLayout.SOUTH);		
				
		setSize(400,300);
		setLocationRelativeTo(this.getParent());		
	}


	/**
	 * Erzeuge den Reiter für die Spracheinstellung
	 * @return JPanel
	 */	
	protected JPanel getLanguageTab() {


		String title = ApplHelper.getLocString("pref.tab.lang.title");
		String localeSupport = ApplHelper.getPreset("locale.support");		
				
		StringTokenizer st = new StringTokenizer(localeSupport);
		Locale[] languages = new Locale[st.countTokens()];
		//String[] langDescriptor = new String[st.countTokens()];
		
		int i=0;
		while (st.hasMoreTokens()) {
			languages[i] = new Locale(st.nextToken(),"");	
			i++;
		}
		
		JComboBox cb = new JComboBox();
		cb.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list,
                                              Object value,
                                              int index,
                                              boolean isSelected,
                                              boolean cellHasFocus) {
                                              	
				super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
				
				if (value instanceof Locale) {
					this.setText( ((Locale)value).getDisplayName());
				}                                              	
				
				return this;
         }
		});
		
		cb.setModel(new DefaultComboBoxModel(languages));		
						
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				Locale l = (Locale)ie.getItem();
				ApplHelper.setPreset("locale",l.getLanguage());
			}
		});				
		
		JPanel sub = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();


		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(8,8,8,8);
		
		sub.setLayout(gbl);
		
		sub.add(new JLabel(title),gbc);
		
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		sub.add(cb,gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 2;
		 
		JTextArea area = new JTextArea();
		area.setWrapStyleWord(true);
		area.setEditable(false);
		area.setBorder(null);
		area.setRows(3);
		area.setOpaque(true);
		area.setBackground(UIManager.getColor("JButton.background"));//sub.getBackground()); // windows-lnf kann kein "opaque"
		Dimension dim = new Dimension(100,30);
		area.setPreferredSize(dim);
		area.setMinimumSize(dim);
		area.setMaximumSize(dim);
		area.setSize(dim);
		area.setText(ApplHelper.getLocString("pref.tab.lang.advice"));
		sub.add(area,gbc);


		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());
		pan.setName(title);
		pan.add(sub,BorderLayout.CENTER);
		
		return pan;
	}


	/**
	 * Erzeuge den Reiter für die Look&Feel- Einstellung
	 * @return JPanel
	 */	
	protected JPanel getLnFTab() {
		JPanel pan = new JPanel();
		pan.setName(ApplHelper.getLocString("pref.tab.lnf.title"));
		
		AbstractAction uiAction = new AbstractAction() {
			public void actionPerformed(ActionEvent ae) {
				try {
					UIManager.setLookAndFeel(ae.getActionCommand());
					m.updateApplicationUI();
					ApplHelper.setPreset("lnf",ae.getActionCommand());
				}
				catch (Exception e) {
					if (ApplHelper.DEBUG) e.printStackTrace();
				}
			}
		};
		
		ButtonGroup group = new ButtonGroup();
		
		UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
		
		for (int i=0; i<info.length; i++) {
			JRadioButton b = new JRadioButton();
			b.setText(info[i].getName());
			b.setActionCommand(info[i].getClassName());
			if (info[i].getName().equals(UIManager.getLookAndFeel().getName()))
				b.setSelected(true);
			b.addActionListener(uiAction);
			group.add(b);
			pan.add(b);
		}
		
		
		return pan;
	}
	
	/**
	 * Aktiviere den DefaultButton
	 */
	public void setVisible(boolean arg) {
		
		if (arg) {
			getRootPane().setDefaultButton(btOk);
		}
		
		super.setVisible(arg);	
	}
}
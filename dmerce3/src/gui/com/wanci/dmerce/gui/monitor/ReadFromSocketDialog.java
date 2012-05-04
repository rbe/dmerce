package com.wanci.dmerce.gui.monitor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import com.wanci.dmerce.gui.ApplHelper;
import com.wanci.dmerce.gui.util.ProcessModel;
/**
 * Dialog für die Eingabe der Host-Adresse und des Ports.
 * @author	Ron Kastner
 */
public class ReadFromSocketDialog extends JDialog implements ActionListener {
		
	private JTextField txHost = new JTextField();
	private JTextField txPort = new JTextField();
	private JButton btOk;
	private JButton btCancel;
		
	public ReadFromSocketDialog(Frame parent,boolean modal) {
		super(parent,modal);
		initView();
	}
	
	/**
	 * EventBehandlung für die Buttons des Dialoges
	 * @param ae	java.awt.event.ActionEvent
	 */
	public void actionPerformed(ActionEvent ae) {
		
		if (ae.getActionCommand().equals("ok")) {
			
			// Presets merken
			ApplHelper.setPreset("host",txHost.getText());
			ApplHelper.setPreset("port",txPort.getText());
			
			int port = Integer.parseInt(txPort.getText());
			ProcessModel.getInstance().openSocketConnnection(getParent(),txHost.getText(),port);
		}
	
		dispose();
	}
	
	/**
	 * Initialisierung der View
	 */
	protected void initView() {
		
		setTitle(ApplHelper.getLocString("socket.title"));


		btOk = new JButton(ApplHelper.getLocString("socket.button.ok.text"));
		btCancel = new JButton(ApplHelper.getLocString("socket.button.cancel.text"));
		
		btOk.setActionCommand("ok");
		btOk.setMnemonic(ApplHelper.getLocString("socket.button.ok.mnemonic").charAt(0));
		btOk.addActionListener(this);
		btCancel.addActionListener(this);
		btCancel.setMnemonic(ApplHelper.getLocString("socket.button.cancel.mnemonic").charAt(0));


		JPanel button_panel = new JPanel();
		button_panel.add(btOk);
		button_panel.add(btCancel);


		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BorderLayout());
		wrapper.add(new JSeparator(),BorderLayout.NORTH);
		wrapper.add(button_panel,BorderLayout.SOUTH);
		
		JPanel content = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();


		txHost = new JTextField();
		txPort = new JTextField();
		
		txHost.setText(ApplHelper.getPreset("host"));
		txPort.setText(ApplHelper.getPreset("port"));
		
		content.setLayout(gbl);				
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets= new Insets(8,4,8,4);
		content.add(new JLabel(ApplHelper.getLocString("socket.host")),gbc);
		gbc.gridy = 1;
		content.add(new JLabel(ApplHelper.getLocString("socket.port")),gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		content.add(txHost,gbc);
		gbc.gridy = 1;
		content.add(txPort,gbc);
				


		getRootPane().setDefaultButton(btOk);


		JLabel pic = new JLabel();
		pic.setOpaque(true);
		pic.setBackground(new Color(76,113,201));		
		pic.setVerticalTextPosition(SwingConstants.TOP);
		pic.setVerticalAlignment(SwingConstants.BOTTOM);
		pic.setIcon( ApplHelper.getIcon( ApplHelper.RESOURCE_PATH+"joggler.jpg") );
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(pic,BorderLayout.CENTER);
		p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4,4,4,4),BorderFactory.createBevelBorder(BevelBorder.LOWERED)));


		JPanel wrap2 = new JPanel();
		wrap2.setLayout(new BorderLayout());
		wrap2.add(content,BorderLayout.CENTER);
		wrap2.add(wrapper,BorderLayout.SOUTH);
		
		content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(p,BorderLayout.WEST);				
		c.add(wrap2,BorderLayout.CENTER);
		
		setSize(400,300);
		setLocationRelativeTo(this.getParent());			
	}
	
	public void setVisible(boolean arg) {
		super.setVisible(arg);
		
		if (arg) {
			getRootPane().setDefaultButton(btOk);
		}
	}
	
}
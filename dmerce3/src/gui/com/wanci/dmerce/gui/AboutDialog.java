package com.wanci.dmerce.gui;

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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;


/**
 * Dialog der Informationen über die Version und Erstellung
 * des Monitor Tools anzeigt.
 * @author	Ron Kastner
 */
public class AboutDialog extends JDialog implements ActionListener {

	// DefaultButton
	private JButton btOk;

	public AboutDialog(Frame parent, boolean modal) {
		super(parent, modal);
		initView();
	}

	/**
	 * Eventbehandlung der Aktionen
	 */
	public void actionPerformed(ActionEvent ae) {
		this.dispose();
	}

	/**
	 * Initialisierung der View
	 */
	protected void initView() {
		setTitle(ApplHelper.getLocString("about.title"));

		JPanel content = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();

		JLabel pic = new JLabel();
		pic.setOpaque(true);
		pic.setBackground(new Color(76, 113, 201));
		pic.setVerticalTextPosition(SwingConstants.TOP);
		pic.setVerticalAlignment(SwingConstants.BOTTOM);
		pic.setIcon(ApplHelper.getIcon(ApplHelper.RESOURCE_PATH + "about.jpg"));
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(pic, BorderLayout.CENTER);
		p.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		content.setLayout(gbl);

		btOk = new JButton(ApplHelper.getLocString("about.button.text"));
		btOk.setMnemonic(
			ApplHelper.getLocString("about.button.mnemonic").charAt(0));
		btOk.addActionListener(this);

		gbc.insets = new Insets(25, 4, 4, 4);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;
		//gbc.weightx=1.0;		
		content.add(
			new JLabel(ApplHelper.getLocString("about.line1.text")),
			gbc);
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.gridy = 1;
		content.add(
			new JLabel(
				ApplHelper.getLocString("about.line2.text"),
				new ImageIcon(ApplHelper.RESOURCE_PATH + "1ci_logo.gif"),
				SwingConstants.RIGHT),
			gbc);
		gbc.gridy = 2;
		content.add(
			new JLabel(ApplHelper.getLocString("about.line3.text")),
			gbc);
		gbc.gridy = 3;
		content.add(
			new JLabel(ApplHelper.getLocString("about.line4.text")),
			gbc);
		gbc.gridy = 4;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		content.add(new JSeparator(), gbc);
		gbc.gridy = 5;
		gbc.weighty = 0.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;
		content.add(btOk, gbc);

		//content.setBorder(BorderFactory.createEmptyBorder(50,70,10,10));
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(p, BorderLayout.WEST);
		c.add(content, BorderLayout.CENTER);

		setSize(400, 250);
		setLocationRelativeTo(this.getParent());

	}

	public void setVisible(boolean arg) {

		if (arg) {
			if (btOk != null)
				this.getRootPane().setDefaultButton(btOk);
		}

		super.setVisible(arg);
	}
}
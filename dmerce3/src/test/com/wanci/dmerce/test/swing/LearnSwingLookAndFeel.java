/*
 * Created on 05.08.2003
 *
 */
package com.wanci.dmerce.test.swing;

/**
 * @author tw
 * @version $Id: LearnSwingLookAndFeel.java,v 1.2 2004/06/16 11:09:02 rb Exp $
 *
 *
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * An application that displays a JButton and several JRadioButtons. The
 * JRadioButtons determine the look and feel used by the application.
 */
public class LearnSwingLookAndFeel extends JPanel {
	static JFrame frame;

	static String metal = "Metal";

	static String metalClassName = "javax.swing.plaf.metal.MetalLookAndFeel";

	static String motif = "Motif";

	static String motifClassName = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";

	static String windows = "Windows";

	static String windowsClassName = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

	JRadioButton metalButton, motifButton, windowsButton;

	public LearnSwingLookAndFeel() {
		// Create the buttons.
		JButton button = new JButton("Hello, world");
		button.setMnemonic('h'); //for looks only; button does nada

		metalButton = new JRadioButton(metal);
		metalButton.setMnemonic('o');
		metalButton.setActionCommand(metalClassName);

		motifButton = new JRadioButton(motif);
		motifButton.setMnemonic('m');
		motifButton.setActionCommand(motifClassName);

		windowsButton = new JRadioButton(windows);
		windowsButton.setMnemonic('w');
		windowsButton.setActionCommand(windowsClassName);

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(metalButton);
		group.add(motifButton);
		group.add(windowsButton);

		// Register a listener for the radio buttons.
		RadioListener myListener = new RadioListener();
		metalButton.addActionListener(myListener);
		motifButton.addActionListener(myListener);
		windowsButton.addActionListener(myListener);

		add(button);
		add(metalButton);
		add(motifButton);
		add(windowsButton);
	}

	/** An ActionListener that listens to the radio buttons. */
	class RadioListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String lnfName = e.getActionCommand();

			try {
				UIManager.setLookAndFeel(lnfName);
				SwingUtilities.updateComponentTreeUI(frame);
				frame.pack();
			} catch (Exception exc) {
				JRadioButton button = (JRadioButton) e.getSource();
				button.setEnabled(false);
				updateState();
				System.err.println("Could not load LookAndFeel: " + lnfName);
			}

		}
	}

	public void updateState() {
		String lnfName = UIManager.getLookAndFeel().getClass().getName();
		if (lnfName.indexOf(metal) >= 0) {
			metalButton.setSelected(true);
		} else if (lnfName.indexOf(windows) >= 0) {
			windowsButton.setSelected(true);
		} else if (lnfName.indexOf(motif) >= 0) {
			motifButton.setSelected(true);
		} else {
			System.err.println("SimpleExample is using an unknown L&F: "
					+ lnfName);
		}
	}

	public static void main(String s[]) {
		/*
		 * NOTE: By default, the look and feel will be set to the Cross Platform
		 * Look and Feel (which is currently Metal). The user may someday be
		 * able to override the default via a system property. If you as the
		 * developer want to be sure that a particular L&F is set, you can do so
		 * by calling UIManager.setLookAndFeel(). For example, the first code
		 * snippet below forcibly sets the UI to be the System Look and Feel.
		 * The second code snippet forcibly sets the look and feel to the Cross
		 * Platform L&F.
		 * 
		 * Snippet 1: try {
		 * UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		 * catch (Exception exc) { System.err.println("Error loading L&F: " +
		 * exc); }
		 * 
		 * Snippet 2: try {
		 * UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
		 * catch (Exception exc) { System.err.println("Error loading L&F: " +
		 * exc); }
		 */

		LearnSwingLookAndFeel panel = new LearnSwingLookAndFeel();

		frame = new JFrame("SimpleExample");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.getContentPane().add("Center", panel);
		frame.pack();
		frame.setVisible(true);

		panel.updateState();
	}
}


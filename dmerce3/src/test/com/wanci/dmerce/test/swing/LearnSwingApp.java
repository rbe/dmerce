/*
 * Created on 05.08.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.test.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * @author tw
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

public class LearnSwingApp {
	private static String labelPrefix = "Number of button clicks: ";
	private int numClicks = 0;

	public Component createComponents() {
		final JLabel label = new JLabel(labelPrefix + "0    ");
		final JLabel header = new JLabel("1[Ci] ClickCounter");

		JButton button = new JButton("Increase Clicks!");
		button.setMnemonic(KeyEvent.VK_I);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numClicks++;
				label.setText(labelPrefix + numClicks);
			}
		});

		JButton button2 = new JButton("Decrease Clicks!");
		button2.setMnemonic(KeyEvent.VK_D);
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numClicks--;
				label.setText(labelPrefix + numClicks);
			}
		});

		JButton reset = new JButton("Reset Clicks!");
		reset.setMnemonic(KeyEvent.VK_R);
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				numClicks = 0;
				label.setText(labelPrefix + numClicks);
			}
		});

		label.setLabelFor(button);
		label.setLabelFor(button2);
		label.setLabelFor(reset);

		/*
		 * An easy way to put space between a top-level container
		 * and its contents is to put the contents in a JPanel
		 * that has an "empty" border.
		 */
		JPanel pane = new JPanel();
		pane.setBorder(BorderFactory.createEmptyBorder(30, //top
		30, //left
		10, //bottom
		30) //right
		);
		pane.setLayout(new GridLayout(0, 1));
		pane.add(header);
		pane.add(button);
		pane.add(button2);
		pane.add(label);
		pane.add(reset);

		return pane;
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
				UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
		}

		//Create the top-level container and add contents to it.
		JFrame frame = new JFrame("SwingApplication");
		LearnSwingApp app = new LearnSwingApp();
		Component contents = app.createComponents();
		frame.getContentPane().add(contents, BorderLayout.CENTER);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
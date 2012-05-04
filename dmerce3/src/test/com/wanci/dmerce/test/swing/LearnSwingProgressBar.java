/*
 * Created on Aug 6, 2003
 *
 */
package com.wanci.dmerce.test.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

/**
 * @author tw
 *
 */

public class LearnSwingProgressBar extends JFrame implements ActionListener {

	private JProgressBar pb;
	private int value = 0;
	
	public LearnSwingProgressBar() {
		
		super("ProgressBar");
		// addWindowListener(new WindowClosingAdapter(true));
		Container cp = getContentPane();
		// Fortschrittsanzeige
		pb = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
		pb.setStringPainted(true);
		cp.add(pb, BorderLayout.NORTH);
		// Weiter-Button
		JButton button = new JButton("Weiter");
		button.addActionListener(this);
		cp.add(button, BorderLayout.SOUTH);
		
	}

	public void actionPerformed(ActionEvent event) {
		value = (value >= 100 ? 0 : value + 5);
		pb.setValue(value);
	}
	
	public static void main(String[] args) {
		LearnSwingProgressBar frame = new LearnSwingProgressBar();
		frame.setLocation(100, 100);
		frame.setSize(300, 150);
		frame.setVisible(true);
		
	}
}

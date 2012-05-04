/*
 * Created on 18.07.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.test.swing;

/**
 * @author tw
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

public class LearnSwingButtons extends JFrame {

	public LearnSwingButtons() {

		super("Drei nette Buttons");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridLayout(3, 1));
		contentPane.add(new JButton("Button 1"));
		contentPane.add(new JButton("Button 2"));
		contentPane.add(new JButton("Button 3"));

	}

	public static void main(String[] args) {

		LearnSwingButtons window = new LearnSwingButtons();
		window.setLocation(100, 100);
		window.setSize(300, 200);
		window.setVisible(true);

	}
}


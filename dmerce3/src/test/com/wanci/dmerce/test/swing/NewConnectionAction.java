/*
 * Created on Oct 17, 2003
 *
 */
package com.wanci.dmerce.test.swing;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 * @author tw2
 *
 * @version $id$
 */

public class NewConnectionAction extends JFrame implements ActionListener, CaretListener {

	public NewConnectionAction() {
		
		super("JTextField");
		// addWindowsListener(new WindowsClosingAdapter(true));
		Container cp = getContentPane();
		cp.setLayout(new GridLayout(5,1));
		// Textfeld mit Inhalt (so lang wie Inhalt)
		JTextField tf;
		tf = new JTextField("Testing 1,2,3");
		cp.add(tf);
		// Leeres Textfeld (20 Zeichen)
		tf = new JTextField(20);
		tf.addActionListener(this);
		cp.add(tf);
		// tf.addCaretListener(this);
		
	}

	public void actionPerformed(ActionEvent event) {
		
		JTextField tf = (JTextField)event.getSource();
		System.out.println("---ActionEvent---");
		System.out.println(tf.getText());
		// System.out.println(tf.getSelectedText());
		// System.out.println(tf.getSelectionStart());
		// System.out.println(tf.getSelectionEnd());
		// System.out.println(tf.getCaretPosition());
				
	}

	public void caretUpdate(CaretEvent event) {
		
		System.out.println("---CaretEvent---");
		System.out.println(event.getDot());
		System.out.println(event.getMark());
		
	}

	public static void main(String[] args) {
		
		NewConnectionAction frame = new NewConnectionAction();
		frame.setBackground(Color.lightGray);
		frame.setForeground(Color.green);
		frame.setLocation(100, 100);
		frame.setSize(300, 150);	
		frame.setVisible(true);
		
	}
	
}

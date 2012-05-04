/*
 * Created on 05.08.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.wanci.dmerce.test.swing;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author tw
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LearnSwingSelectBox extends JFrame {

	private static final String[] QUARTALE =
		{ "1. Quartal", "2. Quartal", "3. Quartal", "4. Quartal" };

	public LearnSwingSelectBox() {

		super("Test einer SelectBox");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public static void main(String[] args) {

		LearnSwingSelectBox window = new LearnSwingSelectBox();
		window.setLocation(100, 100);
		window.setSize(300, 200);
		window.setVisible(true);
		String selection =
			(String) JOptionPane.showInputDialog(
				window,
				"Wählen Sie das Quartal aus",
				"JOptionPane.showInputDialog",
				JOptionPane.QUESTION_MESSAGE,
				null,
				QUARTALE,
				QUARTALE[0]);
		System.out.println("Ausgewählt wurde " + selection);

	}
}

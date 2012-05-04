/*
 * Created on 18.07.2003
 */
package com.wanci.dmerce.dbmgr;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.Border;

/**
 * @author tw
 * 
 * @ version $id$
 *
 */
public class SplashScreen extends JWindow implements Runnable {

	private int millis = 2500;

	public SplashScreen(String image, String text) {

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());

		Border bd1 =
			BorderFactory.createBevelBorder(
				javax.swing.border.BevelBorder.RAISED);
		Border bd2 = BorderFactory.createEtchedBorder();
		Border bd3 = BorderFactory.createCompoundBorder(bd1, bd2);
		contentPane.setBorder(bd3);

		contentPane.add(new JLabel(" ", JLabel.CENTER), BorderLayout.NORTH);
		contentPane.add(
			new JLabel(new ImageIcon(image), JLabel.CENTER),
			BorderLayout.CENTER);
		contentPane.add(new JLabel(text, JLabel.CENTER), BorderLayout.SOUTH);
		contentPane.setBackground(java.awt.Color.white);
		setContentPane(contentPane);

	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		showSplashScreen();
	}

	private void showSplashScreen() {

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width / 3, dim.height / 3);
		setSize(dim.width / 3, dim.height / 3);
		setVisible(true);
		try {
			Thread.sleep(millis);
		}
		catch (InterruptedException e) {
		}
		setVisible(false);

	}

	public void setShowFor(int millis) {
		this.millis = millis;
	}

}
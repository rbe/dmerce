/*
 * Created on 06.08.2003
 *
 */
package com.wanci.dmerce.test.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * @author tw
 *
 */
public class LearnSwingTabs extends JFrame{

	JTabbedPane tp;

	public LearnSwingTabs() {
		
		super("JTabbedPane");
		// addWindowListener(new WindowClosingAdapter(true));
		tp = new JTabbedPane();
		for (int i = 1; i < 5; i++) {
			
			JPanel panel = new JPanel();
			panel.add(new JLabel("Karte " + i));
			JButton back = new JButton("Zurück");
			JButton next = new JButton("Weiter");
			next.addActionListener(new NextTabActionListener());
			panel.add(next);
			back.addActionListener(new BackTabActionListener());
			panel.add(back);
			tp.addTab("Tab" + i, panel);
			
		}
		
		getContentPane().add(tp, BorderLayout.CENTER);
		
	}

	class NextTabActionListener implements ActionListener {
		
		public void actionPerformed(ActionEvent event) {
			
			int tab = tp.getSelectedIndex();
			tab = (tab >= tp.getTabCount() -1 ? 0 : tab + 1);
			tp.setSelectedIndex(tab);
			// ((JPanel)tp.getSelectedComponent()).requestDefaultFocus();
			
		}
	}


	class BackTabActionListener implements ActionListener {
		
			public void actionPerformed(ActionEvent event) {
			
				int tab = tp.getSelectedIndex();
				tab = (tab >= tp.getTabCount() -1 ? 0 : tab - 1);
				tp.setSelectedIndex(tab);
				// ((JPanel)tp.getSelectedComponent()).requestDefaultFocus();
			
			}
		}

	public static void main(String[] args) {
		
		LearnSwingTabs frame = new LearnSwingTabs();
		frame.setLocation(100, 100);
		frame.setSize(300, 200);
		frame.setVisible(true);
		
	}
}

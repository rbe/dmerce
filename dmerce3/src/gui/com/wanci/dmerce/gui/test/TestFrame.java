/*
 * Datei angelegt am 02.12.2003
 */
package com.wanci.dmerce.gui.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.xml.bind.JAXBException;

import com.wanci.dmerce.forms.OPTION;
import com.wanci.dmerce.forms.ObjectFactory;
import com.wanci.dmerce.gui.forms.KeyValueDetailPanel;
import com.wanci.dmerce.gui.forms.SqlDetailPanel;

/**
 * @author Masanori Fujita
 */
public class TestFrame extends JFrame {

	public TestFrame() {
		super("Universelles Test-Fenster");
	}
	
	private void initSqlDetailPanel() {
		SqlDetailPanel manuelsPanel = new SqlDetailPanel();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(manuelsPanel, BorderLayout.CENTER);
		// Knopf zum Testen hinzufügen
		JButton testKnopf = new JButton("Test it!");
		testKnopf.addActionListener(new TestAction1(manuelsPanel));
		getContentPane().add(testKnopf, BorderLayout.EAST);
	}
	
	private void initKeyValueDetailPanel() {
		KeyValueDetailPanel manuelsPanel = new KeyValueDetailPanel();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(manuelsPanel, BorderLayout.PAGE_START);
		// Knopf zum Testen hinzufügen
		JButton testKnopf = new JButton("Test it!");
		testKnopf.addActionListener(new TestAction2(manuelsPanel));
		getContentPane().add(testKnopf, BorderLayout.EAST);
	}
	
	private class TestAction1 implements ActionListener {
		private SqlDetailPanel panel;
		public TestAction1(SqlDetailPanel panel) {
			this.panel = panel;
		}
		public void actionPerformed(ActionEvent e) {
			panel.setSql("SELECT * FROM anything");
			panel.setSqlkey("id");
			panel.setSqlvalue("name");
		}
	}
	
	private class TestAction2 implements ActionListener {
		private KeyValueDetailPanel panel;
		public TestAction2(KeyValueDetailPanel panel) {
			this.panel = panel;
		}
		public void actionPerformed(ActionEvent e) {
			try {
				ObjectFactory factory = new ObjectFactory();
				OPTION o1 = factory.createOPTION();
				o1.setKey("key1");
				o1.setValue("value1");
				OPTION o2 = factory.createOPTION();
				o2.setKey("key2");
				o2.setValue("value2");
				List list = new ArrayList();
				list.add(o1);
				list.add(o2);
				panel.setOptionList(list);
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static void main (String[] args) {
		TestFrame instance = new TestFrame();
		instance.initKeyValueDetailPanel();
		instance.setSize(300, 200);
		instance.show();
	}

}

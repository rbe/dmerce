/*
 * Created on Oct 28, 2003
 *
 */
package com.wanci.dmerce.test.swing;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author tw2
 *
 * @version $id$
 */
public class LearnSwingSimpleTree {

	public static void main(String[] args) {

		JFrame frame = new SimpleTreeFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.show();

	}
}

class SimpleTreeFrame extends JFrame {

	public SimpleTreeFrame() {

		setTitle("SimpleTree");
		setSize(WIDTH, HEIGHT);

		// Baummodelldaten einrichten

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("1Ci");
		DefaultMutableTreeNode division =
			new DefaultMutableTreeNode("Geschäftsführung");
		root.add(division);
		DefaultMutableTreeNode person = new DefaultMutableTreeNode("CE");
		division.add(person);
		person = new DefaultMutableTreeNode("RB");
		division.add(person);
		division = new DefaultMutableTreeNode("Programmierung");
		root.add(division);
		person = new DefaultMutableTreeNode("MM");
		division.add(person);
		person = new DefaultMutableTreeNode("PG");
		division.add(person);
		person = new DefaultMutableTreeNode("MF");
		division.add(person);
		division = new DefaultMutableTreeNode("Entwicklung");
		root.add(division);
		person = new DefaultMutableTreeNode("TW");
		division.add(person);
		person = new DefaultMutableTreeNode("RQ");
		division.add(person);

		// Baum konstruieren und ihn in einen Bildlaufbereich stellen

		JTree tree = new JTree(root);
		Container contentPane = getContentPane();
		contentPane.add(new JScrollPane(tree));

	}

	private static final int WIDTH = 300;
	private static final int HEIGHT = 200;

}
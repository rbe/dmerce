package com.wanci.dmerce.gui.ncc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.ncc.scanner.HostScanner;
import com.wanci.ncc.scanner.NccScanner;
import com.wanci.ncc.scanner.NetworkScanner;

/**
 * scanner gui
 * @author pg
 * @version $Id: Scanner.java,v 1.2 2004/02/02 09:41:47 rb Exp $
 *
 */
public class Scanner extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private JEditorPane htmlPane;

	/**
	 * 
	 */
	static boolean DEBUG = false;

	/**
	 * 
	 */
	NccScanner scanner;

	/**
	 * 
	 */
	private JMenuBar menuBar;

	/**
	 * 
	 */
	private JMenu menu;

	/**
	 * 
	 */
	private JMenuItem menuItem;

	/**
	 * 
	 *
	 */
	public Scanner() throws XmlPropertiesFormatException {

		super("1Ci(R) 1[NCC] 2.0 - http://www.1ci.com");

		//scanner stuff
		scanner = new NccScanner();
		if (!scanner.readXmlConfig()) {
			System.err.println("could not read config file. Aborting...");
			System.exit(103);
		}

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.err.println("exit");
				System.exit(0);
			}
		});

		//Create the nodes.
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Scanner");
		createNodes(top);

		//Create a tree that allows one selection at a time.
		final JTree tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION);

		//Listen for when the selection changes.
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node =
					(DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();

				if (node == null)
					return;

				Object nodeInfo = node.getUserObject();
				if (node.isLeaf()) {
					if (!scanner.isScanReady())
						displayText("Scan has not been performed yet. Please scan first");
					else {
						//host or network?
						String name = (String) nodeInfo;
						if (name.indexOf('/') > 0 || name.indexOf('-') > 0) {
							//network
							//host
							NetworkScanner ns = scanner.getNetworkByName(name);
							if (!(ns == null))
								displayText(ns.toHtml());

						}
						else {
							//host
							HostScanner hs = scanner.getHostByName(name);
							if (!(hs == null))
								displayText(hs.toHtml());
						}
					}

				}
				else {
					initHelp();
				}
				if (DEBUG) {
					System.out.println(nodeInfo.toString());
				}
			}
		});

		tree.putClientProperty("JTree.lineStyle", "Angled");

		//scrolling for tree 
		JScrollPane treeView = new JScrollPane(tree);

		//html viewing pane
		htmlPane = new JEditorPane("text/html", "initializing application...");
		htmlPane.setEditable(false);
		initHelp();
		JScrollPane htmlView = new JScrollPane(htmlPane);

		//Add the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treeView);
		splitPane.setRightComponent(htmlView);

		Dimension minimumSize = new Dimension(100, 50);
		htmlView.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);

		splitPane.setDividerLocation(150);
		splitPane.setPreferredSize(new Dimension(500, 300));

		//Add the split pane to this frame.
		getContentPane().add(splitPane, BorderLayout.CENTER);

		createMenuBar();

		JPanel jpane = new JPanel();

		JButton b1 = new JButton("Scan");
		b1.setVerticalTextPosition(AbstractButton.CENTER);
		b1.setHorizontalTextPosition(AbstractButton.LEFT);
		b1.setMnemonic(KeyEvent.VK_D);
		b1.setActionCommand("scan");
		b1.addActionListener(this);
		jpane.add(b1, BorderLayout.EAST);

		b1 = new JButton("Close");
		b1.setVerticalTextPosition(AbstractButton.CENTER);
		b1.setHorizontalTextPosition(AbstractButton.LEFT);
		b1.setMnemonic(KeyEvent.VK_D);
		b1.setActionCommand("close");
		//	b1.addActionListener(this);
		b1.addActionListener(this);
		jpane.add(b1, BorderLayout.CENTER);

		getContentPane().add(jpane, BorderLayout.SOUTH);
	}

	/**
	 * create menu 
	 */
	private void createMenuBar() {

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// File

		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription("file menu");
		menuBar.add(menu);

		menuItem = new JMenuItem("Save", KeyEvent.VK_S);
		menuItem.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
			"Saves results in XML file");
		menuItem.setActionCommand("saveResult");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem("Close", KeyEvent.VK_C);
		menuItem.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
			"close the application");
		menuItem.setActionCommand("close");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// Configuration

		menu = new JMenu("Configuration");
		menu.setMnemonic(KeyEvent.VK_O);
		menu.getAccessibleContext().setAccessibleDescription(
			"configuration menu");
		menuBar.add(menu);

		menuItem = new JMenuItem("tu dies");
		menuItem.getAccessibleContext().setAccessibleDescription(
			"konfiguriert was");
		menuItem.setActionCommand("tudies");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		// ?

		menu = new JMenu("?");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription("file menu");
		menuBar.add(menu);

		menuItem = new JMenuItem("About", KeyEvent.VK_C);
		menuItem.getAccessibleContext().setAccessibleDescription(
			"Displays about dialog");
		menuItem.setActionCommand("about");
		menuItem.addActionListener(this);
		menu.add(menuItem);

	}

	/**
	 * scanning stuff
	 */
	public void doScan() throws XmlPropertiesFormatException {
		scanner.discover();
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("scan")) {

			System.err.println("scan button pressed");
			try {
				doScan();
			}
			catch (XmlPropertiesFormatException e1) {
				e1.printStackTrace();
			}

		}
		else if (e.getActionCommand().equals("close")) {

			System.err.println("close button pressed");
			System.exit(0);

		}
		else {
			System.err.println(e.getActionCommand());
		}

	}

	/**
	 *
	 */
	void initHelp() {

		String s =
			"<strong>Welcome to the NCCScanner</strong>"
				+ "<p>This Application gathers data about your networks and hosts.</p>"
				+ "<p>It reads the scannable hosts and networks from the ncc.xml file in your etc directory</p>"
				+ "<p>Please make sure this File exists, otherwise you cannot start scanning</p>"
				+ "<br>"
				+ "<strong>Usage</strong>"
				+ "<p>To start scanning please press the \"Scan\" button, then go and drink some coffee."
				+ "While NCCScanner does its work, you cannot interact with the program.</p>"
				+ "<p>After the scan is ended, you can display the gathered data about each network and host by clicking it.</p>"
				+ "<p>ENJOY!</p>";
		displayText(s);

	}

	/**
	 * 
	 * @param url
	 */
	/*
	private void displayURL(URL url) {
		try {
			htmlPane.setPage(url);
		} catch (IOException e) {
			System.err.println("Attempted to read a bad URL: " + url);
		}
	}
	*/

	/**
	 * 
	 * @param str
	 */
	void displayText(String str) {
		htmlPane.setText(str);
	}

	/**
	 * 
	 * @param top
	 */
	private void createNodes(DefaultMutableTreeNode top) {

		DefaultMutableTreeNode category = null;
		DefaultMutableTreeNode item = null;

		category = new DefaultMutableTreeNode("Networks");
		top.add(category);
		//get networks
		Iterator iterator = scanner.getNetworks().iterator();
		while (iterator.hasNext()) {
			Vector network = (Vector) iterator.next();
			item = new DefaultMutableTreeNode(network.get(0));
			category.add(item);
		}

		category = new DefaultMutableTreeNode("Hosts");
		top.add(category);
		//get hosts
		iterator = scanner.getHosts().iterator();
		while (iterator.hasNext()) {
			Vector host = (Vector) iterator.next();
			item = new DefaultMutableTreeNode(host.get(0));
			category.add(item);
		}

	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)
		throws XmlPropertiesFormatException {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
		}

		JFrame frame = new Scanner();

		frame.pack();
		frame.setVisible(true);

	}

}
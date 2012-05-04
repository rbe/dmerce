package com.wanci.dmerce.dbmgr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * @author tw
 *
 * @version $Id: ShowDBTree.java,v 1.6 2003/10/30 17:51:32 pg Exp $
 * TODO: Fenster schliessen!
 */
public class ShowDBTree extends JInternalFrame implements ActionListener {

	/**
	 * application
	 */
	private DBmgr dbmgr;
	
	/**
	 * database variable
	 */
	private JTable table;
	
	/**
	 * tree representation
	 */
	private JTree tree;

	/**
	 * inner class: listener on tree selection
	 * if you change the selection in the tree show db-table
	 */
	public class MyTreeSelectionListener implements TreeSelectionListener {
		
		/**
		 * value has changed methos: show another db-table
		 */
		public void valueChanged(TreeSelectionEvent e){
			
			TreePath path = e.getNewLeadSelectionPath();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			
			if (node.isLeaf())
				showDBTable(node.getUserObject().toString());
		} //value changed
		
	} //inner class

	/**
	 * constructor
	 * @param title
	 * @param dbmgr
	 */
	public ShowDBTree(String title, DBmgr dbmgr) {

		super(title, true, true);
		setIconifiable(false);
		setMaximizable(true);
		setResizable(true);
		setBackground(Color.lightGray);

		//set application object
		this.dbmgr = dbmgr;
		
		PropertiesHandler ph = PropertiesHandler.getSingleInstance();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(ph.getName());

		//display table
		try {
			DefaultMutableTreeNode table = new DefaultMutableTreeNode();
			ResultSet rs = dbmgr.getTables();
			while (rs.next()) {
				table = new DefaultMutableTreeNode(rs.getString("TABLE_NAME"));
				root.add(table);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			//TODO: PopUp!
			System.out.println("The accessable Tables were not created!");
		}

		tree = new JTree(root);
		tree.addTreeSelectionListener(new MyTreeSelectionListener());
		table = new JTable();

		JSplitPane pane = new JSplitPane();
		pane.setLeftComponent( new JScrollPane(tree) );
		pane.setRightComponent( new JScrollPane(table) );
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add( pane, BorderLayout.CENTER );
			
	}

	/**
	 * show a database table
	 * @param name
	 */
	public void showDBTable(String name) {
		
		Vector columns = dbmgr.getColumns(name);
		Vector data = dbmgr.getData(name, columns.size());
		table.setModel(dbmgr.convertToTableModel(data, columns)); 
	
	}
	
	/**
	 * action handler
	 */
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("Load")) {
			System.out.println("LOAD");
		} else {
			//TODO: PopUp Fehler
			System.out.println("ERROR");
		}
	}

}
/*
 * Created on Dec 9, 2003
 */
package com.wanci.dmerce.gui.workflow;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.wanci.dmerce.gui.actions.CloseMainframeAction;
import com.wanci.dmerce.gui.tablemodel.FieldmapTableModel;
import com.wanci.dmerce.gui.util.DialogCreator;

/**
 * Klasse zum Anzeigen der FIELDMAP Attribute in einer Tabelle
 * in einem neuen Frame.
 * 
 * @author mm
 * @version $Id: FieldmapDetailPanel.java,v 1.7 2004/02/05 16:30:22 mm Exp $
 */
public class FieldmapDetailPanel extends JFrame {

	private JPanel fieldmapPanel;
	private FieldmapTableModel tableModel;
	private JButton closeButton;
	private JButton saveButton;
	private JFrame frame;
	private CloseMainframeAction miL;

	public FieldmapDetailPanel() {
		super();
		new JFrame("Fieldmap");
		setTitle("Fieldmap");

		fieldmapPanel = new JPanel(new BorderLayout(1,2));

		fieldmapPanel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Fieldmap Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		//neues TableModel erzeugen
		tableModel = new FieldmapTableModel();
		//neue Tabelle erzeugen
		JTable table = new JTable(tableModel);
		table.setPreferredScrollableViewportSize(new Dimension(450, 150));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		//ein ScrollPane erzeugen mit automatischer Scroll-Leiste
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setAutoscrolls(true);
		fieldmapPanel.add(scrollPane, BorderLayout.CENTER);

		//Buttons zum Schlieﬂen und Speichern erzeugen.
		closeButton = new JButton("schlieﬂen");
		
		saveButton = new JButton("speichern");

		//Fenster wird erst dann geschlossen, wenn es im Listener
		//aufgerufen wird.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		frame = this;

		//Listener f¸r das Fenster.
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int chosenOption = DialogCreator.closeDialog(frame);
				//TODO: das Fenster schlieﬂen + speichern!
				switch (chosenOption) {
					case JOptionPane.YES_OPTION :
						frame.dispose();
						break;
					
					case JOptionPane.NO_OPTION :
						frame.dispose();
						break;

					default :
						break;
				}
			}
		});
		
		//sobald man auf den Button klickt, wird closeDialog aufgerufen.
		this.closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int chosenOption = DialogCreator.closeDialog(frame);
//				TODO: das Fenster schlieﬂen + speichern!
				switch (chosenOption) {
					case JOptionPane.YES_OPTION :
						frame.dispose();
						break;
					
					case JOptionPane.NO_OPTION :
						frame.dispose();
						break;

					default :
						break;
				}

			}
		});
		
		fieldmapPanel.add(closeButton, BorderLayout.SOUTH);
		//fieldmapPanel.add(saveButton, BorderLayout.SOUTH);
		getContentPane().add(fieldmapPanel);
	}
	
	/**
	 * @return Returns the JFrame of this class.
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * @param frame
	 *            The JFrame of this class to set.
	 */
	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	/**
	 * @param fieldmapList
	 *            The List of FIELDMAP objects to set.
	 */
	public void setFieldmapList(List fieldmapList) {
		tableModel.setFieldmapList(fieldmapList);
	}
}
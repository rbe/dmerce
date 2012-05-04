/*
 * Created on Nov 25, 2003
 */
package com.wanci.dmerce.gui.workflow;

import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.wanci.dmerce.gui.listener.TransitionTableListener;
import com.wanci.dmerce.gui.tablemodel.TransitionTableModel;
import com.wanci.dmerce.workflow.xmlbridge.ACTION;
import com.wanci.dmerce.workflow.xmlbridge.CONDITION;

/**
 * Erzeugt ein Tabellenlayout, in dem schon mal die Sruktur des Panels
 * festgelegt wird. Die Werte werden vom Mainframe aus übergeben und in der
 * Tabelle gefüllt.
 * 
 * @author mm
 * @version $Id: TransitionDetailPanel.java,v 1.10 2004/02/17 16:51:02 mm Exp $
 */
public class TransitionDetailPanel extends JPanel {

	private TransitionTableModel tableModel;
	private ConditionActionDetailPanel caPanel;
	private JTable table;

	private ACTION action;
	private CONDITION condition;
	
	private JInternalFrame mainInternalFrame;
	
	/**
	 * GridBagLayout setzen und die einzelnen Tabellenfelder erzeugen und
	 * beschriften.
	 * 
	 * @param mainInternalFrame
	 *            The internal Frame to set.
	 */
	public TransitionDetailPanel(JInternalFrame mainInternalFrame) {
		super();
		this.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("TRANSITION Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		//neues TableModel erzeugen
		tableModel = new TransitionTableModel();
		table = new JTable(tableModel);
		table.setPreferredScrollableViewportSize(new Dimension(450, 100));
		table.setToolTipText(
			"Um die Condition und Action Attribute zu betrachten bzw. zu bearbeiten, die Zeile doppelt klicken.");

		//mainInternalFrame mitgeben, damit dieses Fenster nicht bearbeitet werden kann
		//während das 2. Fenster von den Transitions geöffnet ist!
		TransitionTableListener tListener = new TransitionTableListener(mainInternalFrame);
		table.addMouseListener(tListener);

		//Bildlaufleiste hinzufügen
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);

		caPanel = new ConditionActionDetailPanel();

		tListener.setTableModel(tableModel);
		tListener.setComponent(caPanel);
		
		mainInternalFrame.getContentPane().add(caPanel);
		
		try {
			mainInternalFrame.setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param transitionList
	 *            The List of the TRANSITION to set.
	 */
	public void setTransitionList(List transitionList) {
		tableModel.setTransitionList(transitionList);
	}
}
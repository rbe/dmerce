/*
 * Created on Jan 14, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package com.wanci.dmerce.gui.listener;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.JTable;

import com.wanci.dmerce.gui.tablemodel.TransitionTableModel;
import com.wanci.dmerce.gui.workflow.ConditionActionDetailPanel;
import com.wanci.dmerce.workflow.xmlbridge.TRANSITION;

/**
 * Listener, der auf die Maus reagiert. Sobald man mit der Maus eine bestimmte
 * Anzahl von Clicks tätigt, wird eine Aktion ausgeführt.
 * 
 * @author mm
 * @version $Id: TransitionTableListener.java,v 1.3 2004/02/17 16:50:26 mm Exp $
 */
public class TransitionTableListener implements MouseListener {

	private TransitionTableModel tableModel;
	private ConditionActionDetailPanel conditionActionPanel;
	private JInternalFrame mainInternalFrame;

	public TransitionTableListener(JInternalFrame mainInternalFrame) {
		this.mainInternalFrame = mainInternalFrame;
	}

	/**
	 * wenn die Maus genau 2 mal geklickt wurde, öffnet sich ein neues Fenster
	 * mit den Conditions und Actions.
	 */
	public void mouseClicked(MouseEvent e) {

		JTable t = (JTable) e.getSource();
		TRANSITION tr =
			(TRANSITION) tableModel.getValueAtIndex(t.getSelectedRow());

		if (e.getClickCount() == 2) {
			
			System.out.println("mouse wurde 2x geklickt.");

			//Die Conditions setzen, damit die JComboBox die richtigen
			//Werte enthält
			try {
				if (tr.getCondition().getType() != null) {

					if (tr.getCondition().getType().equals("buttonpressed")) {
						conditionActionPanel.setCobConditions("buttonpressed");
						conditionActionPanel.setCondition(tr.getCondition());

					} else if (tr.getCondition().getType().equals("equals")) {

						conditionActionPanel.setCobConditions("equals");
						conditionActionPanel.setConditionParameterList(
							tr.getCondition().getParameter());
						conditionActionPanel.setConditionFormfieldList(
							tr.getCondition().getFormfield());
						conditionActionPanel.setCondition(tr.getCondition());

					} else if (tr.getCondition().getType().equals("javaclass"))
						conditionActionPanel.setCobConditions("javaclass");
						conditionActionPanel.setCondition(tr.getCondition());
					
				} else {
					conditionActionPanel.setCobConditions("keine Condition");
				}

			} catch (Exception ex) {
				conditionActionPanel.setCobConditions("keine Condition");
			}

			//Die Actions setzen, damit die JComboBox die richtigen
			//Werte enthält
			try {

				if (tr.getAction().getType() != null) {

					if (tr.getAction().getType().equals("maintain")) {

						conditionActionPanel.setCobActions("maintain");
						conditionActionPanel.setActionFormfieldList(
							tr.getAction().getFormfield());

					} else if (tr.getAction().getType().equals("set")) {

						conditionActionPanel.setCobActions("set");
						conditionActionPanel.setActionParameterList(
							tr.getAction().getParameter());

					} else if (tr.getAction().getType().equals("javaclass")) {

						conditionActionPanel.setCobActions("javaclass");
						conditionActionPanel.setAction(tr.getAction());

					} else if (tr.getAction().getType().equals("callproc")) {

						conditionActionPanel.setCobActions("callproc");
						conditionActionPanel.setAction(tr.getAction());
						conditionActionPanel.setActionFormfieldList(tr.getAction().getFormfield());
						conditionActionPanel.setActionParameterList(tr.getAction().getParameter());
					}

				} else {
					conditionActionPanel.setCobActions("keine Action");
				}		

			} catch (Exception ex) {
				conditionActionPanel.setCobActions("keine Action");
			}

			if (tr != null)
				conditionActionPanel.setTransition(tr);
			conditionActionPanel.setSize(new Dimension(500, 300));
			conditionActionPanel.pack();
			conditionActionPanel.show();
			conditionActionPanel.setVisible(true);
			try {
				//TODO: wenn conditionActionPanel aktiv ist, dann muss das erste
				// interne Fenster nicht veränderbar sein! also modal!
				conditionActionPanel.setSelected(true);
				if (conditionActionPanel.isSelected()) {
					mainInternalFrame.setEnabled(true);
					mainInternalFrame.updateUI();
				}
				else {
					mainInternalFrame.setEnabled(false);
					mainInternalFrame.updateUI();
				}
			} catch (PropertyVetoException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * machen nichts
	 */
	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * @param tableModel
	 */
	public void setTableModel(TransitionTableModel tableModel) {
		this.tableModel = tableModel;
	}

	/**
	 * @param panel
	 *            The ConditionActionDetailPanel to set.
	 */
	public void setComponent(ConditionActionDetailPanel panel) {
		this.conditionActionPanel = panel;
	}
}
/*
 * Created on Nov 21, 2003
 */
package com.wanci.dmerce.gui.listener;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.wanci.dmerce.gui.ClearDetailPanel;
import com.wanci.dmerce.gui.Mainframe;
import com.wanci.dmerce.gui.tree.JAXBWrapper;
import com.wanci.dmerce.gui.workflow.PageDetailPanel;
import com.wanci.dmerce.workflow.xmlbridge.PAGE;
import com.wanci.dmerce.workflow.xmlbridge.WORKFLOW;
import com.wanci.dmerce.workflow.xmlbridge.Workflows;

/**
 * Ändert die Attribut-Werte im rechten Fenster, sobald sich etwas im linken
 * Fenster (JTree) ändert
 * 
 * @author mm
 * @version $Id: WorkflowTreeSelectionListener.java,v 1.1 2003/11/21 17:41:35
 *          mm Exp $
 */
public class WorkflowTreeSelectionListener implements TreeSelectionListener {

	private Mainframe mainframe;

	private static boolean flag = false;

	/**
	 * @param textField
	 */
	public WorkflowTreeSelectionListener(Mainframe mf) {

		this.mainframe = mf;
	}

	/**
	 * sobald sich im JTree etwas ändert oder aktiviert wird, werden die Werte
	 * neu gesetzt.
	 */
	public void valueChanged(TreeSelectionEvent e) {

		JAXBWrapper wrapper = (JAXBWrapper) e.getPath().getLastPathComponent();

		ClearDetailPanel clearPanel;
		
		if (getFlag()) {
			System.out.println("WorkflowTreeSelectionListener: flag == " + getFlag());
			//TODO: Dialog aufrufen + speichern! Action ausführen!
		}

		if (wrapper.getWrappedObject() instanceof Workflows) {
			//Feld leer lassen
			mainframe.clearWindow("Workflows Attribute");
		}

		else if (wrapper.getWrappedObject() instanceof WORKFLOW) {
			//Feld leer lassen
			mainframe.clearWindow("WORKFLOW Attribute");
		}

		else if (wrapper.getWrappedObject() instanceof PAGE) {
			PAGE page = (PAGE) wrapper.getWrappedObject();

			//pagePanel holen
			PageDetailPanel pagePanel = mainframe.getPagePanel();

			//Werte setzen
			if (page != null)
				pagePanel.setPage(page);

			//Formmap Werte aufrufen
			if (page.getFormmap() == null)
				pagePanel.setFmpVisibility(false);
			else {
				pagePanel.setFormmap(page.getFormmap());
				pagePanel.setFieldmapList(page.getFormmap().getFieldmap());
				pagePanel.setFmpVisibility(true);
			}

			//Transition Werte aufrufen
			if (!page.getTransition().isEmpty()) {
				pagePanel.setTransitionList(page.getTransition());
				pagePanel.setTpVisibility(true);
			}
			else {
				pagePanel.setTpVisibility(false);
			}

			//pagePanel zu detailPanel adden
			mainframe.showPageDetails();
		}
	}

	/**
	 * @param b
	 *            Wert zum Setzen ob sich was geändert hat.
	 */
	public static void setFlag(boolean b) {
		flag = b;
		FormTreeSelectionListener.setFlag(b);
	}

	/**
	 * @return Returned, ob sich was im JTextField geändert hat.
	 */
	public static boolean getFlag() {
		return flag;
	}
}
/*
 * Created on Nov 21, 2003
 */
package com.wanci.dmerce.gui.listener;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.wanci.dmerce.forms.FIELD;
import com.wanci.dmerce.forms.FORM;
import com.wanci.dmerce.forms.Forms;
import com.wanci.dmerce.gui.Mainframe;
import com.wanci.dmerce.gui.forms.FieldDetailPanel;
import com.wanci.dmerce.gui.forms.FormDetailPanel;
import com.wanci.dmerce.gui.tree.JAXBWrapper;
import com.wanci.dmerce.gui.util.DialogCreator;

/**
 * Ändert die Attribut-Werte im rechten Fenster, sobald sich etwas im linken
 * Fenster (JTree) ändert
 * 
 * @author mm
 * @version $Id: FormTreeSelectionListener.java,v 1.1 2003/11/21 17:41:35 mm
 *          Exp $
 */
public class FormTreeSelectionListener implements TreeSelectionListener {

	private Mainframe mainframe;

	private static boolean flag = false;

	/**
	 * @param mainframe
	 */
	public FormTreeSelectionListener(Mainframe mainframe) {

		this.mainframe = mainframe;
	}

	/**
	 * sobald sich im JTree etwas ändert oder aktiviert wird, werden die Werte
	 * neu gesetzt.
	 */
	public void valueChanged(TreeSelectionEvent e) {

		FormDetailPanel formPanel = mainframe.getFormPanel();
		FieldDetailPanel fieldPanel = mainframe.getFieldPanel();
		
		if (formPanel != null && formPanel.getFlag()) {
			setFlag(formPanel.getFlag());
			int chosenOption = DialogCreator.closeDialog(mainframe);
			
		}
		//if (fieldPanel != null && fieldPanel.getFlag())
		//	;

		JAXBWrapper wrapper = (JAXBWrapper) e.getPath().getLastPathComponent();

		if (wrapper.getWrappedObject() instanceof Forms) {
			//Feld leer lassen
			mainframe.clearWindow("Forms Attribute");
		}

		else if (wrapper.getWrappedObject() instanceof FORM) {
			FORM form = (FORM) wrapper.getWrappedObject();

			//formPanel holen
			//FormDetailPanel 
			formPanel = mainframe.getFormPanel();
			
			//Werte im FormDetailPanel setzen
			//wenn flag nicht gesetzt ist, neues form anzeigen!
			if (form != null)
				formPanel.setForm(form);
			
			//formPanel zu detailPanel adden
			mainframe.showFormPanel();
		}

		else if (wrapper.getWrappedObject() instanceof FIELD) {
			FIELD field = (FIELD) wrapper.getWrappedObject();

			//fieldPanel holen
			//FieldDetailPanel 
			fieldPanel = mainframe.getFieldPanel();

			//Werte im FieldDetailPanel setzen
			if (field != null)
				fieldPanel.setField(field);
			
			//Type setzen
			if (field.getType() != null)
				fieldPanel.setType(field.getType());
			
			//OPTIONS Objekte!
			if (field.getOptions() == null) {
				fieldPanel.setOptions("keine options");
			}
			else if (field.getOptions().getSql() != null) {
				fieldPanel.setFieldOptions(field.getOptions());
				fieldPanel.setOptions("sql");
			}
			else if (
				field.getOptions().getSql() == null
					&& !field.getOptions().getOption().isEmpty()) {
				fieldPanel.setOptions("key-value");
				fieldPanel.setFieldOptionList(field.getOptions().getOption());
			}

			//CONSTRAINTS Objekte
			if (field.getConstraints() == null) {
				fieldPanel.setConstraints("keine constraints");
			}
			else {
				fieldPanel.setConstraintList(
					field.getConstraints().getConstraint());
				fieldPanel.setConstraints("constraints");
			}

			mainframe.showFieldPanel();
		}
	}

	/**
	 * @param b
	 *            Wert zum Setzen ob sich was geändert hat.
	 */
	public static void setFlag(boolean b) {
		flag = b;
		WorkflowTreeSelectionListener.setFlag(b);
	}

	/**
	 * @return Returned, ob sich was im JTextField geändert hat.
	 */
	public static boolean getFlag() {
		return flag;
	}
}
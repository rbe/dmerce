/*
 * Created on Nov 25, 2003
 */
package com.wanci.dmerce.gui.workflow;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wanci.dmerce.workflow.xmlbridge.FORMMAP;
import com.wanci.dmerce.workflow.xmlbridge.PAGE;

/**
 * Erzeugt ein Tabellenlayout, in dem schon mal die Sruktur des Panels
 * festgelegt wird. Die Werte werden vom Mainframe aus gefüllt. Es werden auf
 * der linken Seite JLabels und auf der rechten Seite JTextFields bzw.
 * JCheckBoxen benutzt.
 * 
 * @author mm
 * @version $Id: PageDetailPanel.java,v 1.5 2004/02/17 16:51:02 mm Exp $
 */
public class PageDetailPanel extends Box {

	private JTextField tfTemplate;
	private JTextField tfFormid;
	private JCheckBox cbEditable;

	private JPanel topPanel;
	private TransitionDetailPanel transitionPanel;
	private FormmapDetailPanel formmapPanel;
	private FieldmapDetailPanel fieldmapPanel;
	private List transitionList;
	private FORMMAP formmap;
	private List fieldmapList;
	private PAGE page;

	/**
	 * Konstruktor GridBagLayout setzen und die einzelnen Tabellenfelder
	 * erzeugen und beschriften.
	 */
	public PageDetailPanel(JInternalFrame mainInternalFrame) {
		super(BoxLayout.Y_AXIS);

		this.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("PAGE Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		//neues JPanel mit Layout für die Felder erzeugen.
		topPanel = new JPanel(new GridBagLayout());
		topPanel.setAlignmentX(0);

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;

		JLabel leftText;

		//komplette linke Seite erzeugen
		//Template Feld links erzeugen
		c.gridx = 0;
		c.gridy = 0;
		leftText = new JLabel();
		leftText.setText("Template:");
		topPanel.add(leftText, c);

		//formId Feld links erzeugen
		c.gridx = 0;
		c.gridy = 1;
		leftText = new JLabel();
		leftText.setText("Form-ID:");
		topPanel.add(leftText, c);

		//editable Feld links erzeugen
		c.gridx = 0;
		c.gridy = 2;
		leftText = new JLabel();
		leftText.setText("editable:");
		topPanel.add(leftText, c);

		//rechte Seite doppelt so gross machen
		c.weightx = 2;

		//komplette rechte Seite erzeugen
		//Template Feld rechts erzeugen
		c.gridx = 1;
		c.gridy = 0;
		tfTemplate = new JTextField(20);
		topPanel.add(tfTemplate, c);

		//formId Feld rechts erzeugen
		c.gridx = 1;
		c.gridy = 1;
		tfFormid = new JTextField(20);
		topPanel.add(tfFormid, c);

		//editable Feld rechts erzeugen
		c.gridx = 1;
		c.gridy = 2;
		cbEditable = new JCheckBox();
		topPanel.add(cbEditable, c);

		add(topPanel);
		
		formmapPanel = new FormmapDetailPanel();
		formmapPanel.setAlignmentX(0);
		add(formmapPanel);
		
		transitionPanel = new TransitionDetailPanel(mainInternalFrame);
		transitionPanel.setAlignmentX(0);
		add(transitionPanel);
	}	

	/**
	 * formmapPanel wird nur angezeigt, wenn getFormmap
	 * nicht null ist.
	 * 
	 * @param b
	 *            The visibility to set.
	 */
	public void setFmpVisibility(boolean b) {
		formmapPanel.setVisible(b);
	}

	/**
	 * transitionPanel wird nur angezeigt, wenn getTransition
	 * nicht null ist.
	 * 
	 * @param b
	 *            The visibility to set.
	 */
	public void setTpVisibility(boolean b) {
		transitionPanel.setVisible(b);
	}

	/**
	 * @return Returns the textfield template.
	 */
	public String getTemplate() {
		return page.getTemplate();
	}

	/**
	 * @param template
	 *            The textfield template to set.
	 */
	public void setTemplate(String template) {
		this.tfTemplate.setText(template);
		page.setTemplate(template);
	}

	/**
	 * @return Returns the textfield formId.
	 */
	public String getFormId() {
		return page.getFormid();
	}

	/**
	 * @param formId
	 *            The textfield formId to set.
	 */
	public void setFormId(String formId) {
		this.tfFormid.setText(formId);
		page.setFormid(formId);
	}

	/**
	 * @return Returns the checkbox editing.
	 */
	public boolean isEditable() {
		return page.isEditable();
	}

	/**
	 * @param editable
	 *            The checkbox editing to set.
	 */
	public void setEditable(boolean editable) {
		this.cbEditable.setSelected(editable);
		page.setEditable(editable);
	}

	/**
	 * @return Returns the List of TRANSITION objects.
	 */
	public List getTransition() {
		return transitionList;
	}

	/**
	 * @param transitionList
	 * 			  The List of TRANSITION objects to set.
	 */
	public void setTransitionList(List transitionList) {
		this.transitionList = transitionList;
		this.transitionPanel.setTransitionList(transitionList);
	}

	/**
	 * @return Returns the FORMMAP object.
	 */
	public FORMMAP getFormmap() {
		return formmap;
	}

	/**
	 * @param formmap
	 * 		      The FORMMAP object to set.
	 */
	public void setFormmap(FORMMAP formmap) {
		this.formmap = formmap;
		this.formmapPanel.setFormmap(formmap);
	}

	/**
	 * @param fieldmapList
	 * 			  The List of FIELDMAP objects to set.
	 */
	public void setFieldmapList(List fieldmapList) {
		this.fieldmapList = fieldmapList;
		this.formmapPanel.setFieldmapList(fieldmapList);
	}

	/**
	 * @return Returns the List of FIELDMAP objects.
	 */
	public List getFieldmapList() {
		return fieldmapList;
	}

	/**
	 * @param page
	 *            The PAGE object to set.
	 */
	public void setPage(PAGE page) {
		this.page = page;
		setTemplate(page.getTemplate());
		setFormId(page.getFormid());
		setEditable(page.isEditable());
	}
}
/*
 * Created on Nov 26, 2003
 */
package com.wanci.dmerce.gui.forms;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wanci.dmerce.forms.FIELD;
import com.wanci.dmerce.forms.OPTIONS;
import com.wanci.dmerce.gui.listener.ConstraintsListener;
import com.wanci.dmerce.gui.listener.OptionsListener;

/**
 * Erzeugt ein Tabellenlayout, in dem schon mal die Sruktur des Panels
 * festgelegt wird. Die Werte werden vom Mainframe ausgefüllt. Es werden auf
 * der linken Seite JLabels und auf der rechten Seite JCheckBox und JTextField
 * benutzt.
 * 
 * FieldDetailPanel ist im rechten Hauptfenster.
 * 
 * @author mm
 * @version $Id: FieldDetailPanel.java,v 1.8 2004/03/24 17:46:32 rb Exp $
 */
public class FieldDetailPanel extends Box {

	private JCheckBox cbRequired;

	private JTextField tfDescription;

	private JComboBox cobType;

	private JComboBox cobOptions;

	private JComboBox cobConstraints;

	private JPanel topPanel;

	private JPanel optionCards;

	private JPanel constraintCards;

	private JPanel emptyPanel;

	private SqlDetailPanel sqlPanel;

	private KeyValueDetailPanel keyValuePanel;

	private ConstraintDetailPanel constraintPanel;

	private OPTIONS fieldOptions;

	private List fieldOptionList;

	private List constraintList;

	private FIELD field;

	private boolean flag = false;

	/**
	 * Konstruktor GridBagLayout setzen und die einzelnen Tabellenfelder
	 * erzeugen und beschriften.
	 */
	public FieldDetailPanel() {
		super(BoxLayout.Y_AXIS);

		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createTitledBorder("Field Attribute"), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));

		//neues JPanel mit Layout für die Felder erzeugen
		topPanel = new JPanel(new GridBagLayout());
		topPanel.setAlignmentX(0);

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;

		JLabel leftText;

		//typen, die ausgewählt werden können
		String[] types =
			{
					"string", "number", "boolean", "file"
			};
		//optionen, die ausgewählt werden können
		String[] options =
			{
					"keine options", "sql", "key-value"
			};
		//constraints, vorhanden oder nicht
		String[] constraints =
			{
					"keine constraints", "constraints"
			};

		//komplette linke Seite erstellen
		//required Feld links
		c.gridx = 0;
		c.gridy = 0;
		leftText = new JLabel("required:");
		leftText.setToolTipText("Benötigtes Feld, wenn aktiviert.");
		topPanel.add(leftText, c);

		//description Feld links
		c.gridx = 0;
		c.gridy = 1;
		leftText = new JLabel("Description:");
		topPanel.add(leftText, c);

		//type Feld links
		c.gridx = 0;
		c.gridy = 2;
		leftText = new JLabel("Typ:");
		topPanel.add(leftText, c);

		//options Feld links
		c.gridx = 0;
		c.gridy = 3;
		leftText = new JLabel("options:");
		topPanel.add(leftText, c);

		//constraints Feld links
		c.gridx = 0;
		c.gridy = 4;
		leftText = new JLabel("constraints");
		topPanel.add(leftText, c);

		//komplette rechte Seite erstellen
		//required Feld rechts
		c.gridx = 1;
		c.gridy = 0;
		cbRequired = new JCheckBox();
		topPanel.add(cbRequired, c);

		//description Feld rechts
		c.gridx = 1;
		c.gridy = 1;
		tfDescription = new JTextField(20);
		tfDescription.setToolTipText("Geben Sie eine Beschreibung ein.");
		topPanel.add(tfDescription, c);

		//type Feld rechts
		c.gridx = 1;
		c.gridy = 2;
		cobType = new JComboBox(types);
		cobType.setToolTipText("Wählen Sie einen Typ aus.");
		//cobType.addItemListener(this);
		topPanel.add(cobType, c);

		//options Feld rechts
		c.gridx = 1;
		c.gridy = 3;
		cobOptions = new JComboBox(options);
		cobOptions.setToolTipText("Wählen Sie eine Option aus.");
		OptionsListener oListener = new OptionsListener();
		cobOptions.addItemListener(oListener);
		topPanel.add(cobOptions, c);

		//constraints Feld rechts
		c.gridx = 1;
		c.gridy = 4;
		cobConstraints = new JComboBox(constraints);
		cobConstraints.setToolTipText("Wählen Sie die Constraints aus.");
		ConstraintsListener cListener = new ConstraintsListener();
		cobConstraints.addItemListener(cListener);
		topPanel.add(cobConstraints, c);

		add(topPanel);

		emptyPanel = new JPanel();

		// Detail-Panels erzeugen
		sqlPanel = new SqlDetailPanel();
		sqlPanel.setAlignmentX(0);
		sqlPanel.setMaximumSize(new Dimension(750, 150));

		keyValuePanel = new KeyValueDetailPanel();
		keyValuePanel.setAlignmentX(0);
		keyValuePanel.setMaximumSize(new Dimension(750, 150));

		/*
		 * neues JPanel mit CardLayout erstellen.
		 */
		optionCards = new JPanel(new CardLayout());
		optionCards.setVisible(false);
		optionCards.setAlignmentX(0);
		optionCards.setMaximumSize(new Dimension(750, 150));
		optionCards.add(emptyPanel, "keine options");
		optionCards.add(sqlPanel, "sql");
		optionCards.add(keyValuePanel, "key-value");

		oListener.setComponent(optionCards);

		add(optionCards);

		constraintPanel = new ConstraintDetailPanel();
		constraintPanel.setAlignmentX(0);
		constraintPanel.setMaximumSize(new Dimension(750, 150));

		//constraintCards nicht sichtbar, erst wenn sich der Wert ändert
		constraintCards = new JPanel(new CardLayout());
		constraintCards.setVisible(false);
		constraintCards.setAlignmentX(0);
		constraintCards.setMaximumSize(new Dimension(750, 150));
		constraintCards.add(emptyPanel, "keine constraints");
		constraintCards.add(constraintPanel, "constraints");

		cListener.setComponent(constraintCards);

		add(constraintCards);
	}

	/**
	 * @return Returns the checkbox cbRequired field.
	 */
	public String getRequired() {

		if (cbRequired.isSelected())
			return field.isRequired() ? "true" : "false";
		else
			return field.isRequired() ? "true" : "false";
	}

	/**
	 * @param cbRequired
	 *            The checkbox field cbRequired to set.
	 */
	public void setRequired(boolean required) {
		this.cbRequired.setSelected(required);
		if (required == true)
			field.setRequired(true);
		else
			field.setRequired(false);
	}

	/**
	 * @return Returns the texfield tfDescription.
	 */
	public String getDescription() {
		return field.getDescription();
	}

	/**
	 * @param tfDescription
	 *            The textfield tfDescription to set.
	 */
	public void setDescription(String description) {
		this.tfDescription.setText(description);
		field.setDescription(description);
	}

	/**
	 * @return Returns the ComboBox type.
	 */
	public String getType() {
		return cobType.getSelectedItem().toString();
	}

	/**
	 * @param tfType
	 *            The ComboBox type to set.
	 */
	public void setType(String type) {
		this.cobType.setSelectedItem(type);
		field.setType(type);
	}

	/**
	 * @return Returns the ComboBox options.
	 */
	public String getOptions() {
		return cobOptions.getSelectedItem().toString();
	}

	/**
	 * @param cobOptions
	 *            The ComboBox options to set.
	 */
	public void setOptions(String options) {
		this.cobOptions.setSelectedItem(options);
	}

	/**
	 * @param options
	 *            The OPTIONS object to set.
	 */
	public void setFieldOptions(OPTIONS options) {
		this.fieldOptions = options;
		this.sqlPanel.setFieldOptions(options);
	}

	/**
	 * @return Returns the OPTIONS object.
	 */
	public OPTIONS getFieldOptions() {
		return fieldOptions;
	}

	/**
	 * @param fieldOptionList
	 *            The List of OPTIONS to set.
	 */
	public void setFieldOptionList(List fieldOptionList) {
		this.fieldOptionList = fieldOptionList;
		this.keyValuePanel.setOptionList(fieldOptionList);
	}

	/**
	 * @return Returns the OPTIONS objects.
	 */
	public List getFieldOptionList() {
		return fieldOptionList;
	}

	/**
	 * @param constraintList
	 *            The List of CONSTRAINT objects to set.
	 */
	public void setConstraintList(List constraintList) {
		this.constraintList = constraintList;
		this.constraintPanel.setConstraintList(constraintList);
	}

	/**
	 * @return Returns the CONSTRAINT objects.
	 */
	public List getConstraintList() {
		return constraintList;
	}

	/**
	 * @param constraints
	 *            The ComboBox constraints to set.
	 */
	public void setConstraints(String constraints) {
		this.cobConstraints.setSelectedItem(constraints);
	}

	/**
	 * @param field
	 *            The FIELD object to set.
	 */
	public void setField(FIELD field) {

		this.field = field;

		if (!field.isRequired())
			setRequired(false);
		else if (field.isRequired())
			setRequired(true);

		setDescription(field.getDescription());
	}

	/**
	 * @return Returns the FIELD object.
	 */
	public FIELD getField() {
		return field;
	}

	/**
	 * @param b
	 *            Wert zum Setzen ob sich was geändert hat.
	 */
	public void setFlag(boolean b) {
		this.flag = b;
	}

	/**
	 * @return Returned, ob sich was im JTextField geändert hat.
	 */
	public boolean getFlag() {
		return flag;
	}

}
/*
 * Created on Jan 6, 2004
 */
package com.wanci.dmerce.gui.workflow;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyVetoException;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.wanci.dmerce.gui.listener.ActionsListener;
import com.wanci.dmerce.gui.listener.ConditionListener;
import com.wanci.dmerce.gui.workflow.action.CallprocActionDetailPanel;
import com.wanci.dmerce.gui.workflow.action.FormfieldActionDetailPanel;
import com.wanci.dmerce.gui.workflow.action.JavaclassActionDetailPanel;
import com.wanci.dmerce.gui.workflow.action.ParameterActionDetailPanel;
import com.wanci.dmerce.gui.workflow.condition.JavaclassConditionDetailPanel;
import com.wanci.dmerce.gui.workflow.condition.ParameterFormfieldConditionDetailPanel;
import com.wanci.dmerce.gui.workflow.condition.TypeValueConditionDetailPanel;
import com.wanci.dmerce.workflow.xmlbridge.ACTION;
import com.wanci.dmerce.workflow.xmlbridge.CONDITION;
import com.wanci.dmerce.workflow.xmlbridge.FORMFIELD;
import com.wanci.dmerce.workflow.xmlbridge.PARAMETER;
import com.wanci.dmerce.workflow.xmlbridge.TRANSITION;

/**
 * ein eigenes Fenster (JFrame) in dem die Conditions und Actions angezeigt
 * werden, falls diese vorhanden sind.
 * 
 * @author mm
 * @version $Id: ConditionActionDetailPanel.java,v 1.3 2004/01/14 16:25:22 mm
 *          Exp $
 */
public class ConditionActionDetailPanel extends JInternalFrame {

	private JPanel caPanel;
	//leeres Panel
	private JPanel emptyPanel;
	//ComboBox für ConditionPanel
	private JPanel cobConditionPanel;
	private JPanel conditionPanel;
	//ComboBox für ActionPanel
	private JPanel cobActionPanel;
	private JPanel actionPanel;

	private JPanel conditionCards;
	private JPanel actionCards;

	private JSplitPane splitPane;

	private TypeValueConditionDetailPanel buttonpressedConditionPanel;
	private JavaclassConditionDetailPanel jcConditionPanel;
	private ParameterFormfieldConditionDetailPanel equalsConditionPanel;

	private ParameterActionDetailPanel parameterActionPanel;
	private FormfieldActionDetailPanel formfieldActionPanel;
	private JavaclassActionDetailPanel jcActionPanel;
	private CallprocActionDetailPanel callprocActionPanel;

	private JComboBox cobCondition;
	private JComboBox cobAction;
	private ACTION action;
	private CONDITION condition;
	private TRANSITION transition;
	private String JcTfJavaclass;
	JFrame frame;

	public ConditionActionDetailPanel() {

		super("Condition and Action", true, true, false);
		try {
			setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}

		//neues JPanel mit Layout die Condition und Action erzeugen
		caPanel = new JPanel(new BorderLayout(1, 1));

		caPanel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(
					"Condition and Action Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		//Fenster wird erst dann geschlossen, wenn es im Listener
		//aufgerufen wird.
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		//Listener für das Fenster.
		/*this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int chosenOption = DialogCreator.closeDialog(frame);
				switch (chosenOption) {
					case JOptionPane.YES_OPTION :
						//TODO: speichern
						frame.dispose();
						break;
					
					case JOptionPane.NO_OPTION :
						//nicht speichern, alte Werte behalten
						frame.dispose();
						break;
					
					default :
						break;
				}
			}
		});*/

		String str = "";
		if (action != null) {
			action = getTransition().getAction();
			if (!action.getType().equals(null) || action.getType() != null) {
				str = action.getType();
			}
		}

		//Layout
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;

		/*
		 * Condition
		 */
		//für die Condition auf der linken Seite
		JLabel conditionText;
		cobConditionPanel = new JPanel(new GridBagLayout());
		cobConditionPanel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Condition-Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		cobConditionPanel.setAlignmentX(0);

		conditionPanel = new JPanel(new BorderLayout());

		//conditions, die in der ComboBox ausgewählt werden können
		String[] conditions =
			{ "keine Condition", "buttonpressed", "equals", "javaclass" };

		//komplette linke Seite erstellen
		//Condition-Typ links
		c.gridx = 0;
		c.gridy = 0;
		conditionText = new JLabel("typ:");
		conditionText.setToolTipText(
			"Art der Condition auswählen, falls eine benötigt wird.");
		cobConditionPanel.add(conditionText, c);

		//komplette rechte Seite erstellen
		//condition-Typen in ComboBox rechts
		c.gridx = 1;
		c.gridy = 0;
		cobCondition = new JComboBox(conditions);
		cobCondition.setToolTipText("Wählen Sie eine Condition aus.");
		ConditionListener cListener = new ConditionListener();
		cobCondition.addItemListener(cListener);
		cobConditionPanel.add(cobCondition, c);

		conditionPanel.add(cobConditionPanel, BorderLayout.NORTH);

		//JPanel für die ComboBoxen erstellen
		emptyPanel = new JPanel();
		buttonpressedConditionPanel = new TypeValueConditionDetailPanel();
		jcConditionPanel = new JavaclassConditionDetailPanel();
		equalsConditionPanel = new ParameterFormfieldConditionDetailPanel();

		//Conditionen
		conditionCards = new JPanel(new CardLayout());
		conditionCards.setVisible(false);
		conditionCards.setAlignmentX(0);
		conditionCards.setMaximumSize(new Dimension(750, 350));
		conditionCards.add(emptyPanel, "keine Condition");
		conditionCards.add(buttonpressedConditionPanel, "buttonpressed");
		conditionCards.add(equalsConditionPanel, "equals");
		conditionCards.add(jcConditionPanel, "javaclass");

		cListener.setComponent(conditionCards);

		conditionPanel.add(conditionCards, BorderLayout.CENTER);

		/*
		 * Action
		 */
		//für die Action auf der linken Seite
		JLabel actionText;
		cobActionPanel = new JPanel(new GridBagLayout());
		cobActionPanel.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Action-Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		cobActionPanel.setAlignmentX(0);

		actionPanel = new JPanel(new BorderLayout());

		//actions, die in der ComboBox ausgewählt werden können
		String[] actions = { "keine Action", "maintain", "set", "javaclass", "callproc" };

		//komplette linke Seite erstellen
		//Action-Typ links
		c.gridx = 0;
		c.gridy = 1;
		actionText = new JLabel("typ:");
		actionText.setToolTipText(
			"Art der Action auswählen, falls eine benötigt wird.");
		cobActionPanel.add(actionText, c);

		//komplette rechte Seite erstellen
		//action-Typen in ComboBox rechts
		c.gridx = 1;
		c.gridy = 1;
		cobAction = new JComboBox(actions);
		cobAction.setToolTipText("Wählen Sie eine Action aus.");
		ActionsListener aListener = new ActionsListener();
		cobAction.addItemListener(aListener);
		cobActionPanel.add(cobAction, c);

		actionPanel.add(cobActionPanel, BorderLayout.NORTH);

		//JPanel für die ComboBoxen erstellen
		jcActionPanel = new JavaclassActionDetailPanel();
		parameterActionPanel = new ParameterActionDetailPanel();
		formfieldActionPanel = new FormfieldActionDetailPanel();
		callprocActionPanel = new CallprocActionDetailPanel();

		//Actions
		actionCards = new JPanel(new CardLayout());
		actionCards.setVisible(false);
		actionCards.setAlignmentX(0);
		actionCards.setMaximumSize(new Dimension(750, 350));
		actionCards.add(emptyPanel, "keine Action");
		actionCards.add(formfieldActionPanel, "maintain");
		actionCards.add(parameterActionPanel, "set");
		actionCards.add(jcActionPanel, "javaclass");
		actionCards.add(callprocActionPanel, "callproc");

		aListener.setComponent(actionCards);

		actionPanel.add(actionCards, BorderLayout.CENTER);

		/*
		 * Fenster mit JSplitPane teilen. Räumliche Trennung von Condition und
		 * Action.
		 */
		splitPane = new JSplitPane();
		splitPane.setLeftComponent(conditionPanel);
		splitPane.setRightComponent(actionPanel);
		splitPane.setOneTouchExpandable(true);

		caPanel.add(splitPane);		

		getContentPane().add(caPanel);
		
		if (getTopLevelAncestor() != null)
			System.out.println("ConditionActionDetailPanel: " + getTopLevelAncestor().getName());
	}

	/**
	 * @param t
	 *            The TRANSITION object to set.
	 */
	public void setTransition(TRANSITION t) {
		this.transition = t;
	}

	/**
	 * @return Returns the TRANSITION object transition.
	 */
	public TRANSITION getTransition() {
		return this.transition;
	}

	/**
	 * @param condition
	 *            The CONDITION object to set.
	 */
	public void setCondition(CONDITION condition) {
		this.condition = condition;
		jcConditionPanel.setCondition(condition);
		buttonpressedConditionPanel.setCondition(condition);
	}

	/**
	 * @return Returns the CONDITION object.
	 */
	public CONDITION getCondition() {
		return condition;
	}

	/**
	 * @param action
	 *            The ACTION object to set.
	 */
	public void setAction(ACTION action) {
		this.action = action;
	}

	/**
	 * @return Returns the ACTION object.
	 */
	public ACTION getAction() {
		return action;
	}

	/**
	 * @param formfieldList
	 *            The List of FORMFIELD objects to set.
	 */
	public void setActionFormfieldList(List formfieldList) {
		if (getAction().getType().equals("callproc"))
			callprocActionPanel.setFormfieldList(formfieldList);
		else
			formfieldActionPanel.setFormfieldList(formfieldList);
	}

	/**
	 * @param parameterList
	 *            The List of PARAMETER objects to set.
	 */
	public void setActionParameterList(List parameterList) {

		PARAMETER parameter = null;
		Iterator iterator = parameterList.iterator();
		while (iterator.hasNext()) {
			parameter = (PARAMETER) iterator.next();
		}
		if (getAction().getType().equals("callproc"))
			callprocActionPanel.setParameter(parameter);
		else
			parameterActionPanel.setParameter(parameter);
	}

	/*
	 * ComboBoxen Beschriftung und Textfeld Beschriftungen setzen!
	 */

	/**
	 * @return Returns the ComboBox cobCondition.
	 */
	public String getCobConditons() {
		return cobCondition.getSelectedItem().toString();
	}

	/**
	 * @param cobConditions
	 *            The ComboBox cobCondition to set.
	 */
	public void setCobConditions(String cobConditions) {
		this.cobCondition.setSelectedItem(cobConditions);
	}

	/**
	 * @return Returns the ComboBox cobAction.
	 */
	public String getCobActions() {
		return cobAction.getSelectedItem().toString();
	}

	/**
	 * @param type
	 *            The ComboBox cobAction to set.
	 */
	public void setCobActions(String type) {
		this.cobAction.setSelectedItem(type);
	}

	/**
	 * @param parameterList
	 *            The List of PARAMETER objects to set.
	 */
	public void setConditionParameterList(List parameterList) {
		
		PARAMETER parameter = null;
		Iterator iterator = parameterList.iterator();
		while (iterator.hasNext()) {
			parameter = (PARAMETER) iterator.next();
		}
		equalsConditionPanel.setParameter(parameter);
		
	}

	/**
	 * @param formfieldList
	 *            The List of FORMFIELD objects to set.
	 */
	public void setConditionFormfieldList(List formfieldList) {

		FORMFIELD formfield = null;
		Iterator iterator = formfieldList.iterator();
		while (iterator.hasNext()) {
			formfield = (FORMFIELD) iterator.next();
		}
		equalsConditionPanel.setFormfield(formfield);
	}
}
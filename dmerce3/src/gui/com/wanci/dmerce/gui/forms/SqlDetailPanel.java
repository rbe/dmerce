/*
 * Created on Dec 2, 2003
 */
package com.wanci.dmerce.gui.forms;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.wanci.dmerce.forms.OPTIONS;

/**
 * Im rechten Hauptfenster im CardLayout. Wenn das OPTIONS-Objekt sql-Attribute
 * beinhaltet, wird dieses Panel vom FieldDetailPanel aufgerufen und angezeigt.
 * 
 * @author mm
 * @version $Id: SqlDetailPanel.java,v 1.3 2004/01/14 14:12:00 mm Exp $
 */
public class SqlDetailPanel extends JPanel {

	private JTextField tfSql;
	private JTextField tfSqlkey;
	private JTextField tfSqlvalue;
	private OPTIONS options;
	
	public SqlDetailPanel() {

		super(new GridBagLayout());

		this.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("SQL Attribute"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		//Layout
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;

		JLabel leftText;

		//komplette linke Seite erstellen
		//sql Feld erzeugen
		c.gridx = 0;
		c.gridy = 0;
		leftText = new JLabel("sql:");
		add(leftText, c);

		//key Feld erzeugen
		c.gridx = 0;
		c.gridy = 1;
		leftText = new JLabel("key:");
		add(leftText, c);

		//value Feld erzeugen
		c.gridx = 0;
		c.gridy = 2;
		leftText = new JLabel("value:");
		add(leftText, c);

		//komplette rechte Seite erstellen
		//sql Feld rechts
		c.gridx = 1;
		c.gridy = 0;
		tfSql = new JTextField(20);
		add(tfSql, c);

		//key Feld rechts
		c.gridx = 1;
		c.gridy = 1;
		tfSqlkey = new JTextField(20);
		add(tfSqlkey, c);

		//value Feld rechts
		c.gridx = 1;
		c.gridy = 2;
		tfSqlvalue = new JTextField(20);
		add(tfSqlvalue, c);
	}

	/**
	 * @return Returns the textfield tfKey.
	 */
	public String getSqlkey() {
		return options.getSqlkey();
	}

	/**
	 * @param tfKey
	 *            The textfield tfKey to set.
	 */
	public void setSqlkey(String sqlKey) {
		this.tfSqlkey.setText(sqlKey);
		this.options.setSqlkey(sqlKey);
	}

	/**
	 * @return Returns the textfield tfSql.
	 */
	public String getSql() {
		return options.getSql();
	}

	/**
	 * @param tfSql
	 *            The textfield tfSql to set.
	 */
	public void setSql(String sql) {
		this.tfSql.setText(sql);
		this.options.setSql(sql);
	}

	/**
	 * @return Returns the textfield tfValue.
	 */
	public String getSqlvalue() {
		return options.getSqlvalue();
	}

	/**
	 * @param tfValue
	 *            The textfield tfValue to set.
	 */
	public void setSqlvalue(String sqlValue) {
		this.tfSqlvalue.setText(sqlValue);
		this.options.setSqlvalue(sqlValue);
	}

	/**
	 * @param options
	 *            The OPTIONS to set.
	 */
	public void setFieldOptions(OPTIONS options) {
		this.options = options;
		setSql(options.getSql());
		setSqlkey(options.getSqlkey());
		setSqlvalue(options.getSqlvalue());
	}
}
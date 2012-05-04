package com.wanci.dmerce.taglib.sql;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.dmerce.taglib.TagOutputFormatter;
import com.wanci.dmerce.webservice.db.QRow;

/**
 * @author pg
 * @author mm
 * @version $Id: FieldTag.java,v 1.21 2004/06/03 23:51:51 rb Exp $
 *  
 */
public class FieldTag extends TagSupport {

	protected boolean DEBUG = false;

	protected boolean DEBUG2 = false;

	/**
	 * Name des Feldes
	 */
	private String name;

	private boolean isFile;

	private String format;

	private int precision = -1;

	private int minprecision = -1;

	private int maxprecision = -1;

	public FieldTag() {

		try {

			DEBUG = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
					"debug");
			DEBUG2 = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
					"core.debug");

		} catch (XmlPropertiesFormatException e) {
		}

	}

	/**
	 * sucht das Feld "name" in der Row und gibt den Inhalt zurück oder nichts
	 * wenn das Feld null ist.
	 * 
	 * @throws JspTagException
	 * @return TAG-int
	 */
	public int doStartTag() throws JspTagException {

		try {

			RowTag rowTag = (RowTag) findAncestorWithClass(this,
					com.wanci.dmerce.taglib.sql.RowTag.class);

			// Aktuelle Zeile holen
			QRow currentRow = rowTag.getRow();
			// Wenn der Cursor von QResult auf EOF steht, wird als aktuelle
			// Zeile null zurückgegeben -> Dann nichts ausgeben
			if (currentRow != null) {

				String fieldName;

				if (isFile) {

					try {

						fieldName = getName()
								+ XmlPropertiesReader
										.getInstance()
										.getProperty(
												"fileupload.columnsuffix.originalfile");

					} catch (XmlPropertiesFormatException e) {
						throw new JspException(e);
					}

				} else
					fieldName = getName();

				String value;
				Object fieldObject = currentRow.getFields().get(fieldName);
				if (fieldObject != null)
					value = new TagOutputFormatter(format).format(fieldObject,
							fieldName);
				else
					value = "";
				/*
				 * else value = "Found unknown field '" + fieldName + "' in
				 * qsql:field-tag";
				 */

				// null results are often OK, in outer joins for example
				// RB: do not write &nbsp; !!!
				if (value != null || !value.equals(""))
					pageContext.getOut().write(value);

			}

		} catch (Exception e) {
			throw new JspTagException(e.getMessage());
		}

		return EVAL_BODY_INCLUDE;

	}

	/**
	 * setter for fieldname
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name.toLowerCase();
	}

	/**
	 * getter for Fieldname
	 * 
	 * @throws JspTagException
	 * @return String
	 */
	public String getName() {
		return this.name;
	}

	public void setFile(boolean value) {
		this.isFile = value;
	}

	/**
	 * Setzt das Format für die Datumsausgabe. Die Syntax kann der Dokumentation
	 * der Klasse java.text.SimpleDateFormat entnommen werden.
	 * 
	 * RB 20041008: Wird nun auch für Text verwendet: sofern das Format auf
	 * "html" gesetzt wurde, werden Zeilenumbrüche in <br>
	 * umgewandelt
	 * 
	 * @param format
	 *            The format to set.
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * Legt die Anzahl der Nachkommastellen fest, die ausgegeben werden sollen.
	 */
	public void setPrecision(String precision) {
		setMinprecision(precision);
		setMaxprecision(precision);
	}

	/**
	 * Legt die Mindestanzahl der Nachkommastellen fest, die ausgegeben werden
	 * sollen.
	 */
	public void setMinprecision(String precision) {
		this.minprecision = Integer.valueOf(precision).intValue();
	}

	/**
	 * Legt die Maximalanzahl der Nachkommastellen fest, die ausgegeben werden
	 * sollen.
	 */
	public void setMaxprecision(String precision) {
		this.maxprecision = Integer.valueOf(precision).intValue();
	}

}
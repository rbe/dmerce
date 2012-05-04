package com.wanci.dmerce.taglib.sql;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.wanci.dmerce.webservice.db.QRow;

/**
 *
 * @author pg
 * @author mm
 * @version $Id: DateFieldTag.java,v 1.3 2004/02/15 18:17:00 rb Exp $
 *
 */
public class DateFieldTag extends TagSupport {

	/**
	 * Name des Feldes
	 */
	private String name;

	private String format;

	/**
	 * sucht das Feld "name" in der Row und gibt den Inhalt zurück oder nichts
	 * wenn das Feld null ist.
	 *
	 * @throws JspTagException
	 * @return TAG-int
	 */
	public int doStartTag() throws JspTagException {
		try {
			RowTag rowTag = (RowTag) findAncestorWithClass(this, com.wanci.dmerce.taglib.sql.RowTag.class);

			// Aktuelle Zeile holen
			QRow currentRow = rowTag.getRow();
			// Wenn der Cursor von QResult auf EOF steht, wird als aktuelle Zeile null zurückgegeben -> Dann nichts ausgeben
			if (currentRow != null) {
				Object fieldobject = currentRow.getFields().get(getName());
				String value;
				if (fieldobject != null) {
					// Calendar-Objekte dürfen nicht angezeigt werden.
					if (!(fieldobject instanceof Calendar)) {
						value =
							"Das Tag <qsql:datefield> kann nur Felder vom Typ java.util.Calendar ausgeben.<br/>Das Feld \""
								+ name
								+ "\" ist jedoch vom Typ "
								+ fieldobject.getClass().getName();
					} else {
						SimpleDateFormat sdf;
						if (format != null && !format.equals("")) {
							sdf = new SimpleDateFormat(format);
						} else {
							sdf = new SimpleDateFormat();
						}
						value = sdf.format(((Calendar) fieldobject).getTime());
					}
				} else {
					value = "Unbekanntes oder leeres (null) Feld \"" + getName() + "\"";
				}
				// Ergebnis rausschreiben
				pageContext.getOut().write(value);
			}

		} catch (Exception e) {
			throw new JspTagException(e.toString());
		}
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * setter for fieldname
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * getter for Fieldname
	 * @throws JspTagException
	 * @return String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * do nothing here
	 * @return TAG-int
	 */
	public int doEndTag() {
		return EVAL_PAGE;
	}

	/**
	 * Gibt das Format für die Datumsausgabe zurück. Die Syntax kann der Dokumentation der Klasse
	 * java.text.SimpleDateFormat entnommen werden. 
	 * @return Returns the format.
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Setzt das Format für die Datumsausgabe. Die Syntax kann der Dokumentation der Klasse
	 * java.text.SimpleDateFormat entnommen werden. 
	 * @param format The format to set.
	 */
	public void setFormat(String format) {
		this.format = format;
	}

}
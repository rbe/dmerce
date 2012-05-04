package com.wanci.dmerce.taglib.sql;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.wanci.dmerce.webservice.db.QRow;

/**
 * 
 * @author pg
 * @author mm
 * @version $Id: RowTag.java,v 1.7 2004/01/08 13:42:47 mf Exp $
 *  
 */
public class RowTag extends BodyTagSupport {

	/**
	 * Iterator auf alle Datensatzzeilen des Abfrageergebnisses
	 */
	private Iterator qResultIterator;

	/**
	 * aktueller Datensatz
	 */
	private QRow currentQRow;

	/**
	 * integer variable für die position des aktuellen Datensatzes im
	 * Abfrageergebnis
	 */
	private int position = 0;

	/**
	 * getter for position
	 * 
	 * @return aktuelle Datensatzposition
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * getter for QRow
	 * 
	 * @return qrow
	 */
	public QRow getRow() {
		return currentQRow;
	}

	/**
	 * Schiebt den Cursor um einen Datensatz in der Abfragemenge weiter. Falls
	 * kein weiterer Datensatz mehr existiert, wird die aktuelle Position auf -1
	 * und die aktuelle Zeile (QRow) auf null gesetzt.
	 */
	public void nextRow() {
		
		if (qResultIterator.hasNext()) {
			position++;
			currentQRow = (QRow) qResultIterator.next();
		} else {
			position = -1;
			currentQRow = null;
		}
		
	}

	/**
	 * start tag
	 * 
	 * @return int TAG-int
	 */
	public int doStartTag() throws JspTagException {

		try {

			ExecuteTag executeTag = (ExecuteTag) findAncestorWithClass(this,
					com.wanci.dmerce.taglib.sql.ExecuteTag.class);

			qResultIterator = executeTag.getRows().iterator();

			if (qResultIterator.hasNext()) {
				position++;
				currentQRow = (QRow) qResultIterator.next();
			} else {
				return SKIP_BODY;
			}

		} catch (Exception e) {
			throw new JspTagException(e.toString());
		}

		return EVAL_BODY_INCLUDE;
	}

	/**
	 * TAGLIB: afterbody
	 * 
	 * @return TAG-int
	 */
	public int doAfterBody() {
		
		if (qResultIterator.hasNext()) {
			
			position++;
			currentQRow = (QRow) qResultIterator.next();
			
			return EVAL_BODY_AGAIN;
			
		} else {
			
			position = 0;
			
			return SKIP_BODY;
			
		}
		
	}

	/**
	 * TAGLIB: end tag
	 * 
	 * @return TAG-int
	 */
	public int doEndTag() throws JspTagException {
		try {
			if (getBodyContent() != null && getPreviousOut() != null) {
				getPreviousOut().write(getBodyContent().getString());
			}
		} catch (IOException e) {
			throw new JspTagException(e.toString());
		}

		return EVAL_PAGE;
	}

}
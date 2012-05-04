/*
 * Datei angelegt am 08.01.2004
 */
package com.wanci.dmerce.taglib.sql;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.wanci.dmerce.webservice.db.QRow;

/**
 * @author Masanori Fujita
 */
public class RowEmptyTag extends BodyTagSupport {

	private boolean DEBUG = false;

	/**
	 * variable inverted shows type of tag if emptyTag is true, we are in
	 * RowEmptyTag, if emptyTag is false, we are in RowNotEmptyTag
	 */
	protected boolean emptyTag = true;

	/**
	 * do start tag
	 */
	public int doStartTag() {
		// Umgebenden Row-Tag holen
		RowTag rowTag = (RowTag) findAncestorWithClass(this, com.wanci.dmerce.taglib.sql.RowTag.class);
		// aktuelle Zeile holen
		QRow currentRow = rowTag.getRow();

		//if we are in RowNotEmptyTag and the current row is empty then SKIP
		if (!emptyTag && currentRow == null) {
			return SKIP_BODY;
		}

		//if we are in RowEmptyTag and the current row is filled with values
		// then SKIP
		if (emptyTag && currentRow != null) {
			return SKIP_BODY;
		}

		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() throws JspException {
		try {
			if (getBodyContent() != null) {
				getPreviousOut().write(getBodyContent().getString());
			}
		} catch (IOException e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}

}

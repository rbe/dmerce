/*
 * Datei angelegt am 08.01.2004
 */
package com.wanci.dmerce.taglib.sql;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.dmerce.webservice.db.QRow;
import com.wanci.java.LangUtil;

/**
 * @author Masanori Fujita
 */
public class FieldEmptyTag extends BodyTagSupport {
	
	private boolean DEBUG = false;

	private boolean inverted;
	private String name;
	private boolean file;
	
	public int doStartTag() throws JspException {
		// Umgebenden Row-Tag holen
		RowTag rowTag = (RowTag) findAncestorWithClass(this, com.wanci.dmerce.taglib.sql.RowTag.class);
		// aktuelle Zeile holen
		QRow currentRow = rowTag.getRow();
		if (currentRow == null) {
			try {
				pageContext.getOut().write("## NO DATA ##");
			} catch (IOException e) {
				throw new JspException(e);
			}
			LangUtil.consoleDebug(DEBUG, "FieldEmptyTag: SKIP_BODY because row is empty.");
			return SKIP_BODY;
		} else {
			String fieldName;
			if (!file) {
				fieldName = getName();
			}
			else {
				try {
					fieldName = getName() + XmlPropertiesReader.getInstance().getProperty("fileupload.columnsuffix.serverfile");
				} catch (XmlPropertiesFormatException e) {
					throw new JspException(e);
				}
			}
			Object o = currentRow.getFields().get(fieldName);
			if (!isInverted() & (o != null && !o.toString().equals(""))) {
				LangUtil.consoleDebug(DEBUG, "FieldEmptyTag: SKIP_BODY in row "+rowTag.getPosition()+" because field "+getName()+" is not empty.");
				return SKIP_BODY;
			}
			if (isInverted() & (o == null || o.toString().equals(""))) {
				LangUtil.consoleDebug(DEBUG, "FieldEmptyTag: SKIP_BODY in row "+rowTag.getPosition()+" because field "+getName()+" is empty.");
				return SKIP_BODY;
			}
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

	/**
	 * @return Returns the inverted.
	 */
	protected boolean isInverted() {
		return inverted;
	}

	/**
	 * @param inverted The inverted to set.
	 */
	protected void setInverted(boolean inverted) {
		this.inverted = inverted;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @param isFile
	 */
	public void setFile(boolean isFile) {
		this.file = isFile;
	}

}

/*
 * Created on Aug 15, 2003
 */
package com.wanci.dmerce.taglib.sql;

import java.util.HashMap;

import javax.servlet.jsp.tagext.BodyTagSupport;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.java.LangUtil;

/**
 * @author mm
 * @version $Id: PrepareTag.java,v 1.9 2004/03/09 16:51:24 rb Exp $
 */
public class PrepareTag extends BodyTagSupport {

	private boolean DEBUG = false;

	/**
	 * id des Datensatzes
	 */
	private String id = null;

	/**
	 * prepared???
	 */
	private String preparedid = null;

	/**
	 * Hashmap mit Parameter
	 */
	private HashMap parameters = new HashMap();

	public PrepareTag() throws XmlPropertiesFormatException {
		super();
		DEBUG = XmlPropertiesReader.getInstance().getPropertyAsBoolean("debug");
	}

	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param preparedid
	 */
	public void setPreparedid(String preparedid) {
		this.preparedid = preparedid;
	}

	/**
	 * setter for parameter
	 * 
	 * @param name
	 * @param value
	 */
	public void setParameter(String name, String value) {
		parameters.put(name, value);
	}

	/**
	 * TAGLIB-specific: Start Tag
	 * 
	 * @return Tag-int
	 */
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	/**
	 * Endverarbeitung des Prepare-Prozesses
	 * 
	 * @throws JspTagException
	 * @return Tag-int
	 */
	public int doAfterBody() {

		//variable für direkten sql-string
		String sql = null;

		//falls keine preparedid angegeben wurde, dann muss
		//in den klammern ein sql statement stehen
		if (preparedid == null) {
			sql = getBodyContent().getString().trim();
			LangUtil.consoleDebug(DEBUG, "Generated SQL statement: " + sql);
		}

		if (sql != null) {
			//sql statement registrieren
			pageContext.setAttribute("qsql." + id + ".query", sql);
		}

		return SKIP_BODY;
	}

}
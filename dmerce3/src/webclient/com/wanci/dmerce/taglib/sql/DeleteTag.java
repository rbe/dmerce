package com.wanci.dmerce.taglib.sql;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * 
 * @author pg
 * @author mm
 * @version $Id: DeleteTag.java,v 1.18 2004/01/21 14:38:33 pg Exp $
 *
 */
public class DeleteTag extends BodyTagSupport {

	/**
	 * the key 
	 */
	private String key;

	/**
	 * the value
	 */
	private String value;

	/**
	 * Beschreibung des Links
	 */
	private String linkstring;

	/**
	 * Seite auf die weitergeleitet wird
	 */
	private String template = null;

	/**
	 * Tabelle der Datenbank in der die Aktion durchgeführt wird
	 */
	private String table = null;

	/**
	 * setter for key
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key.toLowerCase();
	}

	/**
	 * Beginn des Delete Tags
	 * EVAL_BODY_BUFFERED wertet den Body aus
	 *
	 * @throws JspTagException
	 * @return TAG-int
	 */
	public int doStartTag() {
		try {
			RowTag rowTag = (RowTag) findAncestorWithClass(this, com.wanci.dmerce.taglib.sql.RowTag.class);
			value = rowTag.getRow().getFields().get(key).toString();
		} catch (Exception e) {
			value = null;
		}
		return EVAL_BODY_BUFFERED;
	}

	/**
	 * setter for template
	 * @param template
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * setter for table
	 * @param table
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * setter for id
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Ende des Delete Tags
	 * mit den Variablen qTemplate, table und id wird ein Link zusammengesetzt,
	 * der das Löschen eines Datensatzes einleitet.
	 * Hierbei kann template vom Webdesigner selbst angegeben werden oder der
	 * Wert wird aus der letzten Seite übernommen, falls template null ist
	 *
	 * @return TAG-int
	 */
	public int doEndTag() {
		String str = "delete.do?";
		if (template == null)
			template = pageContext.getRequest().getParameter("qTemplate");
		if (template == null) {
			template = ((HttpServletRequest) pageContext.getRequest()).getRequestURI();
		}

		str += "qTemplate=" + template + "&";
		str += "table=" + table + "&";
		if (key != null)
			str += "key=" + key;
		else
			str += "key=id";
		str += "&";
		str += "id=" + value;

		str = "<a href=\"" + str + "\">";
		if (linkstring != null)
			str = str + linkstring;
		else
			str = str + "del";
		str = str + "</a>";

		//TODO: code generieren (md-5hash zur verifikation des löschvorgangs

		try {
			if (value == null) {
				pageContext.getOut().write("!?" + linkstring + "?!: key \"" + key + "\" doesn't exist in field collection");
			} else {
				pageContext.getOut().write(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return EVAL_PAGE;
	}

	/**
	 * @throws JspTagException
	 * @return TAG-int
	 */
	public int doAfterBody() {
		linkstring = getBodyContent().getString().trim();
		return SKIP_BODY;
	}

}
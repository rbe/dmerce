package com.wanci.dmerce.taglib.sql;

import javax.servlet.jsp.tagext.TagSupport;

public class NumberTag extends TagSupport {
	
	/**
	 * Nummerierung für die Tabellenzeilen
	 */
	private int counter = 0;
	
	/**
	 * counter holt sich die aktuelle Position der Tabellenzeile und
	 * gibt diese im jsp aus.
	 * 
	 * @throws JspTagException
	 * @return TAG-int
	 */
	public int doStartTag() {

		try {
			RowTag rowTag = (RowTag) findAncestorWithClass(this, com.wanci.dmerce.taglib.sql.RowTag.class);
			counter = rowTag.getPosition();
			if (counter >= 0) {
				pageContext.getOut().write(String.valueOf(counter));
			}
			else {
				pageContext.getOut().write("#ERROR#");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return EVAL_BODY_INCLUDE;
	}
	
}

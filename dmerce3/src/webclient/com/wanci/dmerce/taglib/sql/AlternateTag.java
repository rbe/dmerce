package com.wanci.dmerce.taglib.sql;

import javax.servlet.jsp.tagext.TagSupport;

/**
 * Das Alternate Tag gibt entweder eine 1 oder eine 2 aus.
 * Dies kann für Listenanzeigen benutzt werden, um alternierend verschiedene Stylesheetangaben aufzurufen, 
 * um die Zeilen abwechselnd in verschiedenen Stilem (Hintergrundfarben) zu verwenden.
 * 
 * @author pg
 */
public class AlternateTag extends TagSupport {

	/**
	 * einzige Methode der Klasse, wird nur bei Start Tag aktiv und gibt in den Context 1 oder 2 aus.
	 * @throws JspTagException
	 * @return TAG-int
	 */
	public int doStartTag() {

		try {
			RowTag rowTag = (RowTag) findAncestorWithClass(this, com.wanci.dmerce.taglib.sql.RowTag.class);
			int alternate = 2 - (rowTag.getPosition() % 2);
			pageContext.getOut().write(String.valueOf(alternate));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return EVAL_BODY_INCLUDE;
	}

}

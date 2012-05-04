package com.wanci.dmerce.taglib.sql;

import javax.servlet.jsp.tagext.TagSupport;

/**
 * Mit dem NextRowTag kann der aktuelle Datensatz innehalb eines Row-Tags manuell
 * weitergesetzt werden. Dieses Verhalten wird ben�tigt, um innerhalb eines Row-Tags
 * mehrere aufeinanderfolgende Datens�tze abzufragen und damit z.B. mehrspaltige
 * Listen aufzubauen.
 * 
 * @author mf
 */
public class NextRowTag extends TagSupport {

	/**
	 * Setzt den Cursor des umgebenden Row-Tags um einen Datensatz weiter, falls m�glich.
	 */
	public int doStartTag() {
		RowTag rowTag = (RowTag) findAncestorWithClass(this, com.wanci.dmerce.taglib.sql.RowTag.class);
		rowTag.nextRow();
		return SKIP_BODY;
	}

}

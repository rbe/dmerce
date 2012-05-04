/*
 * Datei angelegt am 03.02.2004
 */
package com.wanci.dmerce.taglib.sql;


/**
 * FieldNotEmptyTag invertiert Fieldempty-TagO
 *
 * @author Masanori Fujita
 */
public class FieldNotEmptyTag extends FieldEmptyTag {
	
	public FieldNotEmptyTag() {
		setInverted(true);
	}

}

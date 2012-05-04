/*
 * Created on Mar 18, 2004
 *
 */
package com.wanci.dmerce.generator;

/**
 * @author rb2
 * @version $Id: Field.java,v 1.1 2004/03/19 14:25:18 rb Exp $
 *  
 */
public class Field {

	private String description;

	private String name;

	private boolean required;

	public Field() {
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String toString() {

		StringBuffer sb = new StringBuffer("<field name=\"\"");

		if (required)
			sb.append(" required=\"true\"");

		sb.append(">");
		sb.append("<description>" + description + "</description>");

		sb.append("</field>");

		return sb.toString();

	}

}
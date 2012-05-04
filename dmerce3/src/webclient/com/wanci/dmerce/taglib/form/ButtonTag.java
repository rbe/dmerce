package com.wanci.dmerce.taglib.form;

import java.io.IOException;

import javax.servlet.jsp.tagext.TagSupport;

/**
 * 
 * @author pg
 * @author mm
 * @version $Id: ButtonTag.java,v 1.6 2004/03/05 13:48:04 mf Exp $
 *
 */
public class ButtonTag extends TagSupport {

	/**
	 * name für den "input type name" im Formular.
	 */
	private String name = "";
	
	/**
	 * type für den "input type" im Formular.
	 */
	private String type = "";
	
	/**
	 * text für den "input type value" im Formular.
	 */
	private String text = "";

	/**
	 * Wenn das Tag in der jsp aufgerufen wird, wird geprüft, ob
	 * der Button ein "submit" oder "reset" beinhaltet und dementsprechend
	 * gesetzt. 
	 */
	public int doStartTag() {
		
		String output = "";
		
		if (type.equals("") || type.equals("submit")) {
			setName("submit");
			output = "<input type=\"submit\" ";
		}
		else {
			output = "<input type=\"reset\" ";
		}
		
		if (!name.equals(""))
			output = output + "name=\"" + name + "\" ";
		
		if (!text.equals(""))
			output = output + "value=\"" + text + "\" ";

		output = output + "/>";

		try {
			pageContext.getOut().write(output);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return SKIP_BODY;
	}

	public void setType(String type) {
		this.type = type;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public void setText(String text) {
		this.text = text;
	}

	protected String getName() {
		return name;
	}

}
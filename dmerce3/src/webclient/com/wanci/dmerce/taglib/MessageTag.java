package com.wanci.dmerce.taglib;

import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * 
 * @author pg
 * @author mm
 * @version $Id: MessageTag.java,v 1.13 2003/11/17 19:21:31 mf Exp $
 *
 */
public class MessageTag extends BodyTagSupport {

	private ArrayList a = null;
	private String output = "";

	public int doStartTag() throws JspTagException {

		a = (ArrayList) pageContext.getSession().getAttribute("qMessage");
		pageContext.getSession().removeAttribute("qMessage");
		
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return EVAL_PAGE;
	}

	public int doEndTag() throws JspTagException {
		String header = "";
		String footer = "";
		String prefix = "";
		String postfix = "<br/>";
		
		/*
		  <q:message>
		   <header><ul></header>
		   <prefix><li></prefix>
		   <postfix></li></postfix>
		   <footer></ul></footer>
		</q:message>
		 */
		
		//hole BodyContent aus dem Tag
		if (a != null) {
			BodyContent bc = getBodyContent();
			if (bc != null) {
				String body = getBodyContent().getString();
				//TODO: header usw. rausziehen
				System.out.println(body);
			}

			output = header;

			//gehe durch Liste von Nachrichten und stelle sie dar
			Iterator iterator = a.iterator();
			while (iterator.hasNext()) {
				output += prefix + (String) iterator.next() + postfix;
			}
			output += footer;
			output = "<p class=\"msg\">" + output + "</p>\n";
			try {
				pageContext.getOut().write(output);
			} catch (Exception e) {
				throw new JspTagException(e.toString());
			}
		}

		pageContext.getSession().removeAttribute("qMessage");
		return EVAL_PAGE;
	}

}
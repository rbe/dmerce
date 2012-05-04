/*
 * WorkflowTag.java
 * 
 * Created on September 11, 2003, 4:50 PM
 */

package com.wanci.dmerce.taglib;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Generiert einen Link, der auf einen Workflow verweist
 * 
 * @author mf
 * @version $$Id: WorkflowLinkTag.java,v 1.5 2004/03/15 11:47:17 rb Exp $$
 */
public class WorkflowLinkTag extends TagSupport {

    private boolean restart;

    /** Creates a new instance of IncludeTag */
    public WorkflowLinkTag() {

    }

    public int doStartTag() throws JspTagException {

        String output = "<a href=\"workflow.do?qWorkflow=" + id;

        if (restart)
            output += "&restart";

        output += "\">";

        try {
            pageContext.getOut().write(output);
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }

        return EVAL_BODY_INCLUDE;

    }

    public int doEndTag() {

        String output = "</a>";

        try {
            pageContext.getOut().write(output);
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }

        return EVAL_PAGE;

    }

    public void setRestart(boolean value) {
        restart = value;
    }

    public void setId(String id) {
        this.id = id;
    }

}

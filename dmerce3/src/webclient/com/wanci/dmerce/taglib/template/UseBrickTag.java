/*
 * Datei angelegt am 21.11.2003
 */
package com.wanci.dmerce.taglib.template;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.java.LangUtil;

/**
 * @author Masanori Fujita
 */
public class UseBrickTag extends TagSupport {

    private String name;

    private static boolean DEBUG = false;

    public UseBrickTag() {

        try {

            DEBUG = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
                "debug");

        }
        catch (Exception e) {
        }

    }

    public int doEndTag() throws JspException {
        
        String template = ((BrickSet) pageContext.getRequest().getAttribute(
            "qBrickSet")).getTemplate(name);
        String includecode = ((BrickSet) pageContext.getRequest().getAttribute(
            "qBrickSet2")).getTemplate(name);
        
        try {
            
            if (template != null) {
                
                LangUtil.consoleDebug(DEBUG, "UseBrick " + name
                    + ": Trying to include " + template);
                
                pageContext.include(template);
                
            }
            else if (includecode != null) {
                
                LangUtil.consoleDebug(DEBUG, "UseBrick " + name
                    + ": include code directly");
                
                pageContext.getOut().println(includecode);
                
            }
            else {
                pageContext.getOut().println(
                    "No template or code for brick with name \"" + name
                        + "\" found.");
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new JspException(e.getCause());
        }
        
        return EVAL_PAGE;
        
    }

    public void setName(String name) {
        this.name = name;
    }

}
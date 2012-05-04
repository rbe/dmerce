/*
 * Datei angelegt am 21.11.2003
 */
package com.wanci.dmerce.taglib.template;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.java.LangUtil;

/**
 * @author Masanori Fujita
 */
public class UseTemplateTag extends BodyTagSupport {

    private String path;

    private static boolean DEBUG = false;

    public UseTemplateTag() {

        try {

            DEBUG = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
                "debug");

        }
        catch (Exception e) {
        }

    }

    public int doStartTag() throws JspException {
        
        pageContext.getRequest().setAttribute("qBrickSet", new BrickSet());
        pageContext.getRequest().setAttribute("qBrickSet2", new BrickSet());
        
        return EVAL_BODY_BUFFERED;
        
    }

    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
        
        try {
            
            LangUtil.consoleDebug(DEBUG, "UseTemplate trying forwarding to "
                + path);
            
            pageContext.getOut().clearBuffer();
            pageContext.forward(path);
            
        }
        catch (Exception e) {
            
            e.printStackTrace();
            throw new JspException(e.getCause());
            
        }
        
        return SKIP_PAGE;
        
    }

    /**
     * @param path
     *            The path to set.
     */
    public void setPath(String path) {
        this.path = path;
    }

}

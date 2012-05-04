package com.wanci.dmerce.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

/**
 * Ein einfaches Tag, das
 * 
 * @author rb
 * @version $$Id: ResTag.java,v 1.3 2004/03/15 19:53:56 rb Exp $$
 */
public class ResTag implements Tag {

    /**
     * pagecontext
     */
    private PageContext pageContext;

    /**
     * parent tag
     */
    private Tag parent;

    /**
     * name der variable
     */
    private String path = "";

    /**
     * Constructor
     *  
     */
    public ResTag() {
        super();
    }

    /**
     * Method called at start of Tag
     * 
     * @return SKIP_BODY
     */
    public int doStartTag() {
        return SKIP_BODY;
    }

    /**
     * Methode die die angegebene variable ausliest und direkt ausgibt falls
     * kein scope angegeben wurde, liest die methode erst aus dem request, dann
     * aus der session ansonsten wird direkt aus der session oder aus dem
     * request gelesen
     * 
     * @return EVAL_PAGE
     */
    public int doEndTag() throws javax.servlet.jsp.JspTagException {

        //read value
        String contextPath = ((HttpServletRequest) pageContext.getRequest())
                .getContextPath();

        //add slash if necessary
        if (path.charAt(0) == '/') {
            path = contextPath + path;
        }
        else {
            path = contextPath + "/" + path;
        }

        //output
        try {
            if (path != null)
                pageContext.getOut().write(path);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return EVAL_PAGE;
    }

    /**
     * setter for name
     * 
     * @param name
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Method called to releases all resources
     */
    public void release() {
    }

    /**
     * Method used by the JSP container to set the current PageContext
     * 
     * @param pageContext
     *            the current PageContext
     */
    public void setPageContext(final javax.servlet.jsp.PageContext pageContext) {
        this.pageContext = pageContext;
    }

    /**
     * Method used by the JSP container to set the parent of the Tag
     * 
     * @param parent
     *            the parent Tag
     */
    public void setParent(final javax.servlet.jsp.tagext.Tag parent) {
        this.parent = parent;
    }

    /**
     * Method for retrieving the parent
     * 
     * @return parent
     */
    public javax.servlet.jsp.tagext.Tag getParent() {
        return parent;
    }

}

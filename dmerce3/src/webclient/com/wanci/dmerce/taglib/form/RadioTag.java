package com.wanci.dmerce.taglib.form;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author pg
 * @author mm
 * @version $Id: RadioTag.java,v 1.7 2004/05/16 19:44:06 rb Exp $
 *  
 */
public class RadioTag extends TagSupport {

    private String name = null;

    private HashMap attribs = new HashMap();

    /**
     *  
     */
    public int doStartTag() {
        return SKIP_BODY;
    }

    /**
     * Holt sich die "field" Elemente und erzeugt nach Prüfung ein " <input
     * type="text"...>" Tag mit den Werten.
     */
    public int doEndTag() throws JspTagException {

        String output = "";
        HtmlFormElement htmlField;
        FormTag form = (FormTag) findAncestorWithClass(this,
            com.wanci.dmerce.taglib.form.FormTag.class);

        FormField f;

        try {
            f = form.getField(name);
        }
        catch (Exception e) {

            String message = e.getMessage();
            message += "<br/>Please check &lt;qform:radio&gt; tag for field '"
                + name
                + "' in '"
                + ((HttpServletRequest) pageContext.getRequest())
                    .getRequestURI() + "'. Exception: " + e.getMessage();

            JspTagException jspe = new JspTagException(message);
            jspe.setStackTrace(e.getStackTrace());
            throw jspe;

        }

        if (f != null) {

            String value = f.getValue();

            htmlField = new HtmlRadioElement(f);
            htmlField.setAttributes(attribs);
            htmlField.setValue(value);
            
            output = htmlField.toHtml();

        }
        else
            output = "&lt;qform:radui&gt;: Form " + name + " not present";
        try {
            pageContext.getOut().print(output);

        }
        catch (IOException e1) {
            e1.printStackTrace();
        }

        return SKIP_BODY;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

}
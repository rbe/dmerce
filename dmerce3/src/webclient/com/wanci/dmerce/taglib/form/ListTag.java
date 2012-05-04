package com.wanci.dmerce.taglib.form;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import com.wanci.dmerce.exceptions.FieldNotFoundException;

/**
 * @author pg
 * @author mm
 * @version $Id: ListTag.java,v 1.8 2004/06/03 23:51:52 rb Exp $
 *  
 */
public class ListTag extends TagSupport {

    private String name = null;

    private String type = "combo";

    private HashMap attribs = new HashMap();

    private Integer size;

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
        HtmlListElement htmlField;

        FormTag form = (FormTag) findAncestorWithClass(this,
            com.wanci.dmerce.taglib.form.FormTag.class);

        FormField f;
        try {
            f = form.getField(name);
        }
        catch (FieldNotFoundException e) {
            String message = e.getMessage();
            String jspPath = ((HttpServletRequest) pageContext.getRequest())
                .getRequestURI();
            message += "<br/>Please check &lt;qform:list name=\"" + name
                + "\"&gt;-Tags in " + jspPath;
            JspTagException jspe = new JspTagException(message);
            jspe.setStackTrace(e.getStackTrace());
            throw jspe;
        }

        String[] values = f.getValues();
        htmlField = new HtmlListElement(f);

        if (size != null) {
            addAttribute("size", String.valueOf(size));
        }
        htmlField.setAttributes(attribs);
        htmlField.setValues(values);
        htmlField.setType(type);
        output = htmlField.toHtml();
        try {
            pageContext.getOut().print(output);
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }

        return SKIP_BODY;
    }

    /**
     * add an key-value-pair as attribute to the list tag
     * 
     * @param key
     *            Schlüssel
     * @param value
     *            Wertname
     */
    private void addAttribute(String key, String value) {
        if (key != null)
            attribs.put(key, value);
    }

    /**
     * setter for name
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * setter for type
     * 
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * HTML: setter for size
     * 
     * @param str
     */
    public void setSize(String str) {
        this.size = Integer.valueOf(str);
    }

    /**
     * HTML: setter for multiple
     */
    public void setMultiple(String str) {
        addAttribute("multiple", "multiple");
    }

}
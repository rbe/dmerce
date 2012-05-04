package com.wanci.dmerce.taglib.form;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author pg
 * @author mm
 * @version $Id: TextTag.java,v 1.26 2004/05/16 19:44:06 rb Exp $
 *  
 */
public class TextTag extends TagSupport {

    private String name = null;

    private Map attribs = new HashMap();

    private String format;

    /**
     * TAG: doStartTag-Method
     * 
     * @return int tag-key
     */
    public int doStartTag() {
        return SKIP_BODY;
    }

    /**
     * Holt sich die "field" Elemente und erzeugt nach Prüfung ein " <input
     * type="text"...>" Tag mit den Werten.
     * 
     * @return int tag-key
     */
    public int doEndTag() throws JspTagException {
        
        FormTag form;
        HtmlFormElement htmlField;
        String output = "";
        form = (FormTag) findAncestorWithClass(this,
            com.wanci.dmerce.taglib.form.FormTag.class);
        
        FormField f;
        
        try {
            f = form.getField(name);
        }
        catch (Exception e) {
            
            String message = e.getMessage();
            message += "<br/>Please check &lt;qform:text&gt; tag for field '"
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
            
            htmlField = new HtmlTextElement(f);
            htmlField.setAttributes(attribs);
            htmlField.setValue(value);
            
            output = htmlField.toHtml();
            
        }
        else
            output = "Form " + name + " not present";
        try {
            pageContext.getOut().print(output);
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        
        return SKIP_BODY;
        
    }

    /**
     * add an attribute
     * 
     * @param key
     * @param value
     */
    private void addAttribute(String key, String value) {
        if (key != null)
            attribs.put(key, value);
    }

    /**
     * TAGLIB: setter for name
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * TAGLIB: setter for size
     */
    public void setSize(String str) {
        addAttribute("size", str);
    }

    /**
     * TAGLIB: setter for MaxLength-Constraint
     * 
     * @param str
     */
    public void setMaxlength(String str) {
        addAttribute("maxlength", str);
    }

    /**
     * TAGLIB: setter for style
     * 
     * @param str
     */
    public void setStyle(String str) {
        addAttribute("style", str);
    }

    /**
     * TAGLIB: setter for class
     * 
     * @param str
     */
    public void setClass(String str) {
        addAttribute("class", str);
    }

    /**
     * Legt das Ausgabeformat des Feldwertes fest. Bei Datumsfeldern wird
     * dieser String in Verbindung mit dem SimpleDateFormat verwendet.
     */
    public void setFormat(String format) {
        this.format = format;
    }
}
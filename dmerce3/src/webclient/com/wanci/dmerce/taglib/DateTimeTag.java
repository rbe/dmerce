/*
 * Created on 14.03.2004
 *  
 */
package com.wanci.dmerce.taglib;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author rb
 * @version $$Id: DateTimeTag.java,v 1.1 2004/03/15 11:45:40 rb Exp $$
 *  
 */
public class DateTimeTag extends TagSupport {

    private String format;

    public int doStartTag() {
        return SKIP_BODY;
    }

    public int doEndTag() {

        try {
            pageContext.getOut().print(
                new SimpleDateFormat(format).format(new Date()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return EVAL_PAGE;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
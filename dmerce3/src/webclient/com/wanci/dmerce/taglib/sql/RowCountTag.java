package com.wanci.dmerce.taglib.sql;

import java.io.IOException;

import javax.servlet.jsp.tagext.TagSupport;

public class RowCountTag extends TagSupport {

    /**
     * rowcount
     */
    private int rowCount;

    /**
     * TAGLIB: starttag
     * 
     * @throws JspTagException
     * @return TAG-int
     */
    public int doStartTag() {

        try {
            
            ExecuteTag executeTag = (ExecuteTag) findAncestorWithClass(this,
                Class.forName("com.wanci.dmerce.taglib.sql.ExecuteTag"));
            
            rowCount = executeTag.getRowCount();
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            pageContext.getOut().write(String.valueOf(rowCount));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return EVAL_BODY_INCLUDE;
        
    }

}
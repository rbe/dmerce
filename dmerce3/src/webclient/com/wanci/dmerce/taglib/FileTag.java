/*
 * Created on 14.03.2004
 *  
 */
package com.wanci.dmerce.taglib;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;
import com.wanci.java.LangUtil;

/**
 * @author rb
 * @version $$Id: FileTag.java,v 1.2 2004/03/15 19:53:21 rb Exp $$
 *  
 */
public class FileTag implements Tag {

    private boolean DEBUG = false;

    private boolean DEBUG2 = false;

    private File file;
    
    private String dateFormat = "dd.MM.yyyy";

    private boolean creationDate = false;

    private boolean modificationDate = false;

    private boolean size = false;

    private String unit = "MB";

    private int minPrecision = 2;

    private int maxPrecision = 2;

    /**
     * pagecontext
     */
    private PageContext pageContext;

    /**
     * parent tag
     */
    private Tag parent;

    public FileTag() {

        try {

            DEBUG = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
                    "debug");
            DEBUG2 = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
                    "core.debug");

        }
        catch (XmlPropertiesFormatException e) {
        }

    }

    public int doStartTag() {

        String output = "";

        LangUtil.consoleDebug(DEBUG, "q:file is examining: '"
                + file.getAbsolutePath() + "'");

        if (creationDate) {
        }

        if (modificationDate) {

            output += new SimpleDateFormat(dateFormat).format(new Date(file
                    .lastModified()));

        }

        if (size) {

            long len = file.length();
            double l = 0;

            NumberFormat n = NumberFormat.getInstance();
            n.setMinimumFractionDigits(minPrecision);
            n.setMaximumFractionDigits(maxPrecision);

            if (unit.equalsIgnoreCase("b") || unit.equalsIgnoreCase("bytes")) {
                l = len;
                n.setMaximumFractionDigits(0);
            }
            else if (unit.equalsIgnoreCase("kB")
                    || unit.equalsIgnoreCase("kilobytes"))
                l = len / 1024;
            else if (unit.equalsIgnoreCase("MB")
                    || unit.equalsIgnoreCase("megabytes"))
                l = len / 1024 / 1024;
            else if (unit.equalsIgnoreCase("GB")
                    || unit.equalsIgnoreCase("gigabytes"))
                l = len / 1024 / 1024 / 1024;
            else if (unit.equalsIgnoreCase("TB")
                    || unit.equalsIgnoreCase("terabytes"))
                l = len / 1024 / 1024 / 1024 / 1024;

            output = n.format(l) + " " + unit;

        }

        try {
            pageContext.getOut().print(output);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return EVAL_BODY_INCLUDE;

    }

    public int doEndTag() {
        return EVAL_PAGE;
    }

    public void setCreationDate(boolean creationDate) {
        this.creationDate = creationDate;
    }

    public void setMaxPrecision(int maxPrecision) {
        this.maxPrecision = maxPrecision;
    }

    public void setMinPrecision(int minPrecision) {

        this.minPrecision = minPrecision;

        if (minPrecision > maxPrecision)
            maxPrecision = minPrecision;

    }

    public void setModificationDate(boolean modificationDate) {
        this.modificationDate = modificationDate;
    }

    public void setPath(String path) {

        file = new File(((HttpServletRequest) pageContext.getRequest())
                .getPathTranslated());

    }

    public void setPrecision(int precision) {
        this.minPrecision = precision;
        this.maxPrecision = precision;
    }

    public void setSize(boolean size) {
        this.size = size;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.Tag#release()
     */
    public void release() {
    }

}
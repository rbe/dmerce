/*
 * Created on May 17, 2004
 *  
 */
package com.wanci.dmerce.taglib;

import javax.servlet.jsp.tagext.TagSupport;

import com.wanci.dmerce.exceptions.XmlPropertiesFormatException;
import com.wanci.dmerce.kernel.XmlPropertiesReader;

/**
 * @author rb
 * @version $Id: DmerceTagSupport.java,v 1.2 2004/05/26 17:02:56 rb Exp $
 * 
 * Base class for dmerce Taglib
 *  
 */
public abstract class DmerceTagSupport extends TagSupport {

    protected boolean DEBUG = false;

    protected boolean DEBUG2 = false;

    public DmerceTagSupport() {

        try {

            DEBUG = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
                "debug");
            DEBUG2 = XmlPropertiesReader.getInstance().getPropertyAsBoolean(
                "core.debug");

        }
        catch (XmlPropertiesFormatException e) {
        }

    }
    
    public abstract int doStartTag();
    
    public abstract int doEndTag();

}
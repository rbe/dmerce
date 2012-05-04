/*
 * Created on Jun 18, 2003
 */
package com.wanci.dmerce.kernel;

/**
 * general config writer for resource strings
 * 
 * @author mm
 * @version $Id: XmlConfigWriterInterface.java,v 1.1 2003/08/26 13:07:53 mm Exp $
 */
public interface XmlConfigWriterInterface {
	
	/**
	 * resource string for xml string
	 */
	void setXmlResource(String string);
	
	/**
	 * write the xml resource
	 * @return true/false
	 */
	boolean write();

}

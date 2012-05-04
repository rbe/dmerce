/*
 * Created on Jun 18, 2003
 */
package com.wanci.dmerce.kernel;

import java.io.InputStream;

/**
 * general config reader for an xml resource string
 * 
 * @author mm
 * @version $Id: ExtendedXmlConfigReaderInterface.java,v 1.1 2003/10/01 09:51:47 mf Exp $
 */
public interface ExtendedXmlConfigReaderInterface extends XmlConfigReaderInterface {
	
	/**
	 * resource string for xml string
	 */
	public void setXmlResource(InputStream stream);
	
	/**
	 * Ermittelt, ob die XML-Datei per Stream oder per String
	 * festgelegt worden ist.
	 * @return
	 */
	public boolean isSetAsStream();

}
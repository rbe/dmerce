/*
 * Created on Jun 25, 2003
 */
package com.wanci.dmerce.kernel;

import java.io.FileOutputStream;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * @author mm
 * @version $Id: XmlConfigWriter.java,v 1.3 2003/08/28 08:54:34 mm Exp $
 * 
 * writer for scanner resource
 * 
 */
public class XmlConfigWriter implements XmlConfigWriterInterface {

	/**
	 * zeigt Fehlermeldungen zum Debuggen an, wenn auf true gesetzt
	 */
	private boolean DEBUG;

	/**
	* resource string for xml config string
	*/
	protected String xmlResource;

	/**
	 * xml document 
	 */
	protected Document document;

	/**
	 * root of document
	 */
	protected Element root;
	
	/**
	 * setter for xml filename
	 * @see com.wanci.dmerce.dmf.XmlConfigWriterInterface#setXmlResource(java.lang.String)
	 */
	public void setXmlResource(String string) {

		xmlResource = string;
	}

	/**
	 * write method for all xml files
	 * @see com.wanci.dmerce.dmf.XmlConfigWriterInterface#write()
	 */
	public boolean write() {
		
		if (xmlResource == "" || xmlResource == null)
			return false;

		// serialize it into a file
		FileOutputStream out = null;
		
		try {
			out = new FileOutputStream(xmlResource);
		} catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}

		//set up xml outputter object
		XMLOutputter serializer = new XMLOutputter();
		
		// whitespaces werden gelöscht
		// indent with 2 spaces and insert newline-chars
		serializer.setTrimAllWhite(true);
		serializer.setIndent("  ");
		serializer.setNewlines(true);
		
		//output
		try {
			serializer.output(document, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			if (DEBUG)
				e.printStackTrace();
		}

		return true;
	}

	public static void main(String[] args) {

	}

}
